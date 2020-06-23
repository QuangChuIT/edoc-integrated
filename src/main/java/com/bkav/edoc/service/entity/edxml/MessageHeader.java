//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.05.15 at 03:39:57 PM ICT 
//


package com.bkav.edoc.service.entity.edxml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for MessageHeader complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MessageHeader">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="From" type="{http://schemas.xmlsoap.org/soap/envelope/}From"/>
 *         &lt;element name="To" type="{http://schemas.xmlsoap.org/soap/envelope/}To" maxOccurs="unbounded"/>
 *         &lt;element name="Code" type="{http://schemas.xmlsoap.org/soap/envelope/}Code"/>
 *         &lt;element name="PromulgationInfo" type="{http://schemas.xmlsoap.org/soap/envelope/}PromulgationInfo"/>
 *         &lt;element name="DocumentType" type="{http://schemas.xmlsoap.org/soap/envelope/}DocumentType"/>
 *         &lt;element name="Subject" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Content" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SignerInfo" type="{http://schemas.xmlsoap.org/soap/envelope/}SignerInfo"/>
 *         &lt;element name="DueDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="ToPlaces" type="{http://schemas.xmlsoap.org/soap/envelope/}ToPlaces"/>
 *         &lt;element name="OtherInfo" type="{http://schemas.xmlsoap.org/soap/envelope/}OtherInfo"/>
 *         &lt;element name="SteeringType" type="{http://www.w3.org/2001/XMLSchema}unsignedShort"/>
 *         &lt;element name="DocumentId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MessageHeader", propOrder = {
    "from",
    "to",
    "code",
    "promulgationInfo",
    "documentType",
    "subject",
    "content",
    "signerInfo",
    "dueDate",
    "toPlaces",
    "otherInfo",
    "steeringType",
    "documentId"
})
public class MessageHeader {

    @XmlElement(name = "From", required = true)
    protected From from;
    @XmlElement(name = "To", required = true)
    protected List<To> to;
    @XmlElement(name = "Code", required = true)
    protected Code code;
    @XmlElement(name = "PromulgationInfo", required = true)
    protected PromulgationInfo promulgationInfo;
    @XmlElement(name = "DocumentType", required = true)
    protected DocumentType documentType;
    @XmlElement(name = "Subject", required = true)
    protected String subject;
    @XmlElement(name = "Content", required = true)
    protected String content;
    @XmlElement(name = "SignerInfo", required = true)
    protected SignerInfo signerInfo;
    @XmlElement(name = "DueDate", required = true)
    protected String dueDate;
    @XmlElement(name = "ToPlaces", required = true)
    protected ToPlaces toPlaces;
    @XmlElement(name = "OtherInfo", required = true)
    protected OtherInfo otherInfo;
    @XmlElement(name = "SteeringType")
    @XmlSchemaType(name = "unsignedShort")
    protected int steeringType;
    @XmlElement(name = "DocumentId", required = true)
    protected String documentId;
    @XmlElement(name = "ResponseFor", required = false)
    protected List<ResponseFor> responseFor;

    public List<ResponseFor> getResponseFor() {
        if (responseFor == null) {
            responseFor = new ArrayList<ResponseFor>();
        }
        return this.responseFor;
    }

    public void setResponseFor(List<ResponseFor> responseFor) {
        this.responseFor = responseFor;
    }

    /**
     * Gets the value of the from property.
     * 
     * @return
     *     possible object is
     *     {@link From }
     *     
     */
    public From getFrom() {
        return from;
    }

    /**
     * Sets the value of the from property.
     * 
     * @param value
     *     allowed object is
     *     {@link From }
     *     
     */
    public void setFrom(From value) {
        this.from = value;
    }

    /**
     * Gets the value of the to property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the to property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link To }
     * 
     * 
     */
    public List<To> getTo() {
        if (to == null) {
            to = new ArrayList<To>();
        }
        return this.to;
    }

    public void setTo(List<To> tos){
        this.to = tos;
    }
    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link Code }
     *     
     */
    public Code getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link Code }
     *     
     */
    public void setCode(Code value) {
        this.code = value;
    }

    /**
     * Gets the value of the promulgationInfo property.
     * 
     * @return
     *     possible object is
     *     {@link PromulgationInfo }
     *     
     */
    public PromulgationInfo getPromulgationInfo() {
        return promulgationInfo;
    }

    /**
     * Sets the value of the promulgationInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link PromulgationInfo }
     *     
     */
    public void setPromulgationInfo(PromulgationInfo value) {
        this.promulgationInfo = value;
    }

    /**
     * Gets the value of the documentType property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentType }
     *     
     */
    public DocumentType getDocumentType() {
        return documentType;
    }

    /**
     * Sets the value of the documentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentType }
     *     
     */
    public void setDocumentType(DocumentType value) {
        this.documentType = value;
    }

    /**
     * Gets the value of the subject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the value of the subject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubject(String value) {
        this.subject = value;
    }

    /**
     * Gets the value of the content property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContent(String value) {
        this.content = value;
    }

    /**
     * Gets the value of the signerInfo property.
     * 
     * @return
     *     possible object is
     *     {@link SignerInfo }
     *     
     */
    public SignerInfo getSignerInfo() {
        return signerInfo;
    }

    /**
     * Sets the value of the signerInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link SignerInfo }
     *     
     */
    public void setSignerInfo(SignerInfo value) {
        this.signerInfo = value;
    }

    /**
     * Gets the value of the dueDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public String getDueDate() {
        return dueDate;
    }

    /**
     * Sets the value of the dueDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDueDate(String value) {
        this.dueDate = value;
    }

    /**
     * Gets the value of the toPlaces property.
     * 
     * @return
     *     possible object is
     *     {@link ToPlaces }
     *     
     */
    public ToPlaces getToPlaces() {
        return toPlaces;
    }

    /**
     * Sets the value of the toPlaces property.
     * 
     * @param value
     *     allowed object is
     *     {@link ToPlaces }
     *     
     */
    public void setToPlaces(ToPlaces value) {
        this.toPlaces = value;
    }

    /**
     * Gets the value of the otherInfo property.
     * 
     * @return
     *     possible object is
     *     {@link OtherInfo }
     *     
     */
    public OtherInfo getOtherInfo() {
        return otherInfo;
    }

    /**
     * Sets the value of the otherInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link OtherInfo }
     *     
     */
    public void setOtherInfo(OtherInfo value) {
        this.otherInfo = value;
    }

    /**
     * Gets the value of the steeringType property.
     * 
     */
    public int getSteeringType() {
        return steeringType;
    }

    /**
     * Sets the value of the steeringType property.
     * 
     */
    public void setSteeringType(int value) {
        this.steeringType = value;
    }

    /**
     * Gets the value of the documentId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocumentId() {
        return documentId;
    }

    /**
     * Sets the value of the documentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumentId(String value) {
        this.documentId = value;
    }

}
