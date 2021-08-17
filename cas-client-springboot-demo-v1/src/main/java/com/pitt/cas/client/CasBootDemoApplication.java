package com.pitt.cas.client;

import com.pitt.cas.client.common.config.CasConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CasBootDemoApplication {

    public static void main(String[] args) {
        CasConfig.fileFlag = isUseExternalConfigFile(args);
        SpringApplication.run(CasBootDemoApplication.class, args);
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
