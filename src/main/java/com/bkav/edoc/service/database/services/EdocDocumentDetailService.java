package com.bkav.edoc.service.database.services;

import com.bkav.edoc.service.database.daoimpl.*;
import com.bkav.edoc.service.database.entity.EdocDocument;
import com.bkav.edoc.service.database.entity.EdocDocumentDetail;
import com.bkav.edoc.service.entity.edxml.MessageHeader;
import com.bkav.edoc.service.entity.edxml.ToPlaces;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EdocDocumentDetailService {
    private final EdocDocumentDaoImpl documentDaoImpl = new EdocDocumentDaoImpl();
    private final EdocDocumentDetailDaoImpl documentDetailDaoImpl = new EdocDocumentDetailDaoImpl();

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "dd/MM/yyyy");

    /**
     * Add document detail
     *
     * @param messageHeader
     * @param docId
     * @return
     */
    public boolean addDocumentDetail(MessageHeader messageHeader, long docId) {
        Session currentSession = documentDetailDaoImpl.openCurrentSession();
        try {
            currentSession.beginTransaction();

            // get info of document detail
            String content = messageHeader.getContent();
            String signerCompetence = messageHeader.getSignerInfo().getCompetence();
            String signerPosition = messageHeader.getSignerInfo().getPosition();
            Date dueDate = null;
            try {
                dueDate = dateFormat.parse(messageHeader.getDueDate());
            } catch (ParseException e) {
                log.error("Error when add document detail " + e.getMessage());
            }

            StringBuilder toPlacesBuffer = new StringBuilder();
            ToPlaces toPlaces = messageHeader.getToPlaces();
            for (int i = 0; i < toPlaces.getPlace().size(); i++) {
                toPlacesBuffer.append(toPlaces.getPlace().get(i));
                toPlacesBuffer.append("#");
            }

            String sphereOfPromulgation = messageHeader.getOtherInfo()
                    .getSphereOfPromulgation();
            String typerNotation = messageHeader.getOtherInfo().getTyperNotation();
            long pageAmount = messageHeader.getOtherInfo().getPageAmount();
            long promulgationAmount = messageHeader.getOtherInfo().getPromulgationAmount();

            int steeringTypeInt = messageHeader.getSteeringType();
            EdocDocumentDetail.SteeringType steeringType = EdocDocumentDetail.SteeringType.values()[steeringTypeInt];

            // create document detail
            EdocDocumentDetail documentDetail = new EdocDocumentDetail();
            documentDetail.setContent(content);
            documentDetail.setSignerCompetence(signerCompetence);
            documentDetail.setSignerPosition(signerPosition);
            documentDetail.setDueDate(dueDate);
            documentDetail.setToPlaces(toPlacesBuffer.toString());
            documentDetail.setSphereOfPromulgation(sphereOfPromulgation);
            documentDetail.setTyperNotation(typerNotation);
            documentDetail.setPageAmount(pageAmount);
            documentDetail.setPromulgationAmount(promulgationAmount);
            documentDetail.setSteeringType(steeringType);
            documentDaoImpl.setCurrentSession(currentSession);
            EdocDocument document = documentDaoImpl.findById(docId);
            documentDetail.setDocument(document);

            documentDetailDaoImpl.persist(documentDetail);
            currentSession.getTransaction().commit();
        } catch (Exception e) {
            log.error(e);
            if (currentSession != null) {
                currentSession.getTransaction().rollback();
            }
            return false;
        } finally {
            documentDetailDaoImpl.closeCurrentSession();
        }
        return true;
    }

    private static final Log log = LogFactory.getLog(EdocDocumentDetailService.class);
}
