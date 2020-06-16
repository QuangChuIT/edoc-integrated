package com.bkav.edoc.service.database.dao;

import com.bkav.edoc.service.database.entity.EdocTrace;

import java.util.List;

public interface EdocTraceDao {
    public List<EdocTrace> getTraceByDocumentId(long documentId);
}