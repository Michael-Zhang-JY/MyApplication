package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.myapplication.Client.AccessToken;
import com.example.myapplication.MQTT.IGetMessageCallBack;
import com.example.myapplication.MQTT.MQTTService;
import com.example.myapplication.MQTT.MyServiceConnection;
import com.example.myapplication.OkHttpUtil.OkHttpPost;
import com.example.myapplication.SerialPort.SerialRead;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lztek.toolkit.Lztek;
import com.lztek.toolkit.SerialPort;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainService extends AppCompatActivity implements IGetMessageCallBack {

    public static final String TAG = MainService.class.getCanonicalName();
    public static MainService me;

    public static int orderCode = 0;        // 订单码
    private boolean ImeiSign = true;
    private int ImeiInt = 0;
    private static String clientId;
    private static String passWord;
    public static SerialPort ser = null;
    public static Lztek lztek = null;
    public static InputStream inPut = null;
    public static OutputStream outPut = null;
    public static boolean isStart = false;
    public static boolean isChans = true;

    private static int chansNum = 0;

    public static int CardCafeNumber = 0; // 员工后台卡号
    public static String CardNumber = null; // 刷卡卡号
    public static String CardAlias = null;  // 员工名字
    public static String MicrosoftCard = null; // 员工编号
    public static String deviceId = null; // 设备Id
    public static int customerId = 0;        // 企业客户ID

    public static boolean BooleanAisle = false;      // 请求通道标志
    public static boolean BooleanComm = false;       // 获取商品信息标志
    public static boolean BooleanReComm = false;       // 请求订单Token标志
    public static boolean BooleanEsta = false;       // 创建订单Token标志
    public static boolean BooleanSerialNo = false;   // 设备信息标志
    public static boolean BooleanCustomerInfo = false;   // 设备用户点位信息标志

    private MyServiceConnection serviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main_service);
        me = this;
        lztek = Lztek.create(this);
        Thread.setDefaultUncaughtExceptionHandler(handler);         // 检测异常  一秒后重启APP
