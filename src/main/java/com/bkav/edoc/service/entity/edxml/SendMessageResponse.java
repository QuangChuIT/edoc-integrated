/**
 *
 */
package com.bkav.edoc.service.entity.edxml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author QuangCV
 *
 */

@XmlType(name = "SendMessageResult", propOrder = {"success", "report"})
@XmlAccessorType(value = XmlAccessType.NONE)
@XmlRootElement(name = "SendMessageResult")
public class SendMessageResponse {
    public SendMessageResponse(boolean success, String report) {
        this.success = success;
        this.report = report;
    }

    public SendMessageResponse() {

    }

    @XmlElement(name = "Success")
    private boolean success;

    @XmlElement(name = "Report")
    private String report;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }
}

