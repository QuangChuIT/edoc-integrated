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
            settings.put(Environment.DRIVER, "com.mysql.jdbc.Driver");
            settings.put(Environment.URL, "jdbc:mysql://localhost:3306/edoc");
            settings.put(Environment.USER, "root");
            settings.put(Environment.PASS, "abcd1234");
            settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect");
            settings.put(Environment.SHOW_SQL, "true");
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