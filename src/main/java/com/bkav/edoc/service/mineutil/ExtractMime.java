/**
 *
 */
package com.bkav.edoc.service.mineutil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;

import com.bkav.edoc.service.commonutil.XmlGregorianCalendarUtil;
import com.bkav.edoc.service.entity.edxml.*;
import com.bkav.edoc.service.kernel.util.GetterUtil;
import com.bkav.edoc.service.resource.EdXmlConstant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.w3c.dom.Document;

public class ExtractMime {

    public Attachment getAttachment(String name, DataHandler input) {
        Attachment attachment = new Attachment();
        attachment.setName(name);
        String contentType = input.getContentType();
        attachment.setContentType(contentType);

        String encoding = "base64";
        attachment.setContentTransfer(encoding);
        InputStream value = null;

        try {
            value = input.getInputStream();
        } catch (IOException e) {
            _log.error(e.getMessage());
            value = new ByteArrayInputStream("".getBytes());
        }
        attachment.setValue(value);

        return attachment;
    }

    public Map<String, String> getAttachmentName(Document envelope) {

        Map<String, String> attNames = new HashMap<String, String>();
        org.jdom2.Document domEnvDoc = XmlUtil.convertFromDom(envelope);

        Element envElement = domEnvDoc.getRootElement();

        Namespace envNs = envElement.getNamespace();

        Element body = getSingerElement(envElement, EdXmlConstant.BODY_TAG,
                envNs);
        if (body != null) {
            Element manifestNode = getSingerElement(body,
                    EdXmlConstant.MANIFEST_TAG, EdXmlConstant.EDXML_NS);
            List<Element> referenceNodes = getMultiElement(manifestNode,
                    EdXmlConstant.REFERENCE_TAG, EdXmlConstant.EDXML_NS);
            if (referenceNodes != null && referenceNodes.size() > 0) {
                for (Element item : referenceNodes) {
                    String name = item.getChildText(
                            EdXmlConstant.ATTACHMENT_NAME_TAG,
                            EdXmlConstant.EDXML_NS);
                    List<Attribute> attributes = item.getAttributes();
                    String hrefValue = null;
                    for (Attribute attribute : attributes) {
                        if (attribute.getName().equals(EdXmlConstant.HREF_ATTR)) {
                            hrefValue = attribute.getValue();
                        }
                    }
                    if (hrefValue != null && hrefValue.contains("cid:")) {
                        hrefValue = hrefValue.replace("cid:", "");
                    }
                    attNames.put(hrefValue, name);
                }
            }
        }

        return attNames;
    }

    /**
     * Get organ id from pending document
     * @param envelope
     * @return
     */
    public String getOrganId(Document envelope, String documentName) {

        org.jdom2.Document domEnvDoc = XmlUtil.convertFromDom(envelope);

        Element envElement = domEnvDoc.getRootElement();

        Namespace envNs = envElement.getNamespace();

        Element body = getSingerElement(envElement, EdXmlConstant.BODY_TAG,
                envNs);
        if (body != null) {
            Element pendingDocumentNode = getSingerElement(body,
                    documentName, null);
            if (pendingDocumentNode != null) {
                return pendingDocumentNode.getChildText("OrganId", null);
            }
        }

        return null;
    }

    public TraceHeaderList getTraceHeaderList(Document envelopeDoc,
                                              boolean isByHeader) throws Exception {

        org.jdom2.Document domEnvDoc = XmlUtil.convertFromDom(envelopeDoc);

        Element rootElement = domEnvDoc.getRootElement();

        Namespace envNs = xmlUtil.getEnvelopeNS(rootElement);

        String parentName = isByHeader ? "Header" : "Body";

        Element parentNode = getSingerElement(rootElement, parentName, envNs);

        if (!isByHeader) {
            parentNode = parentNode.getChild("UpdateTraces");
            // parentNode = getSingerElement(parentNode, "UpdateTraces",
            // currentNs);
        }

        Element traceHeaderListNode = isByHeader ? getSingerElement(parentNode,
                "TraceHeaderList", EdXmlConstant.EDXML_NS) : parentNode
                .getChild("TraceHeaderList");

        TraceHeaderList traceList = new TraceHeaderList();

        traceList
                .setTraceHeaders(GetTraceList(traceHeaderListNode, isByHeader));

        return traceList;
    }

