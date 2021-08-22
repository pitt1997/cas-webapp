package com.pitt.cas.client;

import com.pitt.cas.client.common.config.CasConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CasProxyClientApplicationV2 {

    public static void main(String[] args) {
        CasConfig.fileFlag = isUseExternalConfigFile(args);
        SpringApplication.run(CasProxyClientApplicationV2.class, args);
    }

    private static Boolean isUseExternalConfigFile(String... args) {
        if (args.length < 1) {
            return false;
        }
        String flag = args[0];
        if ("true".equals(flag)) {
            return true;
        }
        return false;
    }
}
