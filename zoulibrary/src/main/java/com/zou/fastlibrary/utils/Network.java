package com.zou.fastlibrary.utils;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.zou.fastlibrary.bean.NetWorkMessage;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by zou on 2018/3/28.
 */

public class Network {
    private static final long cacheSize = 1024 * 1024 * 20;// 缓存文件最大限制大小20M
    private static String cacheDirectory = Environment.getExternalStorageDirectory() + "/okttpcaches"; // 设置缓存文件路径
    private static Cache cache = new Cache(new File(cacheDirectory), cacheSize);  //

    private static Network instance = new Network();

    public static Network getnetwork() {
        return instance;
    }

    private Network() {
    }

    public void getjson(HashMap<String, String> hashMap, final String url, final Handler handler, final int i) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addNetworkInterceptor(new CacheInterceptor())
                .cache(cache)
                .build();//创建OkHttpClient对象。
        String pram = "";
        if (null != hashMap) {
            for (String s : hashMap.keySet()) {
                pram = pram + "&" + s + "=" + hashMap.get(s);
            }
        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
        Log.d("发送请求URL" + url + "?" + pram);
        Request request = new Request.Builder()
                .cacheControl(new CacheControl.Builder().maxStale(10,TimeUnit.SECONDS).maxAge(2, TimeUnit.SECONDS)
                        .build())
                .url(url + "?" + pram)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (e instanceof SocketTimeoutException) {
                    //判断超时异常
                    EventBus.getDefault().post(new NetWorkMessage("服务器连接失败，请检查网络"));
                    Log.d("请求超时" + url);
                }
                if (e instanceof ConnectException) {
                    ////判断连接异常，
                    EventBus.getDefault().post(new NetWorkMessage("网络异常，请检查网络"));
                    Log.d("连接异常" + url);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message = new Message();
                String s = response.body().string();
                JSONObject jsonObject = null;
                try {
                    jsonObject = com.alibaba.fastjson.JSON.parseObject(s);
                } catch (JSONException e) {
                    EventBus.getDefault().post(new NetWorkMessage("服务器内部错误" + s));
                    return;
                }
                boolean b = false;
                try {
                    b = jsonObject.containsKey("Code");
                } catch (Exception e) {
                    Log.d(s);
                    EventBus.getDefault().post(new NetWorkMessage("请求错误" + s));
                    return;
                }

                if (!b) {
                    Log.d(s);
                    EventBus.getDefault().post(new NetWorkMessage("服务器内部错误"));
                    return;
                }
                if (null != handler) {
                    message.what = i;
                    message.obj = s;
                    handler.sendMessage(message);
                }

            }
        });
    }

    public void postform(String key, String valu, String url, final Handler handler, final int i) {
        HashMap<String, String> data = new HashMap<>();
        data.put(key, valu);
        postform(data, url, handler, i);
    }

    public void postform(String key, String valu, String key2, String valu2, String url, final Handler handler, final int i) {
        HashMap<String, String> data = new HashMap<>();
        data.put(key, valu);
        data.put(key2, valu2);
        postform(data, url, handler, i);
    }


    public void postform(HashMap<String, String> data, String url, final Handler handler, final int i) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(1000, TimeUnit.SECONDS)
                .readTimeout(1000, TimeUnit.SECONDS)
                .build();//创建OkHttpClient对象。
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (String keyset : data.keySet()) {
            builder.addFormDataPart(keyset, data.get(keyset));
        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (e instanceof SocketTimeoutException) {
                    //判断超时异常
                    Message message = new Message();
                    String s = "{\"message\":\"请求超时\",\"_code\":\"-200\",\"data\":[{}]}";
                    message.what = i;
                    message.obj = s;
                    handler.sendMessage(message);
                    Log.d("555", "请求超时");
                }
                if (e instanceof ConnectException) {
                    ////判断连接异常，
                    Message message = new Message();
                    String s = "{\"message\":\"连接异常\",\"_code\":\"-100\",\"data\":[{}]}";
                    message.what = i;
                    message.obj = s;
                    handler.sendMessage(message);
                    Log.d("555", "连接异常");
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message = new Message();
                String s = response.body().string();
                message.what = i;
                message.obj = s;
                handler.sendMessage(message);
            }
        });
    }


    /**
     * post提交Json数据
     *
     * @param date    要提交的数据
     * @param url     访问地址
     * @param handler 回调handler
     * @param i       请求标志
     */
    public void postJson(String date, final String url, final Handler handler, final int i) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();//创建OkHttpClient对象。

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
        String jsonStr = date;//json数据.
        Log.d("发送请求URL" + url + "请求体" + jsonStr);
        RequestBody body = RequestBody.create(JSON, jsonStr);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("异常" + e.toString());
                if (e instanceof SocketTimeoutException) {
                    //判断超时异常
                    EventBus.getDefault().post(new NetWorkMessage("服务器连接失败，请检查网络"));
                    Log.d("请求超时" + url);
                }
                if (e instanceof ConnectException) {
                    ////判断连接异常，
                    EventBus.getDefault().post(new NetWorkMessage("网络异常，请检查网络"));
                    Log.d("连接异常" + url);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message = new Message();
                String s = response.body().string();
                JSONObject jsonObject = null;
                try {
                    jsonObject = com.alibaba.fastjson.JSON.parseObject(s);
                } catch (JSONException e) {
                    Log.d(s);
                    EventBus.getDefault().post(new NetWorkMessage("服务器内部错误"));
                    return;
                }
                boolean b = false;
                try {
                    b = jsonObject.containsKey("Code");
                } catch (Exception e) {
                    Log.d(s);
                    EventBus.getDefault().post(new NetWorkMessage("错误" + s));
                }

                if (!b) {
                    Log.d("返回码不存在"+s);
                    EventBus.getDefault().post(new NetWorkMessage("服务器出错了"));
                    return;
                }
                if (null != handler) {
                    message.what = i;
                    message.obj = s;
                    handler.sendMessage(message);
                }

            }
        });
    }

    public void uploadimg(String data, String url, String paths, final Handler handler, int what) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", data);
        List<String> list = new ArrayList<>();
        list.add(paths);
        uploadimg(map, url, list, handler, what);
    }

    public void uploadimg(HashMap<String, String> data, String url, List<String> paths, final Handler handler, final int what) {
        OkHttpClient mOkHttpClent = new OkHttpClient();
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (String keyset : data.keySet()) {
            builder.addFormDataPart(keyset, data.get(keyset));
        }
        for (String path : paths) {
            com.zou.fastlibrary.utils.Log.d(path);
            builder.addFormDataPart("file", "file.jpg", RequestBody.create(MediaType.parse("image/png"), new File(path)));
        }

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = mOkHttpClent.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (e instanceof SocketTimeoutException) {
                    //判断超时异常
                    Message message = new Message();
                    String s = "{\"message\":\"请求超时\",\"_code\":-200,\"data\":[{}]}";
                    message.obj = s;
                    message.what = what;
                    handler.sendMessage(message);
                    Log.d("555", "请求超时");
                }
                if (e instanceof ConnectException) {
                    ////判断连接异常，
                    Message message = new Message();
                    String s = "{\"message\":\"连接异常\",\"_code\":-100,\"data\":[{}]}";
                    message.obj = s;
                    message.what = what;
                    handler.sendMessage(message);
                    Log.d("555", "连接异常");
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message = new Message();
                String s = response.body().string();
                message.obj = s;
                message.what = what;
                handler.sendMessage(message);
            }
        });
    }


    /**
     * 发送请求头的连接
     *
     * @param date
     * @param url
     * @param handler
     * @param headerkey
     * @param headervalue
     * @param i
     */
    public void connectnet(String date, String url, final Handler handler, String headerkey, String headervalue, final int i) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();//创建OkHttpClient对象。
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
        String jsonStr = date;//json数据.
        Log.d("5555", "发送请求URL" + url + "请求体" + jsonStr);
        RequestBody body = RequestBody.create(JSON, jsonStr);
        Request request = new Request.Builder()
                .url(url)
                .addHeader(headerkey, headervalue)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (e instanceof SocketTimeoutException) {
                    //判断超时异常
                    Message message = new Message();
                    String s = "{\"message\":\"请求超时\",\"retCode\":\"-2\",\"data\":[{}]}";
                    message.what = i;
                    message.obj = s;
                    handler.sendMessage(message);
                    Log.d("555", "请求超时");
                }
                if (e instanceof ConnectException) {
                    ////判断连接异常，
                    Message message = new Message();
                    String s = "{\"message\":\"连接异常\",\"retCode\":\"-1\",\"data\":[{}]}";
                    message.what = i;
                    message.obj = s;
                    handler.sendMessage(message);
                    Log.d("555", "连接异常");
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message = new Message();
                String s = response.body().string();
                message.what = i;
                message.obj = s;
                handler.sendMessage(message);
            }
        });
    }

    /**
     * 缓存拦截器
     */
    private static class CacheInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {

            Request request = chain.request();
            Response response = chain.proceed(request);
            Response response1 = response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    //cache for 30 days
                    .header("Cache-Control", "max-age=1000")
                    .build();
            return response1;
        }
    }

}