    /**
     * @param envelopDoc
     * @return
     * @throws Exception
     */
    public Status getStatus(Document envelopDoc) throws Exception {
        org.jdom2.Document jdomEnvDoc = XmlUtil.convertFromDom(envelopDoc);

        Element rootElement = jdomEnvDoc.getRootElement();

        Namespace envNs = xmlUtil.getEnvelopeNS(rootElement);

        Element headerNode = getSingerElement(rootElement, "Body", envNs);

        Element statusNode = headerNode.getChild("Status");
        Status status = new Status();

        ResponseFor responseFor = getResponseForStatus(statusNode);
        status.setResponseFor(responseFor);

        From from = getFromInTrace(statusNode);
        status.setFrom(from);

        StaffInfo staffInfo = getStaffInfo(statusNode);

        status.setStaffInfo(staffInfo);

        String description = statusNode.getChildText("Description");
        status.setDescription(description);

//        String documentId = statusNode.getChildText("DocumentId");
//        status.setDocumentId(documentId);

        String statusCode = statusNode.getChildText("StatusCode");

        status.setStatusCode(statusCode);

        String timeStamp = statusNode.getChildText("Timestamp");

        status.setTimeStamp(timeStamp);

        return status;
    }

    private From getFromInTrace(Element traceNode) throws Exception {
        From from = new From();

        Element fromNode = traceNode.getChild("From");

        from.setOrganId(fromNode.getChildText("OrganId"));

        from.setOrganInCharge(fromNode.getChildText("OrganizationInCharge"));

        from.setOrganName(fromNode.getChildText("OrganName"));

        from.setOrganAdd(fromNode.getChildText("OrganAdd",
                EdXmlConstant.EDXML_NS));

        from.setEmail(fromNode.getChildText("Email"));

        from.setTelephone(fromNode.getChildText("Telephone",
                EdXmlConstant.EDXML_NS));

        from.setFax(fromNode.getChildText("Fax"));

        from.setWebsite(fromNode.getChildText("Website"));
        return from;
    }

    public ResponseFor getResponseForMessageHeader(Element responseForNode) {
        ResponseFor responseFor = new ResponseFor();

        responseFor.setCode(responseForNode.getChildText("Code"));

        responseFor.setOrganId(responseForNode.getChildText("OrganId"));

        responseFor.setPromulgationDate(responseForNode
                .getChildText("PromulgationDate"));

        responseFor.setDocumentId(responseForNode.getChildText("DocumentId"));

        return responseFor;
    }

    public ResponseFor getResponseForStatus(Element statusNode) {
        ResponseFor responseFor = new ResponseFor();
        Element responseForNode = statusNode.getChild("ResponseFor");

        responseFor.setCode(responseForNode.getChildText("Code"));

        responseFor.setOrganId(responseForNode.getChildText("OrganId"));

        responseFor.setPromulgationDate(responseForNode
                .getChildText("PromulgationDate"));

        responseFor.setDocumentId(responseForNode.getChildText("DocumentId"));

        return responseFor;
    }

    /**
     *
     * @param statusNode
     * @return
     * @throws Exception
     */
    public StaffInfo getStaffInfo(Element statusNode) throws Exception {
        StaffInfo staffInfo = new StaffInfo();

        Element staffInfoNode = statusNode.getChild("StaffInfo");
        staffInfo.setDepartment(staffInfoNode.getChildText("Department"));

        staffInfo.setStaff(staffInfoNode.getChildText("Staff"));

        staffInfo.setEmail(staffInfoNode.getChildText("Email"));

        staffInfo.setMobile(staffInfoNode.getChildText("Mobile"));
        return staffInfo;
    }

