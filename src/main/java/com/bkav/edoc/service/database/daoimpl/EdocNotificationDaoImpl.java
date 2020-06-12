package com.bkav.edoc.service.database.daoimpl;

import com.bkav.edoc.service.database.dao.EdocNotificationDao;
import com.bkav.edoc.service.database.entity.EdocNotification;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EdocNotificationDaoImpl extends RootDaoImpl<EdocNotification, Long> implements EdocNotificationDao {
    public EdocNotificationDaoImpl() {
        super(EdocNotification.class);
    }

    /**
     * get document id by domain
     * @param organId
     * @return
     */
    public List<Long> getDocumentIdsByOrganId(String organId) {
        Session currentSession = getCurrentSession();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT en.document.id FROM EdocNotification en where en.receiverId=:receiverId and en.taken=:taken");
        Query query = currentSession.createQuery(sql.toString());
        query.setString("receiverId", organId);
        query.setBoolean("taken", false);
        List result = query.list();
        return result;
    }

    /**
     * check allow of this domain with document
     * @param documentId
     * @param organId
     * @return
     */
    public boolean checkAllowWithDocument(String documentId, String organId) {
        Session currentSession = getCurrentSession();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT en.document.id FROM EdocNotification en where en.receiverId=:receiverId and en.document.id=:documentId");
        Query query = currentSession.createQuery(sql.toString());
        query.setString("receiverId", organId);
        query.setString("documentId", documentId);
        List result = query.list();
        if(result == null || result.size() == 0) {
            return false;
        }
        return true;
    }

    public void setNotificationTaken(String documentId, String organId) throws SQLException {
        Session currentSession = getCurrentSession();
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE EdocNotification en SET en.taken=:taken where en.receiverId=:receiverId and en.document.id=:documentId");
        Query query = currentSession.createQuery(sql.toString());
        query.setBoolean("taken", true);
        query.setString("receiverId", organId);
        query.setString("documentId", documentId);
        query.executeUpdate();
    }
}
