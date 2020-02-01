package com.weiquding.map.util;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.weiquding.map.MapApplication;
import com.weiquding.map.domain.Hospital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 高德关键字搜索检索
 * https://lbs.amap.com/api/webservice/guide/api/search
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/30
 */
public class AMapSearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AMapSearchService.class);

    public static final String AMAP_WEB_API_KEY = PropertiesUtil.getProperty("AMAP_WEB_API_KEY");

    public static final RestTemplate REST_TEMPLATE = new RestTemplate();

    public static final String URL = "https://restapi.amap.com/v3/place/text?keywords={hospital}&city={city}&children=1&offset=20&page=1&extensions=base&output=JSON&citylimit=false&key=" + AMAP_WEB_API_KEY;

    public static final String NO_CITY_URL = "https://restapi.amap.com/v3/place/text?keywords={hospital}&children=1&offset=20&page=1&extensions=base&output=JSON&citylimit=false&key=" + AMAP_WEB_API_KEY;

    /**
     * 医院转换
     */
    public static final Map<String, String> CONVERT_HOSPITAL_MAP = new ConcurrentHashMap<>();

    static {
        CONVERT_HOSPITAL_MAP.put("海旅社区卫生服务中心(精卫中心)", "上海市奉贤区海湾医院");
        CONVERT_HOSPITAL_MAP.put("昌宁县妇幼保健计划生育服务中心", "昌宁县妇幼保健院");
        CONVERT_HOSPITAL_MAP.put("昌宁天和医院", "云南省保山市昌宁县昌宁天和医院");
        CONVERT_HOSPITAL_MAP.put("弥渡县妇幼保健计划生育服务中心", "弥渡县妇幼保健院");
        CONVERT_HOSPITAL_MAP.put("大理民族医院有限责任公司", "云南省大理白族自治州大理市大理民族医院");
        CONVERT_HOSPITAL_MAP.put("瑞丽市妇幼保健计划生育服务中心", "瑞丽市妇幼保健院");
        CONVERT_HOSPITAL_MAP.put("丘北县妇幼保健计划生育服务中心", "丘北县妇幼保健院");
        CONVERT_HOSPITAL_MAP.put("云南省保健康复中心(原云南省干部疗养院)", "云南省保健康复中心");
        CONVERT_HOSPITAL_MAP.put("宣威市妇幼保健计划生育服务中心", "宣威市妇幼保健院");
        CONVERT_HOSPITAL_MAP.put("罗平县妇幼保健计划生育服务中心", "罗平县妇幼保健院");
        CONVERT_HOSPITAL_MAP.put("牟定县妇幼保健计划生育服务中心", "牟定县妇幼保健院");
        CONVERT_HOSPITAL_MAP.put("姚安县妇幼保健院", "姚安县妇幼保健计划生育中心");
        CONVERT_HOSPITAL_MAP.put("内蒙古第一机械集团有限公司医院", "内蒙古第一机械集团有限公司离退休干部管理中心");
        CONVERT_HOSPITAL_MAP.put("赤峰市精神病防治院", "赤峰市精神卫生中心");
        CONVERT_HOSPITAL_MAP.put("平罗县妇幼保健计划生育服务中心", "平罗县妇幼保健院");
        CONVERT_HOSPITAL_MAP.put("曲阜市妇幼保健计划生育服务中心", "曲阜市妇幼保健院");
        CONVERT_HOSPITAL_MAP.put("解放军第80集团军医院", "中国人民解放军第八十九医院");
        CONVERT_HOSPITAL_MAP.put("高唐县妇幼保健计划生育服务中心", "高唐县计划生育服务站");
        CONVERT_HOSPITAL_MAP.put("86团医院", "八十六团医院");
        CONVERT_HOSPITAL_MAP.put("石大一附院", "石河子大学医学院第一附属医院");

    }

    /**
     * 高德关键字搜索API服务地址：
     * https://restapi.amap.com/v3/place/text?keywords=北京大学&city=beijing&output=xml&offset=20&page=1&key=<用户的key>&extensions=all
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
                Map<String, Object> result = null;
                // 资源名可使用任意有业务语义的字符串，比如方法名、接口名或其它可唯一标识的字符串。
                try (Entry spEntry = SphU.entry(MapApplication.RESOURCE_AMAP_SEARCH)) {
                    result = REST_TEMPLATE.getForObject(URL, Map.class, convertHospital(hospital), city);
                    Hospital retHospital = getHospital(type, city, result, hospital);
                    if (retHospital == null) {
                        // 容错,执行全国查询
                        LOGGER.warn("POI检索===》开始执行全国查询:[{}][{}]", hospital, city);
                        result = REST_TEMPLATE.getForObject(NO_CITY_URL, Map.class, convertHospital(hospital));
                        retHospital = getHospital(type, city, result, hospital);
                    }
                    if (retHospital == null) {
                        LOGGER.warn("POI检索===》全国查询没有结果:[{}][{}]", hospital, city);
                        continue;
                    }
                    retHospitals.add(retHospital);
                } catch (BlockException ex) {
                    LOGGER.warn("POI检索限流异常:[{}][{}][{}]", hospital, city, ex.getMessage());
                } catch (Exception e) {
                    LOGGER.warn("POI检索异常:[{}][{}][{}]", hospital, city, e.getMessage());
                }
            }
        }
        return retHospitals;
    }

    /**
     * 针对部分数据，无法搜索的情况，进行容错。
     *
     * @param hospital 医院名称
     * @return 容错名称
     */
    private static String convertHospital(String hospital) {
        return CONVERT_HOSPITAL_MAP.getOrDefault(hospital, hospital);
    }

    @SuppressWarnings("all")
    public static Hospital getHospital(String type, String city, Map<String, Object> result, String hospital) {
        if (result == null) {
            LOGGER.warn("POI检索失败:[{}][{}][{}]", hospital, city, result);
            return null;
        }
        //结果状态值，值为0或1==》0：请求失败；1：请求成功
        String status = (String) result.get("status");
        if ("0".equals(status)) {
            LOGGER.warn("POI检索失败:[{}][{}][{}]", hospital, city, result.get("info"));
            return null;
        }
        Map<String, Object> firstPoi = null;
        List<Map<String, Object>> results = (List<Map<String, Object>>) result.get("pois");
        boolean isSugAddress = false;
        if (results == null || results.isEmpty()) {
            //  建议地址结果当搜索结果并非是POI（是地址时），且没有搜索到POI时返回
            isSugAddress = true;
            LOGGER.warn("POI检索容错，取sug_address:[{}][{}][{}]", hospital, city, result);
            firstPoi = (Map<String, Object>) result.get("sug_address");
        }
        if (results != null && !results.isEmpty()) {
            firstPoi = results.get(0);
            for (int i = 0, size = results.size(); i < size; i++) {
                Map<String, Object> poi = results.get(i);
                String name = getValueFromPoi(poi, "name", "");
                if (hospital.equals(name)) {
                    firstPoi = poi;
                    break;
                }
            }
        }
        if (firstPoi == null || firstPoi.isEmpty()) {
            LOGGER.warn("POI检索没有结果:[{}][{}][{}]", hospital, city, result);
            return null;
        }
        if (isSugAddress) {
            //对于建议地址sug_address必定是name包含hospital
            String name = getValueFromPoi(firstPoi, "name", "");
            String subHospital = hospital.length() > 2 ? hospital.substring(0, 2) : hospital;
            if (!name.contains(subHospital)) {
                LOGGER.warn("POI检索没有结果==>建议地址不包含医院短名称:城市:[{}],建议地址:[{}],医院名称:[{}]", city, name, hospital);
                return null;
            }
        }
        Hospital retHospital = new Hospital();
        //经纬度==>格式：X,Y
        String location = getValueFromPoi(firstPoi, "location", "");
        if (location == null || location.isEmpty()) {
            LOGGER.warn("POI检索没有经纬度信息:[{}][{}][{}]", hospital, city, result);
            return null;
        }
        String[] locations = location.split(",");
        retHospital.setBd_lon(new BigDecimal(locations[0]));
        retHospital.setBd_lat(new BigDecimal(locations[1]));
        retHospital.setHospital(hospital);
        retHospital.setType(type);
        retHospital.setAddress(getValueFromPoi(firstPoi, "address", getValueFromPoi(firstPoi, "adname", "")));
        retHospital.setProvince(getValueFromPoi(firstPoi, "pname", ""));
        city = getValueFromPoi(firstPoi, "cityname", city);
        city = city.replace("特别行政区", "").replace("市", "").replace("县", "");
        retHospital.setCity(city);
        return retHospital;
    }

    private static String getValueFromPoi(Map<String, Object> poi, String key, String defaultValue) {
        Object value = poi.get(key);
        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof List) {
            List<String> list = (List<String>) value;
            if (!list.isEmpty()) {
                return list.get(0);
            }
        }
        return defaultValue;
    }

    /*
     * 结果示例：
     * {
     * "status": "1",
     * "count": "5",
     * "info": "OK",
     * "infocode": "10000",
     * "suggestion": {
     * "keywords": [],
     * "cities": []
     * },
     * "pois": [{
     * "id": "B0FFI8C1Q3",
     * "parent": [],
     * "childtype": [],
     * "name": "沭阳中西医结合医院",
     * "type": "医疗保健服务;医疗保健服务场所;医疗保健服务场所",
     * "typecode": "090000",
     * "biz_type": [],
     * "address": "205国道",
     * "location": "118.750099,34.165847",
     * "tel": "0527-83313190",
     * "distance": [],
     * "biz_ext": [],
     * "pname": "江苏省",
     * "cityname": "宿迁市",
     * "adname": "沭阳县",
     * "importance": [],
     * "shopid": [],
     * "shopinfo": "0",
     * "poiweight": [],
     * "photos": []
     * }, {
     * "id": "B0FFGENNDI",
     * "parent": [],
     * "childtype": [],
     * "name": "沭阳县中健大药房",
     * "type": "医疗保健服务;医药保健销售店;药房",
     * "typecode": "090601",
     * "biz_type": [],
     * "address": "上海中路28号中医院西门",
     * "location": "118.779434,34.125662",
     * "tel": [],
     * "distance": [],
     * "biz_ext": [],
     * "pname": "江苏省",
     * "cityname": "宿迁市",
     * "adname": "沭阳县",
     * "importance": [],
     * "shopid": [],
     * "shopinfo": "0",
     * "poiweight": [],
     * "photos": []
     * }, {
     * "id": "B0FFHR3Z5Z",
     * "parent": [],
     * "childtype": [],
     * "name": "中国联通(中医院西门营业厅店)",
     * "type": "生活服务;电讯营业厅;中国联通营业厅",
     * "typecode": "070604",
     * "biz_type": [],
     * "address": "上海中路99号中医院西门南边",
     * "location": "118.779331,34.125542",
     * "tel": [],
     * "distance": [],
     * "biz_ext": [],
     * "pname": "江苏省",
     * "cityname": "宿迁市",
     * "adname": "沭阳县",
     * "importance": [],
     * "shopid": [],
     * "shopinfo": "0",
     * "poiweight": [],
     * "photos": []
     * }, {
     * "id": "B0FFJ5H4R3",
     * "parent": [],
     * "childtype": [],
     * "name": "峰力助听器",
     * "type": "医疗保健服务;医药保健销售店;医疗保健用品",
     * "typecode": "090602",
     * "biz_type": [],
     * "address": "上海中路72-1号(中医院西门向南50米)峰力助听器",
     * "location": "118.779293,34.124276",
     * "tel": "18936916755",
     * "distance": [],
     * "biz_ext": [],
     * "pname": "江苏省",
     * "cityname": "宿迁市",
     * "adname": "沭阳县",
     * "importance": [],
     * "shopid": [],
     * "shopinfo": "1",
     * "poiweight": [],
     * "photos": []
     * }, {
     * "id": "B0FFKG1OKZ",
     * "parent": "B020F0IGJS",
     * "childtype": "202",
     * "name": "淮阳面馆",
     * "type": "餐饮服务;中餐厅;综合酒楼",
     * "typecode": "050101",
     * "biz_type": "diner",
     * "address": "上海中路中医院西门北万德福南金洁蛋糕东50米",
     * "location": "118.779712,34.126008",
     * "tel": "15850987707",
     * "distance": [],
     * "biz_ext": [],
     * "pname": "江苏省",
     * "cityname": "宿迁市",
     * "adname": "沭阳县",
     * "importance": [],
     * "shopid": [],
     * "shopinfo": "0",
     * "poiweight": [],
     * "photos": []
     * }]
     * }
     */
}
