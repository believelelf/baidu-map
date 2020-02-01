package com.weiquding.map;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.weiquding.map.domain.Hospital;
import com.weiquding.map.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * description
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/30
 */
public class MapApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapApplication.class);

    public static final String IMAGE_PATH = "E:\\Java\\idea_workspaces\\baidu-map\\images\\发热门诊图片";

    public static final String DD_IMAGE_PATH = "E:\\Java\\idea_workspaces\\baidu-map\\images\\定点医院图片";

    public static final String FR_NAME_JSON_PATH = "E:\\Java\\idea_workspaces\\baidu-map\\json\\发热门诊医院JSON";

    public static final String FR_POI_JSON_PATH = "E:\\Java\\idea_workspaces\\baidu-map\\json\\发热门诊POIJSON";

    public static final String DD_NAME_JSON_PATH = "E:\\Java\\idea_workspaces\\baidu-map\\json\\定点医院名称JSON";

    public static final String DD_POI_JSON_PATH = "E:\\Java\\idea_workspaces\\baidu-map\\json\\定点医院POIJSON";

    public static final String FINAL_JSON_PATH = "E:\\Java\\idea_workspaces\\baidu-map\\json\\最终输出JSON";

    public static final String RESOURCE_AIP_OCR = "AipOcr";

    public static final String RESOURCE_MAP_SEARCH = "BaiDuMapSearch";

    public static final String RESOURCE_MAP_GEO_CONV = "BaiDuMapGeoConv";

    public static final String RESOURCE_AMAP_SEARCH = "AMapSearch";

    public static void main(String[] args) throws IOException {
        //completeBaiDuMapRun();
        resumeAMapRun3();
    }

    /**
     * 完整运行百度地图搜索
     *
     * @throws IOException
     */
    private static void completeBaiDuMapRun() throws IOException {
        // 加载规则
        initFlowRules();

        // 切分图片
        ImageUtil.splitImages(IMAGE_PATH);
        ImageUtil.splitImages(DD_IMAGE_PATH);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        String date = simpleDateFormat.format(new Date());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        Map<String, Set<String>> cityRefHospital = new LinkedHashMap<>();
        AipOcrService.basicGeneralByPath(IMAGE_PATH, cityRefHospital);
        LOGGER.info("发热门诊图片-文字识别返回数据为:{}", cityRefHospital);
        objectMapper.writeValue(new File(FR_NAME_JSON_PATH + File.separator + "fr_name_" + date + ".json"), cityRefHospital);

        Set<Hospital> hospitals = MapSearchService.searchPOI("发热门诊", cityRefHospital);
        LOGGER.info("发热门诊图片-POI检索后总数:{}", hospitals.size());
        objectMapper.writeValue(new File(FR_POI_JSON_PATH + File.separator + "fr_poi_" + date + ".json"), hospitals);

        cityRefHospital = new LinkedHashMap<>();
        AipOcrService.basicGeneralByPath(DD_IMAGE_PATH, cityRefHospital);
        LOGGER.info("定点医院图片-文字识别返回数据为:{}", cityRefHospital);
        objectMapper.writeValue(new File(DD_NAME_JSON_PATH + File.separator + "dd_name_" + date + ".json"), cityRefHospital);

        Set<Hospital> ddHospitals = MapSearchService.searchPOI("定点医院", cityRefHospital);
        LOGGER.info("定点医院图片-POI检索后总数:{}", ddHospitals.size());
        objectMapper.writeValue(new File(DD_POI_JSON_PATH + File.separator + "dd_poi_" + date + ".json"), ddHospitals);

        List<Hospital> finalList = new ArrayList<>(hospitals.size() + ddHospitals.size());
        finalList.addAll(hospitals);
        finalList.addAll(ddHospitals);
        objectMapper.writeValue(new File(FINAL_JSON_PATH + File.separator + "data_" + date + ".json"), finalList);
    }

    /**
     * 完整运行高德API，包括高德地图API
     *
     * @throws IOException
     */
    public static void completeAMapRun() throws IOException {
        // 加载规则
        initFlowRules();

        // 切分图片
        ImageUtil.splitImages(IMAGE_PATH);
        ImageUtil.splitImages(DD_IMAGE_PATH);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        String date = simpleDateFormat.format(new Date());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        Map<String, Set<String>> cityRefHospital = new LinkedHashMap<>();
        AipOcrService.basicGeneralByPath(IMAGE_PATH, cityRefHospital);
        LOGGER.info("发热门诊图片-文字识别返回数据为:{}", cityRefHospital);
        objectMapper.writeValue(new File(FR_NAME_JSON_PATH + File.separator + "fr_name_" + date + ".json"), cityRefHospital);

        Set<Hospital> hospitals = AMapSearchService.searchPOI("发热门诊", cityRefHospital);
        LOGGER.info("发热门诊图片-POI检索后总数:{}", hospitals.size());
        // 写一份数据(高德坐标系)
        objectMapper.writeValue(new File(FR_POI_JSON_PATH + File.separator + "fr_amap_poi_" + date + ".json"), hospitals);

        // 转为百度坐标系
        BaiduMapGeoConvService.geoConvert(hospitals);
        // 写一份数据(百度坐标系)
        objectMapper.writeValue(new File(FR_POI_JSON_PATH + File.separator + "fr_poi_" + date + ".json"), hospitals);

        cityRefHospital = new LinkedHashMap<>();
        AipOcrService.basicGeneralByPath(DD_IMAGE_PATH, cityRefHospital);
        LOGGER.info("定点医院图片-文字识别返回数据为:{}", cityRefHospital);
        objectMapper.writeValue(new File(DD_NAME_JSON_PATH + File.separator + "dd_name_" + date + ".json"), cityRefHospital);


        Set<Hospital> ddHospitals = AMapSearchService.searchPOI("定点医院", cityRefHospital);
        LOGGER.info("定点医院图片-POI检索后总数:{}", ddHospitals.size());
        // 写一份数据(高德坐标系)
        objectMapper.writeValue(new File(DD_POI_JSON_PATH + File.separator + "dd_amap_poi_" + date + ".json"), ddHospitals);

        // 转为百度坐标系
        BaiduMapGeoConvService.geoConvert(ddHospitals);
        // 写一份数据(百度坐标系)
        objectMapper.writeValue(new File(DD_POI_JSON_PATH + File.separator + "dd_poi_" + date + ".json"), ddHospitals);

        List<Hospital> finalList = new ArrayList<>(hospitals.size() + ddHospitals.size());
        finalList.addAll(hospitals);
        finalList.addAll(ddHospitals);
        objectMapper.writeValue(new File(FINAL_JSON_PATH + File.separator + "data_" + date + ".json"), finalList);
    }

    /**
     * 特定情况下中断恢复运行
     *
     * @throws IOException
     */
    public static void resumeBaiDuMapRun() throws IOException {
        // 加载规则
        initFlowRules();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        String date = simpleDateFormat.format(new Date());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        Set<Hospital> hospitals = objectMapper.readValue(new File(FR_POI_JSON_PATH + File.separator + "fr_poi_202001311929.json"), new TypeReference<LinkedHashSet<Hospital>>() {
        });
        LOGGER.info("发热门诊图片-重新加载后总数:{}", hospitals.size());
        // 过滤前期保留的未检索出值的无效数据
        hospitals.removeIf(hospital ->
                hospital.getProvince() == null
                        || hospital.getBd_lat() == null
                        || hospital.getBd_lon() == null
                        || hospital.getAddress() == null
        );
        // 重新写一份数据
        objectMapper.writeValue(new File(FR_POI_JSON_PATH + File.separator + "fr_poi_" + date + ".json"), hospitals);

        Map<String, Set<String>> cityRefHospital = objectMapper.readValue(new File(DD_NAME_JSON_PATH + File.separator + "dd_name_202001311929.json"), new TypeReference<LinkedHashMap<String, Set<String>>>() {
        });
        LOGGER.info("定点医院图片-重新加载后数据为:{}", cityRefHospital);

        Set<Hospital> ddHospitals = MapSearchService.searchPOI("定点医院", cityRefHospital);
        LOGGER.info("定点医院图片-POI检索后总数:{}", ddHospitals.size());
        objectMapper.writeValue(new File(DD_POI_JSON_PATH + File.separator + "dd_poi_" + date + ".json"), ddHospitals);

        List<Hospital> finalList = new ArrayList<>(hospitals.size() + ddHospitals.size());
        finalList.addAll(hospitals);
        finalList.addAll(ddHospitals);
        objectMapper.writeValue(new File(FINAL_JSON_PATH + File.separator + "data_" + date + ".json"), finalList);
    }


    /**
     * 特定情况下中断恢复运行
     *
     * @throws IOException
     */
    public static void resumeBaiDuMapRun2() throws IOException {
        // 加载规则
        initFlowRules();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        String date = simpleDateFormat.format(new Date());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        Map<String, Set<String>> cityRefHospital = objectMapper.readValue(new File(FR_NAME_JSON_PATH + File.separator + "fr_name_202001311929.json"), new TypeReference<LinkedHashMap<String, Set<String>>>() {
        });
        Set<Hospital> hospitals = MapSearchService.searchPOI("发热门诊", cityRefHospital);

        LOGGER.info("发热门诊图片-重新加载后总数:{}", hospitals.size());
        // 重新写一份数据
        objectMapper.writeValue(new File(FR_POI_JSON_PATH + File.separator + "fr_poi_" + date + ".json"), hospitals);

        cityRefHospital = objectMapper.readValue(new File(DD_NAME_JSON_PATH + File.separator + "dd_name_202001311929.json"), new TypeReference<LinkedHashMap<String, Set<String>>>() {
        });
        LOGGER.info("定点医院图片-重新加载后数据为:{}", cityRefHospital);

        Set<Hospital> ddHospitals = MapSearchService.searchPOI("定点医院", cityRefHospital);
        LOGGER.info("定点医院图片-POI检索后总数:{}", ddHospitals.size());
        objectMapper.writeValue(new File(DD_POI_JSON_PATH + File.separator + "dd_poi_" + date + ".json"), ddHospitals);

        List<Hospital> finalList = new ArrayList<>(hospitals.size() + ddHospitals.size());
        finalList.addAll(hospitals);
        finalList.addAll(ddHospitals);
        objectMapper.writeValue(new File(FINAL_JSON_PATH + File.separator + "data_" + date + ".json"), finalList);
    }

    /**
     * 特定情况下中断恢复运行，包括高德地图API
     *
     * @throws IOException
     */
    public static void resumeAMapRun() throws IOException {
        // 加载规则
        initFlowRules();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        String date = simpleDateFormat.format(new Date());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        Map<String, Set<String>> cityRefHospital = objectMapper.readValue(new File(FR_NAME_JSON_PATH + File.separator + "fr_name_202001311929.json"), new TypeReference<LinkedHashMap<String, Set<String>>>() {
        });
        Set<Hospital> hospitals = AMapSearchService.searchPOI("发热门诊", cityRefHospital);
        LOGGER.info("发热门诊图片-重新加载后总数:{}", hospitals.size());
        // 写一份数据(高德坐标系)
        objectMapper.writeValue(new File(FR_POI_JSON_PATH + File.separator + "fr_amap_poi_" + date + ".json"), hospitals);

        // 转为百度坐标系
        BaiduMapGeoConvService.geoConvert(hospitals);
        // 写一份数据(百度坐标系)
        objectMapper.writeValue(new File(FR_POI_JSON_PATH + File.separator + "fr_poi_" + date + ".json"), hospitals);


        cityRefHospital = objectMapper.readValue(new File(DD_NAME_JSON_PATH + File.separator + "dd_name_202001311929.json"), new TypeReference<LinkedHashMap<String, Set<String>>>() {
        });
        LOGGER.info("定点医院图片-重新加载后数据为:{}", cityRefHospital);

        Set<Hospital> ddHospitals = AMapSearchService.searchPOI("定点医院", cityRefHospital);
        LOGGER.info("定点医院图片-POI检索后总数:{}", ddHospitals.size());
        // 写一份数据(高德坐标系)
        objectMapper.writeValue(new File(DD_POI_JSON_PATH + File.separator + "dd_amap_poi_" + date + ".json"), ddHospitals);

        // 转为百度坐标系
        BaiduMapGeoConvService.geoConvert(ddHospitals);
        // 写一份数据(百度坐标系)
        objectMapper.writeValue(new File(DD_POI_JSON_PATH + File.separator + "dd_poi_" + date + ".json"), ddHospitals);

        List<Hospital> finalList = new ArrayList<>(hospitals.size() + ddHospitals.size());
        finalList.addAll(hospitals);
        finalList.addAll(ddHospitals);
        objectMapper.writeValue(new File(FINAL_JSON_PATH + File.separator + "data_" + date + ".json"), finalList);
    }
    /**
     * 特定情况下中断恢复运行，包括高德地图API
     *
     * @throws IOException
     */
    public static void resumeAMapRun2() throws IOException {
        // 加载规则
        initFlowRules();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        String date = simpleDateFormat.format(new Date());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        Set<Hospital> hospitals = objectMapper.readValue(new File(FR_POI_JSON_PATH + File.separator + "fr_amap_poi_202002011444.json"), new TypeReference<LinkedHashSet<Hospital>>() {
        });
        LOGGER.info("发热门诊图片-重新加载后总数:{}", hospitals.size());

        // 转为百度坐标系
        BaiduMapGeoConvService.geoConvert(hospitals);
        // 写一份数据(百度坐标系)
        objectMapper.writeValue(new File(FR_POI_JSON_PATH + File.separator + "fr_poi_" + date + ".json"), hospitals);


        Map<String, Set<String>> cityRefHospital = objectMapper.readValue(new File(DD_NAME_JSON_PATH + File.separator + "dd_name_202001311929.json"), new TypeReference<LinkedHashMap<String, Set<String>>>() {
        });
        LOGGER.info("定点医院图片-重新加载后数据为:{}", cityRefHospital);

        Set<Hospital> ddHospitals = AMapSearchService.searchPOI("定点医院", cityRefHospital);
        LOGGER.info("定点医院图片-POI检索后总数:{}", ddHospitals.size());
        // 写一份数据(高德坐标系)
        objectMapper.writeValue(new File(DD_POI_JSON_PATH + File.separator + "dd_amap_poi_" + date + ".json"), ddHospitals);

        // 转为百度坐标系
        BaiduMapGeoConvService.geoConvert(ddHospitals);
        // 写一份数据(百度坐标系)
        objectMapper.writeValue(new File(DD_POI_JSON_PATH + File.separator + "dd_poi_" + date + ".json"), ddHospitals);

        List<Hospital> finalList = new ArrayList<>(hospitals.size() + ddHospitals.size());
        finalList.addAll(hospitals);
        finalList.addAll(ddHospitals);
        objectMapper.writeValue(new File(FINAL_JSON_PATH + File.separator + "data_" + date + ".json"), finalList);
    }

 /**
     * 特定情况下中断恢复运行，包括高德地图API
     *
     * @throws IOException
     */
    public static void resumeAMapRun3() throws IOException {
        // 加载规则
        initFlowRules();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        Set<Hospital> hospitals = objectMapper.readValue(new File(FR_POI_JSON_PATH + File.separator + "fr_poi_202002011540.json"), new TypeReference<LinkedHashSet<Hospital>>() {
        });
        LOGGER.info("发热门诊图片-重新加载后总数:{}", hospitals.size());


        Set<Hospital> ddHospitals= objectMapper.readValue(new File(DD_POI_JSON_PATH + File.separator + "dd_amap_poi_202002011540.json"), new TypeReference<LinkedHashSet<Hospital>>() {
        });
        LOGGER.info("定点医院图片-重新加载后总数:{}", ddHospitals.size());

        // 转为百度坐标系
        BaiduMapGeoConvService.geoConvert(ddHospitals);
        // 写一份数据(百度坐标系)
        objectMapper.writeValue(new File(DD_POI_JSON_PATH + File.separator + "dd_poi_202002011540.json"), ddHospitals);

        Set<Hospital> finalList = new LinkedHashSet<>(hospitals.size() + ddHospitals.size());
        finalList.addAll(hospitals);
        finalList.addAll(ddHospitals);
        objectMapper.writeValue(new File(FINAL_JSON_PATH + File.separator + "data_202002011540.json"), finalList);
    }

    /**
     * 初始化Sentinel流控规则
     * https://github.com/alibaba/Sentinel/wiki/%E6%B5%81%E9%87%8F%E6%8E%A7%E5%88%B6-%E5%8C%80%E9%80%9F%E6%8E%92%E9%98%9F%E6%A8%A1%E5%BC%8F
     * https://github.com/alibaba/Sentinel/blob/master/sentinel-demo/sentinel-demo-basic/src/main/java/com/alibaba/csp/sentinel/demo/flow/PaceFlowDemo.java
     * <p>
     * If {@link RuleConstant#CONTROL_BEHAVIOR_RATE_LIMITER} is set, incoming
     * requests are passing at regular interval. When a new request arrives, the
     * flow rule checks whether the interval between the new request and the
     * previous request. If the interval is less than the count set in the rule
     * first. If the interval is large, it will pass the request; otherwise,
     * sentinel will calculate the waiting time for this request. If the waiting
     * time is longer than the {@link FlowRule#maxQueueingTimeMs} set in the rule,
     * the request will be rejected immediately.
     */
    private static void initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();
        // 通用文字识别 免费使用 50000次/天 QPS限制：2
        FlowRule rule = new FlowRule();
        rule.setResource(RESOURCE_AIP_OCR);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(2);
        rule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER);
        //  Max queueing time in rate limiter behavior. 每一个请求的最长等待时间2h
        rule.setMaxQueueingTimeMs(2 * 60 * 60 * 1000);
        rules.add(rule);

        // 百度地图地点检索
        rule = new FlowRule();
        rule.setResource(RESOURCE_MAP_SEARCH);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        /*
         * QPS上限：50QPS）
         */
        rule.setCount(50);
        rule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER);
        //  Max queueing time in rate limiter behavior. 每一个请求的最长等待时间2h
        rule.setMaxQueueingTimeMs(2 * 60 * 60 * 1000);
        rules.add(rule);

        // 百度地图坐标转换
        rule = new FlowRule();
        rule.setResource(RESOURCE_MAP_GEO_CONV);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        /*
         * QPS上限：200QPS）
         */
        rule.setCount(200);
        rule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER);
        //  Max queueing time in rate limiter behavior. 每一个请求的最长等待时间2h
        rule.setMaxQueueingTimeMs(2 * 60 * 60 * 1000);
        rules.add(rule);

        // 高德地图关键字搜索
        rule = new FlowRule();
        rule.setResource(RESOURCE_AMAP_SEARCH);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        /*
         * 并发量上限（次/秒）: 50
         */
        rule.setCount(50);
        rule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER);
        //  Max queueing time in rate limiter behavior. 每一个请求的最长等待时间2h
        rule.setMaxQueueingTimeMs(2 * 60 * 60 * 1000);
        rules.add(rule);

        FlowRuleManager.loadRules(rules);
    }

}
