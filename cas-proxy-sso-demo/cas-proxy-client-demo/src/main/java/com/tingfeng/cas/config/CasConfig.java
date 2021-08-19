package com.tingfeng.cas.config;

/**
 * Cas 的一些配置项
 */
public class CasConfig {

    /**
     * 当前应用程序的baseUrl（注意最后面的斜线）
     */
    public static String SERVER_NAME = "http://localhost:8888/";

    /**
     * 代理回调地址
     */
    public static String PROXY_CALLBACK_URL = "http://localhost:8888/proxy/callback";

    /**
     * CAS服务器地址
     */
    public static String CAS_SERVER_PATH = "http://localhost:8080/cas";

    /**
     * CAS登陆服务器地址
     */
    public static String CAS_SERVER_LOGIN_PATH = "http://localhost:8080/cas/login";

    /**
     * CAS登出服务器地址
     */
    public static String CAS_SERVER_LOGOUT_PATH = "http://localhost:8080/cas/logout";


}
