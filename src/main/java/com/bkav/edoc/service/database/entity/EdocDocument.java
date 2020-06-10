package com.bkav.edoc.service.database.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class EdocDocument {

    public enum DocumentType {
        NONE, LEGAL, ADMINISTRATIVE
    }

    private Long documentId;
    private String edXMLDocId;
    private Date createDate;
    private Date modifiedDate;
    private String subject;
    private String codeNumber;
    private String codeNotation;
    private String promulgationPlace;
    private Date promulgationDate;
    private DocumentType documentType;
    private String documentTypeName;
    private String documentTypeDetail;
    private boolean draft;

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    private Date sentDate;
    private String toOrganDomain;
    private String fromOrganDomain;
    private Boolean visible;
    private EdocDocumentDetail documentDetail;
    private Set<EdocTraceHeaderList> traceHeaderList = new HashSet<>();
    private EdocPriority priority;
    private Set<EdocNotification> notifications = new HashSet<>();
    private Set<EdocTrace> traces = new HashSet<>();
    private Set<EdocAttachment> attachments = new HashSet<>();

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getEdXMLDocId() {
        return edXMLDocId;
    }

    public void setEdXMLDocId(String edXMLDocId) {
        this.edXMLDocId = edXMLDocId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCodeNumber() {
        return codeNumber;
    }

    public void setCodeNumber(String codeNumber) {
        this.codeNumber = codeNumber;
    }

    public String getCodeNotation() {
        return codeNotation;
    }

    public void setCodeNotation(String codeNotation) {
        this.codeNotation = codeNotation;
    }

    public String getPromulgationPlace() {
        return promulgationPlace;
    }

    public void setPromulgationPlace(String promulgationPlace) {
        this.promulgationPlace = promulgationPlace;
    }

    public Date getPromulgationDate() {
        return promulgationDate;
    }

    public void setPromulgationDate(Date promulgationDate) {
        this.promulgationDate = promulgationDate;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public String getDocumentTypeName() {
        return documentTypeName;
    }

    public void setDocumentTypeName(String documentTypeName) {
        this.documentTypeName = documentTypeName;
    }

    public String getDocumentTypeDetail() {
        return documentTypeDetail;
    }

    public void setDocumentTypeDetail(String documentTypeDetail) {
        this.documentTypeDetail = documentTypeDetail;
    }

    public void setDraft(Boolean draft) {
        this.draft = draft;
    }

    public Boolean isDraft() {
        return draft;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public String getToOrganDomain() {
        return toOrganDomain;
    }

    public void setToOrganDomain(String toOrganDomain) {
        this.toOrganDomain = toOrganDomain;
    }

    public String getFromOrganDomain() {
        return fromOrganDomain;
    }

    public void setFromOrganDomain(String fromOrganDomain) {
        this.fromOrganDomain = fromOrganDomain;
    }

    public Boolean isVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Set<EdocTraceHeaderList> getTraceHeaderList() {
        return traceHeaderList;
    }

    public void setTraceHeaderList(Set<EdocTraceHeaderList> traceHeaderList) {
        this.traceHeaderList = traceHeaderList;
    }

    public EdocDocumentDetail getDocumentDetail() {
        return documentDetail;
    }

    public void setDocumentDetail(EdocDocumentDetail documentDetail) {
        this.documentDetail = documentDetail;
    }

    public EdocPriority getPriority() {
        return priority;
    }

    public void setPriority(EdocPriority priority) {
        this.priority = priority;
    }

    public void setNotifications(Set<EdocNotification> notifications) {
        this.notifications = notifications;
    }

    public Set<EdocNotification> getNotifications() {
        return notifications;
    }

    public Set<EdocTrace> getTraces() {
        return traces;
    }

    public void setTraces(Set<EdocTrace> traces) {
        this.traces = traces;
    }

    public Set<EdocAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<EdocAttachment> attachments) {
        this.attachments = attachments;
    }
}
