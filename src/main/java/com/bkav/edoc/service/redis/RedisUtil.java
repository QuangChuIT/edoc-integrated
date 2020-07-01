package com.bkav.edoc.service.redis;

import com.bkav.edoc.service.kernel.string.StringPool;
import com.bkav.edoc.service.util.PropsUtil;
import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import redis.clients.jedis.Jedis;

public class RedisUtil {

    private static RedisUtil INSTANCE;

    private Jedis jedisClient;

    private Gson gson = new Gson();


    static public RedisUtil getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new RedisUtil();
        }
        return INSTANCE;
    }

    private static String buildKey(String key) {
        StringBuilder keyBuilder = new StringBuilder(String.valueOf(System.nanoTime()));
        keyBuilder.append(StringPool.UNDERLINE);
        keyBuilder.append(KEY_NAMESPACE_PREFIX);
        keyBuilder.append(key);
        return keyBuilder.toString();
    }

    private static final String KEY_NAMESPACE_PREFIX = "edoc_service_";

    private RedisUtil() {
        jedisClient = new Jedis(PropsUtil.get("eDoc.service.redis.host"), Integer.parseInt(PropsUtil.get("eDoc.service.redis.port")));
    }

    public static void main(String[] args) {
        Test test = new Test(1, "abc");
        RedisUtil.getInstance().set("entry-1", test);
        try {
            Test entry = RedisUtil.getInstance().get("entry-1", Test.class);
            System.out.println(entry);
        } catch (Exception e) {
            _log.error(e);
        }
    }

    public <T> T get(String key, Class<T> classOfT) {
        String json = jedisClient.get(key);
        T object = gson.fromJson(json, classOfT);
        return object;
    }

    public boolean set(String key, Object value) {
        String json = gson.toJson(value);
        return jedisClient.set(key, json).equals("OK");

    }

    public boolean delete(String key) {

        return jedisClient.del(key) > 0;
    }

    public boolean clearAllCache() {

        try {

            if (jedisClient.flushAll().equals("OK"))
                return true;
            else
                return false;
        } catch (Exception e) {
            _log.error("Error when clear all cached !!! " + e.getMessage());
            return false;
        }
    }

    private static Log _log = LogFactory.getLog(RedisUtil.class);
}