    /**
     * @param envelopeDoc
     * @return
     * @throws Exception
     */
    public MessageHeader getMessageHeader(Document envelopeDoc)
            throws Exception {

        org.jdom2.Document domENV = XmlUtil.convertFromDom(envelopeDoc);

        Element rootElement = domENV.getRootElement();

        MessageHeader messageHeader = new MessageHeader();

        Namespace envNs = xmlUtil.getEnvelopeNS(rootElement);

        Element headerNode = getSingerElement(rootElement, "Header", envNs);

        Element messageHeaderNode = getSingerElement(headerNode,
                "MessageHeader", EdXmlConstant.EDXML_NS);

        From from = GetFromInfo(messageHeaderNode);
        messageHeader.setFrom(from);

        String dueDate = GetterUtil.getString(messageHeaderNode.getChildText("DueDate",
                EdXmlConstant.EDXML_NS), "");

        messageHeader.setDueDate(dueDate);

        List<To> tos = GetTos(messageHeaderNode);
        messageHeader.setTo(tos);
        messageHeader.setDocumentId(GetDocumentId(messageHeaderNode));

        Code code = GetCode(messageHeaderNode);
        messageHeader.setCode(code);

        PromulgationInfo pro = GetPromulgationInfo(messageHeaderNode);
        messageHeader.setPromulgationInfo(pro);

        DocumentType docType = GetDocumentType(messageHeaderNode);
        messageHeader.setDocumentType(docType);

        messageHeader.setSubject(GetSubject(messageHeaderNode));

        messageHeader.setContent(GetContent(messageHeaderNode));

        SignerInfo signerInfo = getSignerInfo(messageHeaderNode);

        messageHeader.setSignerInfo(signerInfo);

        ToPlaces toPlaces = GetToPlaces(messageHeaderNode);
        messageHeader.setToPlaces(toPlaces);

        OtherInfo otherInfo = GetOtherInfo(messageHeaderNode);
        messageHeader.setOtherInfo(otherInfo);

        int steeringType = GetterUtil.getInteger(messageHeaderNode.getChildText("SteeringType", EdXmlConstant.EDXML_NS), 0);

        messageHeader.setSteeringType(steeringType);

        String edxmlDocumentId = GetterUtil.getString(messageHeaderNode.getChildText("DocumentId", EdXmlConstant.EDXML_NS), "");

        messageHeader.setDocumentId(edxmlDocumentId);

        List<ResponseFor> responseFors = GetResponseForsMessageHeader(messageHeaderNode);
        messageHeader.setResponseFor(responseFors);

        return messageHeader;
    }

    /**
     * @param messageHeaderNode
     * @return
     * @throws Exception
     */
    public List<To> GetTos(Element messageHeaderNode) throws Exception {

        List<To> tos = new ArrayList<To>();

        List<Element> toNodes = getMultiElement(messageHeaderNode, "To",
                EdXmlConstant.EDXML_NS);

        for (Element toNode : toNodes) {
            tos.add(GetToInfo(toNode));
        }

        return tos;

    }

    /**
     * @param messageHeaderNode
     * @return
     * @throws Exception
     */
    public List<ResponseFor> GetResponseForsMessageHeader(Element messageHeaderNode) throws Exception {

        List<ResponseFor> responseFors = new ArrayList<ResponseFor>();

        List<Element> responseForNodes = getMultiElement(messageHeaderNode, "ResponseFor",
                EdXmlConstant.EDXML_NS);

        // check null
        if(responseForNodes == null) return null;

        for (Element responseForNode : responseForNodes) {
            responseFors.add(getResponseForMessageHeader(responseForNode));
        }

        return responseFors;

    }

