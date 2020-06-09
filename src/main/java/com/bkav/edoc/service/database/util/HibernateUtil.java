package com.bkav.edoc.service.database.util;

import com.bkav.edoc.service.util.PropsUtil;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            Configuration configuration = new Configuration();
            Properties settings = new Properties();
            settings.put(Environment.DRIVER, PropsUtil.get(Environment.DRIVER));
            settings.put(Environment.URL, PropsUtil.get(Environment.URL));
            settings.put(Environment.USER, PropsUtil.get(Environment.USER));
            settings.put(Environment.PASS, PropsUtil.get(Environment.PASS));
            settings.put(Environment.DIALECT, PropsUtil.get(Environment.DIALECT));
            settings.put(Environment.SHOW_SQL, PropsUtil.get(Environment.SHOW_SQL));
            settings.put(Environment.AUTO_CLOSE_SESSION, PropsUtil.get(Environment.AUTO_CLOSE_SESSION));

            configuration.setProperties(settings);
            configuration.addInputStream( HibernateUtil.class.getClassLoader().getResourceAsStream("entity/EdocDocument.hbm.xml"));
            configuration.addInputStream( HibernateUtil.class.getClassLoader().getResourceAsStream("entity/EdocDocumentDetail.hbm.xml"));
            configuration.addInputStream( HibernateUtil.class.getClassLoader().getResourceAsStream("entity/EdocTraceHeaderList.hbm.xml"));
            configuration.addInputStream( HibernateUtil.class.getClassLoader().getResourceAsStream("entity/EdocTrace.hbm.xml"));
            configuration.addInputStream( HibernateUtil.class.getClassLoader().getResourceAsStream("entity/EdocNotification.hbm.xml"));
            configuration.addInputStream( HibernateUtil.class.getClassLoader().getResourceAsStream("entity/EdocPriority.hbm.xml"));
            configuration.addInputStream( HibernateUtil.class.getClassLoader().getResourceAsStream("entity/EdocAttachment.hbm.xml"));
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            return configuration.buildSessionFactory(serviceRegistry);
        }
        catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        // Close caches and connection pools
        getSessionFactory().close();
    }

}