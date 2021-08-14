package com.pitt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletContextInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@ServletComponentScan
@SpringBootApplication
public class SpringBootMainApplication implements ServletContextInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

    }

    public static void main(String... args) {
        SpringApplication.run(SpringBootMainApplication.class, args);
    }
}
