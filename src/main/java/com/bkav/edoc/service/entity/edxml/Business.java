/**
 * 
 */
package com.bkav.edoc.service.entity.edxml;

/**
 * @author quangcv
 *
 */
public class Business {
	private String documentId;
	
	private long businessDocType;
	
	private String businessDocReason;
	
	private long paper;

	public Business(String documentId, long businessDocType,
			String businessDocReason, long paper) {
		this.documentId = documentId;
		this.businessDocType = businessDocType;
		this.businessDocReason = businessDocReason;
		this.paper = paper;
	}

	public Business() {
	
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
	 * @return the businessDocType
	 */
	public long getBusinessDocType() {
		return businessDocType;
	}

	/**
	 * @param businessDocType the businessDocType to set
	 */
	public void setBusinessDocType(long businessDocType) {
		this.businessDocType = businessDocType;
	}

	/**
	 * @return the businessDocReason
	 */
	public String getBusinessDocReason() {
		return businessDocReason;
	}

	/**
	 * @param businessDocReason the businessDocReason to set
	 */
	public void setBusinessDocReason(String businessDocReason) {
		this.businessDocReason = businessDocReason;
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
