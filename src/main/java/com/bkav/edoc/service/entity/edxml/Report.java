package com.bkav.edoc.service.entity.edxml;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.bkav.edoc.service.resource.StringPool;


@XmlRootElement(name = "Report", namespace = StringPool.TARGET_NAMESPACE)
@XmlType(propOrder = { "isSuccess", "errorList" })
@XmlAccessorType(XmlAccessType.FIELD)
public class Report {
	@XmlElement(name = "IsSuccess")
	boolean isSuccess;

	@XmlElement(name = "ErrorList")
	ErrorList errorList;

	public Report(boolean isSuccess, ErrorList errorList) {
		super();
		
		this.isSuccess = isSuccess;

		this.errorList = errorList == null ? new ErrorList() : errorList;
	}

	public Report() {
		
		isSuccess = false;
		
		this.errorList = new ErrorList();
	}

	/**
	 * @return the isSuccess
	 */
	public boolean isIsSuccess() {
		return isSuccess;
	}

	/**
	 * @param isSuccess
	 *            the isSuccess to set
	 */
	public void setIsSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	/**
	 * @return the errorList
	 */
	public ErrorList getErrorList() {
		return errorList;
	}

	/**
	 * @param errorList the errorList to set
	 */
	public void setErrorList(ErrorList errorList) {
		this.errorList = errorList;
	}

	

}
