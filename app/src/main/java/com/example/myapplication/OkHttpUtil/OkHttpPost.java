package com.example.myapplication.OkHttpUtil;

import android.util.Log;

import com.example.myapplication.Client.OkHttpUtils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpPost {
    public static OkHttpClient okHttpClient;

    public static void sendOkHttpRequest(String address, RequestBody requestBody, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(20, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpRequestMicrosoft(String address, String token, RequestBody requestBody, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(20, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url(address)
                .header("token", token)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpRequestNull(String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(20, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url(address)
                .post(RequestBody.create(null, ""))
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpRequestBody(HttpUrl address, RequestBody requestBody, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(20, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        assert request.body() != null;
        client.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpRequestHeader(String header, String address, RequestBody requestBody, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(20, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url(address)
                .header("Authorization", header)
                .post(requestBody)
                .build();
        Log.d("11111", "sendOkHttpRequestBody: " + request.url()
                + "\n请求头" + request.header("Authorization")
                + "\n请求参数" + request.body().toString());
        client.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpRequestHeaderToke(String address, String header, RequestBody requestBody, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(20, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url(address)
                .header("Authorization", header)
                .post(requestBody)
                .build();
//        Log.d("11111", "sendOkHttpRequestBody: " + request.url()
//                + "\n请求头" + request.header("Authorization")
//                + "\n请求参数" + request.body().toString());
        client.newCall(request).enqueue(callback);
    }

    public static Response sendOkHttpRequestHeaderTokeSys(HttpUrl Url, RequestBody requestBody) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(20, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url(Url)
                .post(requestBody)
                .build();
//        Log.d("11111", "sendOkHttpRequestBody: " + request.url()
//                + "\n请求头" + request.header("Authorization")
//                + "\n请求参数" + request.body().toString());
        return client.newCall(request).execute();
    }

    /**
     * @ 鉴权信息
     * */
    public static Response sendInterceptor(HttpUrl Url, RequestBody requestBody) throws IOException {
        if (okHttpClient == null){
            try {
                okHttpClient = OkHttpUtils.create(60L, 5L, 120L,
                        3, 10, 20);
            } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
                e.printStackTrace();
            }
        }
        Request request = new Request.Builder()
                .url(Url)
                .post(requestBody)
                .build();
        return okHttpClient.newCall(request).execute();

    }

    /**
     * @ 注销token
     * */
    public static void sendCancelToken(HttpUrl Url, okhttp3.Callback callback) throws IOException {
        if (okHttpClient == null){
            try {
                okHttpClient = OkHttpUtils.create(60L, 5L, 120L,
                        3, 10, 20);
            } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
                e.printStackTrace();
            }
        }
        Request request = new Request.Builder()
                .url(Url)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void post(String Url, String header, RequestBody requestBody, okhttp3.Callback callback){
        if (okHttpClient == null){
            try {
                okHttpClient = OkHttpUtils.create(60L, 5L, 120L,
                        3, 10, 20);
            } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
                e.printStackTrace();
            }
        }
        Request request = new Request.Builder()
                .url(Url)
                .header("Authorization", header)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void get(String Url, String header, okhttp3.Callback callback){
        if (okHttpClient == null){
            try {
                okHttpClient = OkHttpUtils.create(60L, 5L, 120L,
                        3, 10, 20);
            } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
                e.printStackTrace();
            }
        }
        Request request = new Request.Builder()
                .url(Url)
                .header("Authorization", header)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }
}
