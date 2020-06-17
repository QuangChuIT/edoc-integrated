package com.bkav.edoc.service.center;

import com.bkav.edoc.service.commonutil.Checker;
import com.bkav.edoc.service.commonutil.ErrorCommonUtil;
import com.bkav.edoc.service.commonutil.XmlChecker;
import com.bkav.edoc.service.database.entity.EdocAttachment;
import com.bkav.edoc.service.database.entity.EdocTrace;
import com.bkav.edoc.service.database.services.*;
import com.bkav.edoc.service.entity.edxml.*;
import com.bkav.edoc.service.entity.edxml.Error;
import com.bkav.edoc.service.kernel.util.GetterUtil;
import com.bkav.edoc.service.mineutil.*;
import com.bkav.edoc.service.redis.RedisKey;
import com.bkav.edoc.service.redis.RedisUtil;
import com.bkav.edoc.service.resource.EdXmlConstant;
import com.bkav.edoc.service.resource.StringPool;
import com.bkav.edoc.service.util.CommonUtil;
import com.bkav.edoc.service.util.PropsUtil;
import com.bkav.edoc.service.util.ResponseUtil;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.ManagedLifecycle;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseLog;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DynamicService extends AbstractMediator implements ManagedLifecycle {

    private final EdocDocumentService documentService = new EdocDocumentService();
    private final EdocNotificationService notificationService = new EdocNotificationService();
    private final EdocAttachmentService attachmentService = new EdocAttachmentService();
    private final EdocTraceHeaderListService traceHeaderListService = new EdocTraceHeaderListService();

    private final EdocTraceService traceService = new EdocTraceService();

    private final String SEPARATOR = File.separator;
    private final ArchiveMime archiveMime = new ArchiveMime();
    private final Mapper mapper = new Mapper();

    public boolean mediate(MessageContext messageContext) {
        log.info("--------------- eDoc mediator invoker by class mediator ---------------");
        org.apache.axis2.context.MessageContext inMessageContext = ((Axis2MessageContext) messageContext).getAxis2MessageContext();

        SynapseLog synLog = getLog(messageContext);

        if (synLog.isTraceOrDebugEnabled()) {
            synLog.traceOrDebug("Start : Log mediator");

            if (synLog.isTraceTraceEnabled()) {
                synLog.traceTrace("Message : " + messageContext.getEnvelope());
            }
        }

        String soapAction = inMessageContext.getSoapAction();

        Map<String, Object> map = new HashMap<>();

        SOAPEnvelope responseEnvelope;

        try {
            Document doc = xmlUtil.convertToDocument(messageContext.getEnvelope());

            switch (soapAction) {
                case "SendDocument":
                    map = sendDocument(doc, inMessageContext);
                    break;
                case "GetListDocument":
                    break;
                case "GetPendingDocumentIds":
                    map = getPendingDocumentIds(doc, inMessageContext);
                    break;
                case "GetDocument":
                    map = getDocument(doc);
                    break;
                case "UpdateTraces":
                    map = updateTraces(doc);
                    break;
                case "GetTraces":
                    map = getTraces(doc);
                    break;
                default:
                    log.error(ErrorCommonUtil.getInfoToLog(
                            "Can't define soap envelop", DynamicService.class));
            }
        } catch (Exception e) {
            log.error(e);
        }
        responseEnvelope = ResponseUtil.buildResultEnvelope(inMessageContext, map, soapAction);
        try {
            inMessageContext.setDoingSwA(true);
            inMessageContext.setEnvelope(responseEnvelope);
        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
        }
        return true;
    }

    private Map<String, Object> getTraces(Document envelop) {
        Map<String, Object> map = new HashMap<>();

        Report report = null;

        Document responseDocument = null;

        Status status = null;

        List<Error> errorList = new ArrayList<>();

        List<EdocTrace> traces = null;

        Document bodyChildDocument;

        try {
            String organId = extractMime.getOrganId(envelop, EdXmlConstant.GET_TRACE);
            if (organId == null || organId.isEmpty()) {
                errorList.add(new Error("M.OrganId", "OrganId is required."));
                report = new Report(false, new ErrorList(errorList));
                bodyChildDocument = xmlUtil.convertEntityToDocument(
                        Report.class, report);
                map.put(StringPool.CHILD_BODY_KEY, bodyChildDocument);

                return map;
            }

            // get trace
            traces = traceService.getEdocTracesByOrganId(organId);
            if(traces == null) {
                traces = new ArrayList<>();
            }
            // disable traces after get traces
            traceService.disableEdocTrace(traces);

            List<Status> statuses = mapper.traceInfoToStatusEntity(traces);
            GetTraceResponse response = new ResponseUtil().createGetTraceResponse(statuses);

            try {
                responseDocument = xmlUtil.convertEntityToDocument(GetTraceResponse.class, response);
            } catch (Exception ex) {
                log.error(ex);
            }

            map.put(StringPool.CHILD_BODY_KEY, responseDocument);
        } catch (Exception e) {
            log.error("Error when get traces " + e.getMessage());
            errorList.add(new Error("M.GetTraces", "Error when process get traces " + e.getMessage()));

            report = new Report(false, new ErrorList(errorList));

            bodyChildDocument = xmlUtil.convertEntityToDocument(
                    Report.class, report);
            map.put(StringPool.CHILD_BODY_KEY, bodyChildDocument);
        }
        return map;
    }

    private Map<String, Object> updateTraces(Document envelop) {
        Map<String, Object> map = new HashMap<>();

        Report report;

        Status status = null;

        List<Error> errorList = new ArrayList<>();

        Document bodyChildDocument;

        long documentId = 0L;

        try {
            // Extract MessageHeader
            status = extractMime.getStatus(envelop);
            // update trace
            if(!traceService.updateTrace(status)) {
                errorList.add(new Error("M.updateTrace", "Error when process update trace"));
                report = new Report(false, new ErrorList(errorList));
            } else{
                report = new Report(true, new ErrorList(errorList));
            }
            bodyChildDocument = xmlUtil.convertEntityToDocument(
                    Report.class, report);
            map.put(StringPool.CHILD_BODY_KEY, bodyChildDocument);
        } catch (Exception e) {
            log.error("Error when update traces " + e.getMessage());
            errorList.add(new Error("M.UpdateTraces", "Error when process get update " + e.getMessage()));

            report = new Report(false, new ErrorList(errorList));

            bodyChildDocument = xmlUtil.convertEntityToDocument(
                    Report.class, report);
            map.put(StringPool.CHILD_BODY_KEY, bodyChildDocument);
        }
        return map;
    }


    private Map<String, Object> getDocument(Document doc) {

        Map<String, Object> map = new HashMap<>();
        List<Error> errors = new ArrayList<>();
        Report report = null;
        List<Error> errorList = new ArrayList<Error>();
        long documentId = 0L;
        String organId = "";

        try {
            documentId = xmlUtil.getDocumentId(doc);
            organId = extractMime.getOrganId(doc, EdXmlConstant.GET_DOCUMENT);
        } catch (Exception ex) {
            log.error("Error when get document " + ex.getMessage());
            documentId = 0L;
            organId = "";
        }
        // check document id and organ id
        if (documentId > 0L && organId != null && !organId.isEmpty()) {

            try {
                // Check quyen voi van ban
                boolean acceptToDocument = false;

                // TODO: Cache
                Object allowObj = null;
                allowObj = RedisUtil.getInstance().get(RedisKey.getKey(organId
                        + documentId, RedisKey.CHECK_ALLOW_KEY), Boolean.class);
                if (allowObj != null) {
                    acceptToDocument = (Boolean) allowObj;
                } else {
                    acceptToDocument = notificationService.checkAllowWithDocument(String.valueOf(documentId), organId);

                    // add to cache
                    RedisUtil.getInstance().set(RedisKey.getKey(organId
                            + documentId, RedisKey.CHECK_ALLOW_KEY), acceptToDocument);
                }

                if (!acceptToDocument) {
                    errors.add(new Error(
                            "M.DOCUMENT",
                            "Not allow with document !!!!"));
                    errorList.add(new Error("Error", "Document does not exist !!!!"));

                    report = new Report(false, new ErrorList(errorList));

                    Document bodyChildDocument = xmlUtil
                            .convertEntityToDocument(Report.class, report);

                    map.put(StringPool.CHILD_BODY_KEY, bodyChildDocument);

                    return map;
                }

                Object attachmentObj = null;
                JSONArray arrayAttachments = new JSONArray();
                attachmentObj = RedisUtil.getInstance().get(RedisKey.GET_ATTACHMENT_BY_DOC_ID + documentId, Object.class);
                if (attachmentObj == null) {
                    List<EdocAttachment> attachments = attachmentService.getEdocAttachmentsByDocId(documentId);
                    for (EdocAttachment attachment : attachments) {
                        JSONObject objectAttachment = new JSONObject();
                        objectAttachment.put("attachmentId", attachment.getAttachmentId());
                        objectAttachment.put("attachmentName", attachment.getName());
                        objectAttachment.put("contentType", attachment.getType());
                        objectAttachment.put("link", PropsUtil.get("service.attachments.link.config") + SEPARATOR + attachment.getAttachmentId());
                        arrayAttachments.put(objectAttachment);
                    }
                    attachmentObj = arrayAttachments;
                    RedisUtil.getInstance().set(RedisKey.GET_ATTACHMENT_BY_DOC_ID + documentId, arrayAttachments);
                }

                List<Attachment> attachmentsByEntity = attachmentService.getAttachmentsByDocumentId(documentId);

                // get saved doc in cache
                String savedDocStr = RedisUtil.getInstance().get(RedisKey
                        .getKey(String.valueOf(documentId), RedisKey.GET_ENVELOP_FILE), String.class);
                Document savedDoc = null;
                if (savedDocStr != null) {
                    savedDoc = xmlUtil.getDocumentFromFile(new ByteArrayInputStream(savedDocStr.getBytes(StandardCharsets.UTF_8)));
                }

                if (savedDoc != null) {
                    map = archiveMime.createMime(savedDoc, attachmentsByEntity, attachmentObj);
                } else {
                    // get info in db
                    Envelope envelopeByEntity = new Envelope();
                    Header headerEntity = new Header();
                    MessageHeader messageHeader = new MessageHeader();

                    messageHeader = documentService.getDocumentById(documentId);

                    // TODO: Get Trace for edXML Message in here
                    TraceHeaderList traceHeaderList = traceHeaderListService.getTraceHeaderListByDocId(documentId);

                    headerEntity.setMessageHeader(messageHeader);
                    headerEntity.setTraceHeaderList(traceHeaderList);
                    envelopeByEntity.setHeader(headerEntity);
                    envelopeByEntity.setBody(new Body());

                    map = archiveMime.createMime(envelopeByEntity,
                            attachmentsByEntity, attachmentObj);
                }

                // remove pending document
                this.removePendingDocumentId(organId, documentId);
            } catch (Exception e) {
                log.error("Error when update traces " + e.getMessage());
                errorList.add(new Error("M.GetDocument", "Error when process get document " + e.getMessage()));

                report = new Report(false, new ErrorList(errorList));

                Document bodyChildDocument = xmlUtil.convertEntityToDocument(
                        Report.class, report);
                map.put(StringPool.CHILD_BODY_KEY, bodyChildDocument);
            }
        }

        return map;
    }

    public Map<String, Object> sendDocument(Document envelop, org.apache.axis2.context.MessageContext messageContext) {

        Map<String, Object> map = new HashMap<>();

        List<Error> errorList = new ArrayList<>();

        List<Attachment> attachmentsEntity;

        MessageHeader messageHeader;

        Document bodyChildDocument = null;

        TraceHeaderList traceHeaderList = null;

        StringBuilder strDocumentId = new StringBuilder();
        Report report = xmlChecker.checkXmlTag(envelop);
        if (report.isIsSuccess()) {
            try {
                // Extract MessageHeader
                messageHeader = extractMime.getMessageHeader(envelop);

                // Extract TraceHeaderList
                traceHeaderList = extractMime.getTraceHeaderList(envelop, true);

                //check message
                report = checker.checkMessageHeader(messageHeader);

                if (!report.isIsSuccess()) {
                    bodyChildDocument = xmlUtil.convertEntityToDocument(
                            Report.class, report);

                    map.put(StringPool.CHILD_BODY_KEY, bodyChildDocument);

                    return map;
                }
                // check trace header list
                report = checker.checkTraceHeaderList(traceHeaderList);

                if (!report.isIsSuccess()) {
                    bodyChildDocument = xmlUtil.convertEntityToDocument(
                            Report.class, report);

                    map.put(StringPool.CHILD_BODY_KEY, bodyChildDocument);

                    return map;
                }

                // Get attachment from context
                Map<String, Object> attachments = attachmentUtil
                        .GetAttachmentDocsByContext(messageContext);

                // Check Attachment attachment
                report = attachmentUtil.checkAllowAttachment(envelop,
                        attachments);
                if (!report.isIsSuccess()) {

                    bodyChildDocument = xmlUtil.convertEntityToDocument(Report.class, report);

                    map.put(StringPool.CHILD_BODY_KEY, bodyChildDocument);

                    return map;
                }

                List<String> attachmentNames = new ArrayList<>();

                attachmentsEntity = attachmentUtil.getAttachments(envelop,
                        attachments);

                // TODO: Turning empty attachment in map
                for (Attachment attachment : attachmentsEntity) {
                    attachmentNames.add(attachment.getName());
                }

                boolean enableCheckExist = GetterUtil.get(PropsUtil.get("eDoc.service.sendDocument.checkExist.enable"), false);

                if (enableCheckExist) {
                    // check exist document
                    if (documentService.checkExistDocument(messageHeader.getSubject(), messageHeader.getCode().getCodeNumber()
                            , messageHeader.getCode().getCodeNotation(), messageHeader.getPromulgationInfo().getPromulgationDate()
                            , messageHeader.getFrom().getOrganId(), messageHeader.getTo(), attachmentNames)) {

                        errorList.add(new Error("M.Exist", "Document is exist on ESB !!!!"));
                        report = new Report(false, new ErrorList(errorList));

                        bodyChildDocument = xmlUtil.convertEntityToDocument(
                                Report.class, report);
                        map.put(StringPool.CHILD_BODY_KEY, bodyChildDocument);

                        return map;
                    }
                }
                StringBuilder attachmentSize = new StringBuilder();

                // add document
                if (!documentService.addDocument(messageHeader, traceHeaderList,
                        attachmentsEntity, strDocumentId, attachmentSize)) {
                    bodyChildDocument = xmlUtil.convertEntityToDocument(Report.class, report);

                    map.put(StringPool.CHILD_BODY_KEY, bodyChildDocument);
                    return map;
                }

                // save envelop file to cache
                saveEnvelopeFileCache(envelop, strDocumentId.toString());

                bodyChildDocument = xmlUtil.convertEntityToDocument(Report.class,
                        report);

                Document docIdResponseElm = xmlUtil.getSendResponseDocId(strDocumentId
                        .toString());

                map.put(StringPool.CHILD_BODY_KEY, bodyChildDocument);
                map.put(StringPool.SEND_DOCUMENT_RESPONSE_ID_KEY, docIdResponseElm);
            } catch (Exception e) {
                log.error(e);
                errorList.add(new Error("M.SendDocument", "Error when send document to esb " + e.getMessage()));

                report = new Report(false, new ErrorList(errorList));

                bodyChildDocument = xmlUtil.convertEntityToDocument(Report.class, report);

                map.put(StringPool.CHILD_BODY_KEY, bodyChildDocument);
            }
        }
        return map;
    }

    /**
     * @param doc
     * @return
     */
    public Map<String, Object> getPendingDocumentIds(Document doc, org.apache.axis2.context.MessageContext messageContext) {

        Map<String, Object> map = new HashMap<>();

        Document responseDocument = null;

        List<Long> notifications = null;

        String organId = extractMime.getOrganId(doc, EdXmlConstant.GET_PENDING_DOCUMENT);

        if (organId == null || organId.isEmpty()) {
            List<Error> errorList = new ArrayList<>();
            errorList.add(new Error("M.OrganId", "OrganId is required."));
            Report report = new Report(false, new ErrorList(errorList));
            Document bodyChildDocument = xmlUtil.convertEntityToDocument(
                    Report.class, report);
            map.put(StringPool.CHILD_BODY_KEY, bodyChildDocument);

            return map;
        }

        // TODO: Cache
        List obj = RedisUtil.getInstance().get(RedisKey.getKey(organId, RedisKey.GET_PENDING_KEY), List.class);
        if (obj != null) {
            notifications = CommonUtil.convertToListLong(obj);
        } else {
            try {
                // notifications = new ArrayList<Long>();
                notifications = notificationService.getDocumentIdsByOrganId(organId);
            } catch (Exception e) {
                log.error(e);
            }
        }

        if (notifications == null) {
            notifications = new ArrayList<Long>();
        }

        GetPendingDocumentIDResponse response = new ResponseUtil()
                .createGetPendingDocumentIDResponse(notifications);

        try {
            responseDocument = xmlUtil.convertEntityToDocument(
                    GetPendingDocumentIDResponse.class, response);

        } catch (Exception ex) {
            log.error(ex);
        }

        map.put(StringPool.CHILD_BODY_KEY, responseDocument);
        return map;
    }

    /**
     * save envelop file to cache
     *
     * @param document
     * @param strDocumentId
     * @throws Exception
     */
    private void saveEnvelopeFileCache(Document document, String strDocumentId) {
        try {
            // read document by string
            DOMSource domSource = new DOMSource(document);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);

            // save envelop file to cache
            RedisUtil.getInstance().set(RedisKey.getKey(strDocumentId, RedisKey.GET_ENVELOP_FILE), writer.toString());
        } catch (Exception e) {
            log.error(e);
        }
    }

    /**
     * remove pending document
     *
     * @param domain
     * @param documentId
     */
    private void removePendingDocumentId(String domain, long documentId) {
        // remove in cache
        removePendingDocumentIdInCache(domain, documentId);
        // remove in db
        notificationService.removePendingDocumentId(domain, documentId);
    }

    /**
     * remove pending document in cache
     *
     * @param domain
     * @param documentId
     */
    private void removePendingDocumentIdInCache(String domain, long documentId) {
        List obj = RedisUtil.getInstance().get(RedisKey.getKey(domain,
                RedisKey.GET_PENDING_KEY), List.class);

        if (obj != null) {
            List<Long> oldDocumentIds = CommonUtil.convertToListLong(obj);
            oldDocumentIds.remove(documentId);

            RedisUtil.getInstance().set(RedisKey.getKey(domain,
                    RedisKey.GET_PENDING_KEY), oldDocumentIds);
        }
    }

    private static final Log log = LogFactory.getLog(DynamicService.class);

    @Override
    public void init(SynapseEnvironment synapseEnvironment) {

    }

    @Override
    public void destroy() {

    }

    private static final XmlChecker xmlChecker = new XmlChecker();
    private static final ExtractMime extractMime = new ExtractMime();
    private static final Checker checker = new Checker();
    private static final AttachmentUtil attachmentUtil = new AttachmentUtil();
    private static final XmlUtil xmlUtil = new XmlUtil();
}
