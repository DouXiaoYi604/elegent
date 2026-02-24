package com.example.demo_redrock.Tools;

import android.os.Handler;
import android.os.Looper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class OkHttpUtil {
    private static OkHttpUtil instance;
    private final OkHttpClient okHttpClient;  //客户端
    private final Handler mainHandler;   //handler用于 回调

    // 获取单例
    public static OkHttpUtil getInstance() {
        if (instance==null) {
            synchronized (OkHttpUtil.class) {
                if (instance==null) {
                    instance=new OkHttpUtil();
                }
            }
        }
        return instance;
    }

    // 初始化clien和Handler
    private OkHttpUtil() {
        okHttpClient=new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)  //连接超时10秒
                .readTimeout(10, TimeUnit.SECONDS)     //连接超时
                .writeTimeout(10, TimeUnit.SECONDS)    //写入超时
                .build();

        // 初始化主线程handler：绑定主线程looper，将子线程的网络回调主线程
        mainHandler=new Handler(Looper.getMainLooper());
    }

    // 登录post
    public void doLoginPost(String url, String username, String password, final HttpCallback callback) {
        // 构建表单参数
        RequestBody requestBody = new FormBody.Builder()
                //添加用户民和 密码
                .add("username", username)
                .add("password", password)
                .build();
        //构建POST请求，需要接口地址请求体
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();


        // 异步请求
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                //切换到主线程执行回调，需要用handler的post
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //回调失败的方法
                        callback.onFailure(e.getMessage());
                    }
                });
            }




            //成功调用后的写法，throws IO为可能抛出的异常
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String result;  //将json字符串存储到result
                final String errorMsg;  //将错误信息存储到errormsg
                //判断相应是否有效
                if (response.isSuccessful()) {
                    //用result接收响应成功的响应体
                    result = response.body().string();
                    errorMsg = null;
                }
                else {
                    result = null;
                    errorMsg = "请求失败：";
                }

                //主线程回调解析结果
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (errorMsg==null) {
                                callback.onSuccess(result);
                            }
                            else {
                                callback.onFailure(errorMsg);
                            }
                        } //捕获解析异常
                        catch (Exception e) {
                            callback.onFailure("解析失败：" + e.getMessage()); //e.getMessage作用是返回该异常信息
                        }
                    }
                });
            }
        });
    }

    // 注册post
    public void doRegisterPost(String url, String username, String password, String repassword, final HttpCallback callback) {
        RequestBody requestBody=new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .add("repassword", repassword)
                .build();

        Request request=new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call,final IOException e) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String result;
                final String errorMsg;
                if (response.isSuccessful()&&response.body()!=null) {
                    result=response.body().string();
                    errorMsg=null;
                }
                else {
                    result=null;
                    errorMsg="请求失败：";
                }

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (errorMsg==null) {
                                callback.onSuccess(result);
                            }
                            else {
                                callback.onFailure(errorMsg);
                            }
                        }
                        catch (Exception e) {
                            callback.onFailure("解析失败：" + e.getMessage());
                        }
                    }
                });
            }
        });
    }

    // 回调接口
    public interface HttpCallback {
        void onSuccess(String result);
        void onFailure(String errorMsg);
    }
}
