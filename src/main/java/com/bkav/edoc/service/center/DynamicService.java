package com.bkav.edoc.service.center;

import com.bkav.edoc.service.commonutil.Checker;
import com.bkav.edoc.service.commonutil.ErrorCommonUtil;
import com.bkav.edoc.service.commonutil.XmlChecker;
import com.bkav.edoc.service.database.services.EdocDocumentService;
import com.bkav.edoc.service.database.services.EdocNotificationService;
import com.bkav.edoc.service.entity.edxml.*;
import com.bkav.edoc.service.mineutil.AttachmentUtil;
import com.bkav.edoc.service.mineutil.ExtractMime;
import com.bkav.edoc.service.mineutil.MimeUtil;
import com.bkav.edoc.service.mineutil.XmlUtil;
import com.bkav.edoc.service.redis.RedisKey;
import com.bkav.edoc.service.redis.RedisUtil;
import com.bkav.edoc.service.resource.StringPool;
import com.bkav.edoc.service.util.CommonUtil;
import com.bkav.edoc.service.util.ResponseUtil;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.ManagedLifecycle;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DynamicService extends AbstractMediator implements ManagedLifecycle {

    private EdocDocumentService documentService = new EdocDocumentService();

    private EdocNotificationService notificationService = new EdocNotificationService();

    public boolean mediate(MessageContext messageContext) {
        log.info("--------------- eDoc mediator invoker by class mediator ---------------");

        org.apache.axis2.context.MessageContext inMessageContext = ((Axis2MessageContext) messageContext).getAxis2MessageContext();

        String soapAction = inMessageContext.getSoapAction();

        Map<String, Object> map = new HashMap<>();

        SOAPEnvelope responseEnvelope = null;

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
                    map = getDocument(doc, inMessageContext);
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
            messageContext.setEnvelope(responseEnvelope);
        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
        }
        return true;
    }

    private Map<String, Object> getDocument(Document doc, org.apache.axis2.context.MessageContext inMessageContext) {
        Map<String, Object> map = new HashMap<>();
        return map;
    }

    public Map<String, Object> sendDocument(Document envelop, org.apache.axis2.context.MessageContext messageContext) {

        Map<String, Object> map = new HashMap<>();

        List<Attachment> attachmentsEntity = new ArrayList<Attachment>();

        MessageHeader messageHeader = null;

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

                }

                List<String> attachmentNames = new ArrayList<>();

                attachmentsEntity = attachmentUtil.getAttachments(envelop,
                        attachments);
                for (Attachment attachment : attachmentsEntity) {
                    attachmentNames.add(attachment.getName());
                }

                // add document
                if (!documentService.addDocument(messageHeader, traceHeaderList,
                        attachmentsEntity, strDocumentId)) {

                }

                // save envelop file to cache
                saveEnvelopeFileCache(envelop, strDocumentId.toString());

            } catch (Exception e) {
                log.error(e);
            }
        }
        return map;
    }

    /**
     * @param doc
     * @return
     */
    public Map<String, Object> getPendingDocumentIds(Document doc, org.apache.axis2.context.MessageContext messageContext) {

        Map<String, Object> map = new HashMap<String, Object>();

        Document responseDocument = null;

        List<Long> notifications = null;

        String organId = extractMime.getOrganId(doc);

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
