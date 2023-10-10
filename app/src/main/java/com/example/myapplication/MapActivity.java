package com.example.myapplication;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.MediaParser;
import android.media.MediaPlayer;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBNRoutePlanManager;
import com.baidu.navisdk.adapter.IBNTTSManager;
import com.baidu.navisdk.adapter.IBaiduNaviManager;
import com.baidu.navisdk.adapter.struct.BNTTsInitConfig;
import com.baidu.navisdk.adapter.struct.BNaviInitConfig;
import com.example.myapplication.entity.Manhole;
import com.example.myapplication.listener.MyLocationListener;
import com.example.myapplication.util.AlarmService;
import com.example.myapplication.util.DBHelper;
import com.example.myapplication.util.HttpRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements LocationCallback {
    private BaiduMap mBaiduMap = null;
    private MapView mMapView = null;
    private Button close_dialog = null;
    MyLocationListener myListener;
    LocationClient mLocationClient = null;
    private Button to_navi = null;
    private double markerLatitude = 29.055444; //纬度
    private double markerLongitude = 111.668724;//经度
    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;
    private ImageButton ibLocation;
    private BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_blue);
    private BitmapDescriptor w_bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_red);
    private static HttpRequest request = new HttpRequest();
    private Manhole manhole;
    private Marker marker;
    private TextView dialog_Mp, dialog_yaw, dialog_pitch, dialog_roll;
    Dialog dialog = null;
    private CoordinateConverter converter;
    volatile boolean flag = false;
    private long lastDialogTimestamp = 0;
    private static MediaPlayer player;
    private DBHelper dbHelper = new DBHelper(this, "alarm_flag", null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initView();
        initLocation();
        update();
        Intent startService = new Intent(this, AlarmService.class);
        startService(startService);

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
        releaseMediaPlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //恢复地图
        mMapView.onResume();
        update();
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
            myListener = new MyLocationListener(mBaiduMap, mMapView);
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

    private void initView() {
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        //回到当前定位
        ibLocation = (ImageButton) findViewById(R.id.ib_location);
        mMapView.showScaleControl(false);  // 设置比例尺是否可见（true 可见/false不可见）
        //mMapView.showZoomControls(false);  // 设置缩放控件是否可见（true 可见/false不可见）
        mMapView.removeViewAt(1);// 删除百度地图Logo
        mBaiduMap = mMapView.getMap();
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.map_dialog_layout);
        close_dialog = dialog.findViewById(R.id.close_dialog);
        to_navi = dialog.findViewById(R.id.to_navigation);
        dialog_Mp = dialog.findViewById(R.id.gas_state);
        dialog_yaw = dialog.findViewById(R.id.yaw);
        dialog_pitch = dialog.findViewById(R.id.pitch);
        dialog_roll = dialog.findViewById(R.id.roll);
        close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        to_navi.setOnClickListener(v -> {
            //到导航页面
            startNavigation();
        });
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d("marker", "marker点击事件被调用");
                LatLng latLng = marker.getPosition();
                dialog.setTitle("井盖详情");
                initNavigation();
                if (manhole != null) {
                    dialog_Mp.setText("" + manhole.getMpvalue());
                }
                dialog.show();
                return true;
            }
        });
        AsyncTask.execute(() -> {
            manhole = request.getOneFromServer("Peng_01");
            if (manhole != null) {
                converter = new CoordinateConverter().from(CoordinateConverter.CoordType.GPS).coord(new LatLng(manhole.getLatitude(), manhole.getLongitude()));
                LatLng position = converter.convert();
                OverlayOptions options = new MarkerOptions().position(position).clickable(true).icon(bitmap);
                marker = (Marker) mBaiduMap.addOverlay(options);
            } else {
                LatLng latLng = new LatLng(markerLatitude, markerLongitude);
                OverlayOptions options = new MarkerOptions().position(latLng).clickable(true).icon(bitmap);
                marker = (Marker) mBaiduMap.addOverlay(options);
            }
        });
    }

    public void startNavigation() {
        try {
            BNRoutePlanNode sNode = new BNRoutePlanNode.Builder()
                    .latitude(currentLatitude)
                    .longitude(currentLongitude)
                    .name("自己")
                    .description("自己")
                    .build();
            BNRoutePlanNode eNode = new BNRoutePlanNode.Builder()
                    .latitude(manhole.getLatitude())
                    .longitude(manhole.getLongitude())
                    .name("终点")
                    .description("井盖")
                    .build();
            List<BNRoutePlanNode> list = new ArrayList<>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManagerFactory.getRoutePlanManager().routePlan(list,
                    IBNRoutePlanManager.RoutePlanPreference.ROUTE_PLAN_PREFERENCE_DEFAULT,
                    null,
                    new Handler(Looper.getMainLooper()) {
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
        } catch (BaiduMapAppNotSupportNaviException be) {
            be.printStackTrace();
        }
    }

    public void initNavigation() {
        BNaviInitConfig config = new BNaviInitConfig.Builder().naviInitListener(new IBaiduNaviManager.INaviInitListener() {
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

        BNTTsInitConfig.Builder builder = new BNTTsInitConfig.Builder();
        builder.context(getApplicationContext());
        BaiduNaviManagerFactory.getTTSManager().initTTS(builder.build());
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
        currentLatitude = latitude;
        currentLongitude = longitude;
    }

    public void update() {
        AsyncTask.execute(() -> {
            while (true) {
                manhole = request.getOneFromServer("Peng_01");
                flag = dbHelper.getFlag();
                if (manhole != null) {
                    LatLng source = new LatLng(manhole.getLatitude(), manhole.getLongitude());
                    converter = new CoordinateConverter().from(CoordinateConverter.CoordType.GPS).coord(source);
                    LatLng p = converter.convert();
                    marker.setPosition(p);
                }
                if (dialog_Mp != null && manhole != null) {
                    Log.d("D", "update: in");
                    runOnUiThread(() -> {
                        dialog_Mp.setText(manhole.getMpvalue() + "");
                        dialog_roll.setText(manhole.getRolll() + "");
                        dialog_pitch.setText(manhole.getPitchh() + "");
                        dialog_yaw.setText(manhole.getYaww() + "");
                    });
                }
                if (flag) {
                    Log.d("TEST", "playAudio yes ");
                    runOnUiThread(() -> {
                        marker.setIcon(w_bitmap);
                        showCustomDialog("警告，井盖出现倾斜");
                        playAudio(getApplicationContext());
                    });
                } else {
                    Log.d("TEST", "playAudio no ");
                    runOnUiThread(() -> {
                        marker.setIcon(bitmap);
                        stopAudio();
                    });
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void showCustomDialog(String message) {
        long currentTimestamp = System.currentTimeMillis();
        if (currentTimestamp - lastDialogTimestamp >= 10 * 60 * 1000) { // 检查时间差是否大于等于十分钟
            // 可以弹出 dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("警告");
            builder.setMessage(message);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            // 更新上一次 dialog 弹出的时间戳
            lastDialogTimestamp = currentTimestamp;
        }
    }

    public void playAudio(Context context) {
        if (player == null) {
            player = new MediaPlayer();
            try {
                // 打开音频文件
                AssetFileDescriptor descriptor = context.getAssets().openFd("warning.mp3");
                player.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                descriptor.close();
                player.prepare(); // 准备 MediaPlayer
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!player.isPlaying()) {
            player.start(); // 开始播放音频
        }
    }
    public  void stopAudio() {
        if (player != null && player.isPlaying()) {
            player.pause(); // 暂停音频播放
        }
    }

    // 释放 MediaPlayer 资源的方法
    public  void releaseMediaPlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
}

