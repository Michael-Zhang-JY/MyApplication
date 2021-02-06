package com.example.myapplication.OkHttpUtil;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OkHttpPost {
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
}
