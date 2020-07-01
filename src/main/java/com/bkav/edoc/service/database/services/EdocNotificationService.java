package com.bkav.edoc.service.database.services;

import com.bkav.edoc.service.database.daoimpl.*;
import com.bkav.edoc.service.database.entity.EdocDocument;
import com.bkav.edoc.service.database.entity.EdocNotification;
import com.bkav.edoc.service.entity.edxml.MessageHeader;
import com.bkav.edoc.service.entity.edxml.To;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

import javax.management.Notification;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EdocNotificationService {
    private final EdocDocumentDaoImpl documentDaoImpl = new EdocDocumentDaoImpl();
    private final EdocNotificationDaoImpl notificationDaoImpl = new EdocNotificationDaoImpl();

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "dd/MM/yyyy");

    /**
     * Add notifications
     *
     * @param messageHeader
     * @param docId
     * @return
     */
    public boolean addNotifications(MessageHeader messageHeader, long docId) {
        Session currentSession = notificationDaoImpl.openCurrentSession();
        try {
            currentSession.beginTransaction();

            documentDaoImpl.setCurrentSession(currentSession);
            EdocDocument document = documentDaoImpl.findById(docId);
            // Insert Notification
            List<To> tos = messageHeader.getTo();
            Date dueDate = null;
            try {
                dueDate = dateFormat.parse(messageHeader.getDueDate());
            } catch (ParseException e) {
                log.error(e);
            }
            for (To to : tos) {
                EdocNotification notification = new EdocNotification();
                Date currentDate = new Date();
                notification.setCreateDate(currentDate);
                notification.setModifiedDate(currentDate);
                notification.setDueDate(dueDate);
                notification.setReceiverId(to.getOrganId());
                notification.setDocument(document);
                notification.setTaken(false);
                notificationDaoImpl.persist(notification);
            }

            currentSession.getTransaction().commit();
        } catch (Exception e) {
            log.error(e);
            if (currentSession != null) {
                currentSession.getTransaction().rollback();
            }
            return false;
        } finally {
            notificationDaoImpl.closeCurrentSession();
        }
        return true;
    }

    /**
     * get document id by domain
     *
     * @param organId
     * @return
     */
    public List<Long> getDocumentIdsByOrganId(String organId) {
        notificationDaoImpl.openCurrentSession();

        List<Long> notificationIds = notificationDaoImpl.getDocumentIdsByOrganId(organId);

        notificationDaoImpl.closeCurrentSession();
        return notificationIds;
    }

    /**
     * check allow of this domain with document
     *
     * @param documentId
     * @param organId
     * @return
     */
    public boolean checkAllowWithDocument(String documentId, String organId) {
        notificationDaoImpl.openCurrentSession();

        boolean checkAllow = notificationDaoImpl.checkAllowWithDocument(documentId, organId);

        notificationDaoImpl.closeCurrentSession();
        return checkAllow;
    }

    public void removePendingDocumentId(String domain, long documentId) {
        Session currentSession = notificationDaoImpl.openCurrentSession();
        try {
            currentSession.beginTransaction();
            notificationDaoImpl.setNotificationTaken(String.valueOf(documentId), domain);
            currentSession.getTransaction().commit();
        } catch (SQLException e) {
            log.error(e);
            if (currentSession != null) {
                currentSession.getTransaction().rollback();
            }
        } finally {
            notificationDaoImpl.closeCurrentSession();
        }
    }

    private static final Log log = LogFactory.getLog(EdocNotificationService.class);
}
