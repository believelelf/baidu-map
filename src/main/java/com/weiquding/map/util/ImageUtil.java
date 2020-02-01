package com.weiquding.map.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 切割发热门诊图片为多张
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/30
 */
public class ImageUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageUtil.class);

    public static final String SPLIT_IMAGE_NAME = "_split";

    public static final int DEFAULT_HEIGHT = 2048;

    public static final Map<String, Integer> cityRefHeight = new HashMap<>();

    static {
        //	图像数据，base64编码后进行urlencode，要求base64编码和urlencode后大小不超过4M，最短边至少15px，最长边最大4096px,支持jjpg/jpeg/png/bmp格式，当image字段存在时url字段失效
        //cityRefHeight.put("天津市", 380);
    }


    /**
     * 切割发热门诊图片为多张
     *
     * @param path 图片路径
     */
    public static void splitImage(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                String name = file.getName();
                String suffix = file.getName().substring(name.lastIndexOf('.') + 1);
                name = name.substring(0, name.lastIndexOf('.'));
                final BufferedImage source = ImageIO.read(file);
                // 计算截图高度
                String city = file.getParent().substring(file.getParent().lastIndexOf("\\") + 1);
                int subImageHeight = cityRefHeight.getOrDefault(city, DEFAULT_HEIGHT);
                int idx = 0;
                // 从270开始取图像，去掉头部
                for (int y = 270, height = source.getHeight(); y < height; y += subImageHeight) {
                    // 去掉可能的尾部(文字：没有更多数据了 本服务由中国政府网和健康中国联合提供 (数据持续更新中) )
                    int h = height - y <= subImageHeight ? height - y - 300 : subImageHeight;
                    if (h < 0) {
                        continue;
                    }
                    int id = idx++;
                    String ids = id < 10 ? "0" + id : String.valueOf(id);
                    ImageIO.write(source.getSubimage(0, y, 460, h), suffix, new File(file.getParent() + File.separator + name + SPLIT_IMAGE_NAME + ids + "." + suffix));
                }
            }
        } catch (IOException e) {
            LOGGER.info("切割发热门诊图片为多张出错:[{}]==>{}", path, e.getMessage());
        }
    }

    /**
     * 遍历目录进行图片切分
     *
     * @param dir 目录
     */
    public static void splitImages(String dir) {
        File file = new File(dir);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File subFile : files) {
                    splitImages(subFile.getPath());
                }
            }
        } else {
            // 增加配置，只切分原图
            if (!file.getName().contains(SPLIT_IMAGE_NAME)) {
                splitImage(file.getPath());
            }
        }
    }

    public static void countImages(String dir, AtomicInteger count) {
        File file = new File(dir);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File subFile : files) {
                    countImages(subFile.getPath(), count);
                }
            }
        } else {
            // 增加配置，只切分原图
            if (!file.getName().contains(SPLIT_IMAGE_NAME)) {
                count.getAndIncrement();
            }
        }
    }
}
