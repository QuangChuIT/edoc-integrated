package com.bkav.edoc.service.database.dao;

import javax.management.Notification;
import java.sql.SQLException;
import java.util.List;

public interface EdocNotificationDao {
    List<Long> getDocumentIdsByOrganId(String organId);

    boolean checkAllowWithDocument(String documentId, String organId);

    void setNotificationTaken(String documentId, String organId) throws SQLException;
}
