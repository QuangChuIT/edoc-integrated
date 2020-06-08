package com.bkav.edoc.service.database.daoimpl;

import com.bkav.edoc.service.database.dao.EdocTraceHeaderListDao;
import com.bkav.edoc.service.database.entity.EdocTraceHeaderList;

public class EdocTraceHeaderListDaoImpl extends RootDaoImpl<EdocTraceHeaderList, Long> implements EdocTraceHeaderListDao {
    public EdocTraceHeaderListDaoImpl() {
        super(EdocTraceHeaderList.class);
    }
}
