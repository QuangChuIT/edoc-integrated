package com.bkav.edoc.service.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropsUtil {

    private static final Log logger = LogFactory.getLog(PropsUtil.class);
    private static Properties prop = new Properties();

    public static Properties readPropertyFile() throws Exception {
        if (prop.isEmpty()) {
            InputStream input = PropsUtil.class.getClassLoader().getResourceAsStream("config.properties");
            try {
                prop.load(input);
            } catch (IOException ex) {
                logger.error(ex);
                throw ex;
            } finally {
                if (input != null) {
                    input.close();
                }
            }
        }
        return prop;
    }

    public static String get(String key) {
        if(prop.isEmpty()) {
            try {
                prop = readPropertyFile();
            } catch (Exception e) {
            }
        }
        return prop.getProperty(key);
    }
}
