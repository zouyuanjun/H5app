package com.xinzhu.myapplication.utils;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.webkit.JavascriptInterface;

import com.xinzhu.myapplication.Log;
import com.xinzhu.myapplication.activity.NativeActivity;
import com.xinzhu.myapplication.bean.SysInfo;
import com.xinzhu.myapplication.event.SaveData;
import com.xinzhu.myapplication.event.ToActivity;
import com.zou.fastlibrary.utils.CommonUtil;
import com.zou.fastlibrary.utils.JsonUtils;

import org.greenrobot.eventbus.EventBus;

import pub.devrel.easypermissions.EasyPermissions;

public class JsApi {


    @JavascriptInterface
    public String testSyn(Object msg) {
        return msg + "通过桥梁调用";
    }


    @JavascriptInterface
    public void toNativeActivity( Object msg) {
        EventBus.getDefault().post(new ToActivity(1));
    }
    @JavascriptInterface
    public void savedata(Object key){
        String data=key.toString();
        String[] s=data.split("\\|");
        EventBus.getDefault().post(new SaveData(s[0],s[1]));
    }

    @JavascriptInterface
    public String getvalue(Object key){
        String data=key.toString();
        SharedPreferences sharedPreferences = DataKeeper.getRootSharedPreferences(MyApplication.getContext());
        String valu = sharedPreferences.getString(data, "key不存在");
        return valu;
    }
    @JavascriptInterface
    public void qr(Object key){
        EventBus.getDefault().post(new ToActivity(2));
    }
    @JavascriptInterface
    public void callPhone(Object key){
        EventBus.getDefault().post(new ToActivity(1,key.toString()));
    }
    @JavascriptInterface
    public void sendMessage(Object key){
        EventBus.getDefault().post(new ToActivity(2,key.toString()));
    }
    @JavascriptInterface
    public String getSysinfo(Object key){
        Log.d("系统版本"+BuildHelper.getAndroidVersion());
        return  JsonUtils.objectToString(new SysInfo(BuildHelper.getMode(),BuildHelper.getAndroidVersion(),BuildHelper.getProduct(),BuildHelper.getCurSDK()));
    }
}
