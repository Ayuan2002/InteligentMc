package com.example.myapplication.util;

import com.ejlchina.okhttps.HTTP;
import com.example.myapplication.entity.Mc_cover;

import java.util.ArrayList;
import java.util.List;

public  class HttpPostUtil {
    private String baseURL="";
    HTTP http=HTTP.builder().baseUrl(baseURL).build();

    public Mc_cover getById(){
        Mc_cover mc=new Mc_cover();
        return mc;
    }
}
