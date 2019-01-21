package com.zou.fastlibrary.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.bravin.btoast.BToast;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**下载工具类
 * @author Lemon
 */
public class DownloadUtil {
    private static final String TAG = "DownloadUtil";

    private static DownloadUtil downloadUtil;
    private final OkHttpClient okHttpClient;

    public static DownloadUtil get() {
        if (downloadUtil == null) {
            downloadUtil = new DownloadUtil();
        }
        return downloadUtil;
    }

    public DownloadUtil() {
        okHttpClient = new OkHttpClient();
    }


    /**
     * @param url          下载连接
     * @param destFileDir  下载的文件储存目录
     * @param destFileName 下载文件名称
     * @param listener     下载监听
     */

    public void download(final String url, final String destFileDir, final String destFileName, final OnDownloadListener listener) {

        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();

//        try {
//              Response response = client.newCall(request).execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //异步请求
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败监听回调
                listener.onDownloadFailed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;

                //储存下载文件的目录
                File dir = new File(destFileDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, destFileName);

                try {

                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        //下载中更新进度条
                        listener.onDownloading(progress);
                    }
                    fos.flush();
                    //下载完成
                    listener.onDownloadSuccess(file);
                } catch (Exception e) {
                    listener.onDownloadFailed(e);
                }finally {

                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {

                    }

                }


            }
        });
    }


    public interface OnDownloadListener{

        /**
         * 下载成功之后的文件
         */
        void onDownloadSuccess(File file);

        /**
         * 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载异常信息
         */

        void onDownloadFailed(Exception e);
    }


    //打开APK程序代码
    public static void openFile(Activity context, File file) {
        if (context == null) {
            Log.e(TAG, "openFile  context == null >> return;");
            return;
        }

        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(getUri(file,context),  "application/msword");
        try {
            context.startActivity(intent);
        }catch (ActivityNotFoundException e){
            BToast.error(context).text("没有找个打开该文件的软件，请安装WPS").show();
        }

    }

    /**
     * 获取对应文件的Uri
     * @param file 文件对象
     * @return
     */
    private static Uri getUri( File file, Context context) {
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //判断版本是否在7.0以上
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件

        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

}
