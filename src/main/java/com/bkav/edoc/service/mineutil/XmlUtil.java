package com.bkav.edoc.service.mineutil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.activation.DataHandler;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import com.bkav.edoc.service.commonutil.Checker;
import com.bkav.edoc.service.database.entity.EdocTraceHeaderList;
import com.bkav.edoc.service.entity.edxml.*;
import com.bkav.edoc.service.entity.soapenv.Body;
import com.bkav.edoc.service.entity.soapenv.Header;
import com.bkav.edoc.service.resource.StringXpath;
import com.bkav.edoc.service.util.EdXMLConfigKey;
import com.bkav.edoc.service.util.EdXMLConfigUtil;
import com.sun.xml.xsom.*;
import com.sun.xml.xsom.parser.XSOMParser;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.apache.axis2.util.XMLUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom2.Attribute;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.DOMBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.bkav.edoc.service.commonutil.ErrorCommonUtil;

import com.bkav.edoc.service.resource.EdXmlConstant;
import com.bkav.edoc.service.resource.StringPool;


public class XmlUtil {

    private static ExtractMime extractMine = new ExtractMime();
    private XpathUtil xpathUtil = new XpathUtil();
    /**
     * @param cls
     * @param obj
     * @return
     */
    @SuppressWarnings("rawtypes")
    public Document convertEntityToDocument(Class cls, Object obj) {
        JAXBContext jc;
        Document document = null;
        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);
        try {
            jc = JAXBContext.newInstance(cls);

            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(obj, result);
            InputStream input = new ByteArrayInputStream(writer.toString()
                    .getBytes("UTF8"));

            document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().parse(input);
        } catch (Exception e) {
            _log.error(ErrorCommonUtil.getInfoToLog(e.getLocalizedMessage(),
                    getClass()));
            e.printStackTrace();
            return null;
        }
        return document;
    }

    /**
     * @param file
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public Document getDocumentFromFile(File file)
            throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();

        return doc;
    }

    /**
     * @param is
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public Document getDocumentFromFile(InputStream is)
            throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(is);

        return doc;
    }

    /**
     * @param envelope
     * @return
     */
    public Document convertToDocument(SOAPEnvelope envelope) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
		return getDocument(envelope, factory);
	}

	private Document getDocument(SOAPEnvelope envelope, DocumentBuilderFactory factory) {
		try {
			String str = envelope.toString();
			InputStream is = new ByteArrayInputStream(str.getBytes("UTF8"));
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(is);
			is.close();
			return doc;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public Document convertToDocument(SOAPEnvelope envelope, boolean awareNS) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(awareNS);
		return getDocument(envelope, factory);
	}

    public static SOAPEnvelope getFromDocument(Document envelopeDoc)
            throws Exception {

        OMElement tempElement = XMLUtils.toOM(envelopeDoc.getDocumentElement());

        StAXSOAPModelBuilder stAXSOAPModelBuilder = new StAXSOAPModelBuilder(
                tempElement.getXMLStreamReader(), null);

        return stAXSOAPModelBuilder.getSOAPEnvelope();
    }

    public static org.jdom2.Document convertFromDom(Document document) {
        DOMBuilder builder = new DOMBuilder();
        org.jdom2.Document output = (org.jdom2.Document) builder
                .build(document);
        return output;
    }

    /**
     * @param xmlString
     * @return
     */
    public Document convertToDocument(String xmlString) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        try {
            InputStream is = new ByteArrayInputStream(
                    xmlString.getBytes("UTF8"));
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            is.close();
            return doc;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * @param handler
     * @return
     */
    public Document convertToDocument(DataHandler handler) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        try {
            InputStream is = handler.getInputStream();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            is.close();
            return doc;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    /**
     * @param traceHeaderList
     * @param ns
     * @return
     * @throws Exception
     */
    public Document getTraceHeaderDoc(TraceHeaderList traceHeaderList,
                                      OMNamespace ns) throws Exception {

        OMFactory factoryOM = OMAbstractFactory.getOMFactory();

        OMElement traceHeaderNode = factoryOM.createOMElement(

                StringPool.EDXML_TRACE_HEADER_BLOCK, ns);
        if (traceHeaderList != null) {
            List<OMNode> traceNodes = getTraceListChild(traceHeaderList, ns);

            // Them cac node con vao trong TraceHeaderList
            for (OMNode omNode : traceNodes) {

                traceHeaderNode.addChild(omNode);

            }
            traceNodes.clear();
        }
        return XMLUtils.toDOM(traceHeaderNode).getOwnerDocument();
    }

    /**
     * @param traceHeaderList
     * @return
     * @throws Exception
     */
    public Document getTraceHeaderDoc(TraceHeaderList traceHeaderList)
            throws Exception {
        OMFactory factoryOM = OMAbstractFactory.getOMFactory();

        OMNamespace ns = factoryOM.createOMNamespace(
                StringPool.TARGET_NAMESPACE, StringPool.BLANK);

        OMElement traceHeaderNode = factoryOM.createOMElement(

                StringPool.EDXML_TRACE_HEADER_BLOCK, ns);

        List<OMNode> traceNodes = getTraceListChild(traceHeaderList, ns);

        // Them cac node con vao trong TraceHeaderList
        for (OMNode omNode : traceNodes) {

            traceHeaderNode.addChild(omNode);

        }
        traceNodes.clear();
        return XMLUtils.toDOM(traceHeaderNode).getOwnerDocument();
    }


    public Document getSendResponseDocId(String docId) throws Exception {
        OMFactory factoryOM = OMAbstractFactory.getOMFactory();

        /*
         * OMNamespace ns = factoryOM.createOMNamespace(
         * StringPool.TARGET_NAMESPACE, StringPool.BLANK);
         */

        QName docIdQName = new QName(StringPool.TARGET_NAMESPACE,
                StringPool.SEND_DOCUMENT_RESPONSE_DOCID);

        OMElement docIdElement = factoryOM.createOMElement(docIdQName);

        docIdElement.setText(docId);

        return XMLUtils.toDOM(docIdElement).getOwnerDocument();
    }

    /**
     * @param bodySoap
     * @param body
     * @param ns
     */
    public void updateBody(SOAPBody bodySoap, Body body, OMNamespace ns) {
        // Node con trong body dung thay the cho node mac dinh
        // cua
        // response
        OMNode bodyNode = getBodyChild(body, ns);

        // Lay ra node mac dinh trong body cua response
        OMElement child = bodySoap.getFirstElement();
        // Xoa bo node mac dinh
        if (child != null)
            child.detach();

        // Them node moi cua edXML vao body
        bodySoap.addChild(bodyNode);
    }

    /**
     * @param envelope
     * @return
     * @throws XPathExpressionException
     */
    public Node getMessageHeader(Document envelope)
            throws XPathExpressionException {
        XPathExpression expr = xpathUtil
                .getXpathExpression(StringXpath.ENV_MESSAGE_HEADER);
        if (expr == null) {
            return null;
        }

        Node messageHeaderNode = (Node) expr.evaluate(envelope,
                XPathConstants.NODE);

        return messageHeaderNode;
    }

    /**
     * @param doc
     * @return
     * @throws XPathExpressionException
     */
    public Node getSignedInfoNode(Node doc) throws XPathExpressionException {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr = xpath
                .compile("//*[local-name()='SignedInfo'][1]");
        if (expr == null) {
            return null;
        }
        Node signedNode = (Node) expr.evaluate(doc, XPathConstants.NODE);

        return signedNode;
    }

    /**
     * @param envelope
     * @return
     * @throws XPathExpressionException
     */
    public NodeList getSignatures(Document envelope)
            throws XPathExpressionException {
        XPathExpression expr = xpathUtil
                .getXpathExpression(StringXpath.SIGNATURE);
        if (expr == null) {
            return null;
        }

        NodeList signatures = (NodeList) expr.evaluate(envelope,
                XPathConstants.NODESET);

        return signatures;
    }

    /**
     * @param xml
     * @return
     * @throws UnsupportedEncodingException
     */
    public boolean validateBySchema(String xml)
            throws UnsupportedEncodingException {

        InputStream is = new ByteArrayInputStream(xml.getBytes("UTF8"));

        Source xmlSource = new StreamSource(is);

        SchemaFactory factory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        String fileDir = EdXMLConfigUtil
                .getValueByKey(EdXMLConfigKey.SCHEMA_FILE_DIR);

        try {

            File file = new File(fileDir);

            Source schemaFile = new StreamSource(file);

            Schema schema = factory.newSchema(schemaFile);

            Validator validator = schema.newValidator();

            validator.validate(xmlSource);

        } catch (Exception ex) {

            _log.error(ErrorCommonUtil.getInfoToLog(ex.getLocalizedMessage(),
                    getClass()));

            return false;
        }

        return true;

    }

    /**
     * @param body
     * @param ns
     * @return
     */
    public OMNode getBodyChild(Body body, OMNamespace ns) {
        OMFactory factoryOM = OMAbstractFactory.getOMFactory();

        OMElement manifest = factoryOM.createOMElement("Manifest", ns);
        List<Reference> refs = body.getManifest().getReference();
        OMNamespace attributeNS = factoryOM.createOMNamespace(
                StringPool.XLINK_NAMESPACE, "xlink");
        for (Reference item : refs) {
            OMElement reference = factoryOM.createOMElement("Reference", ns);

            OMElement attachmentName = factoryOM.createOMElement(
                    "AttachmentName", ns);
            attachmentName.setText(item.getAttachmentName());

            OMAttribute href = factoryOM.createOMAttribute("href", attributeNS,
                    "xlink");
            href.setAttributeValue(item.getHref());

            OMAttribute type = factoryOM.createOMAttribute("type", attributeNS,
                    "xlink");
            type.setAttributeValue("simple");

            reference.addAttribute(href);
            reference.addAttribute(type);

            OMElement description = factoryOM
                    .createOMElement("Description", ns);
            description.setText(item.getDescription());

            reference.addChild(attachmentName);
            reference.addChild(description);

            manifest.addChild(reference);
        }
        return manifest;
    }

    /**
     * @param manifest
     * @param ns
     * @return
     * @throws Exception
     */
    public Document getBodyChildDoc(Manifest manifest, OMNamespace ns)
            throws Exception {
        if (manifest == null)
            return null;
        OMFactory factoryOM = OMAbstractFactory.getOMFactory();

        OMElement manifestNode = factoryOM.createOMElement("Manifest", ns);
        List<Reference> refs = manifest.getReference();
        OMNamespace attributeNS = factoryOM.createOMNamespace(
                StringPool.XLINK_NAMESPACE, "xlink");
        for (Reference item : refs) {
            OMElement reference = factoryOM.createOMElement("Reference", ns);

            OMElement attachmentName = factoryOM.createOMElement(
                    "AttachmentName", ns);
            attachmentName.setText(item.getAttachmentName());

            OMAttribute href = factoryOM.createOMAttribute("href", attributeNS,
                    "xlink");
            href.setAttributeValue(item.getHref());

            OMAttribute type = factoryOM.createOMAttribute("type", attributeNS,
                    "xlink");
            type.setAttributeValue("simple");

            reference.addAttribute(href);
            reference.addAttribute(type);

            OMElement description = factoryOM
                    .createOMElement("Description", ns);
            description.setText(item.getDescription());

            reference.addChild(attachmentName);
            reference.addChild(description);

            manifestNode.addChild(reference);
        }
        return XMLUtils.toDOM(manifestNode).getOwnerDocument();
    }

    /**
     * @param traceHeaderList
     * @param ns
     * @return
     */
    private List<OMNode> getTraceListChild(TraceHeaderList traceHeaderList,
                                           OMNamespace ns) {

        List<OMNode> nodes = new ArrayList<OMNode>();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        OMFactory factoryOM = OMAbstractFactory.getOMFactory();

        List<TraceHeader> traceHeaders = traceHeaderList.getTraceHeaders();

        for (TraceHeader item : traceHeaders) {

            // Tao <TraceHeader>
            OMElement traceHeader = factoryOM
                    .createOMElement("TraceHeader", ns);

            OMElement domain = null;

            domain = factoryOM.createOMElement("OrganId", ns);
            if (domain != null) {

                domain.setText(item.getOrganId());

                traceHeader.addChild(domain);
            }

            OMElement timestamp = factoryOM.createOMElement("Timestamp", ns);

            timestamp.setText(sdf.format(item.getTimeStamp()));

            traceHeader.addChild(timestamp);

            nodes.add(traceHeader);
        }

        // get bussiness node
        OMElement bussinessNode = getBussinessChild(traceHeaderList, ns);
        nodes.add(bussinessNode);

        return nodes;
    }

    private OMElement getBussinessChild(TraceHeaderList traceHeaderList, OMNamespace ns) {
        OMFactory factoryOM = OMAbstractFactory.getOMFactory();

        Bussiness bussiness = traceHeaderList.getBussiness();

        OMElement bussinessNode = factoryOM.createOMElement("Bussiness", ns);

        OMElement bussinessDocTypeNode = factoryOM.createOMElement("BussinessDocType", ns);
        bussinessDocTypeNode.setText(String.valueOf(bussiness.getBussinessDocType()));
        bussinessNode.addChild(bussinessDocTypeNode);

        OMElement bussinessDocReasonNode = factoryOM.createOMElement("BussinessDocReason", ns);
        bussinessDocReasonNode.setText(bussiness.getBussinessDocReason());
        bussinessNode.addChild(bussinessDocReasonNode);

        OMElement paperNode = factoryOM.createOMElement("Paper", ns);
        paperNode.setText(String.valueOf(bussiness.getPaper()));
        bussinessNode.addChild(paperNode);

        // staff info
        OMElement staffInfoNode = getStaffInfoNode(bussiness.getStaffInfo(), ns);
        bussinessNode.addChild(staffInfoNode);

        // replacement info list
        if(bussiness.getBussinessDocType() == EdocTraceHeaderList.BussinessDocType.REPLACE.ordinal()) {
            OMElement replacementInfoListNode = getReplacementInfoListNode(bussiness.getReplacementInfoList(), ns);
            bussinessNode.addChild(replacementInfoListNode);
        }
        else if(bussiness.getBussinessDocType() == EdocTraceHeaderList.BussinessDocType.UPDATE.ordinal()) {
            OMElement bussinessDocumentInfoNode = getBussinessDocumentInfoNode(bussiness.getBussinessDocumentInfo(), ns);
            bussinessNode.addChild(bussinessDocumentInfoNode);
        }

        return bussinessNode;
    }

    private OMElement getReplacementInfoListNode(List<ReplacementInfo> replacementInfoList, OMNamespace ns) {
        OMFactory factoryOM = OMAbstractFactory.getOMFactory();

        OMElement replacementInfoListNode = factoryOM.createOMElement("ReplacementInfoList", ns);

        for(ReplacementInfo replacementInfo: replacementInfoList) {
            OMElement replacementInfoNode = factoryOM.createOMElement("ReplacementInfo", ns);

            OMElement documentIdNode = factoryOM.createOMElement("DocumentId", ns);
            documentIdNode.setText(replacementInfo.getDocumentId());
            replacementInfoNode.addChild(documentIdNode);

            OMElement organIdListNode = factoryOM.createOMElement("OrganIdList", ns);
            for(String organId: replacementInfo.getOrganIdList()) {
                OMElement organIdNode = factoryOM.createOMElement("OrganId", ns);
                organIdNode.setText(organId);
                organIdListNode.addChild(organIdNode);
            }
            replacementInfoNode.addChild(organIdListNode);

            replacementInfoListNode.addChild(replacementInfoNode);
        }

        return replacementInfoListNode;
    }

    private OMElement getBussinessDocumentInfoNode(BussinessDocumentInfo bussinessDocumentInfo, OMNamespace ns) {
        OMFactory factoryOM = OMAbstractFactory.getOMFactory();

        OMElement bussinessDocumentInfoNode = factoryOM.createOMElement("BussinessDocumentInfo", ns);

        OMElement documentInfoNode = factoryOM.createOMElement("DocumentInfo", ns);
        documentInfoNode.setText(bussinessDocumentInfo.getDocumentInfo());
        bussinessDocumentInfoNode.addChild(documentInfoNode);

        OMElement documentReceiverNode = factoryOM.createOMElement("DocumentReceiver", ns);
        documentReceiverNode.setText(bussinessDocumentInfo.getDocumentReceiver());
        bussinessDocumentInfoNode.addChild(documentReceiverNode);

        OMElement receiverListNode = factoryOM.createOMElement("ReceiverList", ns);
        for(Receiver receiver: bussinessDocumentInfo.getReceiverList()) {
            OMElement receiverNode = factoryOM.createOMElement("Receiver", ns);

            OMElement receiverTypeNode = factoryOM.createOMElement("ReceiverType", ns);
            receiverTypeNode.setText(String.valueOf(receiver.getReceiverType()));
            receiverNode.addChild(receiverTypeNode);

            OMElement organIdNode = factoryOM.createOMElement("OrganId", ns);
            organIdNode.setText(String.valueOf(receiver.getOrganId()));
            receiverNode.addChild(organIdNode);

            receiverListNode.addChild(receiverNode);
        }
        bussinessDocumentInfoNode.addChild(receiverListNode);

        return bussinessDocumentInfoNode;
    }

    private OMElement getStaffInfoNode(StaffInfo staffInfo, OMNamespace ns) {
        OMFactory factoryOM = OMAbstractFactory.getOMFactory();
        OMElement staffInfoNode = factoryOM.createOMElement("StaffInfo", ns);

        OMElement departmentNode = factoryOM.createOMElement("Department", ns);
        departmentNode.setText(staffInfo.getDepartment());
        staffInfoNode.addChild(departmentNode);

        OMElement staffNode = factoryOM.createOMElement("Staff", ns);
        staffNode.setText(staffInfo.getStaff());
        staffInfoNode.addChild(staffNode);

        OMElement mobileNode = factoryOM.createOMElement("Mobile", ns);
        mobileNode.setText(staffInfo.getMobile());
        staffInfoNode.addChild(mobileNode);

        OMElement emailNode = factoryOM.createOMElement("Email", ns);
        emailNode.setText(staffInfo.getEmail());
        staffInfoNode.addChild(emailNode);

        return staffInfoNode;
    }

    /**
     * @param envelope
     * @return
     * @throws NumberFormatException
     * @throws XMLStreamException
     */
    public long getDocumntId(Document envelope) throws NumberFormatException,
            XMLStreamException {
        long documentId = 0L;
        String strData = StringPool.BLANK;
        // String strData = getElementData(envelope, elementName);
        org.jdom2.Document jdomEnvDoc = convertFromDom(envelope);

        org.jdom2.Element rootElement = jdomEnvDoc.getRootElement();

        Namespace envNs = getEnvelopeNS(rootElement);

        org.jdom2.Element bodyNode = extractMine.getSingerElement(rootElement,
                EdXmlConstant.BODY_TAG, envNs);

        Iterator<org.jdom2.Element> targetNodes = bodyNode
                .getDescendants(new ElementFilter("DocumentId",
                        EdXmlConstant.EDXML_NS));

        if (targetNodes != null && targetNodes.hasNext()) {
            strData = targetNodes.next().getTextTrim();
        }

        documentId = Long.parseLong(strData);

        return documentId;
    }

    public long getDocumentId(Document envelope) throws NumberFormatException,
            XMLStreamException {
        long documentId = 0L;
        String elementName = "documentId";
        String strData = getElementData(envelope, elementName);
        if (strData == null) {
            _log.error("Khong doc duoc du lieu trong file envelope");
            return documentId;
        } else {
            try {
                documentId = Long.parseLong(strData);
            } catch (Exception ex) {
                _log.error(ex);
                documentId = 0l;
            }
        }
        return documentId;
    }

    public long getCodeNumber(Document envelope) throws NumberFormatException,
            XMLStreamException {
        long codeNumber = 0L;
        String elementName = "codeNumber";
        String strData = getElementData(envelope, elementName);
        if (strData == null) {
            _log.error("Khong doc duoc du lieu trong file envelope");
            return codeNumber;
        } else {
            try {
                codeNumber = Long.parseLong(strData);
            } catch (Exception ex) {
                _log.error(ex);
                codeNumber = 0l;
            }
        }
        return codeNumber;
    }

    public String getCodeNotation(Document envelope) throws NumberFormatException,
            XMLStreamException {
        String codeNotation = "";
        String elementName = "codeNotation";
        String strData = getElementData(envelope, elementName);
        if (strData == null) {
            _log.error("Khong doc duoc du lieu trong file envelope");
            return codeNotation;
        } else {
            codeNotation = strData;
        }
        return codeNotation;
    }

    public String getDocCode(Document envelope) throws NumberFormatException,
            XMLStreamException {
        String docCode = "";
        String elementName = "Code";
        String strData = getElementData(envelope, elementName);
        if (strData == null) {
            _log.error("Khong doc duoc du lieu trong file envelope");
            return docCode;
        } else {
            docCode = strData;
        }
        return docCode;
    }

    /**
     * @param envelope
     * @param elementName
     * @return
     * @throws NumberFormatException
     * @throws XMLStreamException
     */
    public String getElementDataByTagName(Document envelope, String elementName)
            throws NumberFormatException, XMLStreamException {
        String strData = getElementData(envelope, elementName);

        if (strData == null) {
            _log.error("Khong doc duoc du lieu trong file envelope");
            return StringPool.BLANK;
        }

        return strData;
    }

    /**
     * @param envelope
     * @return
     * @throws NumberFormatException
     * @throws XMLStreamException
     */
    public String getOrganDomain(Document envelope)
            throws NumberFormatException, XMLStreamException {
        DOMSource source = new DOMSource(envelope);

        XMLStreamReader reader = XMLInputFactory.newInstance()
                .createXMLStreamReader(source);

        if (reader == null) {
            return StringPool.BLANK;
        }

        String organDomain = StringPool.BLANK;

        while (reader.hasNext()) {

            int type = reader.next();

            if (type == XMLStreamReader.START_ELEMENT) {

                if (reader.getLocalName().equalsIgnoreCase("organId")) {

                    // Chuyen den phan text cua element
                    reader.next();

                    String strId = reader.getText().trim();

                    if (!strId.equals("\n")
                            && !strId
                            .equals(StringPool.SPACE)) {

                        organDomain = strId;

                        break;
                    }
                }
            }
        }
        return organDomain;
    }

    /**
     * @param envelope
     * @return
     * @throws NumberFormatException
     * @throws XMLStreamException
     */
    public String getOrganToken(Document envelope)
            throws NumberFormatException, XMLStreamException {
        DOMSource source = new DOMSource(envelope);

        XMLStreamReader reader = XMLInputFactory.newInstance()
                .createXMLStreamReader(source);

        if (reader == null) {
            return StringPool.BLANK;
        }

        String token = StringPool.BLANK;

        while (reader.hasNext()) {

            int type = reader.next();

            if (type == XMLStreamReader.START_ELEMENT) {

                if (reader.getLocalName().equalsIgnoreCase("Token")) {

                    // Chuyen den phan text cua element
                    reader.next();

                    String strId = reader.getText().trim();

                    if (!strId.equals("\n")
                            && !strId
                            .equals(StringPool.SPACE)) {

                        token = strId;

                        break;
                    }
                }
            }
        }
        return token;
    }

    /**
     * @param reader
     * @return
     * @throws NumberFormatException
     * @throws XMLStreamException
     */
    public long getDocumntId(XMLStreamReader reader)
            throws NumberFormatException, XMLStreamException {
        if (reader == null)
            return 0L;
        long documentId = 0L;
        while (reader.hasNext()) {
            int type = reader.next();
            if (type == XMLStreamReader.START_ELEMENT) {
                if (reader.getLocalName().equalsIgnoreCase("documentId")) {
                    reader.next();
                    String strId = reader.getText().trim();
                    if (!strId.equals("\n") && !strId.equals(" ")) {
                        documentId = Long.parseLong(strId);
                        _log.info("Da lay thanh cong documentId");
                        break;
                    }
                }
            }
        }
        return documentId;
    }

    /**
     * @param envelope
     * @return
     */
    @SuppressWarnings("rawtypes")
    public String getClientToken(SOAPEnvelope envelope) {
        SOAPHeader header = envelope.getHeader();
        if (header == null)
            return StringPool.BLANK;
        Iterator iter = header.getChildElements();
        while (iter.hasNext()) {
            OMElement element = (OMElement) iter.next();
            if (StringPool.TOKEN_ELEMENT_NAME.equals(element.getLocalName())) {
                return element.getText();
            }
        }
        return StringPool.BLANK;
    }

    /**
     * @param soapEnv
     * @return
     * @throws XMLStreamException
     * @throws FactoryConfigurationError
     */
    public Map<String, Object> getParamForDocumentPendings(Document soapEnv)
            throws XMLStreamException, FactoryConfigurationError {

        final String numberLabel = "number";

        final String dateLabel = "date";

        Map<String, Object> params = new HashMap<String, Object>();

        DOMSource source = new DOMSource(soapEnv);

        XMLStreamReader reader = XMLInputFactory.newInstance()
                .createXMLStreamReader(source);
        if (reader == null)
            return params;

        Object number = null;

        Object date = null;

        boolean availableNumberParam = false;

        boolean availableDateParam = false;

        while (reader.hasNext()) {

            int type = reader.next();

            if (type == XMLStreamReader.START_ELEMENT) {

                if (reader.getLocalName().equalsIgnoreCase(numberLabel)) {

                    // Chuyen den phan text cua element
                    reader.next();

                    String strNumber = reader.getText().trim();

                    if (!strNumber.equals("\n") && !strNumber.equals(" ")) {

                        number = Integer.parseInt(strNumber);

                        params.put(numberLabel, number);

                        availableNumberParam = true;
                    }
                } else if (reader.getLocalName().equalsIgnoreCase(dateLabel)) {

                    // Chuyen den phan text cua element
                    reader.next();

                    String strDate = reader.getText().trim();

                    if (!strDate.equals("\n") && !strDate.equals(" ")) {

                        date = strDate;

                        availableDateParam = true;

                        params.put(dateLabel, date);
                    }
                }

                if (availableDateParam && availableNumberParam) {
                    break;
                }
            }
        }

        return params;
    }

    /**
     * @param soapEnv
     * @return
     * @throws XMLStreamException
     * @throws FactoryConfigurationError
     */
    public long getCustomContactId(Document soapEnv) throws XMLStreamException,
            FactoryConfigurationError {

        long contactId = 0L;

        String elementName = "contactId";

        String strData = getElementData(soapEnv, elementName);

        if (strData == null) {
            _log.error("Khong doc duoc du lieu trong file envelope");
            return contactId;
        } else {
            contactId = Long.parseLong(strData);
        }

        return contactId;
    }

    /**
     * @param soapEnv
     * @return
     * @throws XMLStreamException
     * @throws FactoryConfigurationError
     */
    public boolean getIsOwnerParam(Document soapEnv) throws XMLStreamException,
            FactoryConfigurationError {
        // TODO: Can sua lai ham nay, da co ham lay du lieu => goi den thay vi
        // lam ham moi ntn
        DOMSource source = new DOMSource(soapEnv);

        boolean isExist = false;

        XMLStreamReader reader = XMLInputFactory.newInstance()
                .createXMLStreamReader(source);

        if (reader == null) {
            return isExist;
        }

        while (reader.hasNext()) {

            int type = reader.next();

            if (type == XMLStreamReader.START_ELEMENT) {

                if (reader.getLocalName().equalsIgnoreCase("isowner")) {
                    // Chuyen den phan text cua element
                    reader.next();
                    String strIsOwner = reader.getText().trim();
                    if (!strIsOwner.equals("\n") && !strIsOwner.equals(" ")) {
                        isExist = Boolean.parseBoolean(strIsOwner);
                        break;
                    }
                }
            }
        }

        return isExist;
    }

    /**
     * @param filePath
     * @return
     */
    public Document parserFileToDocument(String filePath) {
        DocumentBuilder documentBuilder;
        Document document = null;
        try {
            documentBuilder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();

            document = documentBuilder.parse(filePath);

        } catch (Exception e) {
            _log.error(e.getMessage());
        }

        return document;
    }

    // Lay signature info

    /**
     * @param document
     * @return
     * @throws XMLStreamException
     * @throws FactoryConfigurationError
     */
    public boolean checkExistSignature(Document document)
            throws XMLStreamException, FactoryConfigurationError {

        DOMSource source = new DOMSource(document);

        boolean isExist = false;

        XMLStreamReader reader = XMLInputFactory.newInstance()
                .createXMLStreamReader(source);

        if (reader == null) {
            return isExist;
        }

        while (reader.hasNext()) {

            int type = reader.next();

            if (type == XMLStreamReader.START_ELEMENT) {

                if (reader.getLocalName().equalsIgnoreCase("Signature")) {
                    isExist = true;
                    break;
                }
            }
        }

        return isExist;
    }

    // ket thuc lay signature info

    // Kiem tra xem la lay theo kieu default contact, system contact, custom
    // contact

    /**
     * @param soapEnv
     * @return
     * @throws XMLStreamException
     */
    public boolean checkExistIsOwner(Document soapEnv)
            throws XMLStreamException {

        DOMSource source = new DOMSource(soapEnv);

        boolean isExist = false;

        XMLStreamReader reader = XMLInputFactory.newInstance()
                .createXMLStreamReader(source);

        if (reader == null) {
            return isExist;
        }

        while (reader.hasNext()) {

            int type = reader.next();

            if (type == XMLStreamReader.START_ELEMENT) {

                if (reader.getLocalName().equalsIgnoreCase("isowner")) {

                    isExist = true;
                    break;
                }
            }
        }

        return isExist;
    }

    /**
     * @param document
     * @param elementName
     * @return
     * @throws XMLStreamException
     */
    public String getElementData(Document document, String elementName)
            throws XMLStreamException {
        DOMSource source = new DOMSource(document);
        XMLStreamReader reader = XMLInputFactory.newInstance()
                .createXMLStreamReader(source);
        if (reader == null) {
            return null;
        }
        String data = "";
        while (reader.hasNext()) {
            int type = reader.next();
            if (type == XMLStreamReader.START_ELEMENT) {
                //System.out.println(reader.getLocalName());
                if (reader.getLocalName().equalsIgnoreCase(elementName)) {
                    // Chuyen den phan text cua element
                    reader.next();

                    String tmpData = reader.getText().trim();
                    if (!tmpData.equals("\n") && !tmpData.equals(" ")) {
                        data = tmpData;
                        _log.info("Da lay thanh cong " + elementName);
                        break;
                    }
                }
            }
        }
        return data;
    }

    public Namespace getEnvelopeNS(org.jdom2.Element rootElement) {
        Namespace envNs = null;
        List<Namespace> nss = rootElement.getNamespacesInScope();
        for (Namespace item : nss) {
            String uri = item.getURI();
            if (uri.equals(EdXmlConstant.SOAP_URI)) {
                envNs = item;
                break;
            }
        }
        return envNs;
    }

    /**
     * @param envelope
     * @param contentId
     * @return
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    public String getAttachmentName(Document envelope, String contentId)
            throws XMLStreamException, XPathExpressionException {
        String body = "//*[local-name()='Body'][1]//*[local-name()='Manifest'][1]";
        XPathExpression expr0 = xpathUtil.getXpathExpression(body);
        if (expr0 == null)
            return StringPool.BLANK;
        Node bodyNode = (Node) expr0.evaluate(envelope, XPathConstants.NODE);

        String reference = String
                .format("//*[local-name()='Reference'][@href='cid:%s']//*[local-name()='AttachmentName']",
                        contentId);

        XPathExpression expr = xpathUtil.getXpathExpression(reference);
        if (expr == null)
            return StringPool.BLANK;
        String attachmentName = expr.evaluate(bodyNode);

        return attachmentName;
    }

    /**
     * @param envelope
     * @return
     * @throws XMLStreamException
     * @throws XPathExpressionException
     */
    public Map<String, String> getAttachmentIds(Document envelope)
            throws XMLStreamException, XPathExpressionException {
        Map<String, String> result = new HashMap<String, String>();

        org.jdom2.Document doc = XmlUtil.convertFromDom(envelope);

        org.jdom2.Element rootElement = doc.getRootElement();

        Namespace envNs = getEnvelopeNS(rootElement);

        org.jdom2.Element bodyNode = extractMine.getSingerElement(rootElement,
                EdXmlConstant.BODY_TAG, envNs);

        org.jdom2.Element maniNode = extractMine.getSingerElement(bodyNode,
                EdXmlConstant.MANIFEST_TAG, EdXmlConstant.EDXML_NS);

        List<org.jdom2.Element> referenceNodes = extractMine.getMultiElement(
                maniNode, EdXmlConstant.REFERENCE_TAG, EdXmlConstant.EDXML_NS);

        for (org.jdom2.Element item : referenceNodes) {
            // Get contentId from href link
            Attribute att = item.getAttribute(EdXmlConstant.HREF_ATTR,
                    EdXmlConstant.XLINK_NS);
            String href = att.getValue();

            // Get att name
            String attName = item.getChildText(
                    EdXmlConstant.ATTACHMENT_NAME_TAG, EdXmlConstant.EDXML_NS);

            result.put(attName, href.substring(href.indexOf(":") + 1));
        }

        /*
         * String body =
         * "//*[local-name()='Body'][1]//*[local-name()='Manifest'][1]";
         *
         * XPathExpression expr0 = xpathUtil.getXpathExpression(body); if (expr0
         * == null) { return null; }
         *
         * Node bodyNode = (Node) expr0.evaluate(envelope, XPathConstants.NODE);
         *
         * String hrefPath =
         * "//*[local-name()='Reference']//@*[local-name()='href']";
         *
         * XPathExpression expr = xpathUtil.getXpathExpression(hrefPath); if
         * (expr == null) { return null; } NodeList hrefNodeList = (NodeList)
         * expr.evaluate(bodyNode, XPathConstants.NODESET);
         *
         * for (int i = 0; i < hrefNodeList.getLength(); i++) { Node
         * currentElement = hrefNodeList.item(0); String contentId =
         * currentElement.getTextContent(); if (contentId.length() == 0) { try {
         * contentId = currentElement.getNodeValue(); } catch (Exception e) {
         * _log.error(e); }
         *
         * }
         *
         * if (contentId.length() > 3) { contentId =
         * contentId.substring(contentId.indexOf(":") + 1); }
         *
         * String attachmentName = getAttachmentName(envelope, contentId);
         *
         * result.put(attachmentName, contentId); }
         */

        return result;
    }

    /**
     * @param xsParticle
     * @param complexName
     * @return
     */
    private StringBuffer getElements(XSParticle xsParticle, String complexName) {
        StringBuffer buffer = new StringBuffer();

        if (xsParticle != null) {

            XSTerm pterm = xsParticle.getTerm();

            if (pterm.isElementDecl()) {

                if (buffer.toString().indexOf(pterm.asElementDecl().getName()) == -1) {

                    buffer.append(complexName + "-"
                            + pterm.asElementDecl().getName());
                    buffer.append(";");

                }
            } else if (pterm.isModelGroup()) {

                XSModelGroup xsModelGroup2 = pterm.asModelGroup();

                XSParticle[] xsParticleArray = xsModelGroup2.getChildren();

                for (XSParticle xsParticleTemp : xsParticleArray) {

                    buffer.append(getElements(xsParticleTemp, complexName));

                }
            }
        }
        return buffer;
    }

    /**
     * @param soapEnv
     * @return
     * @throws XMLStreamException
     * @throws FactoryConfigurationError
     */
    private Object getLevelForSystemContact(Document soapEnv)
            throws XMLStreamException, FactoryConfigurationError {
        DOMSource source = new DOMSource(soapEnv);

        Object level = null;

        XMLStreamReader reader = XMLInputFactory.newInstance()
                .createXMLStreamReader(source);

        if (reader == null) {
            return level;
        }

        while (reader.hasNext()) {

            int type = reader.next();

            if (type == XMLStreamReader.START_ELEMENT) {

                if (reader.getLocalName().equalsIgnoreCase("level")) {
                    // Chuyen den phan text cua element
                    reader.next();
                    String strLevel = reader.getText().trim();
                    if (!strLevel.equals("\n") && !strLevel.equals(" ")) {
                        level = Integer.parseInt(strLevel);
                        break;
                    }
                }
            }
        }

        return level;
    }

    /**
     * @param messageHeader
     * @param ns
     * @return
     * @throws Exception
     */
    public Document getMessHeaderDoc(MessageHeader messageHeader, OMNamespace ns)
            throws Exception {
        OMFactory factoryOM = OMAbstractFactory.getOMFactory();
        OMElement messageHeaderNode = factoryOM.createOMElement(
                StringPool.EDXML_MESSAGE_HEADER_BLOCK, ns);
        List<OMNode> nodes = getHeaderChild(messageHeader, ns);

        for (OMNode omNode : nodes) {
            messageHeaderNode.addChild(omNode);
        }

        nodes.clear();

        return XMLUtils.toDOM(messageHeaderNode).getOwnerDocument();
    }

    /**
     * @param currentHeader
     * @param ns
     * @return
     * @throws SAXException
     * @throws IOException
     */
    private List<OMNode> getHeaderChild(MessageHeader currentHeader,
                                        OMNamespace ns) throws SAXException, IOException {

        List<OMNode> nodes = new ArrayList<OMNode>();

        // SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        OMFactory factoryOM = OMAbstractFactory.getOMFactory();

        boolean allowAll = false;

        // String allowElements = new XmlUtil().getAllDefineElementName();
        String allowElements = "";

        if (allowElements.length() == 0) {
            allowAll = true;
        }

        Checker checker = new Checker();
        // Tao from Element
        if (checker.checkAllowElement(allowElements,
                StringPool.MESSAGE_HEADER_FROM) || allowAll) {

            OMElement from = factoryOM.createOMElement("From", ns);

            if (checker.checkAllowElement(allowElements,
                    StringPool.FROM_ORGAN_ID) || allowAll) {

                OMElement fromOrganId = factoryOM
                        .createOMElement("OrganId", ns);

                fromOrganId.setText(currentHeader.getFrom().getOrganId());

                from.addChild(fromOrganId);
            }

            if (checker.checkAllowElement(allowElements,
                    StringPool.FROM_ORGAN_NAME) || allowAll) {

                OMElement fromOrganName = factoryOM.createOMElement(
                        "OrganName", ns);

                fromOrganName.setText(currentHeader.getFrom().getOrganName());

                from.addChild(fromOrganName);
            }

            if (checker.checkAllowElement(allowElements,
                    StringPool.FROM_ORGAN_IN_CHARGE) || allowAll) {

                OMElement fromOrganName = factoryOM.createOMElement(
                        "OrganInCharge", ns);

                fromOrganName.setText(currentHeader.getFrom()
                        .getOrganInCharge());

                from.addChild(fromOrganName);
            }

            if (checker.checkAllowElement(allowElements,
                    StringPool.FROM_ORGAN_ADD) || allowAll) {

                OMElement fromOrganAdd = factoryOM.createOMElement("OrganAdd",
                        ns);

                fromOrganAdd.setText(currentHeader.getFrom().getOrganAdd());

                from.addChild(fromOrganAdd);
            }

            if (checker.checkAllowElement(allowElements, StringPool.FROM_EMAIL)
                    || allowAll) {

                OMElement fromEmail = factoryOM.createOMElement("Email", ns);

                fromEmail.setText(currentHeader.getFrom().getEmail());

                from.addChild(fromEmail);
            }

            if (checker.checkAllowElement(allowElements,
                    StringPool.FROM_TELEPHONE) || allowAll) {

                OMElement fromTelephone = factoryOM.createOMElement(
                        "Telephone", ns);

                fromTelephone.setText(currentHeader.getFrom().getTelephone());

                from.addChild(fromTelephone);
            }

            if (checker.checkAllowElement(allowElements, StringPool.FROM_FAX)
                    || allowAll) {

                OMElement fromFax = factoryOM.createOMElement("Fax", ns);

                fromFax.setText(currentHeader.getFrom().getFax());

                from.addChild(fromFax);
            }

            if (checker.checkAllowElement(allowElements,
                    StringPool.FROM_WEBSITE) || allowAll) {

                OMElement fromWebsite = factoryOM
                        .createOMElement("Website", ns);

                fromWebsite.setText(currentHeader.getFrom().getWebsite());

                from.addChild(fromWebsite);
            }
            nodes.add(from);
        }

        if (checker.checkAllowElement(allowElements,
                StringPool.MESSAGE_HEADER_TO) || allowAll) {

            // Tao To element
            List<To> tos = currentHeader.getTo();
            String strDueDate = currentHeader.getDueDate();
            boolean isCreateDueDate = false;
            if(strDueDate != null && !strDueDate.isEmpty()){
                isCreateDueDate = true;
            }
            for (To item : tos) {

                OMElement to = factoryOM.createOMElement("To", ns);

                if (checker.checkAllowElement(allowElements,
                        StringPool.TO_ORGAN_ID) || allowAll) {
                    OMElement toOrganId = factoryOM.createOMElement("OrganId",
                            ns);
                    toOrganId.setText(item.getOrganId());
                    to.addChild(toOrganId);
                }

                if (checker.checkAllowElement(allowElements,
                        StringPool.TO_ORGAN_NAME) || allowAll) {
                    OMElement toOrganName = factoryOM.createOMElement(
                            "OrganName", ns);
                    toOrganName.setText(item.getOrganName());
                    to.addChild(toOrganName);
                }

                if (checker.checkAllowElement(allowElements,
                        StringPool.TO_ORGAN_ADD) || allowAll) {
                    OMElement toOrganAdd = factoryOM.createOMElement(
                            "OrganAdd", ns);
                    toOrganAdd.setText(item.getOrganAdd());
                    to.addChild(toOrganAdd);
                }

                if (checker.checkAllowElement(allowElements,
                        StringPool.TO_EMAIL) || allowAll) {
                    OMElement toEmail = factoryOM.createOMElement("Email", ns);
                    toEmail.setText(item.getEmail());
                    to.addChild(toEmail);
                }

                if (checker.checkAllowElement(allowElements,
                        StringPool.TO_TELEPHONE) || allowAll) {
                    OMElement toTelephone = factoryOM.createOMElement(
                            "Telephone", ns);
                    toTelephone.setText(item.getTelephone());
                    to.addChild(toTelephone);
                }

                if (checker.checkAllowElement(allowElements, StringPool.TO_FAX)
                        || allowAll) {
                    OMElement toFax = factoryOM.createOMElement("Fax", ns);
                    toFax.setText(item.getFax());
                    to.addChild(toFax);
                }

                if (checker.checkAllowElement(allowElements,
                        StringPool.TO_WEBSITE) || allowAll) {
                    OMElement toWebsite = factoryOM.createOMElement("Website",
                            ns);
                    toWebsite.setText(item.getWebsite());
                    to.addChild(toWebsite);
                }

                if (checker.checkAllowElement(allowElements,
                        StringPool.TO_DUE_DATE) || allowAll) {
                    if(strDueDate != null && strDueDate.length() > 0){
                            OMElement toDueDate = factoryOM.createOMElement("DueDate",
                                    ns);
                            toDueDate.setText(strDueDate);
                            to.addChild(toDueDate);
                    }
                }

                nodes.add(to);
            }
        }

        if (checker.checkAllowElement(allowElements,
                StringPool.MESSAGE_HEADER_DOCUMENT_ID) || allowAll) {
            // Tao <edXML:DocumentId>
            OMElement documentId = factoryOM.createOMElement("DocumentId", ns);
            documentId.setText(currentHeader.getDocumentId());
            nodes.add(documentId);
        }

        if (checker.checkAllowElement(allowElements,
                StringPool.MESSAGE_HEADER_CODE) || allowAll) {
            // Tao Code
            OMElement code = factoryOM.createOMElement("Code", ns);

            if (checker
                    .checkAllowElement(allowElements, StringPool.CODE_NUMBER)
                    || allowAll) {
                OMElement codeNumber = factoryOM.createOMElement("CodeNumber",
                        ns);
                codeNumber.setText(currentHeader.getCode().getCodeNumber());
                code.addChild(codeNumber);
            }
            if (checker.checkAllowElement(allowElements,
                    StringPool.CODE_NOTATION) || allowAll) {
                OMElement codeNotation = factoryOM.createOMElement(
                        "CodeNotation", ns);
                codeNotation.setText(currentHeader.getCode().getCodeNotation());
                code.addChild(codeNotation);
            }

            nodes.add(code);
        }

        if (checker.checkAllowElement(allowElements,
                StringPool.MESSAGE_HEADER_PROMULGATION_INFO) || allowAll) {
            // Tao PromulgationInfo

            OMElement promulgationInfo = factoryOM.createOMElement(
                    "PromulgationInfo", ns);

            if (checker.checkAllowElement(allowElements,
                    StringPool.PROMULGATION_INFO_DATE) || allowAll) {
                OMElement promulgationDate = factoryOM.createOMElement(
                        "PromulgationDate", ns);

                String dateStr = currentHeader.getPromulgationInfo()
                        .getPromulgationDate();

                promulgationDate
                        .setText(dateStr.isEmpty() ? StringPool.DEFAULT_DATE
                                : dateStr);
                promulgationInfo.addChild(promulgationDate);
            }

            if (checker.checkAllowElement(allowElements,
                    StringPool.PROMULGATION_INFO_PLACE) || allowAll) {
                OMElement promulgationPlace = factoryOM.createOMElement(
                        "Place", ns);
                promulgationPlace.setText(currentHeader.getPromulgationInfo()
                        .getPlace());
                promulgationInfo.addChild(promulgationPlace);
            }

            nodes.add(promulgationInfo);
        }

        if (checker.checkAllowElement(allowElements,
                StringPool.MESSAGE_HEADER_DOCUMENT_TYPE) || allowAll) {
            // Tao DocumentType
            OMElement documentType = factoryOM.createOMElement("DocumentType",
                    ns);

            if (checker.checkAllowElement(allowElements,
                    StringPool.DOCUMENT_TYPE_TYPE) || allowAll) {
                OMElement type = factoryOM.createOMElement("Type", ns);
                String typeString = String.valueOf(currentHeader
                        .getDocumentType().getType());
                type.setText(typeString.isEmpty() ? StringPool.DEFAUlt_INTEGER
                        : typeString);
                documentType.addChild(type);
            }
            if (checker.checkAllowElement(allowElements,
                    StringPool.DOCUMENT_TYPE_TYPE_NAME) || allowAll) {
                OMElement typeName = factoryOM.createOMElement("TypeName", ns);
                typeName.setText(currentHeader.getDocumentType().getTypeName());
                documentType.addChild(typeName);
            }

            nodes.add(documentType);
        }

        if (checker.checkAllowElement(allowElements,
                StringPool.MESSAGE_HEADER_SUBJECT) || allowAll) {
            // Tao Subject
            OMElement subject = factoryOM.createOMElement("Subject", ns);
            subject.setText(currentHeader.getSubject());
            nodes.add(subject);
        }

        if (checker.checkAllowElement(allowElements,
                StringPool.MESSAGE_HEADER_CONTENT) || allowAll) {
            // Tao Content
            OMElement content = factoryOM.createOMElement("Content", ns);
            content.setText(currentHeader.getContent());
            nodes.add(content);
        }

        // TODO: Chua check on/off cho the Related
//        OMElement related = factoryOM.createOMElement("Related", ns);
//        Code item = currentHeader.getCode();
//        OMElement relatedCode = factoryOM.createOMElement("Code", ns);
//
//        OMElement relatedCodeNumber = factoryOM.createOMElement(
//                "CodeNumber", ns);
//        relatedCodeNumber.setText(item.getCodeNumber());
//        relatedCode.addChild(relatedCodeNumber);
//
//        OMElement relatedCodeNotation = factoryOM.createOMElement(
//                "CodeNotation", ns);
//        relatedCodeNotation.setText(item.getCodeNotation());
//        relatedCode.addChild(relatedCodeNotation);
//        related.addChild(relatedCode);
//        nodes.add(related);

//        if (checker.checkAllowElement(allowElements,
//                StringPool.MESSAGE_HEADER_AUTHOR) || allowAll) {
//            // Tao Author
//            OMElement author = factoryOM.createOMElement("SignerInfo", ns);
//
//            if (checker.checkAllowElement(allowElements,
//                    StringPool.AUTHOR_COMPETENCE) || allowAll) {
//                OMElement competence = factoryOM.createOMElement("Competence",
//                        ns);
//                competence.setText(currentHeader.getAuthor().getCompetence());
//                author.addChild(competence);
//            }
//            if (checker.checkAllowElement(allowElements,
//                    StringPool.AUTHOR_FUNCTION) || allowAll) {
//                OMElement function = factoryOM.createOMElement("Position", ns);
//                function.setText(currentHeader.getAuthor().getFunction());
//                author.addChild(function);
//            }
//            if (checker.checkAllowElement(allowElements,
//                    StringPool.AUTHOR_FULLNAME) || allowAll) {
//                OMElement fullName = factoryOM.createOMElement("FullName", ns);
//                fullName.setText(currentHeader.getAuthor().getFullName());
//                author.addChild(fullName);
//            }
//
//            nodes.add(author);
//        }

        if (checker.checkAllowElement(allowElements,
                StringPool.MESSAGE_HEADER_RESPONSE_DATE) || allowAll) {
            // Tao ResponseDate
            OMElement responseDate = factoryOM.createOMElement("DueDate", ns);
//            String dateStr = currentHeader.getResponseDate();
            String dateStr = currentHeader.getDueDate();
            if(dateStr == null || dateStr.isEmpty()) {
                responseDate.setText("");
            } else {
                responseDate.setText(dateStr);
            }
            nodes.add(responseDate);
        }

        if (checker.checkAllowElement(allowElements,
                StringPool.MESSAGE_HEADER_TOPLACES) || allowAll) {
            // Tao ToPlaces
            OMElement toplaces = factoryOM.createOMElement("ToPlaces", ns);
            for (String placeStr : currentHeader.getToPlaces().getPlace()) {
                OMElement place = factoryOM.createOMElement("Place", ns);
                place.setText(placeStr);
                toplaces.addChild(place);
            }
            nodes.add(toplaces);
        }

        if (checker.checkAllowElement(allowElements,
                StringPool.MESSAGE_HEADER_OTHERINFO) || allowAll) {
            // Tao OtherInfo
            OMElement otherInfo = factoryOM.createOMElement("OtherInfo", ns);

            if (checker.checkAllowElement(allowElements,
                    StringPool.OTHERINFO_PRIORITY) || allowAll) {
                OMElement priority = factoryOM.createOMElement("Priority", ns);
                String priorityStr = String.valueOf(currentHeader
                        .getOtherInfo().getPriority());
                priority.setText(priorityStr.isEmpty() ? StringPool.DEFAUlt_INTEGER
                        : priorityStr);
                otherInfo.addChild(priority);
            }
            if (checker.checkAllowElement(allowElements,
                    StringPool.OTHERINFO_SPHERE_OF_PROMULGATION) || allowAll) {
                OMElement sphere = factoryOM.createOMElement(
                        "SphereOfPromulgation", ns);
                sphere.setText(currentHeader.getOtherInfo()
                        .getSphereOfPromulgation());
                otherInfo.addChild(sphere);
            }
            if (checker.checkAllowElement(allowElements,
                    StringPool.OTHERINFO_TYPER_NOTATION) || allowAll) {
                OMElement typerNotation = factoryOM.createOMElement(
                        "TyperNotation", ns);
                typerNotation.setText(currentHeader.getOtherInfo()
                        .getTyperNotation());
                otherInfo.addChild(typerNotation);
            }
            if (checker.checkAllowElement(allowElements,
                    StringPool.OTHERINFO_PROMULGATION_AMOUNT) || allowAll) {
                OMElement promulAmount = factoryOM.createOMElement(
                        "PromulgationAmount", ns);
                String promulAmountStr = String.valueOf(currentHeader
                        .getOtherInfo().getPromulgationAmount());
                promulAmount
                        .setText(promulAmountStr.isEmpty() ? StringPool.DEFAUlt_INTEGER
                                : promulAmountStr);
                otherInfo.addChild(promulAmount);
            }
            if (checker.checkAllowElement(allowElements,
                    StringPool.OTHERINFO_PAGE_AMOUNT) || allowAll) {
                OMElement pageAmount = factoryOM.createOMElement("PageAmount",
                        ns);
                String pageAmountStr = String.valueOf(currentHeader
                        .getOtherInfo().getPageAmount());
                pageAmount
                        .setText(pageAmountStr.isEmpty() ? StringPool.DEFAUlt_INTEGER
                                : pageAmountStr);
                otherInfo.addChild(pageAmount);
            }

//            if (checker.checkAllowElement(allowElements,
//                    StringPool.MESSAGE_HEADER_APPENDIXES) || allowAll) {
//                // Tao Appendixes
//                OMElement appendixes = factoryOM.createOMElement("Appendixes",
//                        ns);
//                for (String item : currentHeader.getAppendixes().getAppendix()) {
//                    OMElement appendix = factoryOM.createOMElement("Appendix",
//                            ns);
//                    appendix.setText(item);
//                    appendixes.addChild(appendix);
//                }
//                otherInfo.addChild(appendixes);
//            }

            nodes.add(otherInfo);
        }

        // get response for revoke document
        if (checker.checkAllowElement(allowElements,
                StringPool.MESSAGE_HEADER_RESPONSE_FOR) || allowAll) {

            // generate response for nodes
            List<ResponseFor> responseFors = currentHeader.getResponseFors();
            for (ResponseFor item : responseFors) {
                OMElement responseFor = factoryOM.createOMElement("ResponseFor", ns);

                if (checker.checkAllowElement(allowElements,
                        StringPool.RESPONSE_FOR_ORGAN_ID) || allowAll) {
                    OMElement organId = factoryOM.createOMElement("OrganId",
                            ns);
                    organId.setText(item.getOrganId());
                    responseFor.addChild(organId);
                }

                if (checker.checkAllowElement(allowElements,
                        StringPool.RESPONSE_FOR_CODE) || allowAll) {
                    OMElement code = factoryOM.createOMElement(
                            "Code", ns);
                    code.setText(item.getCode());
                    responseFor.addChild(code);
                }

                if (checker.checkAllowElement(allowElements,
                        StringPool.RESPONSE_FOR_PROMULGATION_DATE) || allowAll) {
                    OMElement promulgationDate = factoryOM.createOMElement(
                            "PromulgationDate", ns);
                    promulgationDate.setText(item.getPromulgationDate());
                    responseFor.addChild(promulgationDate);
                }

                if (checker.checkAllowElement(allowElements,
                        StringPool.RESPONSE_FOR_DOCUMENT_ID) || allowAll) {
                    OMElement documentId = factoryOM.createOMElement("DocumentId", ns);
                    documentId.setText(item.getDocumentId());
                    responseFor.addChild(documentId);
                }

                nodes.add(responseFor);
            }
        }

        return nodes;
    }

    private static Log _log = LogFactory.getLog(XmlUtil.class);

}
