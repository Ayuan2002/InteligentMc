package com.example.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBNRoutePlanManager;
import com.baidu.navisdk.adapter.IBNTTSManager;
import com.baidu.navisdk.adapter.IBaiduNaviManager;
import com.baidu.navisdk.adapter.struct.BNTTsInitConfig;
import com.baidu.navisdk.adapter.struct.BNaviInitConfig;
import com.example.myapplication.entity.Mc_cover;
import com.example.myapplication.handler.MyThreadHandler;
import com.example.myapplication.listener.MyLocationListener;
import com.example.myapplication.util.HttpPostUtil;
import com.example.myapplication.util.WarnVideoPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MapActivity extends AppCompatActivity implements LocationCallback , MyThreadHandler.HandlerCallback {
    private BaiduMap mBaiduMap = null;
    private MapView mMapView = null;
    private Button close_dialog=null;
    MyLocationListener myListener;
    LocationClient mLocationClient = null;
    private Button to_navi=null;
    private double currentLatitude=0.0;
    private double currentLongitude=0.0;
    private ImageButton ibLocation;
    private Mc_cover mc;
    Dialog dialog=null;
    HttpPostUtil util;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private MyThreadHandler myThreadHandler;
    private Marker manhole=null;
    private WarnVideoPlayer player;
    BitmapDescriptor blue=BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_blue);
    BitmapDescriptor red=BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_red);
    private TextView dialog_longitude,dialog_latitude,dialog_water_level,dialog_harmful_gas_concentration,
    dialog_pitch_angle,dialog_roll_angle,dialog_yaw_angle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initView();
        initLocation();
        player=new WarnVideoPlayer(this);
        util=new HttpPostUtil();
        myThreadHandler=new MyThreadHandler(getMainLooper(),this);
        executorService.submit(() -> {
            Log.d("MyThread","后台请求线程启动");
            while (true){
                Mc_cover cover = util.getById();
                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = cover;
                myThreadHandler.sendMessage(msg);
                Thread.sleep(5000);

            }
        });
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                /*Log.d("MapClick", "onMapClick: " + latLng.latitude + ", " + latLng.longitude);*/
            }
            @Override
            public void onMapPoiClick(MapPoi mapPoi) {

            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭地图定位图层
        mLocationClient.stop();
        //释放资源
        mMapView.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient = null;
        executorService.shutdownNow();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //恢复地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //暂停地图
        mMapView.onPause();
    }
    /**
     * 定位初始化
     */
    public void initLocation() {
        //添加隐私合规政策
        LocationClient.setAgreePrivacy(true);
        // 开启定位图层
        this.mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        if (mLocationClient == null) {
            try {
                mLocationClient = new LocationClient(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mLocationClient != null) {
            myListener = new MyLocationListener(mBaiduMap,mMapView);
            myListener.setLocationCallback(this);
            mLocationClient.registerLocationListener(myListener);
            LocationClientOption option = new LocationClientOption();
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置高精度定位
            option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
            option.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
            option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
            option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
            option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
            mLocationClient.setLocOption(option);
            mLocationClient.start();//开始定位
        }
    }
    private void initView(){
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        //回到当前定位
        ibLocation = (ImageButton) findViewById(R.id.ib_location);
        ibLocation.setOnClickListener(v -> {
            LatLng latLng = new LatLng(currentLatitude, currentLongitude);
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(latLng).zoom(15.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        });
        mMapView.showScaleControl(false);  // 设置比例尺是否可见（true 可见/false不可见）
        //mMapView.showZoomControls(false);  // 设置缩放控件是否可见（true 可见/false不可见）
        mMapView.removeViewAt(1);// 删除百度地图Logo
        mBaiduMap = mMapView.getMap();
        dialog =new Dialog(this);
        dialog.setContentView(R.layout.map_dialog_layout);
        dialog_longitude=dialog.findViewById(R.id.longitude);
        dialog_latitude=dialog.findViewById(R.id.latitude);
        dialog_harmful_gas_concentration=dialog.findViewById(R.id.harmful_gas_concentration);
        dialog_water_level=dialog.findViewById(R.id.water_level);
        dialog_pitch_angle=dialog.findViewById(R.id.pitch_angle);
        dialog_roll_angle=dialog.findViewById(R.id.roll_angle);
        dialog_yaw_angle=dialog.findViewById(R.id.yaw_angle);
        close_dialog=dialog.findViewById(R.id.close_dialog);
        to_navi=dialog.findViewById(R.id.to_navigation);
        close_dialog.setOnClickListener(v -> dialog.dismiss());
        to_navi.setOnClickListener(v -> {
            //到导航页面
            startNavigation(mc.getLatitude(),mc.getLongitude());
        });
        mBaiduMap.setOnMarkerClickListener(marker -> {
            dialog.setTitle("井盖详情");

            dialog_longitude.setText(String.valueOf(mc.getLongitude()));
            dialog_latitude.setText(String.valueOf(mc.getLatitude()));
            dialog_water_level.setText(String.valueOf(mc.getWater_level()));
            dialog_harmful_gas_concentration.setText(String.valueOf(mc.getHarmful_gas_concentration()));
            dialog_pitch_angle.setText(String.valueOf(mc.getPitch_angle()));
            dialog_roll_angle.setText(String.valueOf(mc.getRoll_angle()));
            dialog_yaw_angle.setText(String.valueOf(mc.getYaw_angle()));
            initNavigation();
            dialog.show();
            return true;
        });

    }
    public void startNavigation(double targetLatitude,double targetLongitude){
        try {
            BNRoutePlanNode sNode = new BNRoutePlanNode.Builder()
                    .latitude(currentLatitude)
                    .longitude(currentLongitude)
                    .name("自己")
                    .description("自己")
                    .build();
            BNRoutePlanNode eNode = new BNRoutePlanNode.Builder()
                    .latitude(targetLatitude)
                    .longitude(targetLongitude)
                    .name("终点")
                    .description("井盖")
                    .build();
            List<BNRoutePlanNode> list = new ArrayList<>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManagerFactory.getRoutePlanManager().routePlan(list,
                    IBNRoutePlanManager.RoutePlanPreference.ROUTE_PLAN_PREFERENCE_DEFAULT,
                    null,
                    new Handler(Looper.getMainLooper()){
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            switch (msg.what) {
                                case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_START:
                                /*Toast.makeText(MapActivity.this.getApplicationContext(),
                                        "算路开始", Toast.LENGTH_SHORT).show();*/
                                    break;
                                case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_SUCCESS:
                                    Toast.makeText(MapActivity.this.getApplicationContext(),
                                            "算路成功", Toast.LENGTH_SHORT).show();
                                    break;
                                case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_FAILED:
                                    Toast.makeText(MapActivity.this.getApplicationContext(),
                                            "算路失败", Toast.LENGTH_SHORT).show();
                                    break;
                                case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_TO_NAVI:
                                    Toast.makeText(MapActivity.this.getApplicationContext(),
                                            "算路成功准备进入导航", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MapActivity.this,
                                            NavigationActivity.class);
                                    startActivity(intent);
                                    break;
                                default:
                                    // nothing
                                    break;
                            }
                        }
                    });
        }catch (BaiduMapAppNotSupportNaviException be){
            be.printStackTrace();
        }

    }
    public void initNavigation(){
        BNaviInitConfig config=new BNaviInitConfig.Builder().naviInitListener(new IBaiduNaviManager.INaviInitListener() {
            @Override
            public void onAuthResult(int i, String s) {
                String result;
                if (0 == i) {
                    result = "key校验成功!";
                } else {
                    result = "key校验失败, " + s;
                }
                Toast.makeText(MapActivity.this, result, Toast.LENGTH_LONG).show();
            }

            @Override
            public void initStart() {

            }

            @Override
            public void initSuccess() {
                // 初始化tts
                initTTs();
            }

            @Override
            public void initFailed(int i) {

            }
        }).build();
        BaiduNaviManagerFactory.getBaiduNaviManager().init(getApplicationContext(), config);
    }
    private void initTTs() {

        BNTTsInitConfig.Builder builder=new BNTTsInitConfig.Builder();
        builder.context(getApplicationContext());
        BaiduNaviManagerFactory.getTTSManager().initTTS( builder.build());
        // 注册同步内置tts状态回调
        BaiduNaviManagerFactory.getTTSManager().setOnTTSStateChangedListener(
                new IBNTTSManager.IOnTTSPlayStateChangedListener() {
                    @Override
                    public void onPlayStart() {
                        Log.e("lmap", "ttsCallback.onPlayStart");
                    }

                    @Override
                    public void onPlayEnd(String speechId) {
                        Log.e("lmap", "ttsCallback.onPlayEnd");
                    }

                    @Override
                    public void onPlayError(int code, String message) {
                        Log.e("lmap", "ttsCallback.onPlayError");
                    }
                }
        );
    }
    @Override
    public void onLocationReceived(double latitude, double longitude) {
        Log.d("MapActivity", "Latitude: " + latitude + ", Longitude: " + longitude);
        currentLatitude=latitude;
        currentLongitude=longitude;
    }
    public void warning_status(){
        player.play();
        LatLng new_position=new LatLng(this.mc.getLatitude(),this.mc.getLongitude());
        OverlayOptions options=new MarkerOptions().position(new_position).icon(red).zIndex(9);
        manhole=(Marker) mBaiduMap.addOverlay(options);
    }
    public boolean is_need_warning(Mc_cover mc){
        // 检查水位
        if (mc.getWater_level() < 10) {
            return true;
        }
        // 检查有害气体浓度
        if (mc.getHarmful_gas_concentration() > 1200) {
            return true;
        }
        // 检查三轴角度
        if (Math.abs(mc.getPitch_angle()) > 15 || Math.abs(mc.getRoll_angle()) > 15  || Math.abs(mc.getYaw_angle()) > 15) {
            return true;
        }
        return false;
    }
    @Override
    public void updateUI(Mc_cover mc) {
        this.mc=mc.clone();
        if(manhole!=null){
            manhole.remove();
        }
        if(is_need_warning(mc)){
            warning_status();
        }else{
        LatLng new_position=new LatLng(mc.getLatitude(),mc.getLongitude());
        OverlayOptions options=new MarkerOptions().position(new_position).icon(blue).zIndex(9);
        manhole=(Marker) mBaiduMap.addOverlay(options);
        }
    }
}

