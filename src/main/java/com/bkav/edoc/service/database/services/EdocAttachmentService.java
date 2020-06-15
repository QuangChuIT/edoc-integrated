package com.bkav.edoc.service.database.services;

import com.bkav.edoc.service.database.daoimpl.*;
import com.bkav.edoc.service.database.entity.EdocAttachment;
import com.bkav.edoc.service.database.entity.EdocDocument;
import com.bkav.edoc.service.entity.edxml.Attachment;
import com.bkav.edoc.service.entity.edxml.MessageHeader;
import com.bkav.edoc.service.entity.edxml.To;
import com.bkav.edoc.service.mineutil.Mapper;
import com.bkav.edoc.service.util.AttachmentGlobalUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class EdocAttachmentService {
    private EdocDocumentDaoImpl documentDaoImpl = new EdocDocumentDaoImpl();
    private EdocAttachmentDaoImpl attachmentDaoImpl = new EdocAttachmentDaoImpl();

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "dd/MM/yyyy");

    private Mapper mapper = new Mapper();

    /**
     * add attachment
     *
     * @param docId
     * @param messageHeader
     * @param attachment
     * @param fileSize
     * @param path
     * @return
     * @throws Exception
     */
    public long addAttachment(long docId, MessageHeader messageHeader, Attachment attachment, long fileSize, String path) throws Exception {
        Session currentSession = attachmentDaoImpl.openCurrentSession();
        long attachmentId = 0L;
        try {
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

            attachmentId = edocAttachment.getAttachmentId();
        } catch (Exception e) {
            log.error(e);
            if (currentSession != null) {
                currentSession.getTransaction().rollback();
            }
        } finally {
            attachmentDaoImpl.closeCurrentSession();
        }
        return attachmentId;
    }

    /**
     * get list edoc attachment
     * @param docId
     * @return
     */
    public  List<EdocAttachment> getEdocAttachmentsByDocId(long docId) {
        attachmentDaoImpl.openCurrentSession();

        List<EdocAttachment> attachments = attachmentDaoImpl.getAttachmentsByDocumentId(docId);

        attachmentDaoImpl.closeCurrentSession();
        return attachments;
    }

    /**
     * get list attachment
     * @param documentId
     * @return
     * @throws IOException
     */
    public List<Attachment> getAttachmentsByDocumentId(long documentId) throws IOException {
        attachmentDaoImpl.openCurrentSession();

        List<EdocAttachment> attachments = attachmentDaoImpl.getAttachmentsByDocumentId(documentId);
        ListIterator<EdocAttachment> iterator = attachments.listIterator();

        List<Attachment> attachmentEntities = new ArrayList<>();

        while (iterator.hasNext()) {

            EdocAttachment attachment = iterator.next();

            Attachment result = mapper.attachmentModelToServiceEntity(attachment);

            attachmentEntities.add(result);

        }

        attachmentDaoImpl.closeCurrentSession();
        return attachmentEntities;
    }

    /**
     * get to organ domain from list tos
     *
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
