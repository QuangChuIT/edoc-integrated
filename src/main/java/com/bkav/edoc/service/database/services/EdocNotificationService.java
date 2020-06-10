package com.bkav.edoc.service.database.services;

import com.bkav.edoc.service.database.daoimpl.*;
import com.bkav.edoc.service.database.entity.EdocDocument;
import com.bkav.edoc.service.database.entity.EdocNotification;
import com.bkav.edoc.service.entity.edxml.MessageHeader;
import com.bkav.edoc.service.entity.edxml.To;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EdocNotificationService {
    private EdocDocumentDaoImpl documentDaoImpl = new EdocDocumentDaoImpl();
    private EdocNotificationDaoImpl notificationDaoImpl = new EdocNotificationDaoImpl();

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "dd/MM/yyyy");

    /**
     * Add notifications
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
            }
            for (To to : tos) {
                EdocNotification notification = new EdocNotification();
                notification.setCreateDate(new Date());
                notification.setDueDate(dueDate);
                notification.setReceiverId(to.getOrganId());
                notification.setDocument(document);
                notificationDaoImpl.persist(notification);
            }

            currentSession.getTransaction().commit();
        } catch (Exception e) {
            log.error(e);
            if(currentSession != null) {
                currentSession.getTransaction().rollback();
            }
            return false;
        } finally {
            notificationDaoImpl.closeCurrentSession();
        }
        return true;
    }

    private static final Log log = LogFactory.getLog(EdocNotificationService.class);
}