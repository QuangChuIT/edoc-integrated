package com.bkav.edoc.service.database.services;

import com.bkav.edoc.service.database.daoimpl.*;
import com.bkav.edoc.service.database.entity.EdocDocument;
import com.bkav.edoc.service.database.entity.EdocTraceHeaderList;
import com.bkav.edoc.service.entity.edxml.Business;
import com.bkav.edoc.service.entity.edxml.TraceHeader;
import com.bkav.edoc.service.entity.edxml.TraceHeaderList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
                    if (traceHeaderList.getBusiness() != null) {
                        traceHeader.setBusinessDocReason(traceHeaderList.getBusiness().getBusinessDocReason());
                        int businessDocType = (int) traceHeaderList.getBusiness().getBusinessDocType();
                        EdocTraceHeaderList.BusinessDocType type = EdocTraceHeaderList.BusinessDocType.values()[businessDocType];
                        traceHeader.setBusinessDocType(type);
                        traceHeader.setPaper((int) traceHeaderList.getBusiness().getPaper());
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

    /**
     * get trace header list by doc id
     * @param documentId
     * @return
     */
    public TraceHeaderList getTraceHeaderListByDocId(long documentId) {
        traceHeaderListDaoImpl.openCurrentSession();

        TraceHeaderList traceHeaderList = null;
        // get list trace
        List<EdocTraceHeaderList> list = traceHeaderListDaoImpl.getTraceHeaderListByDocId(documentId);
        if(list != null && list.size() > 0) {
            traceHeaderList = new TraceHeaderList();
            Business business = new Business();
            List<TraceHeader> traceHeaders = new ArrayList<>();
            for(EdocTraceHeaderList edocTraceHeader: list) {
                // add trace header
                TraceHeader traceHeader = new TraceHeader();
                traceHeader.setOrganId(edocTraceHeader.getOrganDomain());
                traceHeader.setTimeStamp(edocTraceHeader.getTimeStamp());
                traceHeaders.add(traceHeader);
                // set business
                business.setBusinessDocReason(edocTraceHeader.getBusinessDocReason());
                if(edocTraceHeader.getBusinessDocType() != null) {
                    business.setBusinessDocType(edocTraceHeader.getBusinessDocType().ordinal());
                }
                business.setPaper(edocTraceHeader.getPaper());
            }
            traceHeaderList.setTraceHeaders(traceHeaders);
            traceHeaderList.setBusiness(business);
        }

        traceHeaderListDaoImpl.closeCurrentSession();
        return traceHeaderList;
    }

    private static final Log log = LogFactory.getLog(EdocTraceHeaderListService.class);
}
