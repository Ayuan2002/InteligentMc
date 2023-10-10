package com.example.myapplication;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import androidx.fragment.app.FragmentActivity;
import com.baidu.navisdk.adapter.BNaviCommonParams;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBNaviListener;
import com.baidu.navisdk.adapter.IBNaviViewListener;
import com.baidu.navisdk.adapter.struct.BNGuideConfig;

public class NavigationActivity extends FragmentActivity {
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = new Bundle();
        // IS_REALNAVI代表导航类型，true表示真实导航，false表示模拟导航，默认是true
        bundle.putBoolean(BNaviCommonParams.ProGuideKey.IS_REALNAVI,true);
        // IS_SUPPORT_FULL_SCREEN代表是否沉浸式，默认是true
        bundle.putBoolean(BNaviCommonParams.ProGuideKey.IS_SUPPORT_FULL_SCREEN, true);
        BNGuideConfig config = new BNGuideConfig.Builder().params(bundle).build();
        View view= BaiduNaviManagerFactory.getRouteGuideManager().onCreate(NavigationActivity.this, config);
        if (view != null) {
            setContentView(view);
        }
        BaiduNaviManagerFactory.getRouteGuideManager().setNaviListener(new IBNaviListener() {
            @Override
            public void onNaviGuideEnd() {
                finish();

            }
        });
        BaiduNaviManagerFactory.getRouteGuideManager().setNaviViewListener(new IBNaviViewListener() {
            @Override
            public void onMainInfoPanCLick() {

            }

            @Override
            public void onNaviTurnClick() {

            }

            @Override
            public void onFullViewButtonClick(boolean b) {

            }

            @Override
            public void onFullViewWindowClick(boolean b) {

            }

            @Override
            public void onNaviBackClick() {
                BaiduNaviManagerFactory.getRouteGuideManager().stopNavi();
            }

            @Override
            public void onBottomBarClick(Action action) {

            }

            @Override
            public void onNaviSettingClick() {

            }

            @Override
            public void onRefreshBtnClick() {

            }

            @Override
            public void onZoomLevelChange(int i) {

            }

            @Override
            public void onMapClicked(double v, double v1) {

            }

            @Override
            public void onMapMoved() {

            }

            @Override
            public void onFloatViewClicked() {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        BaiduNaviManagerFactory.getRouteGuideManager().onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BaiduNaviManagerFactory.getRouteGuideManager().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BaiduNaviManagerFactory.getRouteGuideManager().onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        BaiduNaviManagerFactory.getRouteGuideManager().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaiduNaviManagerFactory.getRouteGuideManager().onDestroy(false);
    }
}