package com.weiquding.map.domain;

import java.math.BigDecimal;
import java.util.List;

/**
 * description
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/7
 */
public class Community {

    private String province;
    private String city;
    private String district;
    private String county;
    private String street;
    private String community;
    private String show_address;
    private String cnt_inc_uncertain;
    private String cnt_inc_certain;
    private String cnt_inc_die;
    private String cnt_inc_recure;
    private String cnt_sum_uncertain;
    private String cnt_sum_certain;
    private String cnt_sum_die;
    private String cnt_sum_recure;
    private String full_address;
    private String lng;
    private String lat;
    private List<Source> source;
    private BigDecimal distance;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getShow_address() {
        return show_address;
    }

    public void setShow_address(String show_address) {
        this.show_address = show_address;
    }

    public String getCnt_inc_uncertain() {
        return cnt_inc_uncertain;
    }

    public void setCnt_inc_uncertain(String cnt_inc_uncertain) {
        this.cnt_inc_uncertain = cnt_inc_uncertain;
    }

    public String getCnt_inc_certain() {
        return cnt_inc_certain;
    }

    public void setCnt_inc_certain(String cnt_inc_certain) {
        this.cnt_inc_certain = cnt_inc_certain;
    }

    public String getCnt_inc_die() {
        return cnt_inc_die;
    }

    public void setCnt_inc_die(String cnt_inc_die) {
        this.cnt_inc_die = cnt_inc_die;
    }

    public String getCnt_inc_recure() {
        return cnt_inc_recure;
    }

    public void setCnt_inc_recure(String cnt_inc_recure) {
        this.cnt_inc_recure = cnt_inc_recure;
    }

    public String getCnt_sum_uncertain() {
        return cnt_sum_uncertain;
    }

    public void setCnt_sum_uncertain(String cnt_sum_uncertain) {
        this.cnt_sum_uncertain = cnt_sum_uncertain;
    }

    public String getCnt_sum_certain() {
        return cnt_sum_certain;
    }

    public void setCnt_sum_certain(String cnt_sum_certain) {
        this.cnt_sum_certain = cnt_sum_certain;
    }

    public String getCnt_sum_die() {
        return cnt_sum_die;
    }

    public void setCnt_sum_die(String cnt_sum_die) {
        this.cnt_sum_die = cnt_sum_die;
    }

    public String getCnt_sum_recure() {
        return cnt_sum_recure;
    }

    public void setCnt_sum_recure(String cnt_sum_recure) {
        this.cnt_sum_recure = cnt_sum_recure;
    }

    public String getFull_address() {
        return full_address;
    }

    public void setFull_address(String full_address) {
        this.full_address = full_address;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public List<Source> getSource() {
        return source;
    }

    public void setSource(List<Source> source) {
        this.source = source;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal distance) {
        this.distance = distance;
    }

    /**
 * {
 * 					"province": "上海市",
 * 					"city": "上海市",
 * 					"district": "嘉定区",
 * 					"county": "",
 * 					"street": "",
 * 					"community": "金园一路1118弄",
 * 					"show_address": "金园一路1118弄",
 * 					"cnt_inc_uncertain": "-1",
 * 					"cnt_inc_certain": "-1",
 * 					"cnt_inc_die": "-1",
 * 					"cnt_inc_recure": "-1",
 * 					"cnt_sum_uncertain": "-1",
 * 					"cnt_sum_certain": "-1",
 * 					"cnt_sum_die": "-1",
 * 					"cnt_sum_recure": "-1",
 * 					"full_address": "上海市嘉定区江桥镇金园一路1118弄",
 * 					"lng": "121.31561",
 * 					"lat": "31.24792",
 * 					"source": [{
 * 						"name": "上海发布",
 * 						"url": ""
 *                                        }],
 * 					"distance": 21951
 */

}
