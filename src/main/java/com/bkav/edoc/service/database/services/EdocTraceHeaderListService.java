package com.bkav.edoc.service.database.services;

import com.bkav.edoc.service.database.daoimpl.*;
import com.bkav.edoc.service.database.entity.EdocDocument;
import com.bkav.edoc.service.database.entity.EdocTraceHeaderList;
import com.bkav.edoc.service.entity.edxml.TraceHeader;
import com.bkav.edoc.service.entity.edxml.TraceHeaderList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

import java.io.File;
import java.text.SimpleDateFormat;

public class EdocTraceHeaderListService {
    private EdocDocumentDaoImpl documentDaoImpl = new EdocDocumentDaoImpl();
    private EdocTraceHeaderListDaoImpl traceHeaderListDaoImpl = new EdocTraceHeaderListDaoImpl();

    /**
     * Add trace header list
     * @param traceHeaderList
     * @param docId
     * @return
     */
    public boolean addTraceHeaderList(TraceHeaderList traceHeaderList, long docId) {
        Session currentSession = traceHeaderListDaoImpl.openCurrentSession();
        try {
            currentSession.beginTransaction();

            if (traceHeaderList != null && traceHeaderList.getTraceHeaders().size() > 0) {
                documentDaoImpl.setCurrentSession(currentSession);
                EdocDocument document = documentDaoImpl.findById(docId);
                for (TraceHeader trace : traceHeaderList.getTraceHeaders()) {
                    EdocTraceHeaderList traceHeader = new EdocTraceHeaderList();
                    traceHeader.setOrganDomain(trace.getOrganId());
                    traceHeader.setTimeStamp(trace.getTimeStamp());
                    if (traceHeaderList.getBussiness() != null) {
                        traceHeader.setBusinessDocReason(traceHeaderList.getBussiness().getBussinessDocReason());
                        int bussinessDocType = (int) traceHeaderList.getBussiness().getBussinessDocType();
                        EdocTraceHeaderList.BusinessDocType type = EdocTraceHeaderList.BusinessDocType.values()[bussinessDocType];
                        traceHeader.setBusinessDocType(type);
                        traceHeader.setPaper((int) traceHeaderList.getBussiness().getPaper());
                    }
                    traceHeader.setDocument(document);
                    traceHeaderListDaoImpl.persist(traceHeader);
                }
            }

            currentSession.getTransaction().commit();
        } catch (Exception e) {
            log.error(e);
            if(currentSession != null) {
                currentSession.getTransaction().rollback();
            }
            return false;
        } finally {
            traceHeaderListDaoImpl.closeCurrentSession();
        }
        return true;
    }

    private static final Log log = LogFactory.getLog(EdocTraceHeaderListService.class);
}
