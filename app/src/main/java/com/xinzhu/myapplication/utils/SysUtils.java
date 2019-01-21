package com.xinzhu.myapplication.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.View;

public class SysUtils {
   static ComponentName defaultComponent;
    static ComponentName testComponent;
   static PackageManager packageManager;
    static ComponentName componentName;
    public static void ChangeIoc(Activity context,int tag){

        componentName = context.getComponentName();

        //拿到我们注册的MainActivity组件
        defaultComponent = new ComponentName(context, "com.xinzhu.myapplication.MainActivity");  //拿到默认的组件
        //拿到我注册的别名test组件
        testComponent = new ComponentName(context, "com.xinzhu.myapplication.test");
        packageManager = context.getPackageManager();
        disableComponent(componentName);
        if (tag==1){
            enableComponent(testComponent);
        }else {
            enableComponent(defaultComponent);
        }
    }

    /**
     * 启用组件
     *
     * @param componentName
     */
    public static void enableComponent(ComponentName componentName) {
        int state = packageManager.getComponentEnabledSetting(componentName);
        if (state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            //已经启用
            return;
        }
        packageManager.setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    /**
     * 禁用组件
     *
     * @param componentName
     */
    private static void disableComponent(ComponentName componentName) {
        int state = packageManager.getComponentEnabledSetting(componentName);
        if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            //已经禁用
            return;
        }
        packageManager.setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
