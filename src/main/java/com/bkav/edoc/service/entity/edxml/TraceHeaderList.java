/**
 *
 */
package com.bkav.edoc.service.entity.edxml;

import java.util.List;

/**
 * @author QuangCV
 *
 */
public class TraceHeaderList {
	private List<TraceHeader> traceHeaders;

	private Bussiness bussiness;

	private String bussinessInfo;

	/**
	 * @param traceHeaders
	 * @param bussiness
	 */
	public TraceHeaderList(List<TraceHeader> traceHeaders, Bussiness bussiness) {
		super();
		this.traceHeaders = traceHeaders;
		this.bussiness = bussiness;
	}

	/**
	 *
	 */
	public TraceHeaderList() {
		super();
	}

	/**
	 * @return the traceHeader
	 */
	public List<TraceHeader> getTraceHeaders() {
		return traceHeaders;
	}

	/**
	 * @param traceHeaders the traceHeader to set
	 */
	public void setTraceHeaders(List<TraceHeader> traceHeaders) {
		this.traceHeaders = traceHeaders;
	}

	/**
	 * @return the bussiness
	 */
	public Bussiness getBussiness() {
		return bussiness;
	}

	/**
	 * @param bussiness the bussiness to set
	 */
	public void setBussiness(Bussiness bussiness) {
		this.bussiness = bussiness;
	}

	public String getBussinessInfo() {
		return bussinessInfo;
	}

	public void setBussinessInfo(String bussinessInfo) {
		this.bussinessInfo = bussinessInfo;
	}
}
