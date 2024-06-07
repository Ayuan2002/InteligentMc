package com.example.myapplication.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.myapplication.R;

public class WarnVideoPlayer {
    private MediaPlayer mediaPlayer;

    public WarnVideoPlayer(Context context) {
        mediaPlayer = MediaPlayer.create(context, R.raw.warn);
        mediaPlayer.setOnCompletionListener(mp -> {
            // 音频播放完成后的回调
            stop();
        });
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            // 音频播放出错的回调
            Log.e("WarnVideoPlayer", "MediaPlayer error: " + what + ", " + extra);
            stop();
            return true;
        });
    }
    public void play() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }
    // 停止音频播放
    public void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // 释放 MediaPlayer 资源
    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
