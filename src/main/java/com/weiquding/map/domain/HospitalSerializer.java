package com.weiquding.map.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * description
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/30
 */
public class HospitalSerializer extends JsonSerializer<Hospital> {

    @Override
    public void serialize(Hospital value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("province", value.getProvince());
        gen.writeStringField("city", value.getCity());
        gen.writeStringField("hospital", value.getHospital());
        gen.writeStringField("class", value.getType());
        gen.writeNumberField("bd_lat", value.getBd_lat());
        gen.writeNumberField("bd_lon", value.getBd_lon());
        gen.writeStringField("address", value.getAddress());
        gen.writeEndObject();
    }
}
