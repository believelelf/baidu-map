package com.weiquding.util;

import com.weiquding.map.MapApplication;
import com.weiquding.map.util.AreaUtil;
import org.junit.Test;

/**
 * 根据省份城市的所有城市，创建目录
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/31
 */
public class AreaUtilTests {

    @Test
    public void testMkdir() {
        AreaUtil.mkdir(MapApplication.IMAGE_PATH);
        AreaUtil.mkdir(MapApplication.DD_IMAGE_PATH);
    }
}
