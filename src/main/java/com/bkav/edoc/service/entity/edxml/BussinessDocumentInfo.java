package com.bkav.edoc.service.entity.edxml;

import java.util.ArrayList;
import java.util.List;

public class BussinessDocumentInfo {
    private String documentInfo;
    private String documentReceiver;
    private List<Receiver> receiverList = new ArrayList<>();

    public String getDocumentInfo() {
        return documentInfo;
    }

    public void setDocumentInfo(String documentInfo) {
        this.documentInfo = documentInfo;
    }

    public String getDocumentReceiver() {
        return documentReceiver;
    }

    public void setDocumentReceiver(String documentReceiver) {
        this.documentReceiver = documentReceiver;
    }

    public List<Receiver> getReceiverList() {
        return receiverList;
    }

    public void setReceiverList(List<Receiver> receiverList) {
        this.receiverList = receiverList;
    }
}
