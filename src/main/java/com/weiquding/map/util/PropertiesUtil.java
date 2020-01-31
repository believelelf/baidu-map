package com.weiquding.map.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * 加载ak文件
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/31
 */
public class PropertiesUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtil.class);

    private static final Properties PROPERTIES = new Properties();

    static {
        try {
            PROPERTIES.load(PropertiesUtil.class.getResourceAsStream("/ak.properties"));
        } catch (IOException e) {
            LOGGER.error("加载ak文件失败", e);
        }
    }

    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key, "");
    }

}
