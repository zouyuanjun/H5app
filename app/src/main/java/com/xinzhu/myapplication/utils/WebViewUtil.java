package com.xinzhu.myapplication.utils;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xinzhu.myapplication.Log;
import com.xinzhu.myapplication.event.Neterr;

import org.greenrobot.eventbus.EventBus;

public class WebViewUtil {

    public static class MyWebViewClient extends WebViewClient {
        Activity context;
        WebView webView;

        public MyWebViewClient(Activity context, WebView webView) {
            this.context = context;
            this.webView = webView;
            init( false);
        }
        public MyWebViewClient(Activity context, WebView webView, boolean horizontalScrollBarEnabled) {
            this.context = context;
            this.webView = webView;
            init( horizontalScrollBarEnabled);
        }
        private void init(boolean HorizontalScrollBarEnabled){
            this.webView.addJavascriptInterface(this, "android");
            WebSettings settings = this.webView.getSettings();//获取webview设置属性
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//把html中的内容放大webview等宽的一列中
            settings.setJavaScriptEnabled(true);//支持js
            this.webView.setHorizontalScrollBarEnabled(false);//水平不显示
            this.webView.setVerticalScrollBarEnabled(HorizontalScrollBarEnabled); //垂直不显示
            this.webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            settings.setUseWideViewPort(false);
            settings.setDomStorageEnabled(true);
            settings.setDefaultTextEncodingName("UTF-8");
            settings.setAllowContentAccess(true); // 是否可访问Content Provider的资源，默认值 true
            settings.setAllowFileAccess(true);    // 是否可访问本地文件，默认值 true
            // 是否允许通过file url加载的Javascript读取本地文件，默认值 false
            settings.setAllowFileAccessFromFileURLs(true);
            // 是否允许通过file url加载的Javascript读取全部资源(包括文件,http,https)，默认值 false
            settings.setAllowUniversalAccessFromFileURLs(true);
        }
        @Override
        public void onPageFinished(WebView view, String url) {
         //   imgReset();
            super.onPageFinished(view, url);
        //    webView.loadUrl("javascript:App.resize(document.body.getBoundingClientRect().height)");
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (request.isForMainFrame()){
                Log.d(request.getUrl().getPath());
                Log.d(error.getDescription().toString());
                Log.d(error.getErrorCode()+"状态码");
                EventBus.getDefault().post(new Neterr(error.getDescription().toString()+error.getErrorCode()+""));
            }

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
        @JavascriptInterface
        public void resize(final float height) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ViewGroup.LayoutParams layoutParams = webView.getLayoutParams();
                    layoutParams.width = (int)((context.getResources().getDisplayMetrics().widthPixels));
                    layoutParams.height = (int) (height * context.getResources().getDisplayMetrics().density)+50;
                    Log.d(layoutParams.width+"高度是"+layoutParams.height+"原始"+height+"级"+context.getResources().getDisplayMetrics().density);
                    webView.setLayoutParams(layoutParams);
                    //Toast.makeText(getActivity(), height + "", Toast.LENGTH_LONG).show();
                    //    wbFeedback.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, (int) (height * getResources().getDisplayMetrics().density)));
                }
            });
        }

        @JavascriptInterface//一定要写，不然H5调不到这个方法
        public String back() {
            TakePhoto.takephoto(context,2);
            return "我是java里的方法返回值";
        }
    }

}
