/**
 * 
 */
package com.bkav.edoc.service.entity.edxml;

import com.bkav.edoc.service.resource.StringPool;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;


/*QuangCV - May 27, 2020*/

/**
 * @author QuangCV
 * 
 */
@XmlRootElement(name = "Traces", namespace = StringPool.TARGET_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
public class GetTraceResponse {

	@XmlElement(name="Status")
	protected List<Status> statuses;

	public List<Status> getStatuses() {
		return statuses;
	}

	public void setStatuses(List<Status> statuses) {
		this.statuses = statuses;
	}

	public GetTraceResponse(List<Status> statuses) {
		super();
		this.statuses = statuses;
	}

	public GetTraceResponse() {
		statuses = new ArrayList<Status>();
	}
}