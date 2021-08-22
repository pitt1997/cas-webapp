package com.pitt.cas.client.common.config;

import com.pitt.cas.client.common.constant.CasConstant;
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

import java.util.HashMap;
import java.util.Map;
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


    /**
     * Cas30ProxyReceivingTicketValidationFilter 验证过滤器
     *
     * 该过滤器负责对Ticket的校验工作，必须配置
     * cas与后台应用服务间确认性验证，保证服务间可信
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean filterValidationRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new Cas30ProxyReceivingTicketValidationFilter());
        // 设定匹配的路径
        registration.addUrlPatterns("/*");
        // 代理模式（代理端）
        //registration.addUrlPatterns("/proxyCallback","/*");
        Map<String,String>  initParameters = new HashMap<>();
        // cas 服务地址，从后台请求CAS服务，得到ticket信息（内部通讯）
        initParameters.put("casServerUrlPrefix", CasConstant.CAS_SERVER_URL_PREFIX);
        // 验证ticket正确后，对当前请求重定向一次的服务地址（主要消除地址中的ticket参数），代理服务的请求（内部通讯）或客户端请求都会处理。可由参数redirectAfterValidation设置不重定向
        initParameters.put("serverName", CasConstant.SERVER_NAME);
        initParameters.put("encoding", "UTF-8");
        // 是否对serviceUrl进行编码，默认true：设置false可以在302对URL跳转时取消显示;jsessionid=xxx的字符串
        initParameters.put("encodeServiceUrl","false");
        // 代理模式(代理端)
        // 发送给CAS服务器，用于代理验证后的回调地址（内部通讯）
        // initParameters.put("proxyCallbackUrl","http://localhost:8080/holiday/proxyCallback");
        // 代理验证请求地址后缀，与proxyCallbackUrl中设置的一致。用于拦截验证回调
        // initParameters.put("proxyReceptorUrl","/holiday/proxyCallback");
        // 代理模式(被代理端)
        initParameters.put("acceptAnyProxy","true");
        initParameters.put("redirectAfterValidation","false");

        //initParameters.put("useSession", "true");
        registration.setInitParameters(initParameters);
        // 设定加载的顺序
        registration.setOrder(1);
        return registration;
    }
}
