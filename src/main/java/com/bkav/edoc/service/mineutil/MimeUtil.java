package com.bkav.edoc.service.mineutil;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.client.Options;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.OperationContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MimeUtil {

    /**
     * @param isSWA
     */
    public static void setOutputSWA(boolean isSWA, MessageContext messageContext) {
        OperationContext operationContext = messageContext.getOperationContext();

        MessageContext outMessageContext;
        try {
            outMessageContext = operationContext
                    .getMessageContext(WSDLConstants.MESSAGE_LABEL_OUT_VALUE);

            Options opt = new Options();

            opt.setProperty(Constants.Configuration.ENABLE_SWA,
                    isSWA ? Constants.VALUE_TRUE : Constants.VALUE_FALSE);

            if (isSWA) {

                opt.setProperty(Constants.Configuration.CONTENT_TYPE,
                        HTTPConstants.MEDIA_TYPE_MULTIPART_RELATED);

                opt.setProperty(Constants.Configuration.MIME_BOUNDARY,
                        "BoundarY");
            }

            outMessageContext.setOptions(opt);

        } catch (AxisFault e) {

            _log.error(e.getMessage());
        }
    }

    private static final Log _log = LogFactory.getLog(MimeUtil.class);

}
