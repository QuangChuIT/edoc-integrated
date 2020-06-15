package com.bkav.edoc.service.database.daoimpl;

import com.bkav.edoc.service.database.dao.EdocTraceDao;
import com.bkav.edoc.service.database.entity.EdocTrace;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class EdocTraceDaoImpl extends RootDaoImpl<EdocTrace, Long> implements EdocTraceDao {
    public EdocTraceDaoImpl() {
        super(EdocTrace.class);
    }

    public List<EdocTrace> getTraceByDocumentId(long documentId) {
        Session currentSession = getCurrentSession();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT et FROM EdocTrace et where et.document.id=:documentId");
        Query query = currentSession.createQuery(sql.toString());
        query.setLong("documentId", documentId);
        List<EdocTrace> result = query.list();
        return result;
    }
}
