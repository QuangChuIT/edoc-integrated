package com.bkav.edoc.service.database.services;

import com.bkav.edoc.service.database.daoimpl.*;
import com.bkav.edoc.service.database.entity.*;
import com.bkav.edoc.service.entity.edxml.*;
import com.bkav.edoc.service.redis.RedisKey;
import com.bkav.edoc.service.redis.RedisUtil;
import com.bkav.edoc.service.util.AttachmentGlobalUtil;
import com.bkav.edoc.service.util.PropsUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EdocDocumentService {
    private EdocDocumentDaoImpl documentDaoImpl = new EdocDocumentDaoImpl();
    private EdocPriorityDaoImpl priorityDaoImpl = new EdocPriorityDaoImpl();
    private EdocDocumentDetailService documentDetailService = new EdocDocumentDetailService();
    private EdocTraceHeaderListService traceHeaderListService = new EdocTraceHeaderListService();
    private EdocAttachmentService attachmentService = new EdocAttachmentService();
    private EdocNotificationService notificationService = new EdocNotificationService();

    private String SPERATOR = File.separator;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "dd/MM/yyyy");

    public EdocDocumentService() {

    }

    public List<EdocDocument> findAll() {
        documentDaoImpl.openCurrentSession();
        List<EdocDocument> result = documentDaoImpl.findAll();
        documentDaoImpl.closeCurrentSession();
        return result;
    }

    /**
     * Add document from envelop
     * @param messageHeader
     * @param traces
     * @param attachments
     * @return
     * @throws Exception
     */
    public boolean addDocument(MessageHeader messageHeader, TraceHeaderList traces, List<Attachment> attachments) throws Exception {
        // add Edoc document
        EdocDocument edocDocument = addEdocDocument(messageHeader);
        if(edocDocument == null) {
            return false;
        }
        long docId = edocDocument.getDocumentId();
        String domain = edocDocument.getFromOrganDomain();

        // Add document to cache (using by getdocument)
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

        // add attachment
        if (!attachmentService.addAttachments(messageHeader, attachments, domain, docId)) {
            return false;
        }

        // add notifications
        if (!notificationService.addNotifications(messageHeader, docId)) {
            return false;
        }

        return true;
    }

    /**
     * Add edoc document, save to database
     * @param messageHeader
     * @return
     * @throws Exception
     */
    public EdocDocument addEdocDocument(MessageHeader messageHeader) throws Exception {
        Session currentSession = documentDaoImpl.openCurrentSession();

        try {
            currentSession.beginTransaction();

            // get info of edoc document
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

            documentDaoImpl.persist(newDocument);
            currentSession.getTransaction().commit();
            return newDocument;
        } catch (Exception e) {
            log.error(e);
            if(currentSession != null) {
                currentSession.getTransaction().rollback();
            }
            return null;
        } finally {
            documentDaoImpl.closeCurrentSession();
        }
    }

    /**
     * get to organ domain from list tos
     * @param tos
     * @return
     */
    private String getToOrganDomain(List<To> tos) {

        StringBuffer toOrganDomainBuffer = new StringBuffer();

        for (int i = 0; i < tos.size(); i++) {
            toOrganDomainBuffer.append(tos.get(i).getOrganId());

            if (tos.size() > 1 && i < tos.size() - 1) {
                toOrganDomainBuffer.append("#");
            }

        }
        return toOrganDomainBuffer.toString();
    }

    /**
     * save document to cache for get document
     * @param documentId
     * @param fromOrganDomain
     * @param sentDate
     */
    private void saveGetDocumentCache(long documentId, String fromOrganDomain, Date sentDate) {
        Map<String, Object> cacheThis = new HashMap<>();
        cacheThis.put("sentDate", sentDate);
        cacheThis.put("fromDomain", fromOrganDomain);

        RedisUtil.getInstance().set(RedisKey.getKey(String.valueOf(documentId),
                RedisKey.GET_DOCUMENT_KEY), cacheThis);
    }

    private static final Log log = LogFactory.getLog(EdocDocumentService.class);
}
