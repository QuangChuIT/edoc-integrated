/**
 * 
 */
package com.bkav.edoc.service.entity.edxml;

import java.util.List;

/**
 * @author QuangCV
 * 
 */
public class ReportResponse {
	private Report Report;

	/**
	 * @param report
	 */
	public ReportResponse(com.bkav.edoc.service.entity.edxml.Report report) {
		super();
		Report = report;
	}

	/**
	 * @param errorList
	 * @param isSuccess
	 */
	public ReportResponse(boolean isSuccess, List<java.lang.Error> errorList) {
		super();
		Report = new Report(isSuccess, new ErrorList(errorList));
	}

	public ReportResponse() {
		Report = new Report();
	}

	/**
	 * @return the report
	 */
	public Report getReport() {
		return Report;
	}

	/**
	 * @param report
	 *            the report to set
	 */
	public void setReport(Report report) {
		Report = report;
	}

}
