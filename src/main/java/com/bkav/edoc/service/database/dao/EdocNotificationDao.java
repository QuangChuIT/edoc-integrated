package com.bkav.edoc.service.database.dao;

import javax.management.Notification;
import java.sql.SQLException;
import java.util.List;

public interface EdocNotificationDao {
    public List<Long> getDocumentIdsByOrganId(String organId);
    public boolean checkAllowWithDocument(String documentId, String organId);
    public void setNotificationTaken(String documentId, String organId) throws SQLException;
}
