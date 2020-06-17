package com.bkav.edoc.service.database.services;

import com.bkav.edoc.service.database.daoimpl.EdocDocumentDaoImpl;
import com.bkav.edoc.service.database.daoimpl.EdocTraceDaoImpl;
import com.bkav.edoc.service.database.entity.EdocDocument;
import com.bkav.edoc.service.database.entity.EdocTrace;
import com.bkav.edoc.service.entity.edxml.Status;
import com.bkav.edoc.service.redis.RedisKey;
import com.bkav.edoc.service.redis.RedisUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        String organInCharge = status.getFrom().getOrganInCharge();
        String toOrganDomain = status.getResponseFor().getOrganId();
        String code = status.getResponseFor().getCode();
        String documentId = status.getResponseFor().getDocumentId();
        String promulgationDateStr = status.getResponseFor().getPromulgationDate();
        Date promulgationDate = null;
        try {
            if (promulgationDateStr != null && !promulgationDateStr.isEmpty()) {
                promulgationDateStr = promulgationDateStr.replaceAll("-", "/");
                promulgationDate = dateFormat.parse(promulgationDateStr);
            }
        } catch (ParseException e) {
            log.error(e);
        }

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
                timestampStr = timestampStr.replaceAll("-", "/");
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

        // test
//        toOrganDomain = "000.00.00.G15";
//        code = "94/BC-SKHĐT";

        Session currentSession = traceDaoImpl.openCurrentSession();
        // search document by from organ domain and code
        documentDaoImpl.setCurrentSession(currentSession);
        EdocDocument edocDocument = documentDaoImpl.searchDocumentByOrganDomainAndCode(toOrganDomain, code);
        if(edocDocument == null) {
            return false;
        }

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
        edocTrace.setOrganizationInCharge(organInCharge);
        edocTrace.setDocument(edocDocument);
        edocTrace.setEdxmlDocumentId(documentId);
        edocTrace.setEnable(true);

        // insert trace to db
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

    private void saveEdocTraceCache(EdocTrace trace, String responseForOrganId) {
        // TODO: Cache
        List obj = RedisUtil.getInstance().get(RedisKey.getKey(responseForOrganId, RedisKey.GET_TRACE_KEY), List.class);
        // if data in cache not exist, create new
        if (obj == null) {
            List<EdocTrace> edocTraces = new ArrayList<EdocTrace>();
            edocTraces.add(trace);
            RedisUtil.getInstance().set(RedisKey.getKey(responseForOrganId, RedisKey.GET_TRACE_KEY), edocTraces);
        } else {
            // add edoc trace to old list in cache
            List<EdocTrace> oldEdocTraces = null;
            if (obj instanceof List) {
                oldEdocTraces = (List<EdocTrace>) obj;
            } else {
                oldEdocTraces = new ArrayList<EdocTrace>();
            }

            oldEdocTraces.add(trace);
            RedisUtil.getInstance().set(RedisKey.getKey(responseForOrganId, RedisKey.GET_TRACE_KEY), oldEdocTraces);
        }
    }

    public  List<EdocTrace> getEdocTracesByOrganId(String responseForOrganId) {
        traceDaoImpl.openCurrentSession();

        List<EdocTrace> traces = traceDaoImpl.getEdocTracesByOrganId(responseForOrganId);

        traceDaoImpl.closeCurrentSession();
        return traces;
    }

    /**
     * disable traces after get traces
     * @param traces
     */
    public void disableEdocTrace(List<EdocTrace> traces) {
        Session currentSession = traceDaoImpl.openCurrentSession();
        try {
            currentSession.beginTransaction();

            for (EdocTrace trace: traces) {
                traceDaoImpl.disableEdocTrace(trace.getTraceId());
            }

            currentSession.getTransaction().commit();
        } catch (Exception e) {
            log.error(e);
            if(currentSession != null) {
                currentSession.getTransaction().rollback();
            }
        } finally {
            traceDaoImpl.closeCurrentSession();
        }
    }

    private static final Log log = LogFactory.getLog(EdocTraceService.class);
}
