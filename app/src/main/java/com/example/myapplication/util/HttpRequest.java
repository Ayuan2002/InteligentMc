package com.example.myapplication.util;

import android.util.Log;

import com.ejlchina.okhttps.HTTP;
import com.ejlchina.okhttps.HttpResult;
import com.example.myapplication.entity.ApiResponse;
import com.example.myapplication.entity.Manhole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class HttpRequest {
    private static String baseURL="http://175.6.178.224:8088/mc_cover";
    private ObjectMapper objectMapper=new ObjectMapper();
    HTTP http= HTTP.builder().baseUrl(baseURL).build();
    public List<Manhole> getFromServer(){
        List<Manhole> list=new ArrayList<>();
        ApiResponse response;
        HttpResult result=http.sync("/getAll").nothrow().get();
        switch (result.getState()){
            case RESPONSED:
            String json=result.getBody().toString();
            try {
                response=objectMapper.readValue(json,ApiResponse.class);
                if (response.getStatusCode()==200){
                    list=response.getData();
                }
            }catch (JsonProcessingException e){
                e.printStackTrace();
            }
            break;

        }
        return list;
    }
    public Manhole getOneFromServer(String mc_id){
        Manhole manhole;
        HttpResult result=http.sync("/getOne").addUrlPara("mc_id",mc_id).nothrow().get();
        switch (result.getState()){
            case RESPONSED:
                String json=result.getBody().toString();
                JsonNode root;
                JsonNode data;
                try {
                    root=objectMapper.readTree(json);
                    data=root.get("data");
                    manhole=objectMapper.readValue(data.asText(),Manhole.class);
                    return manhole;
                }catch (JsonProcessingException e){
                    e.printStackTrace();
                }
                break;
        }
        return null;
    }
}
