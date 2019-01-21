package com.xinzhu.myapplication.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xinzhu.myapplication.R;
import com.xinzhu.myapplication.utils.Constant;
import com.xinzhu.myapplication.utils.Record;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

public class RecordActivity extends AppCompatActivity {
    @BindView(R.id.button2)
    Button button2;
    @BindView(R.id.button3)
    Button button3;
    @BindView(R.id.textView2)
    TextView textView2;
    CountDownTimer countDownTimer;
    int time = 0;
    String path;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        ButterKnife.bind(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            EasyPermissions.requestPermissions(this, "需要获取相册读写权限", 0, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO);
            //    Toast.makeText(this, "请在应用管理中打开“读写存储”访问权限！", Toast.LENGTH_LONG).show();
        }else {
            Record.startRecord();
        }

        countDownTimer = new CountDownTimer(15 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time++;
                textView2.setText("正在录音。。。。" + time + "s");
            }

            @Override
            public void onFinish() {
                textView2.setText("结束录音");
                path= Record.stopRecord();
            }
        };
        countDownTimer.start();
    }

    @OnClick({R.id.button2, R.id.button3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button2:
                countDownTimer.cancel();
                path=Record.stopRecord();
                Intent intent=new Intent();
                intent.putExtra(Constant.INTENT_STRING,path);
                setResult(Constant.RECORD_SEND_CODE,intent);
                finish();
                break;
            case R.id.button3:
                countDownTimer.cancel();
                Record.stopRecord();
                setResult(Constant.RECORD_SEND_CANCEL);
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Record.recording){
            Record.stopRecord();
        }

        countDownTimer.cancel();
    }
}