    /**
     * @param messageHeaderNode
     * @return
     * @throws XPathExpressionException
     */
    public String GetDocumentId(Element messageHeaderNode) {
        String documentId = messageHeaderNode.getChildText("DocumentId",
                EdXmlConstant.EDXML_NS);
        return documentId;
    }

    /**
     * @param messageHeaderNode
     * @return
     * @throws XPathExpressionException
     */
    public Code GetCode(Element messageHeaderNode) {

        Code code = new Code();

        Element codeNode = getSingerElement(messageHeaderNode, "Code",
                EdXmlConstant.EDXML_NS);

        code.setCodeNumber(codeNode.getChildText("CodeNumber",
                EdXmlConstant.EDXML_NS));

        code.setCodeNotation(codeNode.getChildText("CodeNotation",
                EdXmlConstant.EDXML_NS));

        return code;
    }

    /**
     * @param messageHeaderNode
     * @return
     * @throws XPathExpressionException
     * @throws ParseException
     */
    public PromulgationInfo GetPromulgationInfo(Element messageHeaderNode)
            throws ParseException {
        PromulgationInfo proInfo = new PromulgationInfo();

        Element proInfoNode = getSingerElement(messageHeaderNode,
                "PromulgationInfo", EdXmlConstant.EDXML_NS);

        proInfo.setPlace(proInfoNode.getChildText("Place",
                EdXmlConstant.EDXML_NS));

        proInfo.setPromulgationDate(proInfoNode.getChildText(
                "PromulgationDate", EdXmlConstant.EDXML_NS));

        return proInfo;
    }

    /**
     * @param messageHeaderNode
     * @return
     * @throws XPathExpressionException
     */
    public DocumentType GetDocumentType(Element messageHeaderNode) {

        DocumentType docType = new DocumentType();

        Element documentTypeNode = getSingerElement(messageHeaderNode,
                "DocumentType", EdXmlConstant.EDXML_NS);

        docType.setType(Integer.parseInt(documentTypeNode.getChildText("Type",
                EdXmlConstant.EDXML_NS)));

        docType.setTypeName(documentTypeNode.getChildText("TypeName",
                EdXmlConstant.EDXML_NS));

        return docType;
    }

    /**
     *
     * @param messageHeaderNode
     * @return
     */
    public SignerInfo getSignerInfo(Element messageHeaderNode) {

        SignerInfo signerInfo = new SignerInfo();

        Element signerInfoNode = getSingerElement(messageHeaderNode, "SignerInfo", EdXmlConstant.EDXML_NS);

        signerInfo.setFullName(signerInfoNode.getChildText("FullName", EdXmlConstant.EDXML_NS));

        signerInfo.setCompetence(signerInfoNode.getChildText("Competence", EdXmlConstant.EDXML_NS));

        signerInfo.setPosition(signerInfoNode.getChildText("Position", EdXmlConstant.EDXML_NS));

        return signerInfo;
    }

    /**
     * @param messageHeaderNode
     * @return
     * @throws XPathExpressionException
     */
    public String GetSubject(Element messageHeaderNode) {
        return messageHeaderNode
                .getChildText("Subject", EdXmlConstant.EDXML_NS);
    }

    /**
     * @param messageHeaderNode
     * @return
     * @throws XPathExpressionException
     */
    public String GetContent(Element messageHeaderNode) {

        return messageHeaderNode
                .getChildText("Content", EdXmlConstant.EDXML_NS);
    }

    /**
     * @param messageHeaderNode
     * @return
     * @throws XPathExpressionException
     */
    public Author GetAuthor(Element messageHeaderNode) {

        Author author = new Author();

        Element authorNode = getSingerElement(messageHeaderNode, "SignerInfo",
                EdXmlConstant.EDXML_NS);

        author.setCompetence(authorNode.getChildText("Competence",
                EdXmlConstant.EDXML_NS));

        author.setFunction(authorNode.getChildText("Position",
                EdXmlConstant.EDXML_NS));

        author.setFullName(authorNode.getChildText("FullName",
                EdXmlConstant.EDXML_NS));

        return author;
    }

