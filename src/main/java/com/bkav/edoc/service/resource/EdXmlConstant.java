/**
 * FirstName LastName - Feb 5, 2015
 */
package com.bkav.edoc.service.resource;

import org.jdom2.Namespace;

public class EdXmlConstant {
    public static final String EDXML_PREFIX = "edXML";
    public static final Namespace EDXML_NS = Namespace.getNamespace("http://www.e-doc.vn/Schema/");
    public static final Namespace XLINK_NS = Namespace.getNamespace("http://www.w3.org/1999/xlink");

    public static final String SOAP_URI = "http://schemas.xmlsoap.org/soap/envelope/";

    public static final String BODY_TAG = "Body";
    public static final String MANIFEST_TAG = "Manifest";
    public static final String REFERENCE_TAG = "Reference";
    public static final String HREF_ATTR = "href";
    public static final String ATTACHMENT_NAME_TAG = "AttachmentName";
    public static final String GET_PENDING_DOCUMENT = "GetPendingDocumentIdsRequest";
    public static final String GET_DOCUMENT = "GetDocumentRequest";
    public static final String GET_TRACE = "GetTraces";
    public static final String CONFIRM_RECEIVED_REQUEST = "ConfirmReceived";
}
