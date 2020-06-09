package com.bkav.edoc.service.redis;

import com.google.gson.Gson;
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

    private RedisUtil() {
        jedisClient = new Jedis("localhost", 6379);
    }

    public static void main(String[] args) {
        Test test = new Test(1, "abc");
        RedisUtil.getInstance().set("entry-1", test);
        try {
            Test entry = RedisUtil.getInstance().get("entry-1", Test.class);
            System.out.println(entry);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> T get(String key, Class<T> classOfT) {
        String json = jedisClient.get(key);
        T object= gson.fromJson(json, classOfT);
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
        }
        catch (Exception e) {
            return false;
        }
    }
}
