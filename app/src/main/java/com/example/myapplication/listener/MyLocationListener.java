package com.example.myapplication.listener;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.myapplication.LocationCallback;

/**
 * 定位SDK监听函数
 */
 public class MyLocationListener extends BDAbstractLocationListener {
    BaiduMap mBaiduMap;
    MapView mMapView;
    private LocationCallback locationCallback;
    boolean isFirstLoc =true;
    public MyLocationListener(BaiduMap mBaiduMap,MapView mMapView){
        this.mMapView=mMapView;
        this.mBaiduMap=mBaiduMap;
    }
    @Override
    public void onReceiveLocation(BDLocation location) {
            // MapView 销毁后不在处理新接收的位置
        if (mMapView == null) {
                return;
            }
        MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())// 设置定位数据的精度信息，单位：米
                    .direction(location.getDirection()) // 此处设置开发者获取到的方向信息，顺时针0-360
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
        locationCallback.onLocationReceived(location.getLatitude(), location.getLongitude());
            // 设置定位数据, 只有先允许定位图层后设置数据才会生效
        mBaiduMap.setMyLocationData(locData);
        if (isFirstLoc) {
            isFirstLoc = false;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(latLng).zoom(20.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }
    public void setLocationCallback(LocationCallback callback) {
        this.locationCallback = callback;
    }

}




