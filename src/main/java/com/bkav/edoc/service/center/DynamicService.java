package com.bkav.edoc.service.center;

import com.bkav.edoc.service.commonutil.Checker;
import com.bkav.edoc.service.commonutil.ErrorCommonUtil;
import com.bkav.edoc.service.commonutil.XmlChecker;
import com.bkav.edoc.service.database.services.EdocDocumentService;
import com.bkav.edoc.service.entity.edxml.Attachment;
import com.bkav.edoc.service.entity.edxml.MessageHeader;
import com.bkav.edoc.service.entity.edxml.Report;
import com.bkav.edoc.service.entity.edxml.TraceHeaderList;
import com.bkav.edoc.service.mineutil.AttachmentUtil;
import com.bkav.edoc.service.mineutil.ExtractMime;
import com.bkav.edoc.service.mineutil.XmlUtil;
import com.bkav.edoc.service.resource.StringPool;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicService extends AbstractMediator implements ManagedLifecycle {

    private EdocDocumentService documentService = new EdocDocumentService();

    public boolean mediate(MessageContext messageContext) {
        log.info("E document  mediator invoker");

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
                default:
                    log.error(ErrorCommonUtil.getInfoToLog(
                            "Can't define soap envelop", DynamicService.class));
            }
        } catch (Exception e) {
            log.error(e);
        }
        responseEnvelope = ResponseUtil.buildResultEnvelope(inMessageContext,map,soapAction);

        try {
            messageContext.setEnvelope(responseEnvelope);
        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
        }
        return true;
    }

    public Map<String, Object> sendDocument(Document envelop, org.apache.axis2.context.MessageContext messageContext) {

        Map<String, Object> map = new HashMap<>();

        List<Attachment> attachmentsEntity = new ArrayList<Attachment>();

        MessageHeader messageHeader = null;

        Document bodyChildDocument = null;

        TraceHeaderList traceHeaderList = null;

        Report report = xmlChecker.checkXmlTag(envelop);
        if (report.isIsSuccess()) {
            try {
                messageHeader = extractMime.getMessageHeader(envelop);

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

                // Kiem tra attachment
                report = attachmentUtil.checkAllowAttachment(envelop,
                        attachments);
                if (!report.isIsSuccess()) {

                }

                List<String> attachmentNames = new ArrayList<String>();

                attachmentsEntity = attachmentUtil.getAttachments(envelop,
                        attachments);
                for (Attachment attachment : attachmentsEntity) {
                    attachmentNames.add(attachment.getName());
                }
                if (!documentService.addDocument(messageHeader, traceHeaderList,
                        attachmentsEntity)) {

                }
            } catch (Exception e) {
                log.error(e);
            }
        }
        return map;
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
