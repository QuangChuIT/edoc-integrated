package com.bkav.edoc.service.database.dao;

import com.bkav.edoc.service.database.entity.EdocDocument;

import java.util.Date;
import java.util.List;

public interface EdocDocumentDao {
    public boolean checkExistDocument(String subject, String codeNumber, String codeNotation, Date promulgationDate, String fromOrganDomain, String toOrganDomain, List<String> attachmentNames);
    public EdocDocument searchDocumentByOrganDomainAndCode(String toOrganDomain, String code);
}
