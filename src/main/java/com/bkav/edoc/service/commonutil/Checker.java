package com.bkav.edoc.service.commonutil;

import com.bkav.edoc.service.entity.edxml.*;
import com.bkav.edoc.service.entity.edxml.Error;
import com.bkav.edoc.service.kernel.util.FileUtil;
import com.bkav.edoc.service.kernel.util.InternetAddressUtil;
import com.bkav.edoc.service.kernel.util.MimeTypesUtil;
import com.bkav.edoc.service.resource.StringPool;
import com.bkav.edoc.service.util.EdXMLConfigKey;
import com.bkav.edoc.service.util.EdXMLConfigUtil;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Checker {
    int level = 4;

    /**
     * @param messageHeader
     * @return
     * @throws Exception
     */
    public Report checkMessageHeader(MessageHeader messageHeader)
            throws Exception {

        List<Error> errorList = new ArrayList<>();

        boolean isSuccess = true;

        errorList.addAll(checkFrom(messageHeader.getFrom()));

        errorList.addAll(checkExistTo(messageHeader.getTo()));

        for (To to : messageHeader.getTo()) {
            errorList.addAll(checkTo(to));
        }

        errorList.addAll(checkDuplicateTo(messageHeader.getTo()));

        errorList.addAll(checkDocumentId(messageHeader.getDocumentId()));

        errorList.addAll(checkCode(messageHeader.getCode()));

        errorList.addAll(checkPromulgationInfo(messageHeader
                .getPromulgationInfo()));

        errorList.addAll(checkDocumentType(messageHeader.getDocumentType()));

        errorList.addAll(checkSubject(messageHeader.getSubject()));

        errorList.addAll(checkContent(messageHeader.getContent()));

        errorList.addAll(checkToPlace(messageHeader.getToPlaces()));

        errorList.addAll(checkOtherInfo(messageHeader.getOtherInfo()));


        if (errorList.size() > 0) {

            isSuccess = false;

        }

        return new Report(isSuccess, new ErrorList(errorList));
    }

    public Report checkTraceHeaderList(TraceHeaderList traceList) {
        List<Error> errorList = new ArrayList<Error>();
        String organErrorCode = "TraceHeaderList.TraceHeader.OrganId";
        String commentErrorCode = "TraceHeaderList.TraceHeader.Comment";
        String timeStampErrorCode = "TraceHeaderList.TraceHeader.TimeStamp";
        String executorErrorCode = "TraceHeaderList.TraceHeader.Executor";

        for (TraceHeader item : traceList.getTraceHeaders()) {
            // Check Domain
            if (item.getOrganId().length() == 0) {
                errorList.add(new Error(String.format("N.%s", organErrorCode),
                        "OrganId is required."));
            }
            if (!checkLength(item.getOrganId(), level)) {

                errorList.add(new Error(String.format("R.%s", organErrorCode),
                        "OrganId is out of range."));

            }

            // Check TimeStamp
            Date timestamp = item.getTimeStamp();
            if (!checkTraceTimeStamp(timestamp)) {
                errorList.add(new Error("R." + timeStampErrorCode, "Timestamp is out of range."));
            }
        }
        boolean isSuccess = errorList.size() <= 0;
        return new Report(isSuccess, new ErrorList(errorList));
    }

    private boolean checkTraceTimeStamp(Date clientTime) {
        return true;
    }

    /**
     * @param number
     * @return
     */
    public Report checkNumber(int number) {

        List<Error> errorList = new ArrayList<Error>();

        boolean isSuccess = true;

        if (number < 0) {

            isSuccess = false;

            errorList.add(new Error("M.NUMBER", "Number is greate of 0."));
        }

        return new Report(isSuccess, new ErrorList(errorList));
    }

    /**
     * @param from
     * @return
     * @throws Exception
     */
    private List<Error> checkFrom(From from) throws Exception {

        List<Error> errorList = new ArrayList<Error>();

        errorList.addAll(checkOrganId(from.getOrganId(), true));

        errorList.addAll(checkOrganName(from.getOrganName(), true));

        errorList.addAll(checkOrganizationInCharge(from.getOrganInCharge()));

        errorList.addAll(checkOrganAdd(from.getOrganAdd(), true));

        errorList.addAll(checkEmail(from.getEmail(), true));

        errorList.addAll(checkTelephone(from.getTelephone(), true));

        errorList.addAll(checkFax(from.getFax(), true));

        errorList.addAll(checkWebsite(from.getWebsite(), true));

        return errorList;
    }

    /**
     * @param tos
     * @return
     * @throws Exception
     */
    private List<Error> checkDuplicateTo(List<To> tos) throws Exception {

        List<Error> errorList = new ArrayList<Error>();

        if (tos != null) {
            Map<String, To> map = new HashMap<>();
            for (To to : tos) {
                if (!map.containsKey(to.getOrganId())) {
                    map.put(to.getOrganId(), to);
                } else {
                    errorList.add(new Error("N.MessageHeader.To.OrganId",
                            "To OrganId is duplicate."));
                    break;
                }

            }

        }
        return errorList;
    }

    /**
     * @param to
     * @return
     * @throws Exception
     */
    private List<Error> checkTo(To to) throws Exception {

        List<Error> errorList = new ArrayList<Error>();

        errorList.addAll(checkOrganId(to.getOrganId(), false));

        errorList.addAll(checkOrganName(to.getOrganName(), false));

        errorList.addAll(checkOrganAdd(to.getOrganAdd(), false));

        errorList.addAll(checkEmail(to.getEmail(), false));

        errorList.addAll(checkTelephone(to.getTelephone(), false));

        errorList.addAll(checkFax(to.getFax(), false));

        errorList.addAll(checkWebsite(to.getWebsite(), false));

        return errorList;
    }

    /**
     * @param tos
     * @return
     * @throws Exception
     */
    private List<Error> checkExistTo(List<To> tos) throws Exception {
        final String errorCode = "N.MessageHeader.To";
        List<Error> errorList = new ArrayList<Error>();
        String error = "noi-nhan-van-ban-khong-duoc-de-trong";
        if (tos == null) {
            errorList.add(new Error(errorCode, error));
        } else {
            if (tos.isEmpty()) {

                errorList.add(new Error(errorCode, error));
            }
        }

        return errorList;
    }

    /**
     * @param attachments
     * @return
     */
    public Report checkAllowAttachment(List<Attachment> attachments) {

        List<Error> errorList = new ArrayList<Error>();

        boolean isSuccess = true;

        for (Attachment attachment : attachments) {
            if (!checkLength(attachment.getName(), 200)) {

                isSuccess = false;

                errorList.add(new Error("R.AttachmentName",
                        "Attachment Name is out of range."));
            }

            String encoding = EdXMLConfigUtil
                    .getValueByKey(EdXMLConfigKey.ATTACHMENT_ENCODING_TYPE_ALLOW);

            if (!encoding.equals("*") || encoding.length() > 0) {

                String attachmentEncoding = attachment.getContentTransfer();

                int index = encoding.indexOf(attachmentEncoding + ";");

                if (index == -1) {

                    isSuccess = false;

                    errorList
                            .add(new Error("R.Attachment-Encoding",
                                    "Attachment content transfer encoding not support."));
                }

            }

            String contentType = EdXMLConfigUtil
                    .getValueByKey(EdXMLConfigKey.ATTACHMENT_TYPE_ALLOW);

            if (!contentType.equals("*") || contentType.length() > 0) {

                /*
                 * boolean result = attachment.getContentType().toLowerCase()
                 * .matches(encoding);
                 */

                String attachmentType = attachment.getContentType();

                int index = contentType.indexOf(attachmentType + ";");

                if (index == -1) {

                    isSuccess = false;

                    errorList.add(new Error("R.Attachment-Type",
                            "Attachment content type not support."));
                }
            }

            if (!isSuccess) {

                break;

            }

        }

        return new Report(isSuccess, new ErrorList(errorList));
    }

    /**
     * @param code
     * @return
     */
    private List<Error> checkCode(Code code) {

        List<Error> errorList = new ArrayList<Error>();

        errorList.addAll(checkCodeNumber(code.getCodeNumber()));

        errorList.addAll(checkCodeNotation(code.getCodeNotation()));

        return errorList;
    }

    /**
     * @param proInfo
     * @return
     */
    private List<Error> checkPromulgationInfo(PromulgationInfo proInfo) {

        List<Error> errorList = new ArrayList<Error>();

        errorList.addAll(checkProPlace(proInfo.getPlace()));

        errorList.addAll(checkProDate(proInfo.getPromulgationDate()));

        return errorList;
    }

    /**
     * @param author
     * @return
     */
    private List<Error> checkAuthor(Author author) {

        List<Error> errorList = new ArrayList<Error>();

        errorList.addAll(checkAuthorCompetence(author.getCompetence()));

        errorList.addAll(checkAuthorFunction(author.getFunction()));

        errorList.addAll(checkAuthorFullName(author.getFullName()));

        return errorList;
    }

    /**
     * @param otherInfo
     * @return
     */
    private List<Error> checkOtherInfo(OtherInfo otherInfo) {

        List<Error> errorList = new ArrayList<Error>();

        errorList.addAll(checkSphereOfPromulgation(otherInfo
                .getSphereOfPromulgation()));

        errorList.addAll(checkPriority(otherInfo.getPriority()));

        errorList.addAll(checkTyperNotation(otherInfo.getTyperNotation()));

        errorList.addAll(checkPromulgationAmount(otherInfo
                .getPromulgationAmount()));

        errorList.addAll(checkPageAmount(otherInfo.getPageAmount()));

        return errorList;
    }

    /**
     * @param places
     * @return
     */
    private List<Error> checkToPlace(ToPlaces places) {

        List<Error> errorList = new ArrayList<Error>();

        for (String place : places.getPlace()) {
            if (!checkLength(place, 150)) {

                errorList.add(new Error("R.MessageHeader.ToPlace.Place",
                        "Place is out of range."));
            }
        }
        return errorList;
    }

    /**
     * @param subject
     * @return
     */
    private List<Error> checkSubject(String subject) {

        List<Error> errorList = new ArrayList<Error>();

        if (subject.isEmpty()) {

            errorList.add(new Error("N.MessageHeader.Subject",
                    "Subject is required."));
        }
        if (!checkLength(subject, 500)) {

            errorList.add(new Error("R.MessageHeader.Subject",
                    "Subject is out of range."));
        }
        return errorList;
    }

    /**
     * @param content
     * @return
     */
    private List<Error> checkContent(String content) {

        List<Error> errorList = new ArrayList<Error>();

        if (!checkLength(content, 500)) {

            errorList.add(new Error("R.MessageHeader.Content",
                    "Content is out of range."));

        }
        return errorList;
    }

    /**
     * @param appendixes
     * @return
     */
    private List<Error> checkAppendixes(Appendixes appendixes) {

        List<Error> errorList = new ArrayList<Error>();

        for (String appendix : appendixes.getAppendix()) {
            if (!checkLength(appendix, 250)) {

                errorList.add(new Error("R.MessageHeader.OtherInfo.Appendix",
                        "Appendix is out of range."));
            }
        }
        return errorList;
    }

    /**
     * @param responseDate
     * @return
     */
    private List<Error> checkResponseDate(String responseDate) {

        List<Error> errorList = new ArrayList<Error>();

        return errorList;
    }

    /**
     * @param documentId
     * @return
     */
    private List<Error> checkDocumentId(String documentId) {

        List<Error> errorList = new ArrayList<Error>();

        if (!checkLength(documentId, 15)) {

            errorList.add(new Error("R.DocumentId",
                    "DocumentId is out of range."));
        }
        return errorList;
    }

    /**
     * @param docType
     * @return
     */
    private List<Error> checkDocumentType(DocumentType docType) {

        List<Error> errorList = new ArrayList<Error>();

        errorList.addAll(checkDocType(docType.getType()));

        errorList.addAll(checkDocTypeName(docType.getTypeName()));

        return errorList;
    }

    /**
     * @param organId
     * @param isFrom
     * @return
     * @throws Exception
     */
    private List<Error> checkOrganId(String organId, boolean isFrom)
            throws Exception {
        String lastOfErrorCode = new StringBuilder("MessageHeader")
                .append(isFrom ? "From" : "To").append("OrganId").toString();
        List<Error> errorList = new ArrayList<Error>();

        if (organId.isEmpty()) {

            errorList.add(new Error(String.format("N.%s", lastOfErrorCode),
                    "OrganId is required."));

        }
        if (!checkLength(organId, level)) {

            errorList.add(new Error(String.format("R.%s", lastOfErrorCode),
                    "OrganId is out of range."));

        }

        return errorList;
    }

    /**
     * @param attachmentName
     * @param attachmentType
     * @param attachmentEncoding
     * @return
     */
    public Report checkAllowAttachment(String attachmentName,
                                       String attachmentType, String attachmentEncoding) {

        List<Error> errorList = new ArrayList<Error>();

        boolean isSuccess = true;

        if (!checkLength(attachmentName, 200)) {

            isSuccess = false;

            errorList.add(new Error("R.AttachmentName",
                    "Attachment Name is out of range."));
        }

        String encoding = EdXMLConfigUtil
                .getValueByKey(EdXMLConfigKey.ATTACHMENT_ENCODING_TYPE_ALLOW);

        if (!encoding.equals("*") && encoding.length() > 0) {

            int index = encoding.toUpperCase().indexOf(
                    attachmentEncoding.toUpperCase() + ",");

            if (index == -1) {

                isSuccess = false;

                errorList.add(new Error("M.Attachment-Encoding",
                        "Attachment content transfer encoding not support."));
            }

        }

        String contentType = EdXMLConfigUtil
                .getValueByKey(EdXMLConfigKey.ATTACHMENT_TYPE_ALLOW);

        if (!contentType.equals("*") && contentType.length() > 0) {

            String fileContentType = MimeTypesUtil
                    .getContentType(attachmentName);

            if (fileContentType.toLowerCase().equals(
                    attachmentType.toLowerCase())) {

                Set<String> attachmentExtension = MimeTypesUtil
                        .getExtensions(attachmentType);

                String fileExtension = "."
                        + FileUtil.getExtension(attachmentName);

                String contentTypeExtension = null;

                Iterator<String> iter = attachmentExtension.iterator();

                while (iter.hasNext()) {

                    String temp = iter.next();

                    if (temp.equals(fileExtension)) {
                        contentTypeExtension = temp;
                        break;
                    }

                }

                if (fileExtension == null || contentTypeExtension == null) {
                    errorList.add(new Error("M.Attachment-Type",
                            "Attachment content type not support."));
                } else {

                    int index = contentType.toLowerCase().indexOf(
                            contentTypeExtension.toLowerCase());

                    if (index == -1) {

                        isSuccess = false;

                        errorList.add(new Error("M.Attachment-Type",
                                "Attachment content type not support."));
                    }
                }
            } else {
                isSuccess = false;

                errorList.add(new Error("M.Attachment-Type",
                        "Attachment content type not support."));
            }

        }

        return new Report(isSuccess, new ErrorList(errorList));
    }

    /**
     * @param organName
     * @return
     */
    private List<Error> checkOrganizationInCharge(String organName) {
        String lastOfErrorCode = new StringBuilder("MessageHeader")
                .append("From").append("OrganizationInCharge").toString();
        List<Error> errorList = new ArrayList<Error>();

        if (!checkLength(organName, 200)) {

            errorList.add(new Error(String.format("R.%s", lastOfErrorCode),
                    "OrganizationInCharge is out of range."));
        }
        return errorList;
    }

    /**
     * @param organName
     * @param isFrom
     * @return
     */
    private List<Error> checkOrganName(String organName, boolean isFrom) {
        String lastOfErrorCode = new StringBuilder("MessageHeader")
                .append(isFrom ? "From" : "To").append("OrganName").toString();
        List<Error> errorList = new ArrayList<Error>();

        if (!checkLength(organName, 200)) {

            errorList.add(new Error(String.format("R.%s", lastOfErrorCode),
                    "OrganName is out of range."));
        }
        return errorList;
    }

    /**
     * @param organAdd
     * @param isFrom
     * @return
     */
    private List<Error> checkOrganAdd(String organAdd, boolean isFrom) {

        List<Error> errorList = new ArrayList<Error>();
        String lastOfErrorCode = new StringBuilder("MessageHeader")
                .append(isFrom ? "From" : "To").append("OrganAdd").toString();
        if (!checkLength(organAdd, 250)) {

            errorList.add(new Error(String.format("R.%", lastOfErrorCode),
                    "OrganAdd is out of range."));

        }
        return errorList;
    }

    /**
     * @param email
     * @param isFrom
     * @return
     */
    private List<Error> checkEmail(String email, boolean isFrom) {

        List<Error> errorList = new ArrayList<Error>();

        String lastOfErrorCode = new StringBuilder("MessageHeader")
                .append(isFrom ? "From" : "To").append("Email").toString();

        if (!email.isEmpty() && email.length() > 0) {
            if (!checkLength(email, 100)) {

                errorList.add(new Error(String.format("R.%s", lastOfErrorCode),
                        "Email is out of range."));
            }
            if (!InternetAddressUtil.isValid(email)) {

                errorList.add(new Error(String.format("M.%s", lastOfErrorCode),
                        "Email invalid."));
            }
        }
        return errorList;
    }

    /**
     * @param telephone
     * @param isFrom
     * @return
     */
    private List<Error> checkTelephone(String telephone, boolean isFrom) {

        List<Error> errorList = new ArrayList<Error>();

        String lastOfErrorCode = new StringBuilder("MessageHeader")
                .append(isFrom ? "From" : "To").append("Telephone").toString();

        if (!checkLength(telephone, 20)) {

            errorList.add(new Error(String.format("R.%s", lastOfErrorCode),
                    "Telephone is out of range."));
        }
        return errorList;
    }

    /**
     * @param fax
     * @param isFrom
     * @return
     */
    private List<Error> checkFax(String fax, boolean isFrom) {

        List<Error> errorList = new ArrayList<Error>();

        String lastOfErrorCode = new StringBuilder("MessageHeader")
                .append(isFrom ? "From" : "To").append("Fax").toString();

        if (!checkLength(fax, 20)) {

            errorList.add(new Error(String.format("R.%s", lastOfErrorCode),
                    "Fax is out of range."));
        }
        return errorList;
    }

    /**
     * @param website
     * @param isFrom
     * @return
     */
    private List<Error> checkWebsite(String website, boolean isFrom) {

        List<Error> errorList = new ArrayList<Error>();

        String lastOfErrorCode = new StringBuilder("MessageHeader")
                .append(isFrom ? "From" : "To").append("Website").toString();

        if (!checkLength(website, 20)) {

            errorList.add(new Error(String.format("R.%s", lastOfErrorCode),
                    "Website is out of range."));
        }
        if (website.matches(StringPool.URL_REGEX)) {

            errorList.add(new Error(String.format("M.%s", lastOfErrorCode),
                    "Website invalid."));
        }
        return errorList;
    }

    /**
     * @param codeNumber
     * @return
     */
    private List<Error> checkCodeNumber(String codeNumber) {

        List<Error> errorList = new ArrayList<Error>();

        String lastOfErrorCode = new StringBuilder("MessageHeader")
                .append("Code").append("CodeNumber").toString();

        if (codeNumber.isEmpty()) {

            errorList.add(new Error(String.format("N.%s", lastOfErrorCode),
                    "CodeNumber is required."));
        }
        if (!checkLength(codeNumber, 11)) {

            errorList.add(new Error(String.format("R.%s", lastOfErrorCode),
                    "CodeNumber is out of range."));
        }
        return errorList;
    }

    /**
     * @param codeNotation
     * @return
     */
    private List<Error> checkCodeNotation(String codeNotation) {

        List<Error> errorList = new ArrayList<Error>();
        String lastOfErrorCode = new StringBuilder("MessageHeader")
                .append("Code").append("CodeNotation").toString();
        if (codeNotation.isEmpty()) {

            errorList.add(new Error(String.format("N.%s", lastOfErrorCode),
                    "CodeNotation is required."));
        }
        if (!checkLength(codeNotation, 30)) {

            errorList.add(new Error(String.format("R.%s", lastOfErrorCode),
                    "CodeNotation is out of range."));
        }
        return errorList;
    }

    /**
     * @param place
     * @return
     */
    private List<Error> checkProPlace(String place) {

        List<Error> errorList = new ArrayList<Error>();

        if (!checkLength(place, 50)) {

            errorList.add(new Error("R.MessageHeader.Promulgation.Place",
                    "Place is out of range."));
        }
        return errorList;
    }

    /**
     * @param strDate
     * @return
     */
    private List<Error> checkProDate(String strDate) {

        List<Error> errorList = new ArrayList<Error>();

        String lastOfErrorCode = new StringBuilder("MessageHeader")
                .append("Code").append("PromulgationDate").toString();

        if (checkDate(strDate)) {
            int result = compareDate(strDate);

            if (result == ERROR_DATE_COMPARE) {
                errorList.add(new Error(String.format("T.%s", lastOfErrorCode),
                        "PromulgationDate is match type dd/MM/yyyy"));
            } else if (result > 0) {
                errorList.add(new Error(String.format("M.%s", lastOfErrorCode),
                        "PromulgationDate can't greater current date"));
            }
        } else {
            errorList.add(new Error("M.PROMULGATION_DATE",
                    "PromulgationDate is match type dd/MM/yyyy"));
        }

        return errorList;
    }

    /**
     * @param type
     * @return
     */
    private List<Error> checkDocType(int type) {

        List<Error> errorList = new ArrayList<Error>();

        if (type != 1 && type != 2) {
            errorList.add(new Error("M.MessageHeader.DocumentType.Type",
                    "Document Type value is only math '1' or '2' "));
        }

        return errorList;
    }

    /**
     * @param typeName
     * @return
     */
    private List<Error> checkDocTypeName(String typeName) {

        List<Error> errorList = new ArrayList<Error>();

        if (!checkLength(typeName, 100)) {

            errorList.add(new Error("R.MessageHeader.DocumentType.TypeName",
                    "TypeName is out of range."));

        }
        return errorList;
    }

    /**
     * @param competence
     * @return
     */
    private List<Error> checkAuthorCompetence(String competence) {

        List<Error> errorList = new ArrayList<Error>();

        if (!checkLength(competence, 100)) {

            errorList.add(new Error("R.MessageHeader.SignerInfo.Competence",
                    "Competence is out of range."));
        }
        return errorList;
    }

    /**
     * @param function
     * @return
     */
    private List<Error> checkAuthorFunction(String function) {

        List<Error> errorList = new ArrayList<Error>();

        if (!checkLength(function, 100)) {

            errorList.add(new Error("R.MessageHeader.SignerInfo.Function",
                    "Function is out of range."));
        }
        return errorList;
    }

    /**
     * @param fullName
     * @return
     */
    private List<Error> checkAuthorFullName(String fullName) {

        List<Error> errorList = new ArrayList<Error>();

        if (!checkLength(fullName, 50)) {

            errorList.add(new Error("R.MessageHeader.SignerInfo.FullName",
                    "FullName is out of range."));
        }
        return errorList;
    }

    /**
     * @param sphere
     * @return
     */
    private List<Error> checkSphereOfPromulgation(String sphere) {

        List<Error> errorList = new ArrayList<Error>();

        if (!checkLength(sphere, 100)) {

            errorList.add(new Error(
                    "R.MessageHeader.OtherInfo.SphereOfPromulgation",
                    "SphereOfPromulgation is out of range."));
        }
        return errorList;
    }

    /**
     * @param priority
     * @return
     */
    private List<Error> checkPriority(int priority) {

        List<Integer> allowValue = new ArrayList<Integer>(Arrays.asList(0, 1,
                2, 3, 4));

        List<Error> errorList = new ArrayList<Error>();

        if (!allowValue.contains(priority)) {
            errorList.add(new Error("M.MessageHeader.OtherInfo.Priority",
                    "Priority value is only math 0, 1, 2, 3, 4"));
        }

        return errorList;
    }

    /**
     * @param typerNotation
     * @return
     */
    private List<Error> checkTyperNotation(String typerNotation) {

        List<Error> errorList = new ArrayList<Error>();

        if (!checkLength(typerNotation, 10)) {

            errorList.add(new Error("R.MessageHeader.OtherInfo.TyperNotation",
                    "TyperNotation is out of range."));
        }
        return errorList;
    }

    /**
     * @param promulgationAmount
     * @return
     */
    private List<Error> checkPromulgationAmount(int promulgationAmount) {

        List<Error> errorList = new ArrayList<Error>();

        if (promulgationAmount < 0) {

            errorList.add(new Error(
                    "M.MessageHeader.OtherInfo.PromulgationAmount",
                    "Promulgation amount type of UnsignShort"));

        }

        return errorList;
    }

    /**
     * @param pageAmount
     * @return
     */
    private List<Error> checkPageAmount(int pageAmount) {

        List<Error> errorList = new ArrayList<Error>();
        if (pageAmount < 0) {

            errorList.add(new Error("M.MessageHeader.OtherInfo.PageAmount",
                    "Page amount type of UnsignShort"));

        }

        return errorList;
    }

    private boolean checkDate(String strDate) {

        try {
            dateFormat.parse(strDate);
            return true;
        } catch (ParseException e) {
            return false;
        }

    }

    /**
     * @param target
     * @param length
     * @return
     */
    public boolean checkLength(String target, int length) {

        if (target == null) {
            return false;
        }

        return !(target.split("\\.").length > length);
    }

    /**
     * @param strDate
     * @return
     */
    private int compareDate(String strDate) {
        try {
            Date resultDate = dateFormat.parse(strDate);

            Date now = Calendar.getInstance().getTime();

            return resultDate.compareTo(now);
        } catch (ParseException e) {
            return ERROR_DATE_COMPARE;
        }
    }

    public boolean checkAllowElement(String allowElements, String targetElement)
            throws SAXException, IOException {

        if (allowElements.indexOf(targetElement) == -1) {

            return false;

        }

        return true;
    }

    private static final Locale _LOCALE = new Locale("vi", "VN");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "dd/MM/yyyy");
    private static final int ERROR_DATE_COMPARE = -3;
}
