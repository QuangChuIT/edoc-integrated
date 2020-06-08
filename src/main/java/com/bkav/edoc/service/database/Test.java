package com.bkav.edoc.service.database;

import com.bkav.edoc.service.database.entity.EdocDocument;
import com.bkav.edoc.service.database.services.EdocDocumentService;

import java.util.List;

public class Test {
    public static void main(String[] args) {
        try {
            EdocDocumentService service = new EdocDocumentService();
            List<EdocDocument> list = service.findAll();
            System.out.println(list.size());
//            System.out.println("Maven + Hibernate + MySQL");
//            Session session = HibernateUtil.getSessionFactory().openSession();
//            session.beginTransaction();
//
//            EdocDocument document = new EdocDocument();
//            document.setCodeNotation("1234");
//            document.setDocumentType(EdocDocument.DocumentType.LEGAL);
//
//            EdocDocumentDetail documentDetail = new EdocDocumentDetail();
//            documentDetail.setContent("abcd");
//            document.setDocumentDetail(documentDetail);
//            documentDetail.setDocument(document);
//
//            EdocTraceHeaderList traceHeaderList = new EdocTraceHeaderList();
//            traceHeaderList.setEmail("abcd@gmail.com");
//            document.setTraceHeaderList(traceHeaderList);
//            traceHeaderList.setDocument(document);
//
//            EdocPriority priority = new EdocPriority();
//            priority.setValue("1");
//            session.save(priority);
//            document.setPriority(priority);
//            session.save(document);
//
//            EdocNotification notification = new EdocNotification();
//            notification.setSendNumber(123456789);
//            notification.setCreateDate(new Date());
//            notification.setDocument(document);
//            document.getNotifications().add(notification);
//            session.save(notification);
//
//            EdocTrace trace = new EdocTrace();
//            trace.setComment("comment");
//            trace.setDocument(document);
//            document.getTraces().add(trace);
//            session.save(trace);
//
//            EdocAttachment attachment = new EdocAttachment();
//            attachment.setName("file");
//            attachment.setDocument(document);
//            document.getAttachments().add(attachment);
//            session.save(attachment);
//
//            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
