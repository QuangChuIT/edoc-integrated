package com.bkav.edoc.service.database.daoimpl;

import com.bkav.edoc.service.database.dao.EdocAttachmentDao;
import com.bkav.edoc.service.database.entity.EdocAttachment;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class EdocAttachmentDaoImpl extends RootDaoImpl<EdocAttachment, Long> implements EdocAttachmentDao {
    public EdocAttachmentDaoImpl() {
        super(EdocAttachment.class);
    }

    public List<EdocAttachment> getAttachmentsByDocumentId(long documentId) {
        Session currentSession = getCurrentSession();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ea FROM EdocAttachment ea where ea.document.id=:documentId");
        Query query = currentSession.createQuery(sql.toString());
        query.setLong("documentId", documentId);
        List<EdocAttachment> result = query.list();
        return result;
    }
}
