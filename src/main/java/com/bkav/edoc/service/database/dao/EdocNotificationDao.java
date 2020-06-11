package com.bkav.edoc.service.database.dao;

import java.util.List;

public interface EdocNotificationDao {
    public List<Long> getDocumentIdsByOrganId(String organId);
}
