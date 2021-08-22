package com.pitt.cas.client.login.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pitt.cas.client.common.config.CasClientConfig;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author ljs
 * @date 2021-08-15
 * @description
 */
@Controller
@RequestMapping("/login")
public class CasLoginController {
    /**
     * CAS接入的目标应用录入4A的资源名称
     *  此名称需和web.xml配置中CAS Authentication Filter、CAS Validation Filter中的system参数保持一致
     */
    private static final String RES_NAME = "CasDemoApp1";
    @Autowired
    private CasClientConfig casConfigDTO;
    private static Logger logger = LoggerFactory.getLogger(CasLoginController.class);

    @RequestMapping("/login")
    @ResponseBody
    public String login(HttpServletRequest request) {
        // 用户4A统一认证登录帐号，即登录页面用户输入的帐号
        String loginName = "";
        // 用户登录目标应用的帐号，即目标应用资源的用户帐号
        String userAppName = "";
        Assertion assertion = (Assertion) request.getSession().getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
        if (assertion == null) {
            return genLoginUserInfo(loginName, userAppName);
        }
        AttributePrincipal principal = assertion.getPrincipal();
        if (principal == null) {
            return genLoginUserInfo(loginName, userAppName);
        }
        loginName = principal.getName();
        Map<String, Object> map = principal.getAttributes();
        // 如果4A平台未对登录用户、接入应用做授权，则返回的slaveAccounts可能为空
        // 如果4A平台登录用户、接入应用做资源授权，则返回数据中只有资源信息
        // [{"resid":"19751","resname":"CasDemoApp1"}]
        // 如果4A平台登录用户、接入应用做资源帐号授权，则返回数据中既有资源信息，也有帐号信息
        // [{"id":"19753","name":"cxy-test","resid":"19752","resname":"CasDemoApp2"}]
        String value = (String) map.get("slaveAccounts");
        logger.info("slaveAccounts -> {}", value);
        String resName = getResName();
        logger.info("resName -> {}", resName);
        JSONArray jsonArray = JSONArray.parseArray(value);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            if (json.containsKey("resname") && resName.equals(json.getString("resname"))) {
                if (json.containsKey("name")) {
                    userAppName = json.getString("name");
                } else {
                    userAppName = principal.getName();
                }
                break;
            }
        }
        return genLoginUserInfo(loginName, userAppName);
    }

    private String genLoginUserInfo(String loginName, String userAppName) {
        JSONObject json = new JSONObject();
        json.put("loginName", loginName);
        json.put("userAppName", userAppName);
        return json.toJSONString();
    }

    private String getResName() {
        if (casConfigDTO == null) {
            return RES_NAME;
        }
        if (StringUtils.isEmpty(casConfigDTO.getResName())) {
            return RES_NAME;
        }
        return casConfigDTO.getResName();
    }
}
