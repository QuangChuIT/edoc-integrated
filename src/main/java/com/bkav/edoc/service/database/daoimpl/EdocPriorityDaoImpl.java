package com.bkav.edoc.service.database.daoimpl;

import com.bkav.edoc.service.database.dao.EdocPriorityDao;
import com.bkav.edoc.service.database.entity.EdocPriority;

public class EdocPriorityDaoImpl extends RootDaoImpl<EdocPriority, Long> implements EdocPriorityDao {
    public EdocPriorityDaoImpl() {
        super(EdocPriority.class);
    }
}
