package com.bkav.edoc.service.database.daoimpl;

import com.bkav.edoc.service.database.dao.EdocAttachmentDao;
import com.bkav.edoc.service.database.entity.EdocAttachment;

public class EdocAttachmentDaoImpl extends RootDaoImpl<EdocAttachment, Long> implements EdocAttachmentDao {
    public EdocAttachmentDaoImpl() {
        super(EdocAttachment.class);
    }
}
