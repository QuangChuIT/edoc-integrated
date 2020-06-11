package com.bkav.edoc.service.database.services;

import com.bkav.edoc.service.database.daoimpl.*;
import com.bkav.edoc.service.database.entity.EdocAttachment;
import com.bkav.edoc.service.database.entity.EdocDocument;
import com.bkav.edoc.service.entity.edxml.Attachment;
import com.bkav.edoc.service.entity.edxml.MessageHeader;
import com.bkav.edoc.service.entity.edxml.To;
import com.bkav.edoc.service.util.AttachmentGlobalUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EdocAttachmentService {
    private EdocDocumentDaoImpl documentDaoImpl = new EdocDocumentDaoImpl();
    private EdocAttachmentDaoImpl attachmentDaoImpl = new EdocAttachmentDaoImpl();

    private String SEPERATOR = File.separator;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "dd/MM/yyyy");

    /**
     * Add attachments
     * @param messageHeader
     * @param attachments
     * @param domain
     * @param docId
     * @return
     * @throws Exception
     */
    public boolean addAttachments(MessageHeader messageHeader, List<Attachment> attachments, String domain, Long docId) throws Exception {
        Session currentSession = attachmentDaoImpl.openCurrentSession();
        try {
            currentSession.beginTransaction();

            // Insert vao bang Attachment
            AttachmentGlobalUtil attUtil = new AttachmentGlobalUtil();
            String rootPath = attUtil.getAttachmentPath();
            long totalSize = 0;
            Calendar cal = Calendar.getInstance();
            for (int i = 0; i < attachments.size(); i++) {
                Attachment attachment = attachments.get(i);
                String dataPath = new StringBuilder(domain).append(SEPERATOR)
                        .append(cal.get(Calendar.YEAR)).append(SEPERATOR)
                        .append(cal.get(Calendar.MONTH) + 1).append(SEPERATOR)
                        .append(cal.get(Calendar.DAY_OF_MONTH)).append(SEPERATOR)
                        .append(docId).append("_").append(i + 1).toString();

                String specPath = new StringBuilder(rootPath)
                        .append((rootPath.endsWith(SEPERATOR) ? "" : SEPERATOR))
                        .append(dataPath).toString();

                long size = 0L;
                InputStream is = attachment.getValue();

                size = attUtil.saveToFile(specPath, is);

                is.close();
                totalSize += size;
                if (size < 0) {
                    System.out.println("Save attachment failed!");
                    return false;
                }

                this.addAttachment(docId, messageHeader, attachment, size, dataPath);
            }

            currentSession.getTransaction().commit();
        } catch (Exception e) {
            log.error(e);
            if(currentSession != null) {
                currentSession.getTransaction().rollback();
            }
        } finally {
            attachmentDaoImpl.closeCurrentSession();
        }
        return true;
    }

    /**
     * add attachment
     * @param docId
     * @param messageHeader
     * @param attachment
     * @param fileSize
     * @param path
     * @return
     * @throws Exception
     */
    private void addAttachment(long docId, MessageHeader messageHeader, Attachment attachment, long fileSize, String path) throws Exception {
        String name = attachment.getName();
        String type = attachment.getContentType();
        String organDomain = messageHeader.getFrom().getOrganId();
        String toOrganDomain = getToOrganDomain(messageHeader.getTo());

        EdocAttachment edocAttachment = new EdocAttachment();
        edocAttachment.setOrganDomain(organDomain);
        edocAttachment.setName(name);
        edocAttachment.setType(type);
        edocAttachment.setToOrganDomain(toOrganDomain);
        edocAttachment.setCreateDate(new Date());
        edocAttachment.setFullPath(path);
        edocAttachment.setSize(String.valueOf(fileSize));
        documentDaoImpl.setCurrentSession(attachmentDaoImpl.getCurrentSession());
        EdocDocument document = documentDaoImpl.findById(docId);
        edocAttachment.setDocument(document);

        attachmentDaoImpl.persist(edocAttachment);
    }

    /**
     * get to organ domain from list tos
     * @param tos
     * @return
     */
    private String getToOrganDomain(List<To> tos) {

        StringBuffer toOrganDomainBuffer = new StringBuffer();

        for (int i = 0; i < tos.size(); i++) {
            toOrganDomainBuffer.append(tos.get(i).getOrganId());

            if (tos.size() > 1 && i < tos.size() - 1) {
                toOrganDomainBuffer.append("#");
            }

        }
        return toOrganDomainBuffer.toString();
    }

    private static final Log log = LogFactory.getLog(EdocAttachmentService.class);
}
