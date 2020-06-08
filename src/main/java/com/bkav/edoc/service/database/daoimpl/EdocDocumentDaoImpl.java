package com.bkav.edoc.service.database.daoimpl;

import com.bkav.edoc.service.database.dao.EdocDocumentDao;
import com.bkav.edoc.service.database.entity.EdocDocument;

public class EdocDocumentDaoImpl extends RootDaoImpl<EdocDocument, Long> implements EdocDocumentDao {

    public EdocDocumentDaoImpl() {
        super(EdocDocument.class);
    }
}
