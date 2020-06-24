package com.bkav.edoc.service.entity.edxml;

import java.util.ArrayList;
import java.util.List;

public class ReplacementInfo {
    private String documentId;
    private List<String> organIdList = new ArrayList<>();

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public List<String> getOrganIdList() {
        return organIdList;
    }

    public void setOrganIdList(List<String> organIdList) {
        this.organIdList = organIdList;
    }
}
