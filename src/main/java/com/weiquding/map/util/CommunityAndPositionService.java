package com.weiquding.map.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 腾讯-新型肺炎小区速查
 * https://ncov.html5.qq.com/community?channelid=17&from=timeline&isappinstalled=0
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/7
 */
public class CommunityAndPositionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommunityAndPositionService.class);

    public static final RestTemplate REST_TEMPLATE = new RestTemplate();

    public static final String GET_POSITION_URL = "https://ncov.html5.qq.com/api/getPosition";

    public static final String GET_COMMUNITY_URL = "https://ncov.html5.qq.com/api/getCommunity?province={province}&city={city}&district={district}";

    /**
     * https://ncov.html5.qq.com/api/getPosition
     * {"position":{"云南省":{"昆明市":{"全部":"","东川区":"","五华区":"","官渡区":"","盘龙区":"","西山区":""},"玉溪市":{"全部":"","红塔区":""},"保山市":{"全部":"","腾冲市":""},"西双版纳州":{"全部":"","景洪市":""},"德宏傣族景颇族自治州":{"全部":"","瑞丽市":""}},"吉林省":{"吉林市":{"全部":"","丰满区":"","昌邑区":""},"长春市":{"全部":"","净月区":"","朝阳区":"","绿园区":"","高新区":"","九台区":"","二道区":"","宽城区":""}},"天津市":{"天津市":{"全部":"","宁河区":"","宝坻区":"","滨海新区":"","东丽区":"","西青区":"","津南区":""}},"安徽省":{"滁州市":{"全部":"","琅琊区":"","凤阳县":"","天长市":"","明光市":""},"阜阳市":{"全部":"","临泉县":"","太和县":"","阜南县":"","颍上县":"","颍东区":"","颍州区":"","颍泉区":"","界首市":"","中市街道办事处":""},"马鞍山市":{"全部":"","和县":"","当涂县":"","雨山区":""},"亳州市":{"全部":"","利辛县":"","涡阳县":"","蒙城县":"","谯城区":"","高新区":""},"合肥市":{"全部":"","包河区":"","巢湖市":"","庐江县":"","庐阳区":"","新站区":"","瑶海区":"","肥东县":"","肥西县":"","蜀山区":"","长丰县":"","高新区":""},"芜湖市":{"全部":"","南陵县":"","弋江区":"","无为市":"","芜湖县":"","镜湖区":"","鸠江区":""},"六安市":{"全部":"","裕安区":"","金安区":"","金寨县":"","霍山县":"","霍邱县":""},"安庆市":{"全部":"","太湖县":"","宜秀区":"","宿松县":"","岳西县":"","怀宁县":"","望江县":"","桐城市":"","潜山市":"","经开区":"","迎江区":"","大观区":""},"蚌埠市":{"全部":"","淮上区":"","禹会区":"","蚌山区":"","龙子湖区":"","洪山区":"","经开区":""}},"山东省":{"临沂市":{"全部":"","兰山区":"","河东区":"","莒南县":"","费县":"","郯城县":"","临沭县":"","兰陵县":"","平邑县":"","沂水县":"","沂南县":""},"泰安市":{"全部":"","新泰市":"","高新区":"","东平县":"","泰山区":"","肥城市":"","宁阳县":""},"济南市":{"全部":"","历下区":"","市中区":"","槐荫区":"","高新区":"","天桥区":"","长清区":"","历城区":"","平阴县":"","章丘区":""},"济宁市":{"全部":"","任城区":"","兖州区":"","高新区":"","嘉祥县":"","曲阜市":"","汶上县":"","金乡县":""},"淄博市":{"全部":"","博山区":"","周村区":"","张店区":"","桓台县":"","高新区":""},"枣庄市":{"全部":"","山亭区":"","峄城区":"","市中区":"","滕州市":"","薛城区":""},"青岛市":{"全部":"","崂山区":"","平度市":""}},"山西省":{"晋城市":{"全部":"","城区":"","泽州县":"","阳城县":"","陵川县":"","沁水县":""}},"广东省":{"东莞市":{"全部":"","厚街镇":"","大岭山镇":"","大朗镇":"","寮步镇":"","常平镇":"","松山湖":"","长安镇":"","高埗镇":"","黄江镇":"","南城街道":"","虎门镇":"","东城区":"","凤岗镇":"","塘厦镇":"","莞城街道":"","石排镇":"","道滘镇":"","望牛墩镇":""},"中山市":{"全部":"","三乡镇":"","东凤镇":"","东区":"","东升镇":"","坦洲镇":"","横栏镇":"","沙溪镇":"","石岐区":"","西区":"","黄圃镇":"","三角镇":"","开发区":"","板芙镇":"","火炬开发区":"","阜沙镇":"","民众镇":""},"佛山市":{"全部":"","三水区":"","南海区":"","禅城区":"","顺德区":""},"广州市":{"全部":"","从化区":"","南沙区":"","增城区":"","天河区":"","海珠区":"","番禺区":"","白云区":"","花都区":"","荔湾区":"","越秀区":"","黄埔区":""},"梅州市":{"全部":"","兴宁市":"","大埔县":"","平远县":"","梅县区":"","蕉岭县":"","梅江区":""},"汕尾市":{"全部":"","陆丰市":"","陆河县":""},"江门市":{"全部":"","北新区":"","鹤山市":"","开平市":"","新会区":"","江海区":"","蓬江区":""},"深圳市":{"全部":"","光明区":"","南山区":"","坪山区":"","大鹏新区":"","宝安区":"","盐田区":"","福田区":"","罗湖区":"","龙华区":"","龙岗区":""},"湛江市":{"全部":"","吴川市":"","廉江市":"","赤坎区":"","雷州市":"","霞山区":"","坡头区":""},"珠海市":{"全部":"","斗门区":"","横琴新区":"","金湾区":"","香洲区":"","高新区":"","高栏港区":""},"肇庆市":{"全部":"","四会市":"","广宁县":"","端州区":"","高要区":"","鼎湖区":"","封开县":""},"阳江市":{"全部":"","江城区":"","海陵区":"","阳东区":"","阳春市":"","阳西县":""},"惠州市":{"全部":"","仲恺区":"","博罗县":"","大亚湾区":"","惠东县":"","惠城区":"","惠阳区":""},"汕头市":{"全部":"","南澳县":"","潮阳区":"","金平区":"","龙湖区":"","潮南区":""},"清远市":{"全部":"","佛冈县":"","清城区":"","英德市":"","连山壮族瑶族自治县":"","阳山县":""}},"江苏省":{"南通市":{"全部":"","启东市":"","如东县":"","如皋市":"","海安市":"","海门市":"","通州区":""},"常州市":{"全部":"","经开区":"","武进区":"","钟楼区":"","天宁区":"","新北区":""},"徐州市":{"全部":"","丰县":"","新沂市":"","沛县":"","贾汪区":"","邳州市":"","鼓楼区":""},"泰州市":{"全部":"","兴化市":"","姜堰区":"","靖江市":"","高港区":""},"苏州市":{"全部":"","虎丘区":"","吴中区":"","吴江区":"","太仓市":"","姑苏区":"","工业园区":"","常熟市":"","昆山市":"","相城区":"","高新区":"","开发区":""},"连云港市":{"全部":"","海州区":""},"扬州市":{"全部":"","广陵区":"","江都区":"","宝应县":"","蜀冈-瘦西湖景区":"","邗江区":""},"淮安市":{"全部":"","涟水县":"","淮安区":"","淮阴区":"","清江浦区":"","生态文旅区":"","金湖县":""},"南京市":{"全部":"","建邺区":"","栖霞区":"","江北新区":"","江宁区":"","溧水区":"","玄武区":"","雨花台区":"","高淳区":"","鼓楼区":""}},"江西省":{"九江市":{"全部":"","修水县":"","八里湖新区":"","共青城市":"","彭泽县":"","德安县":"","柴桑区":"","武宁县":"","永修县":"","浔阳区":"","湖口县":"","濂溪区":"","经开区":"","都昌县":"","庐山市":""},"南昌市":{"全部":"","东湖区":"","南昌县":"","安义县":"","新建区":"","红谷滩新区":"","经济技术开发区":"","西湖区":"","进贤县":"","青云谱区":"","青山湖区":"","高新技术产业开发区":"","国家高新产业技术开发区":""},"新余市":{"全部":"","渝水区":"","分宜县":"","其他":""},"赣州市":{"全部":"","于都县":"","会昌县":"","南康区":"","安远县":"","章贡区":"","赣县区":"","赣州经开区":"","龙南县":""},"上饶市":{"全部":"","万年县":"","余干县":"","信州区":"","婺源县":"","广丰区":"","广信区":"","德兴市":"","玉山县":"","鄱阳县":"","铅山县":""},"吉安市":{"全部":"","万安县":"","吉水县":"","峡江县":"","新干县":"","永丰县":"","永新县":"","泰和县":"","遂川县":"","吉安县":""},"鹰潭市":{"全部":"","余江区":"","信江新区":"","月湖区":"","贵溪市":"","高新区":""}},"河南省":{"信阳市":{"全部":"","光山县":"","商城县":"","固始县":"","平桥区":"","息县":"","新县":"","浉河区":"","淮滨县":"","潢川县":"","罗山县":"","羊山新区":"","中心城区":""},"南阳市":{"全部":"","卧龙区":"","内乡县":"","南召县":"","唐河县":"","城乡一体化示范区":"","官庄工区":"","宛城区":"","新野县":"","方城县":"","桐柏县":"","淅川县":"","社旗县":"","西峡县":"","邓州市":"","镇平县":"","高新区":"","示范区":""},"周口市":{"全部":"","太康县":""},"平顶山市":{"全部":"","宝丰县":"","鲁山县":"","卫东区":"","叶县":"","新华区":"","汝州市":"","湛河区":""},"开封市":{"全部":"","尉氏县":"","杞县":"","通许县":"","龙亭区":""},"新乡市":{"全部":"","辉县市":"","卫滨区":"","卫辉市":"","延津县":"","新乡县":"","红旗区":"","长垣市":""},"郑州市":{"全部":"","上街区":"","中原区":"","二七区":"","巩义市":"","惠济区":"","新密市":"","新郑市":"","登封市":"","管城区":"","荥阳市":"","郑东新区":"","金水区":"","中牟县":""},"商丘市":{"全部":"","宁陵县":"","虞城县":"","梁园区":"","睢县":"","夏邑县":"","柘城县":"","民权县":"","永城市":"","睢阳区":"","示范区":""},"鹤壁市":{"全部":"","山城区":""},"洛阳市":{"全部":"","偃师市":"","洛龙区":"","涧西区":"","瀍河区":"","老城区":"","西工区":"","高新区":""}},"湖南省":{"岳阳市":{"全部":"","临湘市":"","云溪区":"","华容县":"","君山区":"","屈原管理区":"","岳阳县":"","岳阳楼区":"","平江县":"","汨罗市":"","湘阴县":"","经开区":"","南湖新区":""},"常德市":{"全部":"","安乡县":"","桃源县":"","武陵区":"","津市市":"","澧县":"","石门县":"","鼎城区":"","临澧县":"","汉寿县":""},"株洲市":{"全部":"","天元区":"","攸县":"","石峰区":"","芦淞区":"","荷塘区":"","醴陵市":"","云龙示范区":"","茶陵县":""},"郴州市":{"全部":"","临武县":"","北湖区":"","嘉禾县":"","安仁县":"","宜章县":"","永兴县":"","汝城县":"","苏仙区":"","资兴市":""},"永州市":{"全部":"","道县":""},"怀化市":{"全部":"","新晃县":"","沅陵县":"","溆浦县":"","芷江县":"","辰溪县":"","鹤城区":"","靖州县":""},"衡阳市":{"全部":"","衡东县":""},"湘潭市":{"全部":"","岳塘区":"","湘乡市":"","湘潭县":"","雨湖区":"","韶山市":""},"邵阳市":{"全部":"","双清区":""},"长沙市":{"全部":"","天心区":"","宁乡市":"","岳麓区":"","开福区":"","望城区":"","浏阳市":"","芙蓉区":"","长沙县":"","雨花区":""}},"福建省":{"厦门市":{"全部":"","思明区":"","湖里区":"","翔安区":"","集美区":""},"泉州市":{"全部":"","丰泽区":"","南安市":"","安溪县":"","晋江市":"","永春县":"","洛江区":"","石狮市":"","鲤城区":""},"福州市":{"全部":"","连江县":"","长乐区":""},"莆田市":{"全部":"","仙游县":"","城厢区":"","涵江区":"","秀屿区":"","荔城区":"","湄洲湾北岸开发区":""},"漳州市":{"全部":"","东山县":"","云霄县":"","芗城区":"","诏安县":"","龙海市":""}},"辽宁省":{"沈阳市":{"全部":"","大东区":"","沈河区":"","皇姑区":"","铁西区":"","法库县":"","于洪区":"","沈北新区":""}},"重庆市":{"重庆市":{"全部":"","万州区":"","九龙坡区":"","奉节县":"","巫溪县":"","开州区":"","江津区":"","石柱县":"","荣昌区":"","大足区":"","梁平区":"","铜梁区":"","万盛经开区":"","丰都县":"","合川区":"","垫江县":"","城口县":"","巫山县":"","巴南区":"","彭水县":"","忠县":"","武隆区":"","江北区":"","沙坪坝区":"","涪陵区":"","渝中区":"","潼南区":"","璧山区":"","綦江区":"","长寿区":"","两江新区":"","云阳区":"","南岸区":"","大渡口区":"","渝北区":""}},"陕西省":{"西安市":{"全部":"","鄠邑区":""}},"贵州省":{"毕节市":{"全部":"","纳雍县":"","大方县":"","赫章县":"","七星关区":"","黔西县":""},"贵阳市":{"全部":"","南明区":"","修文县":"","花溪区":"","观山湖区":""},"遵义市":{"全部":"","仁怀市":"","道真县":""},"黔南州":{"全部":"","贵定县":""},"黔西南州":{"全部":"","义龙新区":""},"安顺市":{"全部":"","黄果树旅游区":"","普定县":""},"黔东南州":{"全部":"","天柱县":""}},"河北省":{"石家庄市":{"全部":"","桥西区":""},"沧州市":{"全部":"","献县":""}},"四川省":{"成都市":{"全部":"","双流区":"","大邑县":"","天府新区":"","成华区":"","武侯区":"","温江区":"","简阳市":"","邛崃市":"","郫都区":"","金堂县":"","金牛区":"","锦江区":"","青白江区":"","青羊区":"","高新区":""},"内江市":{"全部":"","东兴区":"","威远县":"","市中区":"","资中县":""}},"北京市":{"北京市":{"全部":"","东城区":"","大兴区":"","怀柔区":"","朝阳区":"","海淀区":"","石景山区":"","西城区":"","丰台区":"","房山区":"","昌平区":"","通州区":"","顺义区":""}},"浙江省":{"嘉兴市":{"全部":"","南湖区":"","桐乡市":"","秀洲区":""},"宁波市":{"全部":"","余姚市":"","北仑区":"","慈溪市":"","江北区":"","海曙区":"","鄞州区":"","镇海区":""},"杭州市":{"全部":"","余杭区":"","拱墅区":"","桐庐县":"","江干区":"","钱塘新区":""}},"黑龙江省":{"哈尔滨市":{"全部":"","五常市":"","南岗区":"","双城市":"","市道外区":"","市道里区":"","方正县":"","道外区":"","道里区":"","香坊区":"","尚志市":""},"齐齐哈尔市":{"全部":"","依安县":"","建华区":"","梅里斯区":"","甘南县":"","道里区":"","龙江县":"","龙沙区":""},"双鸭山市":{"全部":"","友谊县":"","四方台区":"","尖山区":"","集贤县":""}},"上海市":{"上海市":{"全部":"","嘉定区":"","宝山区":"","徐汇区":"","杨浦区":"","松江区":"","浦东新区":"","长宁区":"","闵行区":"","黄浦区":"","奉贤区":"","普陀区":""}},"海南省":{"海口市":{"全部":"","琼山区":"","美兰区":"","龙华区":""}},"甘肃省":{"兰州市":{"全部":"","七里河区":"","西固区":""}}}}
     * <p>
     * https://ncov.html5.qq.com/api/getCommunity?province=%E8%B4%B5%E5%B7%9E%E7%9C%81&city=%E6%AF%95%E8%8A%82%E5%B8%82&district=%E5%85%A8%E9%83%A8&lat=31.271644&lng=121.687813
     * {"code":0,"community":{"贵州省":{"毕节市":{"纳雍县":[{"province":"贵州省","city":"毕节市","district":"纳雍县","county":"纳雍县","street":"","community":"","show_address":"乐治镇","cnt_inc_uncertain":"-1","cnt_inc_certain":"-1","cnt_inc_die":"-1","cnt_inc_recure":"-1","cnt_sum_uncertain":"-1","cnt_sum_certain":"2","cnt_sum_die":"-1","cnt_sum_recure":"-1","full_address":"贵州省毕节市纳雍县乐治镇","lng":"105.472198","lat":"26.832529","source":[{"name":"贵州省卫生健康委员会","url":""}],"distance":1651638}],"大方县":[{"province":"贵州省","city":"毕节市","district":"大方县","county":"大方县","street":"红旗街道","community":"","show_address":"红旗街道","cnt_inc_uncertain":"-1","cnt_inc_certain":"-1","cnt_inc_die":"-1","cnt_inc_recure":"-1","cnt_sum_uncertain":"-1","cnt_sum_certain":"1","cnt_sum_die":"-1","cnt_sum_recure":"-1","full_address":"贵州省毕节市大方县红旗街道","lng":"105.608391","lat":"27.143","source":[{"name":"贵州省卫生健康委员会","url":""}],"distance":1626767},{"province":"贵州省","city":"毕节市","district":"大方县","county":"大方县","street":"顺德街道","community":"","show_address":"顺德街道","cnt_inc_uncertain":"-1","cnt_inc_certain":"-1","cnt_inc_die":"-1","cnt_inc_recure":"-1","cnt_sum_uncertain":"-1","cnt_sum_certain":"1","cnt_sum_die":"-1","cnt_sum_recure":"-1","full_address":"贵州省毕节市大方县顺德街道","lng":"105.589417","lat":"27.156038","source":[{"name":"贵州省卫生健康委员会","url":""}],"distance":1628029}],"赫章县":[{"province":"贵州省","city":"毕节市","district":"赫章县","county":"赫章县","street":"白果街道","community":"","show_address":"白果街道","cnt_inc_uncertain":"-1","cnt_inc_certain":"-1","cnt_inc_die":"-1","cnt_inc_recure":"-1","cnt_sum_uncertain":"-1","cnt_sum_certain":"1","cnt_sum_die":"-1","cnt_sum_recure":"-1","full_address":"贵州省毕节市赫章县白果街道","lng":"104.69265","lat":"27.10656","source":[{"name":"贵州省卫生健康委员会","url":""}],"distance":1713420},{"province":"贵州省","city":"毕节市","district":"赫章县","county":"赫章县","street":"","community":"","show_address":"妈姑镇","cnt_inc_uncertain":"-1","cnt_inc_certain":"-1","cnt_inc_die":"-1","cnt_inc_recure":"-1","cnt_sum_uncertain":"-1","cnt_sum_certain":"1","cnt_sum_die":"-1","cnt_sum_recure":"-1","full_address":"贵州省毕节市赫章县妈姑镇","lng":"104.561867","lat":"26.965281","source":[{"name":"贵州省卫生健康委员会","url":""}],"distance":1730974}],"七星关区":[{"province":"贵州省","city":"毕节市","district":"七星关区","county":"","street":"","community":"","show_address":"撒拉溪镇","cnt_inc_uncertain":"-1","cnt_inc_certain":"-1","cnt_inc_die":"-1","cnt_inc_recure":"-1","cnt_sum_uncertain":"-1","cnt_sum_certain":"1","cnt_sum_die":"-1","cnt_sum_recure":"-1","full_address":"贵州省毕节市七星关区撒拉溪镇","lng":"105.04155","lat":"27.19664","source":[{"name":"贵州省卫生健康委员会","url":""}],"distance":1677503}],"黔西县":[{"province":"贵州省","city":"毕节市","district":"黔西县","county":"黔西县","street":"","community":"","show_address":"百花村","cnt_inc_uncertain":"-1","cnt_inc_certain":"-1","cnt_inc_die":"-1","cnt_inc_recure":"-1","cnt_sum_uncertain":"-1","cnt_sum_certain":"2","cnt_sum_die":"-1","cnt_sum_recure":"-1","full_address":"贵州省毕节市黔西县素朴镇百花村","lng":"106.34758","lat":"26.99025","source":[{"name":"贵州省卫生健康委员会","url":""}],"distance":1564302}]}}}}
     */


    @SuppressWarnings("all")
    public static Map<String, Object> getPosition() {
        Map<String, Object> result = REST_TEMPLATE.getForObject(GET_POSITION_URL, LinkedHashMap.class);
        return (Map<String, Object>) result.get("position");
    }

    @SuppressWarnings("all")
    public static Map<String, Object> getAllCommunity(Map<String, Object> positions) {
        Map<String, Object> allCommunitys = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : positions.entrySet()) {
            String province = entry.getKey();
            Map<String, Object> pCommunitys = (Map<String, Object>) allCommunitys.get(province);
            if (pCommunitys == null) {
                pCommunitys = new LinkedHashMap<>();
                allCommunitys.put(province, pCommunitys);
            }
            Map<String, Object> citys = (Map<String, Object>) entry.getValue();
            for (Map.Entry<String, Object> cEntry : citys.entrySet()) {
                String city = cEntry.getKey();
                Map<String, Object> result = REST_TEMPLATE.getForObject(GET_COMMUNITY_URL, LinkedHashMap.class, province, city, "全部");
                int code = (int) result.get("code");
                if (code == 0) {
                    Map<String, Object> community = (Map<String, Object>) result.get("community");
                    if (community.containsKey(province)) {
                        pCommunitys.putAll((Map<String, Object>) community.get(province));
                    }
                }
            }
        }
        return allCommunitys;
    }

    @SuppressWarnings("all")
    public static Map<String, Object> convertLngAndLat(Map<String, Object> allCommunitys) {
        for (Map.Entry<String, Object> pEntry : allCommunitys.entrySet()) {
            Map<String, Object> citys = (Map<String, Object>) pEntry.getValue();
            for (Map.Entry<String, Object> cEntry : citys.entrySet()) {
                Map<String, Object> districts = (Map<String, Object>) cEntry.getValue();
                for (Map.Entry<String, Object> dEntry : districts.entrySet()) {
                    List<Map<String, Object>> communitys = (List<Map<String, Object>>) dEntry.getValue();
                    for (Map<String, Object> community : communitys) {
                        String lng = (String) community.get("lng");
                        String lat = (String) community.get("lat");
                        String full_address = (String) community.get("full_address");
                        String city = (String) community.get("city");
                        if (StringUtils.hasText(lng) && StringUtils.hasText(lat)) {
                            String[] geoConvs = BaiduMapGeoConvService.geoConv(lng, lat);
                            community.put("lng", geoConvs[0]);
                            community.put("lat", geoConvs[1]);
                        } else if ("上海市浦东新区汤臣豪园四期".equals(full_address)) {
                            // 修改坐标
                            community.put("lng", "121.598908");
                            community.put("lat", "31.20417");
                        } else {
                            LOGGER.warn("[{}][{}]没有经纬度信息，开始进行百度地址检索", city, full_address);
                            // 通过百度api查询坐标信息
                            Map<String, Object> firstPoi = MapSearchService.getLocation(full_address, city);
                            Map<String, Object> location = (Map<String, Object>) firstPoi.get("location");
                            if (location == null) {
                                LOGGER.warn("POI检索没有经纬度信息:[{}][{}]", full_address, city);
                                continue;
                            }
                            community.put("lng", location.get("lng").toString());
                            community.put("lat", location.get("lat").toString());
                        }
                    }
                }
            }
        }
        return allCommunitys;
    }
}
