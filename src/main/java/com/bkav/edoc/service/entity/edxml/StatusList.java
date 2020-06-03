/**
 * 
 */
package com.bkav.edoc.service.entity.edxml;

import java.util.List;

/**
 * @author quangcv
 *
 */
public class StatusList {
	private List<Status> status;

	/**
	 * @param traces
	 */
	public StatusList(List<Status> status) {
		this.status = status;
	}

	/**
	 * 
	 */
	public StatusList() {
		super();
	}

	/**
	 * @return the status
	 */
	public List<Status> getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(List<Status> status) {
		this.status = status;
	}

}
