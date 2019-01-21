package com.zou.fastlibrary.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zou.fastlibrary.R;
import com.zou.fastlibrary.ui.CustomNavigatorBar;
import com.zou.fastlibrary.utils.Log;

public class WebActivity extends BaseActivity {
    WebView webView;
    Activity context;
    String url="";
    CustomNavigatorBar customNavigatorBar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        webView=findViewById(R.id.webview);
        customNavigatorBar=findViewById(R.id.appbar);
        context=this;
        url=getIntent().getStringExtra("URL");
        WebSettings webSettings = webView.getSettings();//获取webview设置属性
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//把html中的内容放大webview等宽的一列中
        webSettings.setJavaScriptEnabled(true);//支持js
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.setWebViewClient(new MyWebViewClient());
        webView.addJavascriptInterface(this, "App");
        if (!url.isEmpty()){
        webView.loadUrl(url);
        customNavigatorBar.setLeftImageOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }}
    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            imgReset();
          //  webView.loadUrl("javascript:App.resize(document.body.getBoundingClientRect().height)");
            super.onPageFinished(view, url);


        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        private void imgReset() {
            webView.loadUrl("javascript:(function(){" +
                    "var objs = document.getElementsByTagName('img'); " +
                    "for(var i=0;i<objs.length;i++)  " +
                    "{"
                    + "var img = objs[i];   " +
                    " img.style.maxWidth = '100%';img.style.height='auto';" +
                    "}" +
                    "})()");
        }
    }
    @JavascriptInterface
    public void resize(final float height) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) webView.getLayoutParams();


                layoutParams.width = (int)((getResources().getDisplayMetrics().widthPixels)*0.7);
                layoutParams.height = (int) (height * getResources().getDisplayMetrics().density)+50;
                Log.d(layoutParams.width+"高度是"+layoutParams.height+"原始"+height+"级"+getResources().getDisplayMetrics().density);
                webView.setLayoutParams(layoutParams);
                //Toast.makeText(getActivity(), height + "", Toast.LENGTH_LONG).show();
                //    wbFeedback.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, (int) (height * getResources().getDisplayMetrics().density)));
            }
        });
    }

}
