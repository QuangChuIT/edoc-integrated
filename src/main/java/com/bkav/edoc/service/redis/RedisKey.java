package com.bkav.edoc.service.redis;

public class RedisKey {

    public final static String CHECK_PERMISSION_KEY = "CheckPermission_";

    public final static String SEND_DOCUMENT_KEY = "SendDocument_";

    public final static String GET_DOCUMENT_KEY = "GetDocument_";

    public final static String GET_PENDING_KEY = "GetPendingDocument_";

    public final static String GET_ENVELOP_FILE = "GetEnvelopFile_";

    public final static String CHECK_ALLOW_KEY = "CheckAllowDocument_";

    public final static String GET_ORGAN_NAME_BY_DOMAIN = "GetOrganNameByDomain_";

    public final static String GLOBAL_IDS_FOR_TEST = "GlobalIdsForTest";

    public final static String GET_ATTACHMENT_BY_DOC_ID = "GetAttachmentByDocID_";

    public final static String[] DEFAULT_SERVER = new String[] { "127.0.0.1:11211" };

    public final static String  SERVER_KEY = "edxml.service.memcached.address";

    public static String getKey(String finalKey, String methodPrefixKey){
        StringBuilder resultKey = new StringBuilder();
        resultKey.append(methodPrefixKey);
        resultKey.append(finalKey);

        return resultKey.toString();
    }
}
