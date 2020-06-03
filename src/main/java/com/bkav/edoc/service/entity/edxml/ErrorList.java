/**
 * 
 */
package com.bkav.edoc.service.entity.edxml;

import java.util.ArrayList;
import java.util.List;

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
@XmlRootElement(name = "ErrorList", namespace = StringPool.TARGET_NAMESPACE)

@XmlType(name = "ErrorList", propOrder = { "errors" })

@XmlAccessorType(XmlAccessType.FIELD)

public class ErrorList {
	
	@XmlElement(name="Error")
	private List<java.lang.Error> errors;

	/**
	 * @return the errors
	 */
	public List<java.lang.Error> getErrors() {
		return errors;
	}

	/**
	 * @param errors the errors to set
	 */
	public void setErrors(List<java.lang.Error> errors) {
		this.errors = errors == null ? new ArrayList<java.lang.Error>() : errors;
	}

	/**
	 * @param errors
	 */
	public ErrorList(List<java.lang.Error> errors) {
		super();
		this.errors = errors;
	}
	
	/**
	 * 
	 */
	public ErrorList() {
		super();
		this.errors = new ArrayList<java.lang.Error>();
		// TODO Auto-generated constructor stub
	}


}
