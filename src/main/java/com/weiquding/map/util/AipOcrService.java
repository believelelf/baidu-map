package com.weiquding.map.util;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.baidu.aip.ocr.AipOcr;
import com.weiquding.map.MapApplication;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * AipOcr是Optical Character Recognition的Java客户端，为使用Optical Character Recognition的开发人员提供了一系列的交互方法。
 * <p>
 * 用户可以参考如下代码新建一个AipOcr,初始化完成后建议单例使用,避免重复获取access_token：
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/30
 */

public class AipOcrService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AipOcrService.class);

    //设置APPID/AK/SK
    private static final String APP_ID = PropertiesUtil.getProperty("APP_ID");
    private static final String API_KEY = PropertiesUtil.getProperty("API_KEY");
    private static final String SECRET_KEY = PropertiesUtil.getProperty("SECRET_KEY");

    // 初始化一个AipOcr
    private static final AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

    static {
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
    }

    private static final Set<String> FILTER_STRINGS = new HashSet<>();

    static {
        FILTER_STRINGS.add("没有更多数据了");
        FILTER_STRINGS.add("本服务由中国政府网和健康中国联合提供");
        FILTER_STRINGS.add("(数据持续更新中)");
        FILTER_STRINGS.add("二级");
        FILTER_STRINGS.add("三级");
        FILTER_STRINGS.add("一级");
        FILTER_STRINGS.add("二级专科");
        FILTER_STRINGS.add("三级专科");
        FILTER_STRINGS.add("一级专科");
        FILTER_STRINGS.add("二级甲等");
        FILTER_STRINGS.add("三级甲等");
        FILTER_STRINGS.add("一级甲等");
        FILTER_STRINGS.add("综合二甲");
        FILTER_STRINGS.add("综合三甲");
        FILTER_STRINGS.add("综合一甲");
        FILTER_STRINGS.add("综合二乙");
        FILTER_STRINGS.add("综合三乙");
        FILTER_STRINGS.add("综合一乙");
        FILTER_STRINGS.add("二级甲");
        FILTER_STRINGS.add("三级甲");
        FILTER_STRINGS.add("一级甲");
        FILTER_STRINGS.add("二级乙等");
        FILTER_STRINGS.add("三级乙等");
        FILTER_STRINGS.add("一级乙等");
        FILTER_STRINGS.add("二级乙");
        FILTER_STRINGS.add("三级乙");
        FILTER_STRINGS.add("一级乙");
        FILTER_STRINGS.add("二级综合");
        FILTER_STRINGS.add("三级综合");
        FILTER_STRINGS.add("一级综合");
        FILTER_STRINGS.add("二级医院");
        FILTER_STRINGS.add("三级医院");
        FILTER_STRINGS.add("一级医院");
        FILTER_STRINGS.add("二甲医院");
        FILTER_STRINGS.add("三甲医院");
        FILTER_STRINGS.add("一甲医院");
        FILTER_STRINGS.add("二乙医院");
        FILTER_STRINGS.add("三乙医院");
        FILTER_STRINGS.add("一乙医院");
        FILTER_STRINGS.add("二级未定级");
        FILTER_STRINGS.add("三级未定级");
        FILTER_STRINGS.add("一级未定级");
        FILTER_STRINGS.add("二级未评审");
        FILTER_STRINGS.add("三级未评审");
        FILTER_STRINGS.add("一级未评审");
        FILTER_STRINGS.add("级");
        FILTER_STRINGS.add("无");
        FILTER_STRINGS.add("二甲");
        FILTER_STRINGS.add("三甲");
        FILTER_STRINGS.add("一甲");
        FILTER_STRINGS.add("二/甲");
        FILTER_STRINGS.add("三/甲");
        FILTER_STRINGS.add("一/甲");
        FILTER_STRINGS.add("二乙");
        FILTER_STRINGS.add("三乙");
        FILTER_STRINGS.add("一乙");
        FILTER_STRINGS.add("二/乙");
        FILTER_STRINGS.add("三/乙");
        FILTER_STRINGS.add("一/乙");
        FILTER_STRINGS.add("未定级");
        FILTER_STRINGS.add("点赞");
        FILTER_STRINGS.add("收藏");
        FILTER_STRINGS.add("上一页");
        FILTER_STRINGS.add("下一页");
        FILTER_STRINGS.add("第1页");
        FILTER_STRINGS.add("第2页");
        FILTER_STRINGS.add("第3页");
        FILTER_STRINGS.add("第4页");
        FILTER_STRINGS.add("发热门诊");
        FILTER_STRINGS.add("定点医院");
        FILTER_STRINGS.add("请输入");
        FILTER_STRINGS.add("搜索");
        FILTER_STRINGS.add("新型冠状病毒肺炎就医查询");
        FILTER_STRINGS.add("级综合");
        FILTER_STRINGS.add("级乙等");
        FILTER_STRINGS.add("级甲等");
        FILTER_STRINGS.add("「二级");
        FILTER_STRINGS.add("「一级");
        FILTER_STRINGS.add("「三级");
        FILTER_STRINGS.add("L二级");
        FILTER_STRINGS.add("L一级");
        FILTER_STRINGS.add("L三级");
        FILTER_STRINGS.add("级医院");
        FILTER_STRINGS.add("甲");
        FILTER_STRINGS.add("乙");
        FILTER_STRINGS.add("级未定级");
        FILTER_STRINGS.add("级未评审");
        FILTER_STRINGS.add("甲医院");
        FILTER_STRINGS.add("乙医院");
        FILTER_STRINGS.add("级甲");
        FILTER_STRINGS.add("级乙");
        FILTER_STRINGS.add("甲等");
        FILTER_STRINGS.add("乙等");
        FILTER_STRINGS.add("一级甲等」");
        FILTER_STRINGS.add("三级甲等」");
        FILTER_STRINGS.add("二级甲等」");
        FILTER_STRINGS.add("一级乙等」");
        FILTER_STRINGS.add("二级乙等」");
        FILTER_STRINGS.add("三级乙等」");
        FILTER_STRINGS.add("「三级甲等");
        FILTER_STRINGS.add("「二级甲等");
        FILTER_STRINGS.add("「一级甲等");
        FILTER_STRINGS.add("「二级乙等");
        FILTER_STRINGS.add("「一级乙等");
        FILTER_STRINGS.add("「三级乙等");
        FILTER_STRINGS.add("「二级甲等」");
        FILTER_STRINGS.add("「一级甲等」");
        FILTER_STRINGS.add("「三级甲等」");
        FILTER_STRINGS.add("「二级乙等」");
        FILTER_STRINGS.add("「一级乙等」");
        FILTER_STRINGS.add("「三级乙等」");
        FILTER_STRINGS.add("一级综合」");
        FILTER_STRINGS.add("二级综合」");
        FILTER_STRINGS.add("三级综合」");
        FILTER_STRINGS.add("「一级综合」");
        FILTER_STRINGS.add("「二级综合」");
        FILTER_STRINGS.add("「三级综合」");
        FILTER_STRINGS.add("「一级综合");
        FILTER_STRINGS.add("「二级综合");
        FILTER_STRINGS.add("「三级综合");
        FILTER_STRINGS.add("没有更多数据了~");
        FILTER_STRINGS.add("未定级了~");
        FILTER_STRINGS.add("三级未定");
        FILTER_STRINGS.add("未定级了");
        FILTER_STRINGS.add("匚末定级");
        FILTER_STRINGS.add("未定级」");
        FILTER_STRINGS.add("「未定级」");
        FILTER_STRINGS.add("「未定级");
        FILTER_STRINGS.add("级未定等");
        FILTER_STRINGS.add("三甲」");
        FILTER_STRINGS.add("「三甲」");
        FILTER_STRINGS.add("「三甲");
        FILTER_STRINGS.add("凸点赞");
        FILTER_STRINGS.add("□一级");
        FILTER_STRINGS.add("甲医");
        FILTER_STRINGS.add("数据");
        FILTER_STRINGS.add("「二级甲");
        FILTER_STRINGS.add("◇收藏");
    }

    /**
     * 根据文件路径进行文字识别
     *
     * @param city 城市
     * @param path 文件路径
     * @return 医院文字
     */
    public static Set<String> basicGeneral(String city, String path) {
        List<String> list = new ArrayList<>(300);
        // 资源名可使用任意有业务语义的字符串，比如方法名、接口名或其它可唯一标识的字符串。
        try (Entry entry = SphU.entry(MapApplication.RESOURCE_AIP_OCR)) {
            JSONObject res = client.basicGeneral(path, new HashMap<String, String>());
            LOGGER.info("路径path:[{}]识别结果为:[{}]", path, res);
            JSONArray wordsResult = res.getJSONArray("words_result");
            for (int i = 0; i < wordsResult.length(); i++) {
                JSONObject jsonObject = wordsResult.getJSONObject(i);
                String words = jsonObject.getString("words");
                if (!FILTER_STRINGS.contains(words)) {
                    list.add(words);
                }
            }
        } catch (BlockException ex) {
            LOGGER.warn("通用文字识别异常==>城市:[{}],路径:[{}],异常:[{}]", new Object[]{city, path, ex.getMessage()});
        }
        // 过滤逻辑
        return filter(city, list);
    }

    /**
     * 特殊地址处理
     *
     * @param city 城市
     * @param list 原地址数据
     * @return 处理后地址
     */
    private static Set<String> filter(String city, List<String> list) {
        Set<String> retList = new LinkedHashSet<>(list.size());
        for (int i = 0, size = list.size(); i < size; i++) {
            String str = list.get(i);
            if (str.length() > 17 && i + 1 < size && list.get(i + 1).length() < 7) {
                str = str + list.get(i + 1);
                LOGGER.warn("特殊地址拼接==>城市:[{}],Prefix:[{}],Suffix:[{}],Append:[{}]", new Object[]{city, list.get(i), list.get(i + 1), str});
                i++;
            } else if (str.length() < 4 || (str.length() < 6 && str.contains("级"))) {
                LOGGER.warn("特殊地址过滤==>城市:[{}],Str:[{}]", new Object[]{city, str});
                continue;
            }

            if (str.matches(".*\\?+2*$")) {
                //"岷县人民医院?2", "漳县中医院???2"
                String temp = str;
                str = str.replaceFirst("\\?+2*$", "");
                LOGGER.warn("特殊地址处理==>城市:[{}],Str:[{}],Replace:[{}]", new Object[]{city, temp, str});
            }
            retList.add(str);
        }
        return retList;
    }

    /**
     * 根据文件目录进行文字识别
     *
     * @param dir             文件目录
     * @param cityRefHospital 文件目录
     */
    public static void basicGeneralByPath(String dir, Map<String, Set<String>> cityRefHospital) {
        File file = new File(dir);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File subFile : files) {
                    basicGeneralByPath(subFile.getPath(), cityRefHospital);
                }
            }
        } else {
            // 增加配置，只处理切分图
            String name = file.getName();
            if (name.contains(ImageUtil.SPLIT_IMAGE_NAME)) {
                String city = file.getParent().substring(file.getParent().lastIndexOf("\\") + 1);
                Set<String> hospitals = cityRefHospital.computeIfAbsent(city, k -> new LinkedHashSet<String>());
                Set<String> basicGenerals = basicGeneral(city, file.getPath());
                hospitals.addAll(basicGenerals);
            }

        }
    }
}
