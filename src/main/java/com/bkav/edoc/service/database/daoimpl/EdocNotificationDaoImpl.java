package com.bkav.edoc.service.database.daoimpl;

import com.bkav.edoc.service.database.dao.EdocNotificationDao;
import com.bkav.edoc.service.database.entity.EdocNotification;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class EdocNotificationDaoImpl extends RootDaoImpl<EdocNotification, Long> implements EdocNotificationDao {
    public EdocNotificationDaoImpl() {
        super(EdocNotification.class);
    }

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
}
