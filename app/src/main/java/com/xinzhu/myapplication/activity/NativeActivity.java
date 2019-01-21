package com.xinzhu.myapplication.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.widget.TextView;

import com.xinzhu.myapplication.Log;
import com.xinzhu.myapplication.R;
import com.xinzhu.myapplication.event.Neterr;
import com.xinzhu.myapplication.utils.JsApi;
import com.xinzhu.myapplication.utils.WebViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import wendu.dsbridge.DWebView;

public class NativeActivity extends AppCompatActivity {
    @BindView(R.id.webview)
    DWebView webview;
    @BindView(R.id.textView3)
    TextView textView3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        webview.setWebViewClient(new WebViewUtil.MyWebViewClient(this, webview));
        webview.setWebChromeClient(new WebChromeClient());
        webview.loadUrl("http://www.baidu.com/");
        webview.addJavascriptObject(new JsApi(), null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void netWorkMessage(Neterr messageEvent) {
        Log.d("收到消息了");
        String s = messageEvent.getMessage();
        webview.setVisibility(View.GONE);
        textView3.setVisibility(View.VISIBLE);
        textView3.setText("这是自定义的错误界面，网络挂掉了，原因："+s);
    }

    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
           finish();
        }
    }
}
