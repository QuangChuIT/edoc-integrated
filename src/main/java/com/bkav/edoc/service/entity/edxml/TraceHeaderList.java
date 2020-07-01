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

    private Business business;

    private String businessInfo;

    /**
     * @param traceHeaders
     * @param business
     */
    public TraceHeaderList(List<TraceHeader> traceHeaders, Business business) {
        super();
        this.traceHeaders = traceHeaders;
        this.business = business;
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
     * @return the business
     */
    public Business getBusiness() {
        return business;
    }

    /**
     * @param business the business to set
     */
    public void setBusiness(Business business) {
        this.business = business;
    }

    public String getBusinessInfo() {
        return businessInfo;
    }

    public void setBusinessInfo(String businessInfo) {
        this.businessInfo = businessInfo;
    }
}
