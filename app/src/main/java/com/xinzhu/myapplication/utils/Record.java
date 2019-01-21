package com.xinzhu.myapplication.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.IOException;

import pub.devrel.easypermissions.EasyPermissions;

import static it.sephiroth.android.library.imagezoom.ImageViewTouchBase.LOG_TAG;

public class Record {
    static MediaRecorder recorder;
   public static  boolean recording=false;
    static String path;
    public static boolean startRecord() {

        recorder = new MediaRecorder();
        try {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        }catch (IllegalStateException e){
            Log.i(LOG_TAG, "设置录音源失败");
            e.printStackTrace();
        }
        DataKeeper.init(MyApplication.getContext());
        path=DataKeeper.getAudioFileCachePath(System.currentTimeMillis()+".");
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
        recorder.setOutputFile(path);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        try {
            recorder.prepare();
        }catch (IOException e){
            Log.e(LOG_TAG, "准备失败");
            e.printStackTrace();
        }
        recorder.start();
        recording=true;
        Log.i(LOG_TAG, "开始录音..."+path);
        return true;
    }
    public static String stopRecord() {
        if (null==recorder){
            Log.i(LOG_TAG, "还没有开始录音");
            return "";
        }else if (recording){
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
            recording=false;
            Log.i(LOG_TAG, "停止录音");
            return path;
        }else{
            return "";
        }

    }

}
