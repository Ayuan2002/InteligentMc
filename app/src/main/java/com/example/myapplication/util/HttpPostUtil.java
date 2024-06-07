package com.example.myapplication.util;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.ejlchina.okhttps.HTTP;
import com.ejlchina.okhttps.HttpResult;
import com.example.myapplication.entity.Mc_cover;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public  class HttpPostUtil {
    private String baseURL="http://134.175.97.153:8088/mc/manholecoverinfo";
    HTTP http=HTTP.builder().baseUrl(baseURL).build();
    ObjectMapper mapper;
    JsonNode root;
    JsonNode data;
    HttpResult result;
    LatLng gps;
    CoordinateConverter converter;
    public HttpPostUtil() {
        mapper=new ObjectMapper();
        converter=new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
    }

    public Mc_cover  getById(){
        Mc_cover mc=new Mc_cover();
        try{
            mc.setID("test001");
            result=http.sync("/get/test001").get();
            String msg=result.getBody().toString();
            root=mapper.readTree(msg);
            data=root.path("data");
            gps=new LatLng(data.path("latitude").asDouble(),data.path("longitude").asDouble());
            converter.coord(gps);
            LatLng bdl=converter.convert();
            mc.setLatitude(bdl.latitude);
            mc.setLongitude(bdl.longitude);
            mc.setWater_level(data.path("waterLevel").asDouble());
            mc.setHarmful_gas_concentration(data.path("harmfulGasConcentration").asDouble());
            mc.setPitch_angle(data.path("pitchAngle").asDouble());
            mc.setRoll_angle(data.path("rollAngle").asDouble());
            mc.setYaw_angle(data.path("yawAngle").asDouble());
        }catch (Exception e){
            Log.e("UtilError", "getById: "+e );
        }
        return mc;
    }

}
