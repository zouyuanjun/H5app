package com.xinzhu.myapplication.fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xinzhu.myapplication.Log;
import com.xinzhu.myapplication.R;
import com.xinzhu.myapplication.activity.NativeActivity;
import com.xinzhu.myapplication.activity.QRActivity;
import com.xinzhu.myapplication.event.SaveData;
import com.xinzhu.myapplication.event.ToActivity;
import com.xinzhu.myapplication.utils.DataKeeper;
import com.xinzhu.myapplication.utils.JsApi;
import com.xinzhu.myapplication.utils.MyApplication;
import com.xinzhu.myapplication.utils.Record;
import com.xinzhu.myapplication.utils.WebViewUtil;
import com.zhihu.matisse.Matisse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pub.devrel.easypermissions.EasyPermissions;
import wendu.dsbridge.DWebView;
import wendu.dsbridge.OnReturnValue;

public class H5Fragment extends Fragment {
    @BindView(R.id.webview)
    DWebView webview;
    @BindView(R.id.button4)
    Button button4;
    @BindView(R.id.button5)
    Button button5;
    @BindView(R.id.textView)
    TextView textView;
    Unbinder unbinder;
    @BindView(R.id.imageView)
    ImageView imageView;
    private MediaRecorder recorder;  // 录音类
    private String fileName;  // 录音生成的文件存储路径
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.h5fragment, container, false);
EventBus.getDefault().register(this);
        unbinder = ButterKnife.bind(this, view);
        webview.setWebViewClient(new WebViewUtil.MyWebViewClient(getActivity(), webview));
        webview.setWebChromeClient(new WebChromeClient());
        // webview.loadDataWithBaseURL(null, "正在加载", "text/html", "UTF-8", null);
        webview.loadUrl("file:///android_asset/js_java_interaction.html");
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
        if (s==1){
         Intent intent=new Intent(getActivity(),NativeActivity.class);
         startActivity(intent);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Savedata(SaveData messageEvent) {
        DataKeeper.init(MyApplication.getContext());
        SharedPreferences sharedPreferences = DataKeeper.getRootSharedPreferences(MyApplication.getContext());
        DataKeeper.save(sharedPreferences,messageEvent.getKey(),messageEvent.getValu());
        Log.d("保存数据："+messageEvent.getKey()+"  值："+messageEvent.getValu());
    }
    @OnClick({R.id.button4, R.id.button5})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button4:
                //录音文件目录
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    EasyPermissions.requestPermissions(getActivity(), "需要获取相册读写权限", 0, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO);
                    //    Toast.makeText(this, "请在应用管理中打开“读写存储”访问权限！", Toast.LENGTH_LONG).show();
                } else {
                    fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audiorecordertest.amr";
                    Record.startRecord();
                }
                break;
            case R.id.button5:

                Record.stopRecord();
                //Android调用有返回值js方法，安卓4.4以上才能用这个方法
                webview.callHandler("sum", new Object[]{1, 5}, new OnReturnValue<String>() {

                    @Override
                    public void onValue(String retValue) {
                        Toast.makeText(getContext(), "js返回的结果为=" + retValue, Toast.LENGTH_LONG).show();
                    }
                });
//                webview.evaluateJavascript("sum(1,2)", new ValueCallback<String>() {
//                    @Override
//                    public void onReceiveValue(String value) {
//                        Log.d("js返回的结果为=" + value);
//                        Toast.makeText(getContext(), "js返回的结果为=" + value, Toast.LENGTH_LONG).show();
//                    }
//                });
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("返回结果");
        if (data == null) {
            return;
        }
        List<Uri> selected = Matisse.obtainResult(data);
        if (selected.size() == 0) {
            return;
        }
        File file = new File(getRealFilePath(getContext(), selected.get(0)));
        Log.d("路径"+file.getPath());
        String path = getRealFilePath(getContext(), selected.get(0));
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(path);
        } catch (
                FileNotFoundException e)

        {
            e.printStackTrace();
        }

        Bitmap bitmap = BitmapFactory.decodeStream(fs);
        if (null == bitmap) {
            imageView.setBackgroundResource(R.drawable.ic_emtpy_light);
        } else {
            imageView.setImageBitmap(bitmap);
        }
        webview.loadUrl("javascript:alertMessage(\"" + "file://" + path + "\")");
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

        if (null == data ||data.isEmpty()) {
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
