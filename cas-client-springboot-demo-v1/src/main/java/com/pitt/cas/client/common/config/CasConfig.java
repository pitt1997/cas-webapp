package com.pitt.cas.client.common.config;

import com.pitt.cas.client.common.util.PropertiesUtil;
import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.Properties;

/**
 * @author ljs
 * @date 2021-08-15
 * @description
 */
@Configuration
public class CasConfig {

    private static Logger logger = LoggerFactory.getLogger(CasConfig.class);

    public static Boolean fileFlag = true;

    @Autowired
    private CasClientConfig casClientConfig;

    @Bean
    public CasClientConfig casConfigDTO() {
        logger.info("file flag : {}", fileFlag);
        Properties properties;
        if (fileFlag) {
            properties = PropertiesUtil.readOuterProperties("cas-client.properties");
            if (properties.isEmpty()) {
                logger.info("read external config failure, using default config");
                properties = PropertiesUtil.readInnerProperties("/properties/cas-client.properties");
            }
        } else {
            properties = PropertiesUtil.readInnerProperties("/properties/cas-client.properties");
        }
        CasClientConfig casClientConfig = new CasClientConfig();
        casClientConfig.setResName(properties.getProperty("res.name"));
        casClientConfig.setCasServerLoginUrl(properties.getProperty("cas.server.login.url"));
        casClientConfig.setCasServerUrlPrefix(properties.getProperty("cas.server.url.prefix"));
        casClientConfig.setCasClientLoginUrl(properties.getProperty("cas.client.login.url"));
        logger.info(casClientConfig.toString());
        return casClientConfig;
    }

    @Bean
    public ServletListenerRegistrationBean servletListenerRegistrationBean() {
        ServletListenerRegistrationBean listenerRegistrationBean = new ServletListenerRegistrationBean();
        listenerRegistrationBean.setListener(new SingleSignOutHttpSessionListener());
        listenerRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return listenerRegistrationBean;
    }

    /**
     * 单点登录身份认证
     *
     * @return org.springframework.boot.web.servlet.FilterRegistrationBean
     */
    @Bean
    public FilterRegistrationBean authenticationFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new AuthenticationFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("CAS Authentication Filter");
        registrationBean.addInitParameter("casServerLoginUrl", casClientConfig.getCasServerUrlPrefix());
        registrationBean.addInitParameter("service", casClientConfig.getCasClientLoginUrl());
        registrationBean.setOrder(3);
        return registrationBean;
    }

    /**
     * 单点登录票据校验
     *
     * @return org.springframework.boot.web.servlet.FilterRegistrationBean
     */
    @Bean
    public FilterRegistrationBean cas20ProxyReceivingTicketValidationFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new Cas30ProxyReceivingTicketValidationFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("CAS Validation Filter");
        registrationBean.addInitParameter("casServerUrlPrefix", casClientConfig.getCasServerUrlPrefix());
        registrationBean.addInitParameter("service", casClientConfig.getCasClientLoginUrl());
        registrationBean.setOrder(4);
        return registrationBean;
    }

    /**
     * 单点登录请求包装
     *
     * @return org.springframework.boot.web.servlet.FilterRegistrationBean
     */
    @Bean
    public FilterRegistrationBean httpServletRequestWrapperFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new HttpServletRequestWrapperFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("CAS HttpServletRequest Wrapper Filter");
        registrationBean.setOrder(5);
        return registrationBean;
    }
}
