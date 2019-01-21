package com.xinzhu.myapplication.fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;

import com.bravin.btoast.BToast;
import com.xinzhu.myapplication.Log;
import com.xinzhu.myapplication.R;
import com.xinzhu.myapplication.activity.NativeActivity;
import com.xinzhu.myapplication.activity.QRActivity;
import com.xinzhu.myapplication.event.ToActivity;
import com.xinzhu.myapplication.utils.JsApi;
import com.xinzhu.myapplication.utils.MyWebChomeClient;
import com.xinzhu.myapplication.utils.TakePhoto;
import com.xinzhu.myapplication.utils.WebViewUtil;
import com.zhihu.matisse.Matisse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pub.devrel.easypermissions.EasyPermissions;
import wendu.dsbridge.DWebView;

public class CameraFragment extends Fragment {
    @BindView(R.id.webview)
    DWebView webview;
    Unbinder unbinder;
    ValueCallback<Uri[]> myfilePathCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.camera_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        webview.setWebViewClient(new WebViewUtil.MyWebViewClient(getActivity(), webview,true));
        webview.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                myfilePathCallback=filePathCallback;
                String s[]=fileChooserParams.getAcceptTypes();
                Log.d("属性长度"+s.length+s[0].toString());
                TakePhoto.takephoto(getActivity(),1);
                return true;
            }
        });
        // webview.loadDataWithBaseURL(null, "正在加载", "text/html", "UTF-8", null);
        webview.loadUrl("file:///android_asset/camera.html");
        webview.addJavascriptObject(new JsApi(),null);
        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void netWorkMessage(ToActivity messageEvent) {
        Log.d("收到消息了");
        int s=messageEvent.getTag();
      if (s==2){
            startScanQR();
        }
    }
    public void startScanQR() {
        if (EasyPermissions.hasPermissions(getContext(), Manifest.permission.CAMERA)) {
           getActivity().startActivityForResult(new Intent(getContext(), QRActivity.class), 100);
        } else {
            EasyPermissions.requestPermissions(getActivity(), "使用扫一扫功能需要允许相机权限哦！", 1, Manifest.permission.CAMERA);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("返回结果"+resultCode);
        if (resultCode==1){
            if (data == null) {
                myfilePathCallback.onReceiveValue(null);
                return;
            }
            List<Uri> selected = Matisse.obtainResult(data);
            if (selected.size() == 0) {
                myfilePathCallback.onReceiveValue(null);
                return;
            }
            Uri[] photos=new Uri[selected.size()];
            for (int i=0;i<selected.size();i++){
                photos[i]=selected.get(i);
            }
            myfilePathCallback.onReceiveValue(photos);
            myfilePathCallback=null;
        }else if (resultCode==2){
            if (null != data) {
                String qrcode = data.getStringExtra("RESULT_QRCODE_STRING");
                BToast.info(getContext()).text("扫描结果：" + qrcode).show();
                webview.callHandler("qrresult",new Object[]{qrcode});

            }
        }



    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }

        if (null!=data&&!data.isEmpty()) {
            if (uri != null) {
                String uriString = uri.toString();
                int index = uriString.lastIndexOf("/");
                String imageName = uriString.substring(index);
                File storageDir;

                storageDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);
                File file = new File(storageDir, imageName);
                if (file.exists()) {
                    data = file.getAbsolutePath();
                } else {
                    storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    File file1 = new File(storageDir, imageName);
                    data = file1.getAbsolutePath();
                }
            }
        }
        return data;
    }
}
