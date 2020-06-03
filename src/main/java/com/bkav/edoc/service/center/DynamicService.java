package com.bkav.edoc.service.center;

import com.bkav.edoc.service.commonutil.ErrorCommonUtil;
import com.bkav.edoc.service.entity.edxml.Attachment;
import com.bkav.edoc.service.entity.edxml.MessageHeader;
import com.bkav.edoc.service.entity.edxml.TraceHeaderList;
import com.bkav.edoc.service.mineutil.ExtractMime;
import com.bkav.edoc.service.mineutil.XmlUtil;
import org.apache.axiom.soap.SOAPBody;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.ManagedLifecycle;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

public class DynamicService extends AbstractMediator implements ManagedLifecycle {

    public boolean mediate(MessageContext messageContext) {
        log.info("E document  mediator invoker");

        org.apache.axis2.context.MessageContext inMessageContext = ((Axis2MessageContext) messageContext).getAxis2MessageContext();

        String soapAction = inMessageContext.getSoapAction();

        String soapNamespace = inMessageContext.getEnvelope().getNamespace()
                .getNamespaceURI();
        log.info(messageContext.getEnvelope());
        XmlUtil xmlUtil = new XmlUtil();
        try {
            Document doc = xmlUtil.convertToDocument(messageContext.getEnvelope());

            switch (soapAction){
                case "SendDocument":
                    sendDocument(doc);
                    break;
                case "GetListDocument":
                    break;
                default:
                    log.error(ErrorCommonUtil.getInfoToLog(
                            "Can't define soap envelop", DynamicService.class));
            }
        } catch (Exception e) {
            log.error(ErrorCommonUtil.getInfoToLog(
                    "Can't define soap envelop", DynamicService.class));
        }

        log.info("Soap Action " + soapAction + " invoke !!!!!!!! ");

        log.info("Soap Namespace " + soapNamespace + " invoke !!!!!!!! ");

        return true;
    }

    public void sendDocument(Document envelop){

        List<Error> errorList = new ArrayList<Error>();

        List<Attachment> attachmentsEntity = new ArrayList<Attachment>();

        MessageHeader messageHeader = null;

        TraceHeaderList traceHeaderList = null;

        try {
            messageHeader = extractMime.getMessageHeader(envelop);
        } catch (Exception e){
            log.error(ErrorCommonUtil.getInfoToLog(
                    "Can't get message header", DynamicService.class));
        }
    }
    private static final Log log = LogFactory.getLog(DynamicService.class);

    @Override
    public void init(SynapseEnvironment synapseEnvironment) {

    }

    @Override
    public void destroy() {

    }

    private static final ExtractMime extractMime = new ExtractMime();;
}