    /**
     * @param messageHeaderNode
     * @return
     * @throws XPathExpressionException
     * @throws ParseException
     */
    public String GetResponseDate(Element messageHeaderNode) {

        return messageHeaderNode
                .getChildText("DueDate", EdXmlConstant.EDXML_NS);
    }

    /**
     * @param messageHeaderNode
     * @return
     * @throws XPathExpressionException
     */
    public ToPlaces GetToPlaces(Element messageHeaderNode) {
        List<String> places = new ArrayList<String>();
        ToPlaces toPlaces = new ToPlaces();

        Element toPlacesNode = getSingerElement(messageHeaderNode, "ToPlaces",
                EdXmlConstant.EDXML_NS);

        List<Element> placeNodes = getMultiElement(toPlacesNode, "Place",
                EdXmlConstant.EDXML_NS);
        for (Element item : placeNodes) {
            places.add(item.getTextTrim());
        }
        toPlaces.setPlace(places);

        return toPlaces;
    }

    /**
     * @param messageHeaderNode
     * @return
     * @throws XPathExpressionException
     */
    public Appendixes GetAppendixes(Element messageHeaderNode) {
        Appendixes appendixes = new Appendixes();
        List<String> appendix = new ArrayList<String>();

        Element otherInfoNode = getSingerElement(messageHeaderNode,
                "OtherInfo", EdXmlConstant.EDXML_NS);

        Element appendixesNode = getSingerElement(otherInfoNode, "Appendixes",
                EdXmlConstant.EDXML_NS);

        List<Element> appendixNodes = getMultiElement(appendixesNode,
                "Appendix", EdXmlConstant.EDXML_NS);

        for (Element item : appendixNodes) {
            appendix.add(item.getTextTrim());
        }

        appendixes.setAppendix(appendix);

        return appendixes;
    }

    /**
     * @param messageHeaderNode
     * @return
     * @throws XPathExpressionException
     */
    public OtherInfo GetOtherInfo(Element messageHeaderNode) {
        OtherInfo otherInfo = new OtherInfo();

        Element otherNode = getSingerElement(messageHeaderNode, "OtherInfo",
                EdXmlConstant.EDXML_NS);

        otherInfo.setPriority(Integer.parseInt(otherNode.getChildText(
                "Priority", EdXmlConstant.EDXML_NS)));

        otherInfo.setSphereOfPromulgation(otherNode.getChildText(
                "SphereOfPromulgation", EdXmlConstant.EDXML_NS));

        otherInfo.setTyperNotation(otherNode.getChildText("TyperNotation",
                EdXmlConstant.EDXML_NS));

        otherInfo.setPromulgationAmount(Integer.parseInt(otherNode
                .getChildText("PromulgationAmount", EdXmlConstant.EDXML_NS)));

        otherInfo.setPageAmount(Integer.parseInt(otherNode.getChildText(
                "PageAmount", EdXmlConstant.EDXML_NS)));

        return otherInfo;
    }

    public List<Code> getRelated(Element messageHeaderNode) {

        Element relatedNode = getSingerElement(messageHeaderNode, "Related",
                EdXmlConstant.EDXML_NS);

        if (relatedNode == null) {
            return new ArrayList<Code>();
        }

        return getRelatedCode(relatedNode);
    }

    private List<Code> getRelatedCode(Element relatedNode) {

        List<Code> codes = new ArrayList<Code>();

        List<Element> codeNodes = getMultiElement(relatedNode, "Code",
                EdXmlConstant.EDXML_NS);

        if (codeNodes != null) {
            Code code;
            for (Element item : codeNodes) {
                code = new Code();
                code.setCodeNumber(item.getChildText("CodeNumber",
                        EdXmlConstant.EDXML_NS));

                code.setCodeNotation(item.getChildText("CodeNotation",
                        EdXmlConstant.EDXML_NS));
                codes.add(code);
            }
        }

        return codes;
    }

