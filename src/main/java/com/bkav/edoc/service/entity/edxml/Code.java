//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.10.10 at 08:40:26 AM ICT 
//


package com.bkav.edoc.service.entity.edxml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Code complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Code">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CodeNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CodeNotation" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Code", propOrder = {
    "codeNumber",
    "codeNotation"
})
public class Code {

    @XmlElement(name = "CodeNumber", required = true)
    protected String codeNumber;
    @XmlElement(name = "CodeNotation", required = true)
    protected String codeNotation;

    /**
     * Gets the value of the codeNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodeNumber() {
        return codeNumber;
    }

    /**
     * Sets the value of the codeNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodeNumber(String value) {               
        this.codeNumber = value;
    }

    /**
     * Gets the value of the codeNotation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodeNotation() {
        return codeNotation;
    }

    /**
     * Sets the value of the codeNotation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodeNotation(String value) {
        this.codeNotation = value;
    }

}
