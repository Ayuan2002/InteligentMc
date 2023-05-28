package com.example.myapplication;

import android.app.Application;
import android.widget.Toast;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.common.BaiduMapSDKException;
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.setAgreePrivacy(this,true);
        try {
            // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
            SDKInitializer.initialize(this);
            /*Toast.makeText(getApplicationContext(),"successful",Toast.LENGTH_LONG).show();*/
        } catch (BaiduMapSDKException e) {
            e.printStackTrace();
        }
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
        SDKInitializer.setApiKey("jl5mFP2lS6FHt6TrVO6QCZpCKgzMXHMN");
        SDKInitializer.setHttpsEnable(true);
    }
}
