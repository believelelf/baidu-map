package com.weiquding.util;

import com.weiquding.map.MapApplication;
import com.weiquding.map.util.ImageUtil;
import org.junit.Test;

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

}
