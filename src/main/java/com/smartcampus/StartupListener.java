package com.smartcampus;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class StartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("==================================");
        System.out.println("Smart Campus API running at");
        System.out.println("http://localhost:8080/smart-campus-api/api/v1");
        System.out.println("To stop: click the red button in NetBeans");
        System.out.println("==================================");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Smart Campus API stopped.");
    }
}