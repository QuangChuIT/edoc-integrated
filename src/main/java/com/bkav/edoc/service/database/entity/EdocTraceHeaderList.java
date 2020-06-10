package com.bkav.edoc.service.database.entity;

import java.util.Date;

public class EdocTraceHeaderList {

    public enum BusinessDocType {
        REVOKE, NEW, UPDATE, REPLACE
    }

    private Long traceId;
    private String organDomain;
    private Date timeStamp;
    private BusinessDocType businessDocType;
    private String businessDocReason;
    private Integer paper;
    private String department;
    private String staff;
    private String mobile;
    private String email;
    private EdocDocument document;

    public Long getTraceId() {
        return traceId;
    }

    public void setTraceId(Long traceId) {
        this.traceId = traceId;
    }

    public String getOrganDomain() {
        return organDomain;
    }

    public void setOrganDomain(String organDomain) {
        this.organDomain = organDomain;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public BusinessDocType getBusinessDocType() {
        return businessDocType;
    }

    public void setBusinessDocType(BusinessDocType businessDocType) {
        this.businessDocType = businessDocType;
    }

    public String getBusinessDocReason() {
        return businessDocReason;
    }

    public void setBusinessDocReason(String businessDocReason) {
        this.businessDocReason = businessDocReason;
    }

    public Integer getPaper() {
        return paper;
    }

    public void setPaper(Integer paper) {
        this.paper = paper;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStaff() {
        return staff;
    }

    public void setStaff(String staff) {
        this.staff = staff;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDocument(EdocDocument document) {
        this.document = document;
    }

    public EdocDocument getDocument() {
        return document;
    }
}
