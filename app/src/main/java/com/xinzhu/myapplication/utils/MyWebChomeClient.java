package com.xinzhu.myapplication.utils;

import android.app.Activity;
import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class MyWebChomeClient extends WebChromeClient {
    Activity activity;

    public MyWebChomeClient(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {

        String types=fileChooserParams.getAcceptTypes()[0];
        if (types.equals("img")){
            TakePhoto.takephoto(activity,1);
        }
        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
    }
}
