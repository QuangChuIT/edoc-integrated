package com.bkav.edoc.service.database.daoimpl;

import com.bkav.edoc.service.database.dao.EdocDocumentDetailDao;
import com.bkav.edoc.service.database.entity.EdocDocumentDetail;

public class EdocDocumentDetailDaoImpl extends RootDaoImpl<EdocDocumentDetail, Long> implements EdocDocumentDetailDao {
    public EdocDocumentDetailDaoImpl() {
        super(EdocDocumentDetail.class);
    }
}
