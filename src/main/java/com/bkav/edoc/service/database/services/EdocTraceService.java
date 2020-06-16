package com.bkav.edoc.service.database.services;

import com.bkav.edoc.service.database.daoimpl.EdocDocumentDaoImpl;
import com.bkav.edoc.service.database.daoimpl.EdocTraceDaoImpl;
import com.bkav.edoc.service.database.entity.EdocDocument;
import com.bkav.edoc.service.database.entity.EdocTrace;
import com.bkav.edoc.service.entity.edxml.Status;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EdocTraceService {

    private final EdocTraceDaoImpl traceDaoImpl = new EdocTraceDaoImpl();
    private final EdocDocumentDaoImpl documentDaoImpl = new EdocDocumentDaoImpl();

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "dd/MM/yyyy");

    public boolean updateTrace(Status status) {
        // get info from status
        String fromOrganDomain = status.getFrom().getOrganId();
        String fromOrganName = status.getFrom().getOrganName();
        String fromOrganAdd = status.getFrom().getOrganAdd();
        String telephone = status.getFrom().getTelephone();
        String fax = status.getFrom().getFax();
        String website = status.getFrom().getWebsite();
        String toOrganDomain = status.getResponseFor().getOrganId();
        String code = status.getResponseFor().getCode();
        String promulgationDateStr = status.getResponseFor().getPromulgationDate();
        Date promulgationDate = null;
        try {
            if (promulgationDateStr != null && !promulgationDateStr.isEmpty()) {
                promulgationDate = dateFormat.parse(promulgationDateStr);
            }
        } catch (ParseException e) {
            log.error(e);
        }
        String documentId = status.getDocumentId();

        // get other info
        String statusCodeStr = status.getStatusCode();
        Integer statusCode = null;
        try {
            if (statusCodeStr != null && !statusCodeStr.isEmpty()) {
                statusCode = Integer.parseInt(statusCodeStr);
            }
        } catch (NumberFormatException e) {
            log.error(e);
        }
        String description = status.getDescription();
        String timestampStr = status.getTimeStamp();
        Date timestamp = null;
        try {
            if (timestampStr != null && !timestampStr.isEmpty()) {
                timestamp = dateFormat.parse(timestampStr);
            }
        } catch (ParseException e) {
            log.error(e);
        }
        // get info staff info
        String department = status.getStaffInfo().getDepartment();
        String email = status.getStaffInfo().getEmail();
        String mobile = status.getStaffInfo().getMobile();
        String staff = status.getStaffInfo().getStaff();

        // search document by from organ domain and code
        EdocDocument edocDocument = documentDaoImpl.searchDocumentByOrganDomainAndCode(toOrganDomain, code);

        // set info to edoc trace
        EdocTrace edocTrace = new EdocTrace();
        edocTrace.setFromOrganDomain(fromOrganDomain);
        edocTrace.setOrganName(fromOrganName);
        edocTrace.setOrganAdd(fromOrganAdd);
        edocTrace.setToOrganDomain(toOrganDomain);
        edocTrace.setCode(code);
        edocTrace.setPromulgationDate(promulgationDate);
        edocTrace.setEdxmlDocumentId(documentId);
        edocTrace.setStatusCode(statusCode);
        edocTrace.setComment(description);
        edocTrace.setTimeStamp(timestamp);
        edocTrace.setDepartment(department);
        edocTrace.setEmail(email);
        edocTrace.setStaffMobile(mobile);
        edocTrace.setStaffName(staff);
        edocTrace.setServerTimeStamp(new Date());
        edocTrace.setFax(fax);
        edocTrace.setTelephone(telephone);
        edocTrace.setWebsite(website);
        edocTrace.setDocument(edocDocument);

        // insert trace to db
        Session currentSession = traceDaoImpl.openCurrentSession();
        try {
            currentSession.beginTransaction();
            traceDaoImpl.persist(edocTrace);
            currentSession.getTransaction().commit();
        } catch (Exception e) {
            log.error(e);
            if(currentSession != null) {
                currentSession.getTransaction().rollback();
            }
            return false;
        } finally {
            traceDaoImpl.closeCurrentSession();
        }
        return true;
    }

    private static final Log log = LogFactory.getLog(EdocTraceService.class);
}