    /**
     * @param messageHeaderNode
     * @return
     * @throws Exception
     */
    private From GetFromInfo(Element messageHeaderNode) throws Exception {

        From from = new From();

        Element fromNode = getSingerElement(messageHeaderNode, "From",
                EdXmlConstant.EDXML_NS);

        from.setOrganId(fromNode
                .getChildText("OrganId", EdXmlConstant.EDXML_NS));

        from.setOrganInCharge(fromNode.getChildText("OrganizationInCharge",
                EdXmlConstant.EDXML_NS));

        from.setOrganName(fromNode.getChildText("OrganName",
                EdXmlConstant.EDXML_NS));

        from.setOrganAdd(fromNode.getChildText("OrganAdd",
                EdXmlConstant.EDXML_NS));

        from.setEmail(fromNode.getChildText("Email", EdXmlConstant.EDXML_NS));

        from.setTelephone(fromNode.getChildText("Telephone",
                EdXmlConstant.EDXML_NS));

        from.setFax(fromNode.getChildText("Fax", EdXmlConstant.EDXML_NS));

        from.setWebsite(fromNode
                .getChildText("Website", EdXmlConstant.EDXML_NS));

        return from;
    }

    /**
     * @param toNode
     * @return
     * @throws Exception
     */
    private To GetToInfo(Element toNode) throws Exception {

        To to = new To();

        to.setOrganId(toNode.getChildText("OrganId", EdXmlConstant.EDXML_NS));

        to.setOrganName(toNode
                .getChildText("OrganName", EdXmlConstant.EDXML_NS));

        to.setOrganAdd(toNode.getChildText("OrganAdd", EdXmlConstant.EDXML_NS));

        to.setEmail(toNode.getChildText("Email", EdXmlConstant.EDXML_NS));

        to.setTelephone(toNode
                .getChildText("Telephone", EdXmlConstant.EDXML_NS));

        to.setFax(toNode.getChildText("Fax", EdXmlConstant.EDXML_NS));

        to.setWebsite(toNode.getChildText("Website", EdXmlConstant.EDXML_NS));

        Element dueDateNode = toNode.getChild("DueDate", EdXmlConstant.EDXML_NS);
        if (dueDateNode != null) {
            String dueDate = dueDateNode.getTextTrim();
        }

        return to;
    }

    public Element getSingerElement(Element rootElement, String childName,
                                    Namespace ns) {
        if (rootElement == null) {
            return null;
        }
        List<Element> list;
        if (ns == null) {
            list = rootElement.getChildren(childName);
        } else {
            list = rootElement.getChildren(childName, ns);
        }
        if (list == null || list.isEmpty())
            return null;
        return (Element) list.get(0);
    }

    public List<Element> getMultiElement(Element rootElement, String childName,
                                         Namespace ns) {
        if (rootElement == null) {
            return null;
        }
        List<Element> list;
        if (ns == null) {
            list = rootElement.getChildren(childName);
        } else {
            list = rootElement.getChildren(childName, ns);
        }
        return list;
    }

