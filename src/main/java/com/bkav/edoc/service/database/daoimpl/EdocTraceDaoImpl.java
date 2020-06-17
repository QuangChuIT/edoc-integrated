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

    public  List<EdocTrace> getEdocTracesByOrganId(String responseForOrganId) {
        Session currentSession = getCurrentSession();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT et FROM EdocTrace et where et.toOrganDomain=:responseForOrganId and et.enable=:enable order by et.timeStamp DESC");
        Query query = currentSession.createQuery(sql.toString());
        query.setString("responseForOrganId", responseForOrganId);
        query.setBoolean("enable", true);
        List<EdocTrace> result = query.list();
        return result;
    }

    public void disableEdocTrace(long traceId) {
        Session currentSession = getCurrentSession();
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE edoc_trace SET enable=:enable where trace_id=:traceId");
        Query query = currentSession.createSQLQuery(sql.toString());
        query.setBoolean("enable", false);
        query.setLong("traceId", traceId);
        query.executeUpdate();
    }
}
