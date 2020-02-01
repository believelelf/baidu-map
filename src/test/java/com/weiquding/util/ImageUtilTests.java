package com.weiquding.util;

import com.weiquding.map.MapApplication;
import com.weiquding.map.util.ImageUtil;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * description
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/31
 */
public class ImageUtilTests {

    @Test
    public void testSplitImages() {
        ImageUtil.splitImages(MapApplication.IMAGE_PATH);
    }

    @Test
    public void testSplitDDImages() {
        ImageUtil.splitImages(MapApplication.DD_IMAGE_PATH);
    }

    @Test
    public void testCountImages() {
        AtomicInteger frCount = new AtomicInteger();
        ImageUtil.countImages(MapApplication.IMAGE_PATH, frCount);
        System.out.println("发热门诊截图：" + frCount.get());
        AtomicInteger ddCount = new AtomicInteger();
        ImageUtil.countImages(MapApplication.DD_IMAGE_PATH, ddCount);
        System.out.println("定点医院截图：" + ddCount.get());
        System.out.println("总截图：" + (frCount.get() + ddCount.get()));
    }


}
