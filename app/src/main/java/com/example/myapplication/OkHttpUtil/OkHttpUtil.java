package com.example.myapplication.OkHttpUtil;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpUtil {
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback){
        OkHttpClient client = getUnsafeOkHttpClient().newBuilder().readTimeout(20, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpRequestToken(String address,String header, okhttp3.Callback callback){
        OkHttpClient client = getUnsafeOkHttpClient().newBuilder().readTimeout(20, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url(address)
                .addHeader("token", header)
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * @ 同步Get请求
     * */
    public static Response sendOkHttpRequestTokenSys(String address,String header) throws IOException {
        OkHttpClient client = getUnsafeOkHttpClient().newBuilder().readTimeout(20, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url(address)
                .header("Authorization", header)
                .build();
        return client.newCall(request).execute();
    }


    public static Response sendOkHttpRequestNoAsync(String address) throws IOException {
        OkHttpClient client = getUnsafeOkHttpClient().newBuilder().readTimeout(20, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url(address)
                .build();
        return client.newCall(request).execute();
    }

    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
