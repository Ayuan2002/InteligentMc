package com.example.myapplication.handler;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.example.myapplication.entity.Mc_cover;

public class MyThreadHandler extends Handler {
    private HandlerCallback callback;
    public interface HandlerCallback {
        void updateUI(Mc_cover mc);
    }
    public MyThreadHandler(Looper looper,HandlerCallback callback){
        super(looper);
        this.callback=callback;
    }
    @Override
    public void handleMessage(@NonNull Message msg) {
        if (msg.what == 1) {
            Mc_cover mc = (Mc_cover) msg.obj;
            if (callback != null) {
                callback.updateUI(mc);
            }
        }
    }
}
