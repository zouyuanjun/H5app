package com.xinzhu.myapplication.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
   static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context=this.getApplicationContext();
    }

    public  static Context getContext() {
        return context;
    }

}
