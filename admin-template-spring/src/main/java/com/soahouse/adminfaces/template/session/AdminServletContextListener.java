package com.soahouse.adminfaces.template.session;

import java.util.ResourceBundle;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminServletContextListener implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(AdminServletContextListener.class.getCanonicalName());


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            StringBuilder sb = new StringBuilder("Using Admin Template ")
                    .append(ResourceBundle.getBundle("admin").getString("admin.version"))
                    .append(" and Admin Theme ").append(ResourceBundle.getBundle("admin-theme").getString("theme.version"));
            log.info(sb.toString());
        }catch (Exception e) {
            log.warn("Could not get AdminFaces version.",e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
