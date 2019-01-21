package com.zou.fastlibrary.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bravin.btoast.BToast;
import com.zou.fastlibrary.bean.NetWorkMessage;
import com.zou.fastlibrary.server.LivelyServer;
import com.zou.fastlibrary.utils.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {
    Context context;
    Intent serverintent;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 添加Activity到堆栈
        AtyContainer.getInstance().addActivity(this);
        EventBus.getDefault().register(this);
        context=this;

    }

    @Override
    protected void onResume() {
        super.onResume();
        serverintent=new Intent(BaseActivity.this,LivelyServer.class);
        startService(serverintent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void netWorkMessage(NetWorkMessage messageEvent) {
        String s=messageEvent.getMessage();
        BToast.error(context).text(s).show();
       }

    @Override
    protected void onPause() {
        stopService(serverintent);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 结束Activity&从栈中移除该Activity
        AtyContainer.getInstance().removeActivity(this);
    }
    public void goToActivity(Context context, Class toActivity) {
        Intent intent = new Intent(context, toActivity);
        startActivity(intent);
        finish();
    }
    public void networktimeout(){
        BToast.error(context).text("网络连接超时···").show();
    }

    public void networkerr(){
        BToast.error(context).text("网络连接失败···").show();
    }
    public void servererr(){
        BToast.error(context).text("服务器内部错误，正在紧急修复中···").show();
    }
    public  void finishAllActivity(){
        AtyContainer.getInstance().finishAllActivity();
    }
}

class AtyContainer {

    private AtyContainer() {
    }

    private static AtyContainer instance = new AtyContainer();
    private static List<Activity> activityStack = new ArrayList<Activity>();

    public static AtyContainer getInstance() {
        return instance;
    }

    public void addActivity(Activity aty) {
        activityStack.add(aty);
    }

    public void removeActivity(Activity aty) {
        activityStack.remove(aty);
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

}
