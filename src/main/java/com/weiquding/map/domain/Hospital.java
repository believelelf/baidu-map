package com.weiquding.map.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 医院
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/30
 */
@JsonSerialize(using = HospitalSerializer.class)
public class Hospital {

    private String province;
    private String city;
    private String hospital;
    @JsonProperty("class")
    private String type;
    private BigDecimal bd_lat;
    private BigDecimal bd_lon;
    private String address;

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

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getBd_lat() {
        return bd_lat;
    }

    public void setBd_lat(BigDecimal bd_lat) {
        this.bd_lat = bd_lat;
    }

    public BigDecimal getBd_lon() {
        return bd_lon;
    }

    public void setBd_lon(BigDecimal bd_lon) {
        this.bd_lon = bd_lon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hospital hospital1 = (Hospital) o;
        return Objects.equals(city, hospital1.city) &&
                Objects.equals(hospital, hospital1.hospital) &&
                Objects.equals(type, hospital1.type) &&
                Objects.equals(bd_lat, hospital1.bd_lat) &&
                Objects.equals(bd_lon, hospital1.bd_lon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, hospital, type, bd_lat, bd_lon);
    }
}
