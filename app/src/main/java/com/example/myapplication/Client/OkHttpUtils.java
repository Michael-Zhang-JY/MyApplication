package com.example.myapplication.Client;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpUtils {


    /**
     * <p>创建OkHttp客户端</p>
     *
     * <p>createTime: 2020/11/3 4:03 下午</p>
     * <p>author: gradual</p>
     *
     *
     * @param connectTimeout    连接超时时间
     * @param readTimeout       读超时时间
     * @param writeTimeout      写超时时间
     * @param retryCount        重试次数
     * @param poolMaxIdleConnections    连接池最大连接数量
     * @param poolKeepAliveDuration     连接持续时间
     * @throws NoSuchAlgorithmException exception
     * @throws KeyStoreException exception
     * @throws KeyManagementException exception
     * @return OkHttp客户端
     */
    public static OkHttpClient create(Long connectTimeout, Long readTimeout, Long writeTimeout, Integer retryCount, Integer poolMaxIdleConnections, Integer poolKeepAliveDuration) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return initBuilder(connectTimeout, readTimeout, writeTimeout, retryCount, poolMaxIdleConnections, poolKeepAliveDuration).build();
    }

    /**
     * <p>创建OkHttp客户端构建器</p>
     *
     * <p>createTime: 2020/11/3 4:03 下午</p>
     * <p>author: gradual</p>
     *
     *
     * @param connectTimeout    连接超时时间
     * @param readTimeout       读超时时间
     * @param writeTimeout      写超时时间
     * @param retryCount        重试次数
     * @param poolMaxIdleConnections    连接池最大连接数量
     * @param poolKeepAliveDuration     连接持续时间
     * @throws NoSuchAlgorithmException exception
     * @throws KeyStoreException exception
     * @throws KeyManagementException exception
     * @return OkHttp客户端
     */
    public static OkHttpClient.Builder initBuilder(Long connectTimeout, Long readTimeout, Long writeTimeout, Integer retryCount, Integer poolMaxIdleConnections, Integer poolKeepAliveDuration) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder().connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(poolMaxIdleConnections, poolKeepAliveDuration, TimeUnit.SECONDS))
                .retryOnConnectionFailure(true);
        return initSsl(builder);
    }

    /**
     * <p>初始化SSL证书</p>
     *
     * <p>createTime: 2020/11/3 3:42 下午</p>
     * <p>author: gradual</p>
     *
     *
     * @param builder 构建器
     */
    private static OkHttpClient.Builder initSsl(OkHttpClient.Builder builder) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore) null);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:"
                    + Arrays.toString(trustManagers));
        }
        X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[] { trustManager }, null);
        return builder.sslSocketFactory(sslContext.getSocketFactory(), trustManager)
                .hostnameVerifier((s, sslSession) -> true);
    }

    static class OkHttpRetryInterceptor implements Interceptor {

        /**
         * 最大重试次数
         */
        public int maxRetry;

        public OkHttpRetryInterceptor(int maxRetry) {
            this.maxRetry = maxRetry;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            int retryNum = 1;
            Response response = chain.proceed(request);
            while (!response.isSuccessful() && retryNum < maxRetry) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                retryNum++;
                response = chain.proceed(request);
            }
            return response;
        }
    }

}
