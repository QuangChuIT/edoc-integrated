package com.bkav.edoc.service.database.dao;

import java.util.Date;
import java.util.List;

public interface EdocDocumentDao {
    public boolean checkExistDocument(String subject, String codeNumber, String codeNotation, Date promulgationDate, String fromOrganDomain, String toOrganDomain, List<String> attachmentNames);
}
