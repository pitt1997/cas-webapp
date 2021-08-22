package com.pitt.cas.modules.properties;

import com.pitt.cas.client.logout.MyLogoutSuccessHandler;
import com.pitt.cas.modules.user.UserServiceImpl;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas30ProxyTicketValidator;
import org.jasig.cas.client.validation.TicketValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import java.util.Arrays;

@Configuration
public class CasSecurityProperties {
    @Value("${cas.server.prefix}")
    private String casServerPrefix;

    @Value("${cas.server.login}")
    private String casServerLogin;

    @Value("${cas.server.logout}")
    private String casServerLogout;

    @Value("${cas.client.prefix}")
    private String casClientPrefix;

    @Value("${cas.client.login}")
    private String casClientLogin;

    @Value("${cas.client.logout}")
    private String casClientLogout;

    /**
     * 设置本cas服务的属性bean
     * serviceProperties.setAuthenticateAllArtifacts(true);
     * 设置带有ticket参数的请求都可以触发认证，主要在代理认证时使用
     */
    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService(casClientPrefix + casClientLogin);
        serviceProperties.setAuthenticateAllArtifacts(true);
        return serviceProperties;
    }

    /**
     * 票据验证，使用cas3.0 代理票据认证器
     * 传递cas server地址进行初始化
     */
    @Bean
    public Cas30ProxyTicketValidator ticketValidator() {
        Cas30ProxyTicketValidator validator = new Cas30ProxyTicketValidator(casServerPrefix);
        return validator;
    }

    /**
     * 接收cas服务器发出的注销请求
     */
    @Bean
    public SingleSignOutFilter singleSignOutFilter() {
        SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
        singleSignOutFilter.setCasServerUrlPrefix(casServerPrefix);
        singleSignOutFilter.setIgnoreInitConfiguration(true);
        return singleSignOutFilter;
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new MyLogoutSuccessHandler();
    }

    /**
     * 注销请求转发到cas server
     * 由于登出会重定向到cas server页面，发生跨域，
     * 此处重写了LogoutSuccessHandler，后台只进行session销毁，然后通知前端进行登出跳转
     */
    @Bean
    public LogoutFilter logoutFilter(LogoutSuccessHandler logoutSuccessHandler) {
        LogoutFilter logoutFilter = new LogoutFilter(logoutSuccessHandler, new SecurityContextLogoutHandler());
        logoutFilter.setFilterProcessesUrl(casClientLogout);
        return logoutFilter;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserServiceImpl userService = new UserServiceImpl();
        return userService;
    }

    @Bean
    public CasAuthenticationProvider casAuthenticationProvider(ServiceProperties sp, TicketValidator ticketValidator, UserDetailsService userDetailsService) {
        CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
        casAuthenticationProvider.setKey("casProvider");
        casAuthenticationProvider.setServiceProperties(sp);
        casAuthenticationProvider.setTicketValidator(ticketValidator);
        casAuthenticationProvider.setUserDetailsService(userDetailsService);
        return casAuthenticationProvider;
    }

    /**
     * cas filter类，针对/login/cas请求做用户认证
     * filter.setAuthenticationDetailsSource(new ServiceAuthenticationDetailsSource(serviceProperties));
     * 该设置可以在认证信息中记录请求原始url，不再使用spring security默认的 /login/cas
     */
    @Bean
    public CasAuthenticationFilter casAuthenticationFilter(ServiceProperties serviceProperties,
                                                           AuthenticationProvider provider) {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setServiceProperties(serviceProperties);
        filter.setAuthenticationManager(new ProviderManager(Arrays.asList(provider)));
        return filter;
    }

    /**
     * filter入口，未登录时跳转到cas server
     */
    @Bean
    @Primary
    public CasAuthenticationEntryPoint authenticationEntryPoint(ServiceProperties serviceProperties) {
        CasAuthenticationEntryPoint entryPoint = new CasAuthenticationEntryPoint();
        entryPoint.setLoginUrl(casServerPrefix + casServerLogin);
        entryPoint.setServiceProperties(serviceProperties);
        return entryPoint;
    }
}