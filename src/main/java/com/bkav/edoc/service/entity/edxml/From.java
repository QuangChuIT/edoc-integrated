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
 * <p>Java class for From complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="From">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OrganId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="OrganAdd" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Email" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Telephone" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Fax" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Website" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "From", propOrder = {
    "organId",
    "organAdd",
    "email",
    "telephone",
    "fax",
    "website"
})
public class From {

    @XmlElement(name = "OrganId", required = true)
    protected String organId;
    @XmlElement(name = "OrganName", required = true)
    protected String organName;
    @XmlElement(name = "OrganInCharge", required = true)
    protected String organInCharge;
	@XmlElement(name = "OrganAdd", required = true)
    protected String organAdd;
    @XmlElement(name = "Email", required = true)
    protected String email;
    @XmlElement(name = "Telephone", required = true)
    protected String telephone;
    @XmlElement(name = "Fax", required = true)
    protected String fax;
    @XmlElement(name = "Website", required = true)
    protected String website;

    /**
     * Gets the value of the organId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    
    public From(){}
    
    public From(String organId, String organName, String organInCharge, String organAdd, String email, String telephone, String fax, String website){
    	this.organId = organId;
    	this.organName = organName;
    	this.organInCharge = organInCharge;
    	this.organAdd = organAdd;
    	this.email = email;
    	this.telephone = telephone;
    	this.fax = fax;
    	this.website = website;
    }
    
    public String getOrganId() {
        return organId;
    }

    /**
     * Sets the value of the organId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganId(String value) {
        this.organId = value;
    }

    /**
     * Gets the value of the organAdd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    
    /**
	 * @return the organName
	 */
	public String getOrganName() {
		return organName;
	}

	/**
	 * @param organName the organName to set
	 */
	public void setOrganName(String organName) {
		this.organName = organName;
	}
    public String getOrganAdd() {
        return organAdd;
    }

    /**
     * Sets the value of the organAdd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganAdd(String value) {
        this.organAdd = value;
    }

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the telephone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * Sets the value of the telephone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelephone(String value) {
        this.telephone = value;
    }

    /**
     * Gets the value of the fax property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFax() {
        return fax;
    }

    /**
     * Sets the value of the fax property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFax(String value) {
        this.fax = value;
    }

    /**
     * Gets the value of the website property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWebsite() {
        return website;
    }

    /**
     * Sets the value of the website property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWebsite(String value) {
        this.website = value;
    }

	public String getOrganInCharge() {
		return organInCharge;
	}

	public void setOrganInCharge(String organInCharge) {
		this.organInCharge = organInCharge;
	}

}
