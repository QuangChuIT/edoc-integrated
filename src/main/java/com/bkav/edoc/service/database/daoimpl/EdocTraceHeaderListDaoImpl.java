package com.bkav.edoc.service.database.daoimpl;

import com.bkav.edoc.service.database.dao.EdocTraceHeaderListDao;
import com.bkav.edoc.service.database.entity.EdocTraceHeaderList;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class EdocTraceHeaderListDaoImpl extends RootDaoImpl<EdocTraceHeaderList, Long> implements EdocTraceHeaderListDao {
    public EdocTraceHeaderListDaoImpl() {
        super(EdocTraceHeaderList.class);
    }

    public List<EdocTraceHeaderList> getTraceHeaderListByDocId(long documentId) {
        Session currentSession = getCurrentSession();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT eth FROM EdocTraceHeaderList eth where eth.document.id=:documentId");
        Query query = currentSession.createQuery(sql.toString());
        query.setLong("documentId", documentId);
        List<EdocTraceHeaderList> result = query.list();
        return result;
    }
}
