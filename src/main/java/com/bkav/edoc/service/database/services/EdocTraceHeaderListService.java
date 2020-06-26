package com.bkav.edoc.service.database.services;

import com.bkav.edoc.service.database.daoimpl.*;
import com.bkav.edoc.service.database.entity.EdocDocument;
import com.bkav.edoc.service.database.entity.EdocTraceHeader;
import com.bkav.edoc.service.database.entity.EdocTraceHeaderList;
import com.bkav.edoc.service.entity.edxml.Bussiness;
import com.bkav.edoc.service.entity.edxml.StaffInfo;
import com.bkav.edoc.service.entity.edxml.TraceHeader;
import com.bkav.edoc.service.entity.edxml.TraceHeaderList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EdocTraceHeaderListService {
    private EdocDocumentDaoImpl documentDaoImpl = new EdocDocumentDaoImpl();
    private EdocTraceHeaderListDaoImpl traceHeaderListDaoImpl = new EdocTraceHeaderListDaoImpl();
    private EdocTraceHeaderDaoImpl traceHeaderDaoImpl = new EdocTraceHeaderDaoImpl();

    /**
     * Add trace header list
     * @param traceHeaderList
     * @param docId
     * @return
     */
    public boolean addTraceHeaderList(TraceHeaderList traceHeaderList, String bussinessInfo, long docId) {
        Session currentSession = traceHeaderListDaoImpl.openCurrentSession();
        try {
            currentSession.beginTransaction();

            if (traceHeaderList != null && traceHeaderList.getTraceHeaders().size() > 0) {
                documentDaoImpl.setCurrentSession(currentSession);
                EdocDocument document = documentDaoImpl.findById(docId);
                EdocTraceHeaderList edocTraceHeaderList = new EdocTraceHeaderList();
                edocTraceHeaderList.setBussinessDocReason(traceHeaderList.getBussiness().getBussinessDocReason());
                int bussinessDocType = (int) traceHeaderList.getBussiness().getBussinessDocType();
                EdocTraceHeaderList.BussinessDocType type = EdocTraceHeaderList.BussinessDocType.values()[bussinessDocType];
                edocTraceHeaderList.setBussinessDocType(type);
                edocTraceHeaderList.setPaper((int) traceHeaderList.getBussiness().getPaper());
                edocTraceHeaderList.setBussinessInfo(bussinessInfo);
                // get staff info
                if(traceHeaderList.getBussiness().getStaffInfo() != null) {
                    StaffInfo staffInfo = traceHeaderList.getBussiness().getStaffInfo();
                    edocTraceHeaderList.setEmail(staffInfo.getEmail());
                    edocTraceHeaderList.setDepartment(staffInfo.getDepartment());
                    edocTraceHeaderList.setMobile(staffInfo.getMobile());
                    edocTraceHeaderList.setStaff(staffInfo.getStaff());
                }

                // save to database
                edocTraceHeaderList.setDocument(document);
                traceHeaderListDaoImpl.persist(edocTraceHeaderList);

                // get list trace header
                for (TraceHeader trace : traceHeaderList.getTraceHeaders()) {
                    EdocTraceHeader traceHeader = new EdocTraceHeader();
                    traceHeader.setOrganDomain(trace.getOrganId());
                    traceHeader.setTimeStamp(trace.getTimeStamp());
                    traceHeader.setTraceHeaderList(edocTraceHeaderList);
                    traceHeaderDaoImpl.setCurrentSession(currentSession);
                    traceHeaderDaoImpl.persist(traceHeader);
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
        EdocTraceHeaderList edocTraceHeaderList = traceHeaderListDaoImpl.getTraceHeaderListByDocId(documentId);

        Hibernate.initialize(edocTraceHeaderList.getTraceHeaders());
        Set<EdocTraceHeader> edocTraceHeaders = edocTraceHeaderList.getTraceHeaders();

        traceHeaderList = new TraceHeaderList();

        Bussiness bussiness = new Bussiness();
        bussiness.setPaper(edocTraceHeaderList.getPaper());
        bussiness.setBussinessDocReason(edocTraceHeaderList.getBussinessDocReason());
        bussiness.setBussinessDocType(edocTraceHeaderList.getBussinessDocType().ordinal());

        StaffInfo staffInfo = new StaffInfo();
        staffInfo.setMobile(edocTraceHeaderList.getMobile());
        staffInfo.setStaff(edocTraceHeaderList.getStaff());
        staffInfo.setEmail(edocTraceHeaderList.getEmail());
        staffInfo.setDepartment(edocTraceHeaderList.getDepartment());
        bussiness.setStaffInfo(staffInfo);

        List<TraceHeader> traceHeaders = new ArrayList<>();
        for(EdocTraceHeader edocTraceHeader: edocTraceHeaders) {
            // add trace header
            TraceHeader traceHeader = new TraceHeader();
            traceHeader.setOrganId(edocTraceHeader.getOrganDomain());
            traceHeader.setTimeStamp(edocTraceHeader.getTimeStamp());
            traceHeaders.add(traceHeader);
        }

        traceHeaderList.setTraceHeaders(traceHeaders);
        traceHeaderList.setBussiness(bussiness);
        traceHeaderList.setBussinessInfo(edocTraceHeaderList.getBussinessInfo());

        traceHeaderListDaoImpl.closeCurrentSession();
        return traceHeaderList;
    }

    private static final Log log = LogFactory.getLog(EdocTraceHeaderListService.class);
}
