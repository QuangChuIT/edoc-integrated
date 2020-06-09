package com.bkav.edoc.service.util;

import com.bkav.edoc.service.mineutil.XmlUtil;
import com.bkav.edoc.service.resource.StringPool;
import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.TransportUtils;
import org.apache.axis2.util.XMLUtils;
import org.apache.synapse.commons.json.JsonUtil;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

public class ResponseUtil {
    private static XmlUtil xmlUtil = new XmlUtil();

    /**
     * Takes a map containing key value pairs and maps those key-value pairs in to tag-value pairs within an
     * OMElement, and creates a new SOAP envelope using this OMElement.
     *
     * @param elements to be written to the envelope in Map format
     * @return SOAP Envelope to be added to the message context
     * @throws IOException If a failure on parsing JSON
     */
    public static SOAPEnvelope buildResultEnvelope(MessageContext messageContext,
                                                   final Map<String, Object> elements, String soapAction) throws Exception {

        SOAPEnvelope responseEnvelope = null;
        OMFactory omFactory = OMAbstractFactory.getOMFactory();

        OMNamespace omNamespace = omFactory.createOMNamespace(
                StringPool.TARGET_NAMESPACE, StringPool.EDXML_PREFIX);
        Object objectResult = elements.get(StringPool.ENVELOPE_SAVED_KEY);
        OMElement omElement = omFactory.createOMElement(soapAction
                + "Response", omNamespace);

        // xoa luon du lieu dau vao
        messageContext.flush();

        if (objectResult != null && objectResult instanceof Document) {
            // SAAJUtil.
            Document doc = (Document) objectResult;

            responseEnvelope = xmlUtil.getFromDocument(doc);
        } else {

            // Check and add body child
            objectResult = elements.get(StringPool.CHILD_BODY_KEY);

            if (objectResult != null) {
                if (objectResult instanceof Document) {

                    Document bodyChild = (Document) objectResult;

                    OMElement resultChild = XMLUtils.toOM(bodyChild
                            .getDocumentElement());

                    if (soapAction.equals("GetDocument")
                            || soapAction.equals("GetMessage")) {

                        responseEnvelope.getBody()
                                .addChild(resultChild);

                    } else {

                        omElement.addChild(resultChild);

                        // Check and add documentId to sendDocument
                        // response
                        if (soapAction.equals("SendDocument")
                                || soapAction.equals("SendMessage")) {

                            objectResult = elements
                                    .get(StringPool.SEND_DOCUMENT_RESPONSE_ID_KEY);

                            if (objectResult != null) {
                                if (objectResult instanceof Document) {

                                    Document documentId = (Document) objectResult;

                                    OMElement documentIdOM = XMLUtils
                                            .toOM(documentId
                                                    .getDocumentElement());

                                    omElement.addChild(documentIdOM);
                                }
                            }

                        }

                        responseEnvelope.getBody().addChild(omElement);
                    }
                }
            }

            // Check and add header child
            objectResult = elements.get(StringPool.MESSAGE_HEADER_KEY);
            if (objectResult != null) {

                if (objectResult instanceof Document) {

                    Document headerChild = (Document) objectResult;

                    OMElement resultChild = XMLUtils.toOM(headerChild
                            .getDocumentElement());

                    responseEnvelope.getHeader().addChild(resultChild);
                }

            }

            // Check and add header child
            objectResult = elements.get(StringPool.TRACE_HEADER_KEY);

            if (objectResult != null) {
                if (objectResult instanceof Document) {

                    Document headerChild = (Document) objectResult;

                    OMElement resultChild = XMLUtils.toOM(headerChild
                            .getDocumentElement());

                    responseEnvelope.getHeader().addChild(resultChild);

                }

            }
        }

        // Check and add attachment
        objectResult = elements.get(StringPool.ATTACHMENT_KEY);

        if (objectResult != null) {

            if (objectResult instanceof Attachments) {

                Attachments attachments = (Attachments) objectResult;

                messageContext.setAttachmentMap(attachments);
            }

        }
        return responseEnvelope;
    }
}