    private List<TraceHeader> GetTraceList(Element traceListNode,
                                           boolean isByHeader) throws Exception {

        List<TraceHeader> results = new ArrayList<TraceHeader>();

        List<Element> traces = isByHeader ? getMultiElement(traceListNode,
                "TraceHeader", EdXmlConstant.EDXML_NS) : traceListNode
                .getChildren();
        if (traces != null) {
            TraceHeader tmpTrace;
            for (Element item : traces) {
                List<Element> listChild = item.getChildren();
                if (listChild == null) {
                    continue;
                }
                if (listChild.size() == 0) {
                    continue;
                }

                tmpTrace = new TraceHeader();
                String temp = isByHeader ? item.getChildText("OrganId",
                        EdXmlConstant.EDXML_NS) : item.getChildText("OrganId");
                tmpTrace.setOrganId(temp);


                String strTime = isByHeader ? item.getChildText("Timestamp",
                        EdXmlConstant.EDXML_NS) : item
                        .getChildText("Timestamp");

                Date timeStamp = XmlGregorianCalendarUtil.convertToDate(
                        strTime, XmlGregorianCalendarUtil.VN_DATE_TIME_FORMAT);
                tmpTrace.setTimeStamp(timeStamp);

                results.add(tmpTrace);
            }
        }

        return results;
    }

    private Business GetBusiness(Element traceListNode) throws Exception {

        Business business = new Business();

        Element businessInfoNode = getSingerElement(traceListNode,"Bussiness", EdXmlConstant.EDXML_NS);

        business.setBusinessDocType(Long.parseLong(businessInfoNode.getChildText("BussinessDocType", EdXmlConstant.EDXML_NS)));

        business.setBusinessDocReason(businessInfoNode.getChildText("BussinessDocReason", EdXmlConstant.EDXML_NS));

        business.setPaper(Long.parseLong(businessInfoNode.getChildText("Paper", EdXmlConstant.EDXML_NS)));

        business.setDocumentId(businessInfoNode.getChildText("DocumentId", EdXmlConstant.EDXML_NS));

        // get staff info
        StaffInfo staffInfo = getStaffInfo(businessInfoNode);
        business.setStaffInfo(staffInfo);

        // get replacement info
        List<ReplacementInfo> replacementInfoList = getReplacementInfos(businessInfoNode);
        business.setReplacementInfoList(replacementInfoList);

        // get bussiness document info

        return business;
    }

    public BussinessDocumentInfo getBussinessDocumentInfo(Element businessNode) throws Exception {
        Element bussinessDocumentInfoNode = businessNode.getChild("BussinessDocumentInfo");
        if(bussinessDocumentInfoNode == null) return null;

        // todo

        return null;
    }

    /**
     *
     * @param businessNode
     * @return
     * @throws Exception
     */
    public List<ReplacementInfo> getReplacementInfos(Element businessNode) throws Exception {
        Element replacementInfoListNode = businessNode.getChild("ReplacementInfoList");
        if (replacementInfoListNode == null) return null;

        List<ReplacementInfo> replacementInfos = new ArrayList<ReplacementInfo>();

        List<Element> replacementInfoForNodes = getMultiElement(replacementInfoListNode, "ReplacementInfo",
                EdXmlConstant.EDXML_NS);

        for (Element replacementInfoNode : replacementInfoForNodes) {
            replacementInfos.add(getReplacementInfo(replacementInfoNode));
        }

        return replacementInfos;
    }

    public ReplacementInfo getReplacementInfo(Element replacementInfoNode) {
        ReplacementInfo replacementInfo = new ReplacementInfo();

        replacementInfo.setDocumentId(replacementInfoNode.getChildText("DocumentId"));

        List<String> organIdList = new ArrayList<>();

        Element organIdListNode = getSingerElement(replacementInfoNode, "OrganIdList", EdXmlConstant.EDXML_NS);
        List<Element> organIdNodes = getMultiElement(organIdListNode, "OrganId",
                EdXmlConstant.EDXML_NS);
        for(Element organIdNode: organIdNodes) {
            organIdList.add(organIdNode.getTextTrim());
        }

        replacementInfo.setOrganIdList(organIdList);

        return replacementInfo;
    }

    private Calendar cal;

    private Log _log = LogFactory.getLog(ExtractMime.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "dd/MM/yyyy");

    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat(
            "dd/MM/yyyy HH:mm:ss");

    private static final XmlUtil xmlUtil = new XmlUtil();


}
