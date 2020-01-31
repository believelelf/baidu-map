package com.weiquding.map.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 根据省份城市的所有城市，创建目录
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/31
 */
public class AreaUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(AreaUtil.class);

    @SuppressWarnings("all")
    public static void mkdir(String basePath) {
        try {
            File file = new File(basePath);
            if (!file.exists()) {
                boolean mkdirs = file.mkdirs();
                if (!mkdirs) {
                    LOGGER.warn("路径[{}]未创建", file);
                }
            }
            Map<String, Object> map = new ObjectMapper().readValue(AreaUtil.class.getResourceAsStream("/area.json"), Map.class);
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String province = entry.getKey();
                List<String> citys = (List<String>) entry.getValue();
                File provinceDir = new File(file.getPath() + File.separator + province);
                if (!provinceDir.exists()) {
                    boolean mkdirs = provinceDir.mkdirs();
                    if (!mkdirs) {
                        LOGGER.warn("路径[{}]未创建", provinceDir);
                    }
                }
                for (String city : citys) {
                    File cityDir = new File(provinceDir.getPath() + File.separator + city);
                    if (!cityDir.exists()) {
                        boolean mkdirs = cityDir.mkdirs();
                        if (!mkdirs) {
                            LOGGER.warn("路径[{}]未创建", cityDir);
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("加载行政区域失败", e);
        }
    }

}
