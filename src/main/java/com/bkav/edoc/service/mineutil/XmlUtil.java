package com.bkav.edoc.service.mineutil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

        return nodes;
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


    private static Log _log = LogFactory.getLog(XmlUtil.class);

}
