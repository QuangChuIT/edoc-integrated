package com.bkav.edoc.service.database.daoimpl;

import com.bkav.edoc.service.database.dao.EdocTraceDao;
import com.bkav.edoc.service.database.entity.EdocTrace;

public class EdocTraceDaoImpl extends RootDaoImpl<EdocTrace, Long> implements EdocTraceDao {
    public EdocTraceDaoImpl() {
        super(EdocTrace.class);
    }
}
