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
import java.util.*;

/**
 * 百度行政区划区域检索
 * http://lbsyun.baidu.com/index.php?title=webapi/guide/webservice-placeapi
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/30
 */
public class MapSearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapSearchService.class);

    public static final String BAIDU_MAP_WEB_API_AK = PropertiesUtil.getProperty("BAIDU_MAP_WEB_API_AK");

    public static final RestTemplate REST_TEMPLATE = new RestTemplate();

    static {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(MediaType.parseMediaTypes("text/javascript;charset=utf-8"));
        REST_TEMPLATE.getMessageConverters().add(0, mappingJackson2HttpMessageConverter);
    }

    public static final String URL = "http://api.map.baidu.com/place/v2/search?query={hospital}&tag=医疗&region={city}&output=json&ak=" + BAIDU_MAP_WEB_API_AK;

    /**
     * 调用百度行政区划区域检索
     * http://api.map.baidu.com/place/v2/search?query=ATM机&tag=银行&region=北京&output=json&ak=您的ak //GET请求
     *
     * @param type            数据类型
     * @param cityRefHospital 医院信息
     * @return POI信息
     */
    @SuppressWarnings("all")
    public static Set<Hospital> searchPOI(String type, Map<String, Set<String>> cityRefHospital) {
        Set<Hospital> retHospitals = new LinkedHashSet<>(6000);
        for (Map.Entry<String, Set<String>> entry : cityRefHospital.entrySet()) {
            String city = entry.getKey();
            Set<String> hospitals = entry.getValue();
            for (String hospital : hospitals) {
                Map<String, Object> firstPoi = getLocation(hospital, city);
                Hospital retHospital = new Hospital();
                Map<String, Object> location = (Map<String, Object>) firstPoi.get("location");
                if (location == null) {
                    LOGGER.warn("POI检索没有经纬度信息:[{}][{}]", hospital, city);
                    continue;
                }
                retHospital.setBd_lat(BigDecimal.valueOf((Double) location.getOrDefault("lat", 0.0)));
                retHospital.setBd_lon(BigDecimal.valueOf((Double) location.getOrDefault("lng", 0.0)));
                retHospital.setHospital(hospital);
                retHospital.setType(type);
                retHospital.setAddress((String) firstPoi.getOrDefault("address", ""));
                retHospital.setProvince((String) firstPoi.getOrDefault("province", ""));
                city = ((String) firstPoi.getOrDefault("city", city));
                retHospital.setCity(city);
                retHospitals.add(retHospital);
            }
        }
        return retHospitals;
    }

    public static Map<String, Object> getLocation(String hospital, String city) {
        Map<String, Object> result = null;
        // 资源名可使用任意有业务语义的字符串，比如方法名、接口名或其它可唯一标识的字符串。
        try (Entry spEntry = SphU.entry(MapApplication.RESOURCE_MAP_SEARCH)) {
            result = REST_TEMPLATE.getForObject(URL, Map.class, hospital, city);
        } catch (BlockException ex) {
            LOGGER.warn("POI检索限流异常:[{}][{}][{}]", hospital, city, ex.getMessage());
        }
        if (result == null) {
            LOGGER.warn("POI检索失败:[{}][{}][{}]", hospital, city, result);
            return new HashMap<>();
        }
        //本次API访问状态，如果成功返回0，如果失败返回其他数字。（见服务状态码）
        int status = (Integer) result.get("status");
        if (status != 0) {
            LOGGER.warn("POI检索失败:[{}][{}][{}]", hospital, city, result.get("message"));
            return new HashMap<>();
        }
        Map<String, Object> firstPoi = null;
        List<Map<String, Object>> results = (List<Map<String, Object>>) result.get("results");
        if (results != null && !results.isEmpty()) {
            firstPoi = results.get(0);
            for (int i = 0, size = results.size(); i < size; i++) {
                Map<String, Object> poi = results.get(i);
                String name = (String) poi.get("name");
                if (hospital.equals(name)) {
                    firstPoi = poi;
                    break;
                }
            }
        }
        if (firstPoi == null) {
            LOGGER.warn("POI检索没有结果:[{}][{}][{}]", hospital, city, result);
            return new HashMap<>();
        }
        return firstPoi;
    }
    /*
     * {
     *     "status":0,
     *     "message":"ok",
     *     "results":[
     *         {
     *             "name":"南阳市张仲景医院",
     *             "location":{
     *                 "lat":32.963638,
     *                 "lng":112.516056
     *             },
     *             "address":"南阳市宛城区雪枫西路1888号",
     *             "province":"河南省",
     *             "city":"南阳市",
     *             "area":"宛城区",
     *             "street_id":"a708e2571ca023b09c9d6892",
     *             "telephone":"(0377)83978888",
     *             "detail":1,
     *             "uid":"a708e2571ca023b09c9d6892"
     *         },
     *         {
     *             "name":"南阳市中医院",
     *             "location":{
     *                 "lat":32.991008,
     *                 "lng":112.523929
     *             },
     *             "address":"南阳市卧龙区七一路939号",
     *             "province":"河南省",
     *             "city":"南阳市",
     *             "area":"卧龙区",
     *             "street_id":"8faa60cf76caa5302fe67be0",
     *             "telephone":"(0377)63176380,(0377)63869888",
     *             "detail":1,
     *             "uid":"8faa60cf76caa5302fe67be0"
     *         },
     *         {
     *             "name":"南阳张仲景医院",
     *             "location":{
     *                 "lat":32.964087,
     *                 "lng":112.516882
     *             },
     *             "address":"19路;35路;k23路;k29路;k33路",
     *             "province":"河南省",
     *             "city":"南阳市",
     *             "area":"宛城区",
     *             "detail":0,
     *             "uid":"6414036b4889e8e6adcb66e8"
     *         },
     *         {
     *             "name":"南阳市张仲景医院-停车场",
     *             "location":{
     *                 "lat":32.96319,
     *                 "lng":112.516716
     *             },
     *             "address":"南阳市宛城区宛雪枫西路辅路南150米",
     *             "province":"河南省",
     *             "city":"南阳市",
     *             "area":"宛城区",
     *             "detail":1,
     *             "uid":"90f1ff9f09a5e5e3018972f1"
     *         },
     *         {
     *             "name":"南阳市张仲景医院-东南门",
     *             "location":{
     *                 "lat":32.962675,
     *                 "lng":112.517732
     *             },
     *             "address":"滨河西路12号附近",
     *             "province":"河南省",
     *             "city":"南阳市",
     *             "area":"宛城区",
     *             "detail":1,
     *             "uid":"a7eb6f4abfd2a8fec91b95cd"
     *         },
     *         {
     *             "name":"南阳张仲景医院-门诊",
     *             "location":{
     *                 "lat":32.963258,
     *                 "lng":112.516484
     *             },
     *             "address":"河南省南阳市宛城区雪枫西路",
     *             "province":"河南省",
     *             "city":"南阳市",
     *             "area":"宛城区",
     *             "detail":1,
     *             "uid":"979352ae99aec8138605bfca"
     *         },
     *         {
     *             "name":"南阳张仲景医院-急诊",
     *             "location":{
     *                 "lat":32.964153,
     *                 "lng":112.516711
     *             },
     *             "address":"河南省南阳市宛城区雪枫西路",
     *             "province":"河南省",
     *             "city":"南阳市",
     *             "area":"宛城区",
     *             "detail":1,
     *             "uid":"567bfdd5f96d27bc5b10934f"
     *         },
     *         {
     *             "name":"南阳张仲景医院-3号康复中心",
     *             "location":{
     *                 "lat":32.963667,
     *                 "lng":112.515325
     *             },
     *             "address":"南阳张仲景医院3号康复中心",
     *             "province":"河南省",
     *             "city":"南阳市",
     *             "area":"宛城区",
     *             "detail":1,
     *             "uid":"91c85fc126e4ffb5f942b206"
     *         },
     *         {
     *             "name":"南阳张仲景医院-1号仲景养生坊",
     *             "location":{
     *                 "lat":32.963561,
     *                 "lng":112.516626
     *             },
     *             "address":"河南省南阳市宛城区雪枫西路",
     *             "province":"河南省",
     *             "city":"南阳市",
     *             "area":"宛城区",
     *             "detail":1,
     *             "uid":"8614713bc53316b33a04b1a0"
     *         },
     *         {
     *             "name":"南阳市张仲景国医院-家属院",
     *             "location":{
     *                 "lat":32.990176,
     *                 "lng":112.524137
     *             },
     *             "address":"张仲景国医院家属院",
     *             "province":"河南省",
     *             "city":"南阳市",
     *             "area":"卧龙区",
     *             "detail":1,
     *             "uid":"b97e8b21268c695c7dc876af"
     *         }
     *     ]
     * }
     */
}
