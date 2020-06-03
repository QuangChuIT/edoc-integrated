package com.bkav.edoc.service.entity.edxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.bkav.edoc.service.resource.StringPool;


@XmlRootElement(name = "Organizations", namespace = StringPool.TARGET_NAMESPACE)
@XmlType(name = "Organizations", propOrder = { "organization" })
@XmlAccessorType(XmlAccessType.FIELD)
public class GetOrganizationsResponse {

	@XmlElement(name = "Organization")
	protected List<Organization> organization;

	/**
	 * @return the organization
	 */
	public List<Organization> getOrganization() {
		return organization;
	}

	/**
	 * @param organizations
	 *            the organization to set
	 */
	public void setOrganization(List<Organization> organizations) {
		if (organizations == null) {
			this.organization = new ArrayList<Organization>();
		} else {
			this.organization = organizations;
		}
	}
}
