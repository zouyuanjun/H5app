package com.xinzhu.myapplication.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.bravin.btoast.BToast;
import com.xinzhu.myapplication.Log;
import com.xinzhu.myapplication.R;
import com.xinzhu.myapplication.event.Neterr;
import com.xinzhu.myapplication.event.ToActivity;
import com.xinzhu.myapplication.utils.Constant;
import com.xinzhu.myapplication.utils.Glide4Engine;
import com.xinzhu.myapplication.utils.JsApi;
import com.xinzhu.myapplication.utils.MyApplication;
import com.xinzhu.myapplication.utils.Record;
import com.xinzhu.myapplication.utils.TakePhoto;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zou.fastlibrary.utils.CommonUtil;
import com.zou.fastlibrary.utils.JSON;
import com.zou.fastlibrary.utils.JsonUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;
import wendu.dsbridge.DWebView;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission_group.CAMERA;

public class WebViewActivity extends AppCompatActivity {
    @BindView(R.id.webview)
    DWebView webview;
    @BindView(R.id.textView3)
    TextView textView3;
    ValueCallback<Uri[]> myfilePathCallback; //文件返回
    Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        this.activity = this;
        webview.setWebViewClient(new MyWebViewClient(this, webview));
        webview.setWebChromeClient(new MyWebChromeClient());
        webview.loadUrl("file:///android_asset/js_java_interaction.html");
        webview.addJavascriptObject(new JsApi(), null);
        pre();
    }

    /**
     * 选择图片
     */
    public void takephoto() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            EasyPermissions.requestPermissions(activity, "需要获取相册读写权限", 0, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, RECORD_AUDIO);
            //    Toast.makeText(this, "请在应用管理中打开“读写存储”访问权限！", Toast.LENGTH_LONG).show();
        } else {
            Matisse.from(activity)
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
                    .forResult(Constant.TAKE_PHOTO_CODE); // 设置作为标记的请求码

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("返回结果" + requestCode);
        switch (requestCode) {
            case Constant.TAKE_PHOTO_CODE:
                if (data == null) {
                    myfilePathCallback.onReceiveValue(null);
                    return;
                }
                List<Uri> selected = Matisse.obtainResult(data);
                if (selected.size() == 0) {
                    myfilePathCallback.onReceiveValue(null);
                    return;
                }
                Uri[] photos = new Uri[selected.size()];
                for (int i = 0; i < selected.size(); i++) {
                    photos[i] = selected.get(i);
                }
                myfilePathCallback.onReceiveValue(photos);
                myfilePathCallback = null;
                break;
            case Constant.RECORD_CODE:
                if (resultCode == Constant.RECORD_SEND_CODE) {
                       if (null!=data){
                           String path=data.getStringExtra(Constant.INTENT_STRING);
                           if (null!=path&&!path.isEmpty()){
                               Uri uri = Uri.fromFile(new File(path));
                               Uri[] record=new Uri[1];
                               record[0]=uri;
                               myfilePathCallback.onReceiveValue(record);
                               Log.d("发送到js");
                               myfilePathCallback=null;
                           }else {
                               myfilePathCallback.onReceiveValue(null);
                           }
                       }else {
                           myfilePathCallback.onReceiveValue(null);
                       }
                }else if (resultCode==Constant.RECORD_SEND_CANCEL){
                    myfilePathCallback.onReceiveValue(null);
                }
                myfilePathCallback=null;
        }
        if (requestCode == Constant.TAKE_PHOTO_CODE) {

        } else if (resultCode == 2) {
            if (null != data) {
                String qrcode = data.getStringExtra("RESULT_QRCODE_STRING");
                BToast.info(activity).text("扫描结果：" + qrcode).show();
                webview.callHandler("qrresult", new Object[]{qrcode});

            }
        }


    }

    public class MyWebViewClient extends WebViewClient {
        Activity context;
        WebView webView;

        public MyWebViewClient(Activity context, WebView webView) {
            this.context = context;
            this.webView = webView;
            init(false);
        }

        public MyWebViewClient(Activity context, WebView webView, boolean horizontalScrollBarEnabled) {
            this.context = context;
            this.webView = webView;
            init(horizontalScrollBarEnabled);
        }

        private void init(boolean HorizontalScrollBarEnabled) {
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
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        /**
         * 拦截网页错误
         *
         * @param view
         * @param request
         * @param error
         */
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (request.isForMainFrame()) {
                Log.d(request.getUrl().getPath());
                Log.d(error.getDescription().toString());
                Log.d(error.getErrorCode() + "状态码");
                webview.setVisibility(View.GONE);
                textView3.setVisibility(View.VISIBLE);
                textView3.setText("这是自定义的错误界面，网络挂掉了，原因："+error.getDescription().toString()+"状态码："+error.getErrorCode());
            }

        }

        /**
         * 处理图片缩放问题
         */
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

    public class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            myfilePathCallback = filePathCallback;
            String accept = fileChooserParams.getAcceptTypes()[0];
            if (accept.equals("image/jpeg")) {
                takephoto();
            } else if (accept.equals("audio/amr-wb")) {
                Intent intent = new Intent(activity, RecordActivity.class);
                startActivityForResult(intent, Constant.RECORD_CODE);
            }
            return true;
        }
    }

    public void pre(){
        if ((ContextCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)||(ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)) {
            EasyPermissions.requestPermissions(activity, "需要获取必要权限才可以正常使用哦", 1, Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE,CAMERA, RECORD_AUDIO,SEND_SMS);
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void netWorkMessage(ToActivity messageEvent) {
        Log.d("收到消息了");
       int tag = messageEvent.getTag();
       String message=messageEvent.getMessage();
        if (tag==1){
            CommonUtil.call(this, message);
        }else if (tag==2){
            if (JSON.isJSONObject(message)){
                String phone=JsonUtils.getStringValue(message,"phone");
                String smsmessage=JsonUtils.getStringValue(message,"message");
                CommonUtil.toMessageChat(this,phone,smsmessage);
            }else {
                Log.d("直接发送短信");
                CommonUtil.toMessageChat(this,message,"222");
            }

        }
    }
}
