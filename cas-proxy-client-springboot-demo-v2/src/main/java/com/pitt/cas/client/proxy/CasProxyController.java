package com.pitt.cas.client.proxy;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.util.AssertionHolder;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author ljs
 * @date 2021-08-15
 * @description
 */
@Controller
public class CasProxyController {

    @RequestMapping("/proxy")
    @ResponseBody
    public String proxy(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder result = new StringBuilder();

        // 被代理应用的URL
        String serviceUrl = "http://localhost:8092/cas-resource/getInfo";

        //1、获取到AttributePrincipal对象
        AttributePrincipal principal = (AttributePrincipal) request.getUserPrincipal();
        if (principal == null) {
            return "用户未登录";
        }

        //2、获取对应的(PT)proxy ticket
        String proxyTicket = principal.getProxyTicketFor(serviceUrl);
        if (proxyTicket == null) {
            return "PGT 或 PT 不存在";
        }

        //3、请求被代理应用时将获取到的proxy ticket以参数ticket进行传递
        URL url = new URL(serviceUrl + "?ticket=" + proxyTicket);// 不需要cookie，只需传入代理票据

        HttpURLConnection conn;
        conn = (HttpURLConnection) url.openConnection();
        //使用POST方式
        conn.setRequestMethod("POST");
        // 设置是否向connection输出，因为这个是post请求，参数要放在
        // http正文内，因此需要设为true
        conn.setDoOutput(true);
        // Post 请求不能使用缓存
        conn.setUseCaches(false);

        //设置本次连接是否自动重定向
        conn.setInstanceFollowRedirects(true);
        // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
        // 意思是正文是urlencoded编码过的form参数
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        // 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
        // 要注意的是connection.getOutputStream会隐含的进行connect。
        conn.connect();

        DataOutputStream out = new DataOutputStream(conn
                .getOutputStream());
        // 正文，正文内容其实跟get的URL中 '? '后的参数字符串一致
        String content = "start=" + URLEncoder.encode("1901-01-01", "UTF-8") + "&end=" + URLEncoder.encode("2018-01-01", "UTF-8");
        // DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写到流里面
        out.writeBytes(content);
        //流用完记得关
        out.flush();
        out.close();
        //获取响应
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        reader.close();
        //连接断了
        conn.disconnect();

        return result.toString();
    }

    @RequestMapping("/proxyCallback")
    @ResponseBody
    public String proxyCallback(HttpServletRequest request, HttpServletResponse response) throws IOException {

        System.out.println("123");

        String tmp = "233";


        return "success";
    }


    @GetMapping("/users")
    public String proxyUsers(HttpServletRequest request, HttpServletResponse response) {

        String result = "无结果";
        try {
            String serviceUrl = "http://client2.com:8889/user/users";

            AttributePrincipal principal = (AttributePrincipal) request.getUserPrincipal();

            //1、获取到AttributePrincipal对象
//            AttributePrincipal principal = AssertionHolder.getAssertion().getPrincipal();
            if (principal == null) {
                return "用户未登录";
            }

            //2、获取对应的(PT)proxy ticket
            String proxyTicket = principal.getProxyTicketFor(serviceUrl);
            if (proxyTicket == null) {
                return "PGT 或 PT 不存在";
            }

            //3、请求被代理应用时将获取到的proxy ticket以参数ticket进行传递
            String url = serviceUrl + "?ticket=" + URLEncoder.encode(proxyTicket, "UTF-8");
            result = HttpProxy.httpRequest(url, "", HttpMethod.GET);

            System.out.println("result结果：" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    @GetMapping("/books")
    public String proxyBooks(HttpServletRequest request, HttpServletResponse response) {

        String result = "无结果";
        try {
            String serviceUrl = "http://client2.com:8889/book/books";

            //1、获取到AttributePrincipal对象
            AttributePrincipal principal = AssertionHolder.getAssertion().getPrincipal();
            if (principal == null) {
                return "用户未登录";
            }

            //2、获取对应的(PT)proxy ticket
            String proxyTicket = principal.getProxyTicketFor(serviceUrl);
            if (proxyTicket == null) {
                return "PGT 或 PT 不存在";
            }

            //3、请求被代理应用时将获取到的proxy ticket以参数ticket进行传递
            String url = serviceUrl + "?ticket=" + URLEncoder.encode(proxyTicket, "UTF-8");


            result = HttpProxy.httpRequest(url, "", HttpMethod.GET);

            System.out.println("result结果：" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


}
