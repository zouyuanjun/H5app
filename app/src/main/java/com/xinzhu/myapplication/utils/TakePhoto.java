package com.xinzhu.myapplication.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import com.xinzhu.myapplication.R;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import pub.devrel.easypermissions.EasyPermissions;

public class TakePhoto {

    public static void takephoto(Activity context,int code){
        int requestCode =code;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            EasyPermissions.requestPermissions(context, "需要获取相册读写权限", 0, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA);
            //    Toast.makeText(this, "请在应用管理中打开“读写存储”访问权限！", Toast.LENGTH_LONG).show();
        } else  {
            Matisse.from(context)
                    .choose(MimeType.ofImage(), false) // 选择 mime 的类型
                    .showSingleMediaType(true)
                    .countable(true)
                    .maxSelectable(9) // 图片选择的最多数量
                    .gridExpectedSize(400)
                    .capture(true)//选择照片时，是否显示拍照
                    .captureStrategy(new CaptureStrategy(false, "com.xinzhu.myapplication.provider"))//参数1 true表示拍照存储在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f) // 缩略图的比例
                    .imageEngine(new Glide4Engine()) // 使用的图片加载引擎
                    .forResult(requestCode); // 设置作为标记的请求码

        }
    }
}
