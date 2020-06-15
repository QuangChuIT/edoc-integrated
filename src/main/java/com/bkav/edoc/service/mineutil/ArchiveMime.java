/**
 *
 */
package com.bkav.edoc.service.mineutil;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.activation.DataHandler;

import com.bkav.edoc.service.entity.edxml.Envelope;
import com.bkav.edoc.service.entity.edxml.Manifest;
import com.bkav.edoc.service.entity.edxml.Reference;
import com.bkav.edoc.service.kernel.util.MimeTypesUtil;
import com.bkav.edoc.service.util.AttachmentGlobalUtil;
import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.attachments.ByteArrayDataSource;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.net.util.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import com.bkav.edoc.service.entity.edxml.Attachment;
import com.bkav.edoc.service.resource.StringPool;

public class ArchiveMime {

    public ArchiveMime() {
    }

    /**
     * create mime for document
     * @param envelope
     * @param attachmentsByEntity
     * @return
     * @throws Exception
     */
    public Map<String, Object> createMime(Document envelope,
                                          List<Attachment> attachmentsByEntity, Object objAttachment) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();

        Map<String, String> attachmentIds = xmlUtil.getAttachmentIds(envelope);

        Attachments attachments = null;
        long dataAttSize = 0L;
        // Bat dau them attachment
        if (attachmentsByEntity != null) {
            attachments = new Attachments();
            for (Attachment attachment : attachmentsByEntity) {
                InputStream attStream = attachment.getValue();
                if (attStream != null) {
                    // Tao attachmentPart
                    String contentId = RandomUtil.randomId();

                    String oldContentId = "";

                    if (attachmentIds != null) {
                        oldContentId = attachmentIds.get(attachment.getName());
                    }

                    if (oldContentId != null && oldContentId.length() > 0) {
                        contentId = oldContentId;
                    }
                    byte[] value = attGlobalUtil
                            .parseBase64ISToBytes(attStream);

                    String contentType = attachment.getContentType();
                    attStream.close();

                    if (!Base64.isArrayByteBase64(value)) {
                        value = Base64.encodeBase64(value, true);
                    }

                    ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource(value, contentType);

                    DataHandler data = new DataHandler(byteArrayDataSource);

                    attachments.addDataHandler(contentId, data);

                    dataAttSize += value.length;

                } else {
                    if (attachment.getName().length() > 0) {
                        log.error("Can't read attachment name: "
                                + attachment.getName());
                    }
                }

            }
        }

        map.put(StringPool.ENVELOPE_SAVED_KEY, envelope);
        map.put(StringPool.ATTACHMENT_KEY, attachments);
        map.put(StringPool.ATTACHMENT_SIZE_KEY, dataAttSize);
        map.put("attachment_detail", objAttachment);
        return map;

    }

    /**
     * create mime for entity
     * @param envelope
     * @param attachmentsByEntity
     * @return
     * @throws Exception
     */
    public Map<String, Object> createMime(Envelope envelope,
                                          List<Attachment> attachmentsByEntity, Object objAttachments) throws Exception {
        Map<String, Object> map = new HashMap<>();

        Document bodyChildDocument = null;

        Document messageHeaderDocument = null;

        Document traceHeaderDocument = null;

        Attachments attachments = null;

        OMFactory factoryOM = OMAbstractFactory.getOMFactory();

        OMNamespace ns = factoryOM.createOMNamespace(
                StringPool.TARGET_NAMESPACE, StringPool.EDXML_PREFIX);

        messageHeaderDocument = xmlUtil.getMessHeaderDoc(envelope.getHeader()
                .getMessageHeader(), ns);

        traceHeaderDocument = xmlUtil.getTraceHeaderDoc(envelope.getHeader()
                .getTraceHeaderList(), ns);

        List<Reference> references = new ArrayList<Reference>();

        long dataAttSize = 0l;

        // Bat dau them attachment
        if (attachmentsByEntity != null) {
            attachments = new Attachments();
            for (Attachment attachment : attachmentsByEntity) {
                InputStream attStream = attachment.getValue();
                if (attStream != null) {
                    // Tao attachmentPart
                    String contentId = RandomUtil.randomId();

                    String contentType = MimeTypesUtil
                            .getContentType(attachment.getName());

                    // TODO: Replace by CommonIO
                    // String base64String = new
                    // AttachmentGlobalUtil().parseBase64ISToString(attachment.getValue());

                    // DataHandler data = new DataHandler(base64String,
                    // contentType);
                    byte[] value = attGlobalUtil
                            .parseBase64ISToBytes(attStream);

                    if (attStream != null) {
                        attStream.close();
                    }

                    DataHandler data = new DataHandler(value, contentType);

                    attachments.addDataHandler(contentId, data);

                    dataAttSize += value.length;

                    // Tao referen tren body
                    Reference reference = new Reference();
                    reference.setHref("cid:" + contentId);
                    reference.setId(contentId);
                    reference.setAttachmentName(attachment.getName());
                    reference.setDescription("Attachment by xml");
                    references.add(reference);

                } else {
                    if (attachment.getName().length() > 0) {
                        log.error("Cannot read attachment name: "
                                + attachment.getName());
                    }
                }

            }
        }

        Manifest manifest = new Manifest();

        manifest.setReference(references);

        bodyChildDocument = xmlUtil.getBodyChildDoc(manifest, ns);

        map.put(StringPool.CHILD_BODY_KEY, bodyChildDocument);

        map.put(StringPool.MESSAGE_HEADER_KEY, messageHeaderDocument);

        map.put(StringPool.TRACE_HEADER_KEY, traceHeaderDocument);

        map.put(StringPool.ATTACHMENT_KEY, attachments);

        map.put(StringPool.ATTACHMENT_SIZE_KEY, dataAttSize);

        map.put("attachment_detail", objAttachments);

        return map;

    }

    private AttachmentGlobalUtil attGlobalUtil = new AttachmentGlobalUtil();
    private XmlUtil xmlUtil = new XmlUtil();
    private static final Log log = LogFactory.getLog(ArchiveMime.class);
}
