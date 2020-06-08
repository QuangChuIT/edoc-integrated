package com.bkav.edoc.service.database.entity;

import java.util.Date;

public class EdocTraceHeaderList {

    public enum BussinessDocType {
        REVOKE, NEW, UPDATE, REPLACE
    }

    private Long documentId;
    private String organDomain;
    private Date timeStamp;
    private BussinessDocType bussinessDocType;
    private String bussinessDocReason;
    private Integer paper;
    private String department;
    private String staff;
    private String mobile;
    private String email;
    private EdocDocument document;

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public Long getDocumentId() {
        return documentId;
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

    public BussinessDocType getBussinessDocType() {
        return bussinessDocType;
    }

    public void setBussinessDocType(BussinessDocType bussinessDocType) {
        this.bussinessDocType = bussinessDocType;
    }

    public String getBussinessDocReason() {
        return bussinessDocReason;
    }

    public void setBussinessDocReason(String bussinessDocReason) {
        this.bussinessDocReason = bussinessDocReason;
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
