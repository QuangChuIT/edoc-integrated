package com.bkav.edoc.service.database.entity;

import java.util.Date;

public class EdocDocumentDetail {

    public enum SteeringType {
        NONE_STEER, STEER, STEER_REPORT
    }

    private Long documentId;
    private String content;
    private String signerCompetence;
    private String signerPosition;
    private Date dueDate;
    private String toPlaces;
    private String sphereOfPromulgation;
    private String typerNotation;
    private Long promulgationAmount;
    private Long pageAmount;
    private String appendixes;
    private String responseForOrganId;
    private String responseForCode;
    private Date responseForPromulgationDate;
    private String responseForDocumentId;
    private SteeringType steeringType;
    private EdocDocument document;

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSignerCompetence() {
        return signerCompetence;
    }

    public void setSignerCompetence(String signerCompetence) {
        this.signerCompetence = signerCompetence;
    }

    public String getSignerPosition() {
        return signerPosition;
    }

    public void setSignerPosition(String signerPosition) {
        this.signerPosition = signerPosition;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getToPlaces() {
        return toPlaces;
    }

    public void setToPlaces(String toPlaces) {
        this.toPlaces = toPlaces;
    }

    public String getSphereOfPromulgation() {
        return sphereOfPromulgation;
    }

    public void setSphereOfPromulgation(String sphereOfPromulgation) {
        this.sphereOfPromulgation = sphereOfPromulgation;
    }

    public String getTyperNotation() {
        return typerNotation;
    }

    public void setTyperNotation(String typerNotation) {
        this.typerNotation = typerNotation;
    }

    public Long getPromulgationAmount() {
        return promulgationAmount;
    }

    public void setPromulgationAmount(Long promulgationAmount) {
        this.promulgationAmount = promulgationAmount;
    }

    public Long getPageAmount() {
        return pageAmount;
    }

    public void setPageAmount(Long pageAmount) {
        this.pageAmount = pageAmount;
    }

    public String getAppendixes() {
        return appendixes;
    }

    public void setAppendixes(String appendixes) {
        this.appendixes = appendixes;
    }

    public String getResponseForOrganId() {
        return responseForOrganId;
    }

    public void setResponseForOrganId(String responseForOrganId) {
        this.responseForOrganId = responseForOrganId;
    }

    public String getResponseForCode() {
        return responseForCode;
    }

    public void setResponseForCode(String responseForCode) {
        this.responseForCode = responseForCode;
    }

    public Date getResponseForPromulgationDate() {
        return responseForPromulgationDate;
    }

    public void setResponseForPromulgationDate(Date responseForPromulgationDate) {
        this.responseForPromulgationDate = responseForPromulgationDate;
    }

    public String getResponseForDocumentId() {
        return responseForDocumentId;
    }

    public void setResponseForDocumentId(String responseForDocumentId) {
        this.responseForDocumentId = responseForDocumentId;
    }

    public SteeringType getSteeringType() {
        return steeringType;
    }

    public void setSteeringType(SteeringType steeringType) {
        this.steeringType = steeringType;
    }

    public EdocDocument getDocument() {
        return document;
    }

    public void setDocument(EdocDocument document) {
        this.document = document;
    }
}
