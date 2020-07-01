//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.05.15 at 03:34:23 PM ICT 
//


package com.bkav.edoc.service.entity.edxml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Header complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Header">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MessageHeader" type="{http://schemas.xmlsoap.org/soap/envelope/}MessageHeader"/>
 *         &lt;element name="TraceHeaderList" type="{http://schemas.xmlsoap.org/soap/envelope/}TraceHeaderList"/>
 *         &lt;element name="ErrorList" type="{http://schemas.xmlsoap.org/soap/envelope/}ErrorList"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Header", propOrder = {
        "messageHeader",
        "traceHeaderList",
        "errorList"
})
public class Header {

    @XmlElement(name = "MessageHeader", required = true)
    protected MessageHeader messageHeader;
    @XmlElement(name = "TraceHeaderList", required = true)
    protected TraceHeaderList traceHeaderList;
    @XmlElement(name = "ErrorList", required = true)
    protected ErrorList errorList;

    /**
     * Gets the value of the messageHeader property.
     *
     * @return possible object is
     * {@link MessageHeader }
     */
    public MessageHeader getMessageHeader() {
        return messageHeader;
    }

    /**
     * Sets the value of the messageHeader property.
     *
     * @param value allowed object is
     *              {@link MessageHeader }
     */
    public void setMessageHeader(MessageHeader value) {
        this.messageHeader = value;
    }

    /**
     * Gets the value of the traceHeaderList property.
     *
     * @return possible object is
     * {@link TraceHeaderList }
     */
    public TraceHeaderList getTraceHeaderList() {
        return traceHeaderList;
    }

    /**
     * Sets the value of the traceHeaderList property.
     *
     * @param value allowed object is
     *              {@link TraceHeaderList }
     */
    public void setTraceHeaderList(TraceHeaderList value) {
        this.traceHeaderList = value;
    }

    /**
     * Gets the value of the errorList property.
     *
     * @return possible object is
     * {@link ErrorList }
     */
    public ErrorList getErrorList() {
        return errorList;
    }

    /**
     * Sets the value of the errorList property.
     *
     * @param value allowed object is
     *              {@link ErrorList }
     */
    public void setErrorList(ErrorList value) {
        this.errorList = value;
    }

}
