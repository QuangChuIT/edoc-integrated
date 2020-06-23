package com.bkav.edoc.service.database.services;

import com.bkav.edoc.service.commonutil.XmlGregorianCalendarUtil;
import com.bkav.edoc.service.database.daoimpl.*;
import com.bkav.edoc.service.database.entity.*;
import com.bkav.edoc.service.entity.edxml.*;
import com.bkav.edoc.service.kernel.util.GetterUtil;
import com.bkav.edoc.service.mineutil.Mapper;
import com.bkav.edoc.service.redis.RedisKey;
import com.bkav.edoc.service.redis.RedisUtil;
import com.bkav.edoc.service.util.AttachmentGlobalUtil;
import com.bkav.edoc.service.util.PropsUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EdocDocumentService {
    private final EdocDocumentDaoImpl documentDaoImpl = new EdocDocumentDaoImpl();
    private final EdocPriorityDaoImpl priorityDaoImpl = new EdocPriorityDaoImpl();
    private final EdocDocumentDetailService documentDetailService = new EdocDocumentDetailService();
    private final EdocTraceHeaderListService traceHeaderListService = new EdocTraceHeaderListService();
    private final EdocAttachmentService attachmentService = new EdocAttachmentService();
    private final EdocNotificationService notificationService = new EdocNotificationService();

    private final String SEPARATOR = File.separator;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "dd/MM/yyyy");
    private Mapper mapper = new Mapper();

    public EdocDocumentService() {

    }

    public EdocDocument getDocument(long documentId) {
        documentDaoImpl.openCurrentSession();
        EdocDocument result = documentDaoImpl.findById(documentId);
        documentDaoImpl.closeCurrentSession();
        return result;
    }

    public List<EdocDocument> findAll() {
        documentDaoImpl.openCurrentSession();
        List<EdocDocument> result = documentDaoImpl.findAll();
        documentDaoImpl.closeCurrentSession();
        return result;
    }

    /**
     * Add document from envelop
     *
     * @param messageHeader
     * @param traces
     * @param attachments
     * @return
     * @throws Exception
     */
    public boolean addDocument(MessageHeader messageHeader, TraceHeaderList traces, List<Attachment> attachments,
                               StringBuilder outDocumentId, StringBuilder attachmentSize) throws Exception {
        // output document id
        if (outDocumentId == null) {
            outDocumentId = new StringBuilder();
        }
        // add eDoc document
        EdocDocument edocDocument = addEdocDocument(messageHeader);
        if (edocDocument == null) {
            return false;
        }
        long docId = edocDocument.getDocumentId();
        outDocumentId.append(docId);
        String domain = edocDocument.getFromOrganDomain();

        // Add document to cache (using by get document)
        saveGetDocumentCache(docId, edocDocument.getFromOrganDomain(),
                edocDocument.getSentDate());

        // Insert document detail
        if (!documentDetailService.addDocumentDetail(messageHeader, docId)) {
            return false;
        }

        // Insert Trace Header List
        if (!traceHeaderListService.addTraceHeaderList(traces, docId)) {
            return false;
        }
        // Insert vao bang Attachment
        AttachmentGlobalUtil attUtil = new AttachmentGlobalUtil();
        String rootPath = attUtil.getAttachmentPath();
        long totalSize = 0;
        JSONArray jsonAttachments = new JSONArray();
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < attachments.size(); i++) {
            Attachment attachment = attachments.get(i);
            String dataPath = new StringBuilder(domain).append(SEPARATOR)
                    .append(cal.get(Calendar.YEAR)).append(SEPARATOR)
                    .append(cal.get(Calendar.MONTH) + 1).append(SEPARATOR)
                    .append(cal.get(Calendar.DAY_OF_MONTH)).append(SEPARATOR)
                    .append(docId).append("_").append(i + 1).toString();

            String specPath = new StringBuilder(rootPath)
                    .append((rootPath.endsWith(SEPARATOR) ? "" : SEPARATOR))
                    .append(dataPath).toString();

            long size = 0L;
            InputStream is = attachment.getValue();

            size = attUtil.saveToFile(specPath, is);
            is.close();
            totalSize += size;
            if (size < 0) {
                System.out.println("Save attachment failed!");
                return false;
            }
            long attachmentId = attachmentService.addAttachment(docId, messageHeader, attachment, size, dataPath);

            if (attachmentId == 0) {
                return false;
            } else {
                JSONObject jsonAttachment = new JSONObject();
                jsonAttachment.put("attachmentId", attachmentId);
                jsonAttachment.put("attachmentName", attachment.getName());
                jsonAttachment.put("contentType", attachment.getContentType());
                jsonAttachment.put("link", GetterUtil.getString(PropsUtil.get("service.attachments.link.config")) + SEPARATOR + attachmentId);
                jsonAttachments.put(jsonAttachment);
            }
        }
        Object object = RedisUtil.getInstance().get(RedisKey.GET_ATTACHMENT_BY_DOC_ID + docId, Object.class);
        if (object == null) {
            RedisUtil.getInstance().set(RedisKey.GET_ATTACHMENT_BY_DOC_ID + docId, jsonAttachments);
        }

        attachmentSize.append(totalSize);

        // add notifications
        if (!notificationService.addNotifications(messageHeader, docId)) {
            return false;
        }

        // save pending document to cache
        savePendingDocumentCache(messageHeader.getTo(), docId);

        return true;
    }

    /**
     * Add eDoc document, save to database
     *
     * @param messageHeader
     * @return
     * @throws Exception
     */
    public EdocDocument addEdocDocument(MessageHeader messageHeader) throws Exception {
        Session currentSession = documentDaoImpl.openCurrentSession();

        try {
            currentSession.beginTransaction();

            // get info of eDoc document
            boolean isDraft = false;
            Date sentDate = new Date();
            String edXMLDocId = messageHeader.getDocumentId();
            String subject = messageHeader.getSubject();
            String codeNumber = messageHeader.getCode().getCodeNumber();
            String codeNotation = messageHeader.getCode().getCodeNotation();

            Date promulgationDate = null;

            try {
                promulgationDate = dateFormat.parse(messageHeader
                        .getPromulgationInfo().getPromulgationDate());
            } catch (ParseException e) {
                // promulgationDate = defaultMinDate;
                // Calendar.getInstance().getTime();
                // e.printStackTrace();
            }
            String promulgationPlace = messageHeader.getPromulgationInfo().getPlace();
            int type = messageHeader.getDocumentType().getType();
            EdocDocument.DocumentType documentType = EdocDocument.DocumentType.values()[type];
            String documentTypeName = messageHeader.getDocumentType().getTypeName();

            long priorityId = messageHeader.getOtherInfo().getPriority();
            priorityDaoImpl.setCurrentSession(currentSession);
            EdocPriority priority = priorityDaoImpl.findById(priorityId);
            String toOrganDomain = getToOrganDomain(messageHeader.getTo());
            String fromOrganDomain = messageHeader.getFrom().getOrganId();

            EdocDocument newDocument = new EdocDocument();
            newDocument.setEdXMLDocId(edXMLDocId);
            newDocument.setSubject(subject);
            newDocument.setCodeNumber(codeNumber);
            newDocument.setCodeNotation(codeNotation);
            newDocument.setPromulgationDate(promulgationDate);
            newDocument.setPromulgationPlace(promulgationPlace);
            newDocument.setDocumentType(documentType);
            newDocument.setDocumentTypeName(documentTypeName);
            newDocument.setPriority(priority);
            newDocument.setToOrganDomain(toOrganDomain);
            newDocument.setFromOrganDomain(fromOrganDomain);
            newDocument.setSentDate(sentDate);
            newDocument.setDraft(isDraft);
            Date currentDate = new Date();
            newDocument.setCreateDate(currentDate);
            newDocument.setModifiedDate(currentDate);

            documentDaoImpl.persist(newDocument);
            currentSession.getTransaction().commit();
            return newDocument;
        } catch (Exception e) {
            log.error(e);
            if (currentSession != null) {
                currentSession.getTransaction().rollback();
            }
            return null;
        } finally {
            documentDaoImpl.closeCurrentSession();
        }
    }

    /**
     * get to organ domain from list tos
     *
     * @param tos
     * @return
     */
    private String getToOrganDomain(List<To> tos) {

        StringBuilder toOrganDomainBuffer = new StringBuilder();

        for (int i = 0; i < tos.size(); i++) {
            toOrganDomainBuffer.append(tos.get(i).getOrganId());

            if (tos.size() > 1 && i < tos.size() - 1) {
                toOrganDomainBuffer.append("#");
            }

        }
        return toOrganDomainBuffer.toString();
    }

    public List<String> getToOrganDomains(List<To> tos) {

        List<String> results = new ArrayList<String>();

        for (To to : tos) {
            results.add(to.getOrganId());
        }
        return results;
    }

    /**
     * check document is exist
     *
     * @param subject
     * @param codeNumber
     * @param codeNotation
     * @param promulgationDateStr
     * @param fromOrganDomain
     * @param tos
     * @param attachmentNames
     * @return
     */
    public boolean checkExistDocument(String subject, String codeNumber, String codeNotation, String promulgationDateStr, String fromOrganDomain, List<To> tos, List<String> attachmentNames) {
        documentDaoImpl.openCurrentSession();

        Date promulgationDate = XmlGregorianCalendarUtil.convertToDate(promulgationDateStr, "dd/MM/yyyy");
        String toOrganDomain = getToOrganDomain(tos);

        boolean check = documentDaoImpl.checkExistDocument(subject, codeNumber, codeNotation, promulgationDate, fromOrganDomain, toOrganDomain, attachmentNames);

        documentDaoImpl.closeCurrentSession();
        return check;
    }

    public boolean checkExistDocument(String edXmlDocumentId) {
        documentDaoImpl.openCurrentSession();

        boolean check = documentDaoImpl.checkExistDocument(edXmlDocumentId);

        documentDaoImpl.closeCurrentSession();
        return check;
    }

    public boolean checkNewDocument(TraceHeaderList traceHeaderList) {
        // get business doc type
        long businessDocType = traceHeaderList.getBusiness().getBusinessDocType();
        // with new document, business doc type = 0
        return businessDocType == 0;
    }

    /**
     * save document to cache for get document
     *
     * @param documentId
     * @param fromOrganDomain
     * @param sentDate
     */
    public void saveGetDocumentCache(long documentId, String fromOrganDomain, Date sentDate) {
        Map<String, Object> cacheThis = new HashMap<>();
        cacheThis.put("sentDate", sentDate);
        cacheThis.put("fromDomain", fromOrganDomain);

        RedisUtil.getInstance().set(RedisKey.getKey(String.valueOf(documentId),
                RedisKey.GET_DOCUMENT_KEY), cacheThis);
    }

    /**
     * save pending document for to domain -> cache
     *
     * @param tos
     * @param docId
     */
    private void savePendingDocumentCache(List<To> tos, long docId) {
        for (To to : tos) {
            // TODO: Cache
            List obj = RedisUtil.getInstance().get(RedisKey.getKey(to.getOrganId(), RedisKey.GET_PENDING_KEY), List.class);
            // if data in cache not exist, create new
            if (obj == null) {
                List<Long> documentIds = new ArrayList<Long>();
                documentIds.add(docId);
                RedisUtil.getInstance().set(RedisKey.getKey(to.getOrganId(), RedisKey.GET_PENDING_KEY), documentIds);
            } else {
                // add document id to old list in cache
                List<Long> oldDocumentIds = null;
                if (obj instanceof List) {
                    oldDocumentIds = (List<Long>) obj;
                } else {
                    oldDocumentIds = new ArrayList<Long>();
                }

                oldDocumentIds.add(docId);
                RedisUtil.getInstance().set(RedisKey.getKey(to.getOrganId(), RedisKey.GET_PENDING_KEY), oldDocumentIds);
            }
        }
    }

    public MessageHeader getDocumentById(long docId) {

        EdocDocument document = this.getDocument(docId);

        EdocDocumentDetail detail = document.getDocumentDetail();

        Set<EdocNotification> notifications = document.getNotifications();

        return mapper.modelToMessageHeader(document, detail, notifications);
    }

    private static final Log log = LogFactory.getLog(EdocDocumentService.class);
}
