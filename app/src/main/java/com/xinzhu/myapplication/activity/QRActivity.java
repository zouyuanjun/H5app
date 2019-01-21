package com.xinzhu.myapplication.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.xinzhu.myapplication.R;
import com.zou.fastlibrary.ui.CustomNavigatorBar;
import com.zou.fastlibrary.ui.ShapeCornerBgView;
import com.zxing.activity.CaptureActivity;
import com.zxing.view.ViewfinderView;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QRActivity extends CaptureActivity {

    @BindView(R.id.svCameraScan)
    SurfaceView svCameraScan;
    @BindView(R.id.vfvCameraScan)
    ViewfinderView vfvCameraScan;
    @BindView(R.id.appbar)
    CustomNavigatorBar appbar;
    boolean isswitchLight = false;
    @BindView(R.id.switchLight)
    ShapeCornerBgView switchLight;
    @BindView(R.id.img)
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_activity);
        ButterKnife.bind(this);
        init(new WeakReference<Activity>(this).get(), (SurfaceView) findViewById(R.id.svCameraScan), (ViewfinderView) findViewById(R.id.vfvCameraScan));
        appbar.setLeftImageOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    });
    }


    @OnClick(R.id.switchLight)
    public void onViewClicked() {
        isswitchLight = !isswitchLight;
        switchLight(isswitchLight);
        if (isswitchLight) {
            img.setImageResource(R.drawable.camera_flash_open);
        }else {
            img.setImageResource(R.drawable.camera_flash_close);
        }
    }
}
