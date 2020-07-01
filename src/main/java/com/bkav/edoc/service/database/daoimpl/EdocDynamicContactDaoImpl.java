package com.bkav.edoc.service.database.daoimpl;

import com.bkav.edoc.service.database.dao.EdocDynamicContactDao;
import com.bkav.edoc.service.database.entity.EdocDynamicContact;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class EdocDynamicContactDaoImpl extends RootDaoImpl<EdocDynamicContact, Long> implements EdocDynamicContactDao {
    public EdocDynamicContactDaoImpl() {
        super(EdocDynamicContact.class);
    }

    public EdocDynamicContact findByDomain(String domain) {
        Session currentSession = getCurrentSession();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT edc FROM EdocDynamicContact edc where edc.domain=:domain");
        Query query = currentSession.createQuery(sql.toString());
        query.setString("domain", domain);
        List<EdocDynamicContact> result = query.list();
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        return null;
    }
}
