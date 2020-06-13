/**
 * 
 */
package com.bkav.edoc.service.mineutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.bkav.edoc.service.database.entity.*;
import com.bkav.edoc.service.database.services.EdocDynamicContactService;
import com.bkav.edoc.service.entity.edxml.*;
import com.bkav.edoc.service.resource.StringPool;
import com.bkav.edoc.service.util.AttachmentGlobalUtil;

public class Mapper {

	private static EdocDynamicContactService dynamicContactService = new EdocDynamicContactService();

	public Mapper() {

	}

	public MessageHeader modelToMessageHeader(EdocDocument document, EdocDocumentDetail detail, Set<EdocNotification> notifications) {
		MessageHeader messageHeader = new MessageHeader();

		From from = new From();
		from.setOrganId(document.getFromOrganDomain());
//		// TODO: Can lay thong tin don vi roi insert vao
		EdocDynamicContact dynamicContact = dynamicContactService.getDynamicContactByDomain(document.getFromOrganDomain());
		if (dynamicContact == null) {
			from.setOrganName(StringPool.DEFAULT_STRING);
			from.setOrganAdd(StringPool.DEFAULT_STRING);
			from.setEmail(StringPool.DEFAULT_STRING);
			from.setTelephone(StringPool.DEFAULT_STRING);
			from.setFax(StringPool.DEFAULT_STRING);
			from.setWebsite(StringPool.DEFAULT_STRING);
		} else {
			from.setOrganName(dynamicContact.getName());
			from.setOrganAdd(dynamicContact.getAddress());
			from.setEmail(dynamicContact.getEmail());
			from.setTelephone(dynamicContact.getTelephone());
			from.setFax(dynamicContact.getFax());
			from.setWebsite(dynamicContact.getWebsite());
		}
		messageHeader.setFrom(from);

		String[] toStr = document.getToOrganDomain().split("#");
		List<To> tos = new ArrayList<To>();
//		messageHeader.setDueDateForOrgans(processNotifications(notifications));
		for (String item : toStr) {
			To to = new To();
			to.setOrganId(item);
//			info = globalUtil.getDynamicContactById(item);
//			if (info == null) {
				to.setOrganName(StringPool.DEFAULT_STRING);
				to.setOrganAdd(StringPool.DEFAULT_STRING);
				to.setEmail(StringPool.DEFAULT_STRING);
				to.setTelephone(StringPool.DEFAULT_STRING);
				to.setFax(StringPool.DEFAULT_STRING);
				to.setWebsite(StringPool.DEFAULT_STRING);
//			} else {
//				to.setOrganName(info.getName());
//				to.setOrganAdd(info.getAddress());
//				to.setEmail(info.getEmail());
//				to.setTelephone(info.getTelephone());
//				to.setFax(info.getFax());
//				to.setWebsite(info.getWebsite());
//			}

			tos.add(to);
		}
		messageHeader.setTo(tos);

		messageHeader.setDocumentId(String.valueOf(document.getDocumentId()));

		Code code = new Code();
		code.setCodeNotation(document.getCodeNotation().isEmpty() ? StringPool.DEFAULT_STRING
				: document.getCodeNotation());
		code.setCodeNumber(document.getCodeNumber().isEmpty() ? StringPool.DEFAULT_STRING
				: document.getCodeNumber());
		messageHeader.setCode(code);

		PromulgationInfo proInfo = new PromulgationInfo();
		proInfo.setPlace(document.getPromulgationPlace().isEmpty() ? StringPool.DEFAULT_STRING
				: document.getPromulgationPlace());

		String dateStr = dateFormat.format(document.getPromulgationDate());
		proInfo.setPromulgationDate(dateStr);
		messageHeader.setPromulgationInfo(proInfo);

		DocumentType type = new DocumentType();
		type.setType(document.getDocumentType().ordinal());
		type.setTypeName(document.getDocumentTypeName());
		messageHeader.setDocumentType(type);

		messageHeader.setSubject(document.getSubject().isEmpty() ? StringPool.DEFAULT_STRING
						: document.getSubject());
		messageHeader.setContent(detail.getContent().isEmpty() ? StringPool.DEFAULT_STRING
						: detail.getContent());

//		Author author = new Author();
//		author.setCompetence(detail.getAuthorCompetence().isEmpty() ? StringPool.DEFAULT_STRING
//				: detail.getAuthorCompetence());
//		author.setFullName(detail.getAuthorFullname().isEmpty() ? StringPool.DEFAULT_STRING
//				: detail.getAuthorFullname());
//		author.setFunction(detail.getAuthorFunction().isEmpty() ? StringPool.DEFAULT_STRING
//				: detail.getAuthorFunction());
//		messageHeader.setAuthor(author);

		messageHeader.setDueDate(dateFormat.format(detail
				.getDueDate()));

		String[] placeStrs = detail.getToPlaces().split("#");
		List<String> places = new ArrayList<String>();
		if (placeStrs == null
				|| (placeStrs.length == 1 && placeStrs[0].isEmpty())) {
			places.add(StringPool.DEFAULT_STRING);
		} else {
			for (String item : placeStrs) {
				places.add(item);
			}
		}
		ToPlaces toPlaces = new ToPlaces();
		toPlaces.setPlace(places);
		messageHeader.setToPlaces(toPlaces);

//		String[] appendixStrs = detail.getAppendixes().split("#");
//		List<String> appendixs = new ArrayList<String>();
//		if (appendixStrs == null
//				|| (appendixStrs.length == 1 && appendixStrs[0].isEmpty())) {
//			places.add(StringPool.DEFAULT_STRING);
//		} else {
//			for (String item : appendixStrs) {
//				appendixs.add(item);
//			}
//		}
//		Appendixes appendixes = new Appendixes();
//		appendixes.setAppendix(appendixs);
//		messageHeader.setAppendixes(appendixes);

		OtherInfo other = new OtherInfo();
		other.setTyperNotation(detail.getTyperNotation().isEmpty() ? StringPool.DEFAULT_STRING
				: detail.getTyperNotation());
		if(detail.getPageAmount() != null) {
			other.setPageAmount(detail.getPageAmount().intValue());
		}
		if(detail.getPromulgationAmount() != null) {
			other.setPromulgationAmount(detail.getPromulgationAmount().intValue());
		}
		if(document.getPriority() != null) {
			other.setPriority(document.getPriority().getPriorityId().intValue());
		}
		other.setSphereOfPromulgation(detail.getSphereOfPromulgation()
				.isEmpty() ? StringPool.DEFAULT_STRING : detail
				.getSphereOfPromulgation());
		messageHeader.setOtherInfo(other);

		return messageHeader;

	}

	public Attachment attachmentModelToServiceEntity(EdocAttachment attachment) throws IOException {

		Attachment attachmentEntity = new Attachment();

		attachmentEntity.setName(attachment.getName());

		String rootPath = _attGlobal.getAttachmentPath();

		StringBuilder filePath = new StringBuilder();


		// Giai nen file dinh kem - Neu chua nen se tra ve duong dan hien
		// tai, neu da nen se giai nen va tra ve dia chi tuyet doi cua file
		// da duoc giai nen
		File file = archiveUtil.decompressFile(attachment);

		attachmentEntity.setValue(new FileInputStream(file));

		String contentTranfer = "base64";

		attachmentEntity.setContentTransfer(contentTranfer);

		attachmentEntity.setContentType(attachment.getType());

		return attachmentEntity;
	}

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"dd/MM/yyyy");
	private static final AttachmentGlobalUtil _attGlobal = new AttachmentGlobalUtil();
	private static final ArchiveUtil archiveUtil = new ArchiveUtil();
}
