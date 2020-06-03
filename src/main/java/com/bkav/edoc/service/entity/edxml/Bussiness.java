/**
 * 
 */
package com.bkav.edoc.service.entity.edxml;

/**
 * @author quangcv
 *
 */
public class Bussiness {
	private String documentId;
	
	private long bussinessDocType;
	
	private String bussinessDocReason;
	
	private long paper;

	public Bussiness(String documentId, long bussinessDocType,
			String bussinessDocReason, long paper) {
		this.documentId = documentId;
		this.bussinessDocType = bussinessDocType;
		this.bussinessDocReason = bussinessDocReason;
		this.paper = paper;
	}

	public Bussiness() {
	
	}

	/**
	 * @return the documentId
	 */
	public String getDocumentId() {
		return documentId;
	}

	/**
	 * @param documentId the documentId to set
	 */
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	/**
	 * @return the bussinessDocType
	 */
	public long getBussinessDocType() {
		return bussinessDocType;
	}

	/**
	 * @param bussinessDocType the bussinessDocType to set
	 */
	public void setBussinessDocType(long bussinessDocType) {
		this.bussinessDocType = bussinessDocType;
	}

	/**
	 * @return the bussinessDocReason
	 */
	public String getBussinessDocReason() {
		return bussinessDocReason;
	}

	/**
	 * @param bussinessDocReason the bussinessDocReason to set
	 */
	public void setBussinessDocReason(String bussinessDocReason) {
		this.bussinessDocReason = bussinessDocReason;
	}

	/**
	 * @return the paper
	 */
	public long getPaper() {
		return paper;
	}

	/**
	 * @param paper the paper to set
	 */
	public void setPaper(long paper) {
		this.paper = paper;
	}
	
	
}
