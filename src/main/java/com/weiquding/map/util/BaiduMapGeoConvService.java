package com.weiquding.map.util;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.weiquding.map.MapApplication;
import com.weiquding.map.domain.Hospital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 百度地图坐标系转换
 * http://lbsyun.baidu.com/index.php?title=webapi/guide/changeposition
 * 坐标转换服务是一类Web API接口服务；
 * 用于将常用的非百度坐标（目前支持GPS设备获取的坐标、google地图坐标、soso地图坐标、amap地图坐标、mapbar地图坐标）转换成百度地图中使用的坐标，并可将转化后的坐标在百度地图JavaScript API、静态图API、Web服务API等产品中使用。
 * <p>
 * 功能介绍
 * 非百度坐标系转换
 * 用户可通过该服务，实现 非百度坐标系→百度坐标系转换。包括：
 * WGS84（大地坐标系）→bd09（百度坐标系）
 * GCJ02（国测局坐标系）→bd09（百度坐标系）
 * <p>
 * 单次请求可批量解析100个坐标
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/30
 */
public class BaiduMapGeoConvService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaiduMapGeoConvService.class);

    public static final String BAIDU_MAP_WEB_API_AK = PropertiesUtil.getProperty("BAIDU_MAP_WEB_API_AK");

    public static final RestTemplate REST_TEMPLATE = new RestTemplate();

    static {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(MediaType.parseMediaTypes("text/javascript;charset=utf-8"));
        REST_TEMPLATE.getMessageConverters().add(0, mappingJackson2HttpMessageConverter);
    }

    public static final String URL = "http://api.map.baidu.com/geoconv/v1/?coords={coords}&from=3&to=5&ak=" + BAIDU_MAP_WEB_API_AK;

    /**
     * 调用百度地图坐标系转换
     * http://api.map.baidu.com/geoconv/v1/?coords=114.21892734521,29.575429778924&from=1&to=5&ak=你的密钥 //GET请求
     *
     * @param type            数据类型
     * @param cityRefHospital 医院信息
     * @return POI信息
     */
    @SuppressWarnings("all")
    public static void geoConvert(Set<Hospital> hospitals) {
        Set<Hospital> retHospitals = new LinkedHashSet<>(6000);
        Hospital[] hospitalsArr = hospitals.toArray(new Hospital[hospitals.size()]);
        // 单次请求可批量解析100个坐标
        for (int i = 0, length = hospitalsArr.length; i < length; i += 100) {
            int lastIndex = i + 100 > length ? length : i + 100;
            StringBuilder builder = new StringBuilder();
            for (int j = i; j < lastIndex; j++) {
                Hospital hospital = hospitalsArr[j];
                //需转换的源坐标，多组坐标以“；”分隔 （经度，纬度）
                builder.append(hospital.getBd_lon()).append(",").append(hospital.getBd_lat()).append(";");
            }
            String coords = builder.substring(0, builder.length() - 1);
            // 资源名可使用任意有业务语义的字符串，比如方法名、接口名或其它可唯一标识的字符串。
            try (Entry spEntry = SphU.entry(MapApplication.RESOURCE_MAP_GEO_CONV)) {
                List<Map<String, Object>> points = geoConv(coords);
                if (points == null) {
                    LOGGER.warn("重新请求坐标转换API:[{}]", coords);
                    points = geoConv(coords);
                    if (points == null) {
                        LOGGER.warn("重新请求坐标转换API没有返回值:[{}]", coords);
                        continue;
                    }
                }
                int pointIndex = 0;
                for (int j = i; j < lastIndex; j++) {
                    Hospital hospital = hospitalsArr[j];
                    Map<String, Object> point = points.get(pointIndex);
                    hospital.setBd_lon(new BigDecimal(point.get("x").toString()));
                    hospital.setBd_lat(new BigDecimal(point.get("y").toString()));
                    pointIndex++;
                }
            } catch (BlockException ex) {
                LOGGER.warn("调用百度地图坐标系转换限流异常:[{}][{}]", coords, ex.getMessage());
            }
        }
    }

    public static String[] geoConv(String lng, String lat) {
        String coords = lng + "," + lat;
        // 资源名可使用任意有业务语义的字符串，比如方法名、接口名或其它可唯一标识的字符串。
        try (Entry spEntry = SphU.entry(MapApplication.RESOURCE_MAP_GEO_CONV)) {
            List<Map<String, Object>> points = geoConv(coords);
            if (points == null) {
                LOGGER.warn("重新请求坐标转换API:[{}]", coords);
                points = geoConv(coords);
                if (points == null) {
                    LOGGER.warn("重新请求坐标转换API没有返回值:[{}]", coords);
                    return new String[]{lng, lat};
                }
            }
            Map<String, Object> point = points.get(0);
            return new String[]{point.get("x").toString(), point.get("y").toString()};
        } catch (BlockException ex) {
            LOGGER.warn("调用百度地图坐标系转换限流异常:[{}][{}]", coords, ex.getMessage());
            return new String[]{lng, lat};
        }
    }

    @SuppressWarnings("all")
    private static List<Map<String, Object>> geoConv(String coords) {
        Map<String, Object> result = REST_TEMPLATE.getForObject(URL, Map.class, coords);
        //本次API访问状态，如果成功返回0，如果失败返回其他数字。（见服务状态码）
        int status = (Integer) result.get("status");
        if (status != 0) {
            LOGGER.warn("POI检索失败:[{}][{}]", coords, result);
            return null;
        }
        // 设置转换坐标
        return (List<Map<String, Object>>) result.get("result");
    }

    /*
     * {
     * 	"status": 0,
     * 	"result": [{
     * 		"x": 121.47383108516212,
     * 		"y": 31.238829800603157
     *        }, {
     * 		"x": 114.22539195429346,
     * 		"y": 29.58158536745758
     *    }]
     * }
     */
}
