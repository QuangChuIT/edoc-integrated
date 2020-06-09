package com.bkav.edoc.service.database.services;

import com.bkav.edoc.service.database.daoimpl.*;
import com.bkav.edoc.service.database.entity.*;
import com.bkav.edoc.service.entity.edxml.*;
import com.bkav.edoc.service.redis.RedisKey;
import com.bkav.edoc.service.redis.RedisUtil;
import com.bkav.edoc.service.util.AttachmentGlobalUtil;
import com.bkav.edoc.service.util.PropsUtil;
import org.hibernate.Session;

import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EdocDocumentService {
    private EdocDocumentDaoImpl documentDaoImpl = new EdocDocumentDaoImpl();
    private EdocPriorityDaoImpl priorityDaoImpl = new EdocPriorityDaoImpl();
    private EdocDocumentDetailDaoImpl documentDetailDaoImpl = new EdocDocumentDetailDaoImpl();
    private EdocTraceHeaderListDaoImpl traceHeaderListDaoImpl = new EdocTraceHeaderListDaoImpl();
    private EdocAttachmentDaoImpl attachmentDaoImpl = new EdocAttachmentDaoImpl();
    private EdocNotificationDaoImpl notificationDaoImpl = new EdocNotificationDaoImpl();

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
        if (!addDocumentDetails(messageHeader, docId)) {
            return false;
        }

        // Insert Trace Header List
        if (!addTraceHeaderList(traces, docId)) {
            return false;
        }

        // add attachment
        if (!addAttachments(messageHeader, attachments, domain, docId)) {
            return false;
        }

        // add notifications
        if (!addNotifications(messageHeader, docId)) {
            return false;
        }

        return true;
    }

    public EdocDocument addEdocDocument(MessageHeader messageHeader) throws Exception {
        Session currentSession = documentDaoImpl.openCurrentSession();

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
        documentDaoImpl.closeCurrentSession();
        return newDocument;
    }

    private boolean addDocumentDetails(MessageHeader messageHeader, long docId) {
        Session currentSession = documentDetailDaoImpl.openCurrentSession();
        currentSession.beginTransaction();

        String content = messageHeader.getContent();
        String signerCompetence = messageHeader.getSignerInfo().getCompetence();
        String signerPosition = messageHeader.getSignerInfo().getPosition();
        Date dueDate = null;
        try {
            dueDate = dateFormat.parse(messageHeader.getDueDate());
        } catch (ParseException e) {
        }

        StringBuffer toPlacesBuffer = new StringBuffer();
        ToPlaces toPlaces = messageHeader.getToPlaces();
        for (int i = 0; i < toPlaces.getPlace().size(); i++) {
            toPlacesBuffer.append(toPlaces.getPlace().get(i));
            toPlacesBuffer.append("#");
        }

        String sphereOfPromulgation = messageHeader.getOtherInfo()
                .getSphereOfPromulgation();
        String typerNotation = messageHeader.getOtherInfo().getTyperNotation();
        long pageAmount = messageHeader.getOtherInfo().getPageAmount();
        long promulgationAmount = messageHeader.getOtherInfo().getPromulgationAmount();

        int steeringTypeInt = messageHeader.getSteeringType();
        EdocDocumentDetail.SteeringType steeringType = EdocDocumentDetail.SteeringType.values()[steeringTypeInt];

        // create document detail
        EdocDocumentDetail documentDetail = new EdocDocumentDetail();
        documentDetail.setContent(content);
        documentDetail.setSignerCompetence(signerCompetence);
        documentDetail.setSignerPosition(signerPosition);
        documentDetail.setDueDate(dueDate);
        documentDetail.setToPlaces(toPlacesBuffer.toString());
        documentDetail.setSphereOfPromulgation(sphereOfPromulgation);
        documentDetail.setTyperNotation(typerNotation);
        documentDetail.setPageAmount(pageAmount);
        documentDetail.setPromulgationAmount(promulgationAmount);
        documentDetail.setSteeringType(steeringType);
        documentDaoImpl.setCurrentSession(currentSession);
        EdocDocument document = documentDaoImpl.findById(docId);
        documentDetail.setDocument(document);

        documentDetailDaoImpl.persist(documentDetail);
        currentSession.getTransaction().commit();
        documentDetailDaoImpl.closeCurrentSession();
        return true;
    }

    private boolean addTraceHeaderList(TraceHeaderList traceHeaderList, long docId) {
        Session currentSession = traceHeaderListDaoImpl.openCurrentSession();
        if (traceHeaderList != null && traceHeaderList.getTraceHeaders().size() > 0) {
            documentDaoImpl.setCurrentSession(currentSession);
            EdocDocument document = documentDaoImpl.findById(docId);
            for (TraceHeader trace : traceHeaderList.getTraceHeaders()) {
                EdocTraceHeaderList traceHeader = new EdocTraceHeaderList();
                traceHeader.setOrganDomain(trace.getOrganId());
                traceHeader.setTimeStamp(trace.getTimeStamp());
                if(traceHeaderList.getBussiness() != null) {
                    traceHeader.setBussinessDocReason(traceHeaderList.getBussiness().getBussinessDocReason());
                    int bussinessDocType = (int)traceHeaderList.getBussiness().getBussinessDocType();
                    EdocTraceHeaderList.BussinessDocType type = EdocTraceHeaderList.BussinessDocType.values()[bussinessDocType];
                    traceHeader.setBussinessDocType(type);
                    traceHeader.setPaper((int)traceHeaderList.getBussiness().getPaper());
                }
                traceHeader.setDocument(document);
                traceHeaderListDaoImpl.persist(traceHeader);
            }
        }
        traceHeaderListDaoImpl.closeCurrentSession();
        return true;
    }

    private boolean addNotifications(MessageHeader messageHeader, long docId) {
        Session currentSession = notificationDaoImpl.openCurrentSession();
        documentDaoImpl.setCurrentSession(currentSession);
        EdocDocument document = documentDaoImpl.findById(docId);
        // Insert Notification
        List<To> tos = messageHeader.getTo();
        Date dueDate = null;
        try {
            dueDate = dateFormat.parse(messageHeader.getDueDate());
        } catch (ParseException e) {
        }
        for (To to : tos) {
            EdocNotification notification = new EdocNotification();
            notification.setCreateDate(new Date());
            notification.setDueDate(dueDate);
            notification.setReceiverId(to.getOrganId());
            notification.setDocument(document);
            notificationDaoImpl.persist(notification);
        }
        notificationDaoImpl.closeCurrentSession();
        return true;
    }

    private boolean addAttachments(MessageHeader messageHeader, List<Attachment> attachments, String domain, Long docId) throws Exception {
        // Insert vao bang Attachment
        AttachmentGlobalUtil attUtil = new AttachmentGlobalUtil();
        String rootPath = attUtil.getAttachmentPath();
        long totalSize = 0;
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < attachments.size(); i++) {
            Attachment attachment = attachments.get(i);
            String dataPath = new StringBuilder(domain).append(SPERATOR)
                    .append(cal.get(Calendar.YEAR)).append(SPERATOR)
                    .append(cal.get(Calendar.MONTH) + 1).append(SPERATOR)
                    .append(cal.get(Calendar.DAY_OF_MONTH)).append(SPERATOR)
                    .append(docId).append("_").append(i + 1).toString();

            String specPath = new StringBuilder(rootPath)
                    .append((rootPath.endsWith(SPERATOR) ? "" : SPERATOR))
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

            if (!addAttachment(docId, messageHeader, attachment, size, dataPath)) {
                return false;
            }
        }
        return true;
    }

    private boolean addAttachment(long docId, MessageHeader messageHeader, Attachment attachment, long fileSize, String path) throws Exception {
        Session currentSession = attachmentDaoImpl.openCurrentSession();
        String name = attachment.getName();
        String type = attachment.getContentType();
        String toOrganDomain = getToOrganDomain(messageHeader.getTo());

        EdocAttachment edocAttachment = new EdocAttachment();
        edocAttachment.setName(name);
        edocAttachment.setType(type);
        edocAttachment.setToOrganDomain(toOrganDomain);
        edocAttachment.setCreateDate(new Date());
        edocAttachment.setFullPath(path);
        edocAttachment.setSize(String.valueOf(fileSize));
        documentDaoImpl.setCurrentSession(currentSession);
        EdocDocument document = documentDaoImpl.findById(docId);
        edocAttachment.setDocument(document);

        attachmentDaoImpl.persist(edocAttachment);
        attachmentDaoImpl.closeCurrentSession();
        return edocAttachment != null;
    }

//    private boolean addTraceHeaderList(TraceHeaderList traceHeaderList, long docId) {
//        traceHeaderListDaoImpl.openCurrentSession();
//
//        traceHeaderList.getTraceHeaders().get(1).
//
//        traceHeaderListDaoImpl.persist(documentDetail);
//        traceHeaderListDaoImpl.closeCurrentSession();
//        return true;
//    }

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

    public void saveGetDocumentCache(long documentId, String fromOrganDomain, Date sentDate) {
        Map<String, Object> cacheThis = new HashMap<>();
        cacheThis.put("sentDate", sentDate);
        cacheThis.put("fromDomain", fromOrganDomain);

        RedisUtil.getInstance().set(RedisKey.getKey(String.valueOf(documentId),
                RedisKey.GET_DOCUMENT_KEY), cacheThis);
    }
}