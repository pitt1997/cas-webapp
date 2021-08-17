package com.pitt.cas.client.proxy;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ljs
 * @date 2021-08-15
 * @description
 */
@Controller
public class CasProxyedController {


    @RequestMapping("/getInfo")
    @ResponseBody
    public String proxy(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder result = new StringBuilder();

        result.append("resource success");

        return result.toString();
    }

}
