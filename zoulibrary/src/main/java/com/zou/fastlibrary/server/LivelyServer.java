package com.zou.fastlibrary.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.zou.fastlibrary.utils.JsonUtils;
import com.zou.fastlibrary.utils.Network;
import com.zou.fastlibrary.utils.SettingUtil;
import com.zou.fastlibrary.utils.StringUtil;

public class LivelyServer extends Service {
long starttime;
long stoptime;
boolean exit=true;
    @Override
    public void onCreate() {
        super.onCreate();
        starttime=System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (exit){
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    stoptime=System.currentTimeMillis();
                    SettingUtil.USERTIME =  SettingUtil.USERTIME+(int) ((stoptime-starttime)/1000/60);
                    starttime=stoptime;
                }


            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        exit=false;
        if (SettingUtil.USERTIME>=1){
            if (!StringUtil.isEmpty(SettingUtil.TOKEN)){
                String data = JsonUtils.keyValueToString2("dictionaryId", 25, "token", SettingUtil.TOKEN);
                data = JsonUtils.addKeyValue(data, "time", SettingUtil.USERTIME);
                Network.getnetwork().postJson(data, "http://demoapp.xuezhiben.com" + "/app/complete-my-task", null, 6);
            }
        }


        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