//        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(Thread thread, Throwable ex) {
//                //在获取到了未捕获的异常后,处理的方法
//                ex.printStackTrace();
//                Log.i(TAG, "捕获到了一个程序的异常");
//
//                //将捕获的异常存储到sd卡中
//                String Path = Objects.requireNonNull(getExternalFilesDir("error")).getAbsolutePath();
//                String path = Path + File.separator +"error74.log";
//                Log.d("1111", "uncaughtException: " + path);
//                File file = new File(path);
//                try {
//                    PrintWriter printWriter = new PrintWriter(file);
//                    ex.printStackTrace(printWriter);
//                    printWriter.close();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                //上传公司的服务器
//                //结束应用
//                System.exit(0);
//            }
//        });
        startNetwork();
    }

    private Thread.UncaughtExceptionHandler handler = (t, e) -> {
        try {
            Thread.sleep(10000);
            finish();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
//        restartApp(); //发生崩溃异常时,重启应用
    };



    /**
     * 重启设备
     * */
    @SuppressLint("WrongConstant")
    private void restartApp(){
        Log.d(TAG, "DeviceReboot: " + "4444444");
        Lztek lztek = Lztek.create(this);
        lztek.softReboot();                 // App异常退出重启软件
    }

    /** 判断网络是否连接 */
    private boolean isConnectIsNormal() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            Log.i(TAG, "当前网络名称：" + name);
            return true;
        } else {
            Log.i(TAG, "没有可用网络");
            return false;
        }
    }

    public static String ReturnCard(){
        return CardNumber;
    }

    public static int ReturnCardCafe(){
        return CardCafeNumber;
    }

    public static int ReturnCustomerId(){
        return customerId;
    }

    public static String ReturnCardName(){
        return CardAlias;
    }

    public static String ReturnMicrosoftCard(){
        return MicrosoftCard;
    }

    public static String ReturndeviceId(){
        return deviceId;
    }

    /**
     * @ 实时监听网络状态
     * */
    private Timer timerNetwork = null;
    private TimerTask timerTaskNetwork = null;
    private boolean cer = false;

    private int OverTime = 0;

    private void startNetwork(){
        if (timerNetwork == null){
            timerNetwork = new Timer();
        }
        if (timerTaskNetwork == null){
            timerTaskNetwork = new TimerTask() {
                @Override
                public void run() {
                    OverTime ++;
                    if (isConnectIsNormal()){
                        stopNetwork();
                        if (!cer){
                            cer = true;                 // 只创建一次设备
                            createDevice();
//                        AuthenticationInformation();
                            MainService.openSerialPort();
                            Intent intentOne = new Intent(MainService.this, MainActivity.class);
                            startActivity(intentOne);
                            Log.d(TAG, "run: " + "Mainservice222");
//                            stopNetwork();
                        }
//                        StartTimerToken();
                    }else {
                        if (OverTime == 120){
                            lztek.alarmPoweron(60);                 // 联网失败，重启设备
                        }
                    }
                }
            };
        }
        timerNetwork.schedule(timerTaskNetwork, 0, 500);
    }

    /**
     * @ 打开串口
     */
    public static void openSerialPort(){
        ser = lztek.openSerialPort("dev/ttyS1", 115200);
        outPut = ser.getOutputStream();
        inPut = ser.getInputStream();
        isStart = true;
        new SerialRead().start();           // 启动ead串口线程

    }

    /**
     * @ 串口写数据
     * */
    public static void writeData (Byte [] data){
        byte[] arr = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            arr[i] = data[i];           // 循环赋值
        }
        try {
            Log.d(TAG, "writeData: " + Arrays.toString(arr));
            MainService.outPut.write(arr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 计算CRC16校验码
     *
     * @param bytes
     * @return
     */
    public static Integer getCRC(Byte[] bytes) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;

        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        //高低位转换，看情况使用（譬如这次规定校验码高位在前低位在后，也就不需要转换高低位)
        //CRC = ( (CRC & 0x0000FF00) >> 8) | ( (CRC & 0x000000FF ) << 8);
//        System.out.println(Integer.toHexString(CRC));
        return CRC;
    }

    /**
     * @ 转换int数据为byte （两位为一个元素）
     */
    public static byte[] intToDoubleBytes(Integer integer) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (integer / 256);
        bytes[1] = (byte) (integer % 256);
        System.out.println(Arrays.toString(bytes));
        return bytes;
    }


    private void stopNetwork(){
        if (timerNetwork != null){
            timerNetwork.cancel();
            timerNetwork = null;
        }
        if (timerTaskNetwork != null){
            timerTaskNetwork.cancel();
            timerTaskNetwork = null;
        }
    }

    /**
     * @ 获取设备IMEI
     * @param context
     * @return IMEI
     * */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    private String getIMEI(Context context){
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            assert telephonyManager != null;
            return telephonyManager.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * @ 返回设定通道数
     * */
    public static int returnChansNum(){
        return chansNum;
    }


    /**
     * @ 创建产品
     * */
    // 自动创建产品
    private void createDevice(){
        try {
            String strOne = "http://api.heclouds.com/register_de?register_code=" + MQTTService.registrationCode;
            String json = null;
            while (ImeiSign){
                if (ImeiInt == 30){
                    Lztek lztek = Lztek.create(this);
                    lztek.softReboot();
                }
                ImeiInt ++;
                String imei = getIMEI(this);
//                String imei = "123456789";
//                json = "{\"sn\":\"" + imei + "\",\"title\":\"" + imei + "\"}";
                json = "{\"sn\":\"" + imei + "\",\"title\":\"" + getIMEI(this) + "\"}";
                try {
                    Thread.sleep(1000);             // 读取硬件IMEI大概需要五秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (imei != null){
                    break;
                }
                Log.d(TAG, "DeviceReboot: " + json + "1" + ImeiInt);
            }
//            Log.d(TAG, "DeviceReboot: " + json + "1" + "2");
            Log.d(TAG, "createDevice: " + "创建产品");
//        logger.debug("createDevice: " + json);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
//        OkHttpPost httpUtilMqtt = new OkHttpPost();
            OkHttpPost.sendOkHttpRequest(strOne, requestBody,new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "onFailure: " + "1");
                    createDevice();
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    Log.d(TAG, "Activity onResponse: -> " + responseData);
//                logger.info("Activity onResponse: -> " + responseData);
                    parseJSONWithGSON(responseData);
                }
            });
        }catch (Exception e){
            Log.d(TAG, "onFailure: " + "12");
            createDevice();
            e.printStackTrace();
        }
    }

    /**
     * @ 获取设备ID Key
     * */
    private void parseJSONWithGSON(String jsonData){
//        Log.d(TAG, "AssemblyData: " + jsonData);
        JsonObject jsonObject = new JsonParser().parse(jsonData).getAsJsonObject();
        JsonObject jsonArray = jsonObject.getAsJsonObject("data");
        clientId = jsonArray.get("device_id").toString().replace("\"","");      // 获取设备ID，去除""
        passWord = jsonArray.get("key").toString().replace("\"","");           // 获取设备key，去除""
        initMqttService();  // 启动MQTT
//        timerURL.schedule(timerTaskURL, 0, 300);
//        merchandiseNews();     // 请求商品数据
    }

    /**
     * @ 启动MQTT
     * */
    private void initMqttService() {
        serviceConnection = new MyServiceConnection();
        serviceConnection.setIGetMessageCallBack(MainService.this);
        Intent intent = new Intent(this, MQTTService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
//        Intent intentOne = new Intent(MainService.this, MainActivity.class);
//        startActivity(intentOne);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (serviceConnection != null) {
            Log.d(TAG, "onDestroy: " + "111111111111");
            isChans = true;
            unbindService(serviceConnection);
            serviceConnection = null;
            stopNetwork();
            StopTimerToken();
//            mService.releaseService();
        }
    }

    /**
     * @ back回调函数
     */
    @Override
    public void onBackPressed() {
       stopNetwork();
       finish();
    }

    /**
     * @ 返回设备ID
     * */
    public static String returnDeviceId(){
        return clientId;
    }

    /**
     * @ 返回设备Key
     * */
    public static String returnKey(){
        return passWord;
    }

    @Override
    public void setMessage(String message) {

    }


    /**
     * @ 下载APK完成进行远程升级
     * */
    public static void APK(String Url){
        me.APKHandler(Url);
    }

    private void APKHandler(String Url){
//        Log.d(TAG, "APKHandler: " + Url);
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("APKUrl", Url);      // 传递自定义异常数据
        message.setData(bundle);
        mHandlerAPK.handleMessage(message);
    }

    /**
     * @ 主线程进行远程升级*/
    @SuppressLint("HandlerLeak")
    private Handler mHandlerAPK = new Handler(Looper.getMainLooper()){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            Log.d(TAG, "APKHandler: " + msg.getData().getString("APKUrl"));
            String Url = msg.getData().getString("APKUrl");
            Log.d(TAG, "handleMessage: " + android.net.Uri.fromFile(new java.io.File(Url)));
            Intent intent = new Intent(Intent.ACTION_VIEW);
            assert Url != null;
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(FileProvider.getUriForFile(MainService.this, "com.example.myapplication", new File(Url)),
                    "application/vnd.android.package-archive");
//            intent.setDataAndType(android.net.Uri.fromFile(new java.io.File(Url)),
//                    "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra("IMPLUS_INSTALL", "SILENT_INSTALL");
            Log.d(TAG, "handleMessage: " + "1111");
            MainService.this.startActivity(intent);
        }
    };


    /**
     * @ 获取app versionCode
     * */
    public static int packageCode(Context context) {
        PackageManager manager = context.getPackageManager();
        int code = 0;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            code = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * @ 获取app versionName
     * */
    public static String packageName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }

    /**
     * @ 客户端鉴权信息
     * */
    public static void AuthenticationInformation(){
        HttpUrl httpUrl = HttpUrl.parse(MQTTService.returnAuthorServiceToken()).newBuilder()
                .addQueryParameter("grant_type", "client_credentials")
                .addQueryParameter("client_id", MQTTService.returnclientId())
                .addQueryParameter("client_secret", MQTTService.returnSecret())
                .addQueryParameter("scope", "android_device")
                .build();
//        Log.d(TAG, "AuthenticationInformation: " + httpUrl.toString());
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf8"), "");
        try {
            Response response = OkHttpPost.sendInterceptor(httpUrl, requestBody);
            assert response.body() != null;
            String string = response.body().string();
            Log.d(TAG, "onResponse: 请求鉴权 -> " + string);
            if (response.code() >= 200 && response.code() < 300) {
                AccessTokenIn(string);
            }
        } catch (IOException e) {
            e.printStackTrace();
//            StopTimerToken();
            Expires_in_timer = 6L;
            Log.d(TAG, "AuthenticationInformation: " + "11111111111");
        }
    }

    public static String AccessToken;
    public static String TokenType;
    public static Long Expires_in_timer = 0L;
    public static Timer TimerToken;
    public static TimerTask TimerTaskToken;

    public static void AccessTokenIn(String token){
//        Log.d(TAG, "AccessTokenIn: " + BooleanAisle);
        try {
            Gson gson = new Gson();
            com.example.myapplication.Client.AccessToken accessToken = gson.fromJson(token, AccessToken.class);
            AccessToken = accessToken.getAccess_token();
            TokenType = accessToken.getToken_type();
            Expires_in_timer = accessToken.getExpires_in() * 2;
            Log.d(TAG, "AccessTokenIn: 鉴权信息 ->" + AccessToken + " " + TokenType + " " + Expires_in_timer + " " + BooleanSerialNo);
            if (Expires_in_timer < 5 && AccessToken != null){
                CancelToken();
            }
            if (BooleanSerialNo){
                MainActivity.RequestData();
                BooleanSerialNo = false;
            }
            if(BooleanCustomerInfo){
                MainActivity.RequestData();
                BooleanCustomerInfo = false;
            }
            if (TimerToken == null){
                StartTimerToken();
            }
            if (BooleanAisle){
                MainActivity.RequestData();
                BooleanAisle = false;
            }
            if (BooleanComm){
                MainActivity.RequestData();
                BooleanComm = false;
            }
            if (BooleanReComm){
                MainInterFace.CafewalkTokenOne();
                BooleanReComm = false;
            }
            if (BooleanEsta){
                MainDetailsPage.CafewalkOrder();
                BooleanEsta = false;
            }
        }catch (Exception e){
            e.printStackTrace();
            Expires_in_timer = 6L;
        }
    }


    /**
     * @ 判定刷新token定时器是否关闭
     * */
    public static void TimerClose(){
        if (TimerToken == null){
            StartTimerToken();
        }else {
            Expires_in_timer = 6L;
        }
    }

    public static String ReturnAccessToken(){
        return AccessToken;
    }

    public static String ReturnTokenType(){
        return TokenType;
    }

    public static void StartTimerToken(){
        if (TimerToken == null){
            TimerToken = new Timer();
        }
        if (TimerTaskToken == null){
            TimerTaskToken = new TimerTask() {
                @Override
                public void run() {
                    if (AccessToken == null){
                        AuthenticationInformation();
                    }
                    if (Expires_in_timer == 5){
//                        AuthenticationInformation();
                        CancelToken();
                        Expires_in_timer--;
                    }else {
                        Expires_in_timer --;
                    }
                }
            };
        }
        TimerToken.schedule(TimerTaskToken, 0, 500);
    }

    /**
     * @ 注销token
     * */
    public static void CancelToken(){
        String string = "https://api.dev.cafewalk.com/oauth-service/exit";
        HttpUrl httpUrl = HttpUrl.parse(string).newBuilder()
                .addQueryParameter("token", AccessToken)
                .build();
        Log.d(TAG, "CancelToken: " + "注销token" + " " + AccessToken + " " + "1111111");
        try {
            OkHttpPost.sendCancelToken(httpUrl, new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String se = response.body().string();
                    Log.d(TAG, "onResponse: 注销token -> " + se);
                    if (response.code() >= 200 && response.code() < 300){
                        AuthenticationInformation();
                    }else {
                        TimerClose();
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void StopTimerToken(){
        if (TimerToken != null){
            TimerToken.cancel();
            TimerToken = null;
        }
        if (TimerTaskToken != null){
            TimerTaskToken.cancel();
            TimerTaskToken = null;
        }
    }

    /**
     * <p>授权服务检测AccessToken是否过期接口.</p>
     * 后台下发
     */
    public static String OAUTH_SERVER_CHECK_TOKEN_URL = "https://api.dev.cafewalk.com/oauth-service/oauth/check_token";

    /**
     * @ token检测是否过期
     * */
    public static void AuthenticationTokenformation(){
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf8"), "");
//        try {
//            Response response = OkHttpPost.sendOkHttpRequestHeader(ReturnAccessToken() + " " + ReturnTokenType(), OAUTH_SERVER_CHECK_TOKEN_URL, requestBody);
//            if (response.isSuccessful()) {
//                String result = response.body().string();
//                Log.d(TAG, "AuthenticationTokenformation: " +result);
//                AccessTokenIn(result);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        OkHttpPost.sendOkHttpRequestHeader(ReturnAccessToken() + " " + ReturnTokenType(), OAUTH_SERVER_CHECK_TOKEN_URL, requestBody, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                AccessTokenIn(string);
            }
        });
    }
}
