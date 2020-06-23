package com.bkav.edoc.service.database.daoimpl;

import com.bkav.edoc.service.database.dao.EdocDocumentDao;
import com.bkav.edoc.service.database.entity.EdocDocument;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.Date;
import java.util.List;

public class EdocDocumentDaoImpl extends RootDaoImpl<EdocDocument, Long> implements EdocDocumentDao {

    public EdocDocumentDaoImpl() {
        super(EdocDocument.class);
    }

    public boolean checkExistDocument(String subject, String codeNumber, String codeNotation, Date promulgationDate, String fromOrganDomain, String toOrganDomain, List<String> attachmentNames) {
        Session currentSession = getCurrentSession();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.`name` FROM edoc_attachment a INNER JOIN edoc_document d ON a.document_id = d.document_id WHERE d.subject = ? AND d.code_number = ? AND d.code_notation = ? AND d.promulgation_date = ? AND d.from_organ_domain = ? AND d.to_organ_domain = ?");
        Query query = currentSession.createSQLQuery(sql.toString());
        query.setParameter(0, subject);
        query.setParameter(1, codeNumber);
        query.setParameter(2, codeNotation);
        query.setParameter(3, promulgationDate);
        query.setParameter(4, fromOrganDomain);
        query.setParameter(5, toOrganDomain);
        List<String> selectedAttachmentNames = query.list();

        return selectedAttachmentNames.containsAll(attachmentNames);
    }

    public boolean checkExistDocument(String edXmlDocumentId) {
        Session currentSession = getCurrentSession();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ed.documentId FROM EdocDocument ed where ed.edXMLDocId=:edXMLDocId");
        Query query = currentSession.createQuery(sql.toString());
        query.setString("edXMLDocId", edXmlDocumentId);
        List result = query.list();
        if (result == null || result.size() == 0) {
            return false;
        }
        return true;
    }

    public EdocDocument searchDocumentByOrganDomainAndCode(String toOrganDomain, String code) {
        Session currentSession = getCurrentSession();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ed FROM EdocDocument ed where ed.fromOrganDomain = :fromOrganDomain and concat(ed.codeNumber, '/', ed.codeNotation) = :code");
        Query query = currentSession.createQuery(sql.toString());
        query.setString("fromOrganDomain",toOrganDomain);
        query.setString("code", code);
        List<EdocDocument> result = query.list();
        if(result != null && result.size() > 0) {
            return result.get(0);
        }
        return null;
    }
}
