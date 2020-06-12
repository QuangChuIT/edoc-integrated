package com.bkav.edoc.service.database.dao;

import com.bkav.edoc.service.database.entity.EdocAttachment;

import java.util.List;

public interface EdocAttachmentDao {
    public List<EdocAttachment> getAttachmentsByDocumentId(long documentId);
}
