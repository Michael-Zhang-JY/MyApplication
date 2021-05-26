package com.example.myapplication.Client;

import android.annotation.SuppressLint;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AgentClient {


    /**
     * <p>参数 client.</p>
     */
    private static OkHttpClient client;

    /**
     * <p>参数 tokenClient.</p>
     */
    private static OkHttpClient tokenClient;

    /**
     * <p>参数 accessToken.</p>
     */
    private static AccessToken accessToken;

    /**
     * 客户端ID
     * 后台下发
     */
    private static String CLIENT_ID = "670b1376-677a-4909-9859-bef01c0431e2";

    /**
     * 客户端秘钥
     * 后台下发
     */
    private static String CLIENT_SECRET = "72780234368651019505992566741190641080481032949187";

    /**
     * <p>授权服务获取AccessToken接口.</p>
     * 后台下发
     */
    private static String OAUTH_SERVER_LOGIN_URL = "https://api.dev.cafewalk.com/oauth-service/oauth/token";

    /**
     * <p>授权服务检测AccessToken是否过期接口.</p>
     * 后台下发
     */
    private static String OAUTH_SERVER_CHECK_TOKEN_URL = "https://api.dev.cafewalk.com/oauth-service/oauth/check_token";

    /**
     * <p>获取 client.， 进行其他需要定制化的请求时可用</p>
     *
     * <p>createTime：2021-01-05 16:03:21</p>
     * <p>author: gradual</p>
     *
     * @return the client
     */
    public static OkHttpClient getClient() {
        return client;
    }


    public static void main(String[] args) {
        String deviceChanInfoUrl = "https://api.dev.cafewalk.com/store-service/store/channel/{0}";
        Long storeId = 72L;
        String url = MessageFormat.format(deviceChanInfoUrl, storeId);
        System.out.println("Url - > " + url);
        AgentClient.init();
        try {
            //客户端初始化
            AgentClient.init();
            //开始请求
            Response response = AgentClient.post(url, null);
            if (response.isSuccessful()) {
                String result = response.body().string();
                System.out.println("結果 -> " +result);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * <p>代理客户端发起GET请求. </p>
     *
     * <p>createTime：2021-01-05 16:03:21</p>
     * <p>author: gradual</p>
     *
     * @param url 接口URL地址
     * @return the response
     * @throws IOException the io exception
     */
    public static Response get(String url) throws IOException {
        AccessToken accessToken = AgentClient.accessToken;
        Request.Builder builder = new Request.Builder().get()
                .addHeader("Authorization", accessToken.getTokenType() + accessToken.getAccessToken())
                .url(url);
        preHandle(builder);
        Request request = builder.build();
        Call call = AgentClient.getClient().newCall(request);
        return call.execute();
    }


    /**
     * <p>代理客户端发起POST请求 </p>
     *
     * <p>createTime：2021-01-05 16:03:21</p>
     * <p>author: gradual</p>
     *
     * @param url  接口URL地址
     * @param body 请求Body内容
     * @return the response
     * @throws IOException the io exception
     */
    public static Response post(String url, String body) throws IOException {
        if (body == null || body.trim().equals("")) {
            body = "{}";
        }
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json;charset=utf-8"), body));
        preHandle(builder);
        Request request = builder.build();
        System.out.println("request - > " + request.toString());
        System.out.println(url + " " + body);
        return AgentClient.getClient().newCall(request).execute();
    }

    /**
     * <p>请求预处理(刷新并添加AccessToken)</p>
     *
     * <p>createTime: 2021/1/5 下午4:08</p>
     * <p>author: gradual</p>
     *
     *
     * @param builder 请求构造器
     */
    private static void preHandle(Request.Builder builder) {
        try {
            System.out.println("检测当前Token是否快过期");
            checkToken();
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException | IOException e) {
            e.printStackTrace();
        }
        System.out.println("添加Token[{}]到请求头中" + accessToken.getAccessToken());
        AccessToken accessToken = AgentClient.accessToken;
        builder.header("Authorization", accessToken.getTokenType() + " " + accessToken.getAccessToken());
        System.out.println(builder.toString());
    }

    /**
     * <p>初始化Token代理客户端.</p>
     *
     * <p>createTime：2021-01-05 16:03:21</p>
     *
     * @author gradual
     */
    public static void init() {
        try {
            if (AgentClient.tokenClient == null) {
                OkHttpClient.Builder builder = OkHttpUtils.initBuilder(60L, 60L, 120L,
                        3, 10, 20);
                //创建用来获取Token的客户端(如果ClientID和ClientSecret变更了，需要重新生成当前客户端)
//                AgentClient.tokenClient = builder.authenticator((route, response) -> {
//                    String credential = Credentials.basic(CLIENT_ID, CLIENT_SECRET);
//                    return response.request().newBuilder().header("Authorization", credential).build();
//                }).build();
                //刷新AccessToken
                refreshToken();
            }
            if (client == null) {
                //创建用来请求接口的客户端
                client = OkHttpUtils.create(60L, 60L, 120L,
                        3, 10, 20);
            }
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException | IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("客户端初始化失败");
        }
    }

    /**
     * <p>检查当前Token是否在有效期内. </p>
     *
     * <p>createTime：2021-01-29 16:59:41</p>
     * <p>author: gradual</p>
     *
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws KeyStoreException        the key store exception
     * @throws KeyManagementException   the key management exception
     * @throws IOException              the io exception
     */
    private static void checkToken() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        System.out.println("检测当前Token是否有效");
        if (!accessToken.isApproachExpired()) {
            return;
        }
        OkHttpClient client = OkHttpUtils.create(60L, 60L, 120L,
                3, 10, 20);
        Response response = client.newCall(new Request.Builder().get().url(OAUTH_SERVER_CHECK_TOKEN_URL + "?token=" + accessToken.getAccessToken()).build()).execute();
        if (response.isSuccessful()) {
            String result = response.body().string();
            System.out.println("获取到的信息 -> " + result);

            JSONObject object = JSONObject.parseObject(result);
            if (object.containsKey("user_id")) {
                System.out.println("当前Token是有效的");
                return;
            }
        }
        System.out.println("不在有效期内，刷新Token");
        refreshToken();
    }


    /**
     * <p>刷新Token. </p>
     *
     * <p>createTime：2021-01-29 17:00:14</p>
     * <p>author: gradual</p>
     *
     * @throws IOException the io exception
     */
    private static void refreshToken() throws IOException {
        System.out.println("获取Token, 客户端：{"+CLIENT_ID+"}，密钥： {"+CLIENT_SECRET+"}");
        HttpUrl httpUrl = HttpUrl.parse(OAUTH_SERVER_LOGIN_URL).newBuilder()
                .addQueryParameter("grant_type", "client_credentials")
                .addQueryParameter("client_id", CLIENT_ID)
                .addQueryParameter("client_secret", CLIENT_SECRET)
                .addQueryParameter("scope", "android_device")
                .build();
        Request request = new Request.Builder()
                .post(RequestBody.create(MediaType.parse("application/json;charset=utf8"), ""))
                .url(httpUrl)
                .build();
        System.out.println(httpUrl.toString());
        Response response = tokenClient.newCall(request).execute();
        String string = response.body().string();
        if (!response.isSuccessful()) {
            System.out.println("44444");
            throw new RuntimeException("刷新Token异常");
        }
//                System.out.println(string);
        AgentClient.accessToken = JSONObject.parseObject(string, AccessToken.class);
        System.out.println("获取到Token -> "+ accessToken);
//        tokenClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String string = response.body().string();
//                System.out.println("3333333");
//                if (!response.isSuccessful()) {
//                    System.out.println("44444");
//                    throw new RuntimeException("刷新Token异常");
//                }
//                System.out.println(string);
//                AgentClient.accessToken = JSONObject.parseObject(string, AccessToken.class);
//                System.out.println("获取到Token -> "+ accessToken);
//                System.out.println("111111111");
//            }
//        });

    }


    /**
     * <p>Access token</p>
     *
     * <p>createTime：2021-01-29 16:59:41</p>
     * <p>Company: Cawa</p>
     *
     * @author gradual
     */
    private static class AccessToken {

        /**
         * The Access token.
         */
        @JSONField(name = "access_token")
        private String accessToken;

        /**
         * The Token type.
         */
        @JSONField(name = "token_type")
        private String tokenType;

        /**
         * The Expires in.
         */
        @JSONField(name = "expires_in")
        private Long expiresIn;

        /**
         * The Create time.
         */
        private LocalDateTime createTime;

        /**
         * <p>To string string. </p>
         *
         * <p>createTime：2021-01-05 16:03:21</p>
         * <p>author: gradual</p>
         *
         * @return the string
         */
        @Override
        public String toString() {
            return "AccessToken{" +
                    "accessToken='" + accessToken + '\'' +
                    ", tokenType='" + tokenType + '\'' +
                    ", expiresIn='" + expiresIn + '\'' +
                    '}';
        }

        /**
         * <p>初始化一个新的 Access token 实例.</p>
         *
         * <p>createTime：2021-01-05 16:03:21</p>
         *
         * @author gradual
         */
        @SuppressLint("NewApi")
        public AccessToken() {
            this.createTime = LocalDateTime.now();
        }

        /**
         * <p>Is approach expired boolean. </p>
         *
         * <p>createTime：2021-01-05 16:03:21</p>
         * <p>author: gradual</p>
         *
         * @return the boolean
         */

        @SuppressLint("NewApi")
        public boolean isApproachExpired() {
            //60s内的都算接近过期
            System.out.println(this.createTime.plusSeconds(this.expiresIn-60) + " " + (this.expiresIn-60));
//            SimpleDateFormat DateFormat = this.simpleDateFormat.
            LocalDateTime expiredTime = this.createTime.plusSeconds(this.expiresIn - 60);
            System.out.println(expiredTime.toString());
            LocalDateTime now = LocalDateTime.now();
            System.out.println(LocalDateTime.now());
            System.out.println(now.isAfter(expiredTime) || now.isEqual(expiredTime));
            System.out.println(now.isAfter(expiredTime));
            System.out.println(now.isEqual(expiredTime));
            return now.isAfter(expiredTime) || now.isEqual(expiredTime);
        }


        public static long Date2ms(String _data){
            SimpleDateFormat format =   new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = format.parse(_data);
                return date.getTime();
            }catch(Exception e){
                return 0;
            }
        }

        public static String ms2HMS(int _ms){
            String HMStime;
            _ms/=1000;
            int hour=_ms/3600;
            int mint=(_ms%3600)/60;
            int sed=_ms%60;
            String hourStr=String.valueOf(hour);
            if(hour<10){
                hourStr="0"+hourStr;
            }
            String mintStr=String.valueOf(mint);
            if(mint<10){
                mintStr="0"+mintStr;
            }
            String sedStr=String.valueOf(sed);
            if(sed<10){
                sedStr="0"+sedStr;
            }
            HMStime=hourStr+":"+mintStr+":"+sedStr;
            return HMStime;
        }
        /**
         * <p>设置 access token.</p>
         *
         * <p>createTime：2021-01-05 16:03:21</p>
         * <p>author: gradual</p>
         *
         * @param accessToken the access token
         */
        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        /**
         * <p>设置 token type.</p>
         *
         * <p>createTime：2021-01-05 16:03:21</p>
         * <p>author: gradual</p>
         *
         * @param tokenType the token type
         */
        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        /**
         * <p>获取 expires in.</p>
         *
         * <p>createTime：2021-01-05 16:03:21</p>
         * <p>author: gradual</p>
         *
         * @return the expires in
         */
        public Long getExpiresIn() {
            return expiresIn;
        }

        /**
         * <p>设置 expires in.</p>
         *
         * <p>createTime：2021-01-05 16:03:21</p>
         * <p>author: gradual</p>
         *
         * @param expiresIn the expires in
         */
        public void setExpiresIn(Long expiresIn) {
            this.expiresIn = expiresIn;
        }

        /**
         * <p>获取 create time.</p>
         *
         * <p>createTime：2021-01-05 16:03:21</p>
         * <p>author: gradual</p>
         *
         * @return the create time
         */
        public LocalDateTime getCreateTime() {
            return createTime;
        }

        /**
         * <p>设置 create time.</p>
         *
         * <p>createTime：2021-01-05 16:03:21</p>
         * <p>author: gradual</p>
         *
         * @param createTime the create time
         */
        public void setCreateTime(LocalDateTime createTime) {
            this.createTime = createTime;
        }

        /**
         * <p>获取 access token.</p>
         *
         * <p>createTime：2021-01-05 16:03:21</p>
         * <p>author: gradual</p>
         *
         * @return the access token
         */
        public String getAccessToken() {
            return accessToken;
        }

        /**
         * <p>获取 token type.</p>
         *
         * <p>createTime：2021-01-05 16:03:21</p>
         * <p>author: gradual</p>
         *
         * @return the token type
         */
        public String getTokenType() {
            return tokenType;
        }

    }
}
