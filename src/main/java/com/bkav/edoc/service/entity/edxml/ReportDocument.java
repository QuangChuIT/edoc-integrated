/**
 * 
 */
package com.bkav.edoc.service.entity.edxml;

/**
 * @author QuyenDN
 *
 */
public class ReportDocument {
	private String docId;
	private String subject;
	private String codeNumber;
	private String codeNotation;
	private String description;
		
	/**
	 * @return the docId
	 */
	public String getDocId() {
		return docId;
	}



	/**
	 * @param docId the docId to set
	 */
	public void setDocId(String docId) {
		this.docId = docId;
	}



	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}



	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}



	/**
	 * @return the codeNumber
	 */
	public String getCodeNumber() {
		return codeNumber;
	}



	/**
	 * @param codeNumber the codeNumber to set
	 */
	public void setCodeNumber(String codeNumber) {
		this.codeNumber = codeNumber;
	}



	/**
	 * @return the codeNotation
	 */
	public String getCodeNotation() {
		return codeNotation;
	}



	/**
	 * @param codeNotation the codeNotation to set
	 */
	public void setCodeNotation(String codeNotation) {
		this.codeNotation = codeNotation;
	}



	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}



	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}



	/**
	 * 
	 */
	public ReportDocument() {
		
	}



	/**
	 * @param docId
	 * @param subject
	 * @param codeNumber
	 * @param codeNotation
	 * @param description
	 */
	public ReportDocument(String docId, String subject, String codeNumber,
			String codeNotation, String description) {
		super();
		this.docId = docId;
		this.subject = subject;
		this.codeNumber = codeNumber;
		this.codeNotation = codeNotation;
		this.description = description;
	}

}
