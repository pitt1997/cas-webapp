package com.pitt.cas.client.common.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author ljs
 * @date 2021-08-15
 * @description
 */
@Getter
@Setter
@ToString
public class CasClientConfig {
    /**
     * 资源名称
     */
    private String resName;
    /**
     * cas服务登录地址
     */
    private String casServerLoginUrl;
    /**
     * cas服务地址前缀
     */
    private String casServerUrlPrefix;
    /**
     * cas客户端登录地址
     */
    private String casClientLoginUrl;
}