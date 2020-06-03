/**
 * 
 */
package com.bkav.edoc.service.entity.edxml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.bkav.edoc.service.resource.StringPool;


/**
 * @author QuyenDN
 *
 */
@XmlRootElement(name = "Error", namespace = StringPool.TARGET_NAMESPACE)
@XmlType(name = "Error", propOrder = { "code", "description" })
@XmlAccessorType(XmlAccessType.FIELD)
public class Error {
	/**
	 * @param code
	 * @param description
	 */
	public Error(String code, String description) {
		super();
		this.code = code;
		this.description = description;
	}

	/**
	 * 
	 */
	public Error() {
		this.code = "";
		this.description = "";
	}

	@XmlElement(name="Code")
	String code;
	
	@XmlElement(name="Description")
	String description;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
