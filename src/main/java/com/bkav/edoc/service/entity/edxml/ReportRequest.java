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

@XmlRootElement(name = "ReportRequest", namespace = StringPool.TARGET_NAMESPACE)
@XmlType(propOrder = { "document", "type" })
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportRequest {

	@XmlElement(name="Type", defaultValue="")
	private String type;
	
	@XmlElement(name="Document")
	private ReportDocument document;	
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the document
	 */
	public ReportDocument getDocument() {
		return document;
	}

	/**
	 * @param document the document to set
	 */
	public void setDocument(ReportDocument document) {
		this.document = document;
	}

	public ReportRequest() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param type
	 * @param document
	 */
	public ReportRequest(String type, ReportDocument document) {
		super();
		this.type = type;
		this.document = document;
	}


}
