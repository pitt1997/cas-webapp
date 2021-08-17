package com.pitt.cas.client.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * @author ljs
 * @date 2021-08-15
 * @description
 */
public class PropertiesUtil {
    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    public static Properties readInnerProperties(String fileName) {
        Properties props = new Properties();
        try (InputStream inputStream = PropertiesUtil.class.getResourceAsStream(fileName);
             InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8")) {
            props.load(reader);
        } catch (IOException e) {
            logger.error("read properties error -> ", e);
        }
        return props;
    }

    public static Properties readOuterProperties(String fileName) {
        Properties props = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(new File(fileName));
             InputStreamReader reader = new InputStreamReader(fileInputStream, "UTF-8")) {
            props.load(reader);
        } catch (IOException e) {
            logger.error("read properties error -> ", e);
        }
        return props;
    }
}
