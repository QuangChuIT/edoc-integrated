/**
 *
 */
package com.bkav.edoc.service.entity.edxml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/*QuyenDN - Oct 10, 2013*/

/**
 * @author QuangCV
 *
 */
@XmlType(name = "request")
@SuppressWarnings("unused")
public class GetMessageRequest {
    @XmlElement(name = "MessageId")
    private int messageId;

    private int getMessageId() {
        return messageId;
    }

    private void setMessageId(int messageId) {
        this.messageId = messageId;
    }
}
