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
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.Client.AccessToken;
import com.example.myapplication.HttpData.ContentInven;
import com.example.myapplication.HttpData.DeviceData;
import com.example.myapplication.HttpData.DeviceInven;
import com.example.myapplication.MQTT.IGetMessageCallBack;
import com.example.myapplication.MQTT.JSONUtils;
import com.example.myapplication.MQTT.MQTTService;
import com.example.myapplication.MQTT.MyServiceConnection;
import com.example.myapplication.OkHttpUtil.OkHttpPost;
import com.example.myapplication.OkHttpUtil.OkHttpUtil;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

    public static String CardNumber = null; // 刷卡卡号
    public static String CardAlias = null;  // 员工名字
    public static String MicrosoftCard = null; // 员工编号
    public static String deviceId = null; // 设备Id


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
//        try {
//            readLocalFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Thread.setDefaultUncaughtExceptionHandler(handler);         // 检测异常  一秒后重启APP
        startNetwork();
    }

    private Thread.UncaughtExceptionHandler handler = (t, e) -> {
        try {
            Thread.sleep(2000);
            finish();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        restartApp(); //发生崩溃异常时,重启应用
    };

    /**
     * 重启设备
     * */
    @SuppressLint("WrongConstant")
    private void restartApp(){
//        Intent intent = new Intent(this, MainActivity.class);
//        PendingIntent restartIntent = PendingIntent.getActivity(me.getApplicationContext(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
//        AlarmManager mgr = (AlarmManager) me.getSystemService(Context.ALARM_SERVICE);
//        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 30000, restartIntent);
//        me.finish();
//        android.os.Process.killProcess(android.os.Process.myPid());
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
                        createDevice();
                        Intent intentOne = new Intent(MainService.this, MainActivity.class);
                        startActivity(intentOne);
                        Log.d(TAG, "run: " + "Mainservice222");
                        stopNetwork();
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
    public static void openSerialPort(Byte[] data){
        ser = lztek.openSerialPort("dev/ttyS1", 115200);
        outPut = ser.getOutputStream();
        inPut = ser.getInputStream();
        new SerialRead().start();           // 启动ead串口线程
        byte[] arr = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            arr[i] = data[i];           // 循环赋值
        }
        try {
            outPut.write(arr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        isStart = true;
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
     * @ 构建十六进制数据下发到下位机 （通道数量设定）
     */
    public static Byte [] byTesArray (int chansnum){
        chansNum = chansnum;
        ArrayList<Byte> dataData = new ArrayList<>();
        dataData.add((byte) 0xFF);
        dataData.add((byte) 0x00);
        dataData.add((byte) 0x01);
        dataData.add((byte) 0x01);
        dataData.add((byte) chansNum);
        Byte [] data = new Byte[dataData.size()];
        dataData.toArray(data);         // 复制数据给data
        Integer crc = getCRC(data);
        byte [] danum = intToDoubleBytes(crc);
        dataData.add(danum[0]);
        dataData.add(danum[1]);
        dataData.add((byte) 0x0D);
        dataData.add((byte) 0x0A);
        Byte [] dataArray =  new Byte[dataData.size()];
        dataData.toArray(dataArray);
        Log.i(TAG, "Activity byTesArray: -> " + Arrays.toString(dataArray));
//        logger.info("Activity byTesArray: -> " + Arrays.toString(dataArray));
        return dataArray;
    }

    /**
     * @ 设备上报补货信息
     * */
    public static void MendChans(String chans){
        JSONObject value = new JSONObject();
        value.put("com", "28");
        value.put("devId", MQTTService.returnDevId());
        value.put("chan", chans);
        String str = JSONUtils.createCommandJSONString(MQTTService.SENSORCHANS, value);
        Log.i(TAG, "Activity upload_Data: -> " + str);
//        logger.info("Activity upload_Data: -> " + str);
        MQTTService.publish("$dp", str);
    }

    /**
     * @ 设备请求后台库存
     * */
    public static void DeviceIdInven(String chans){
        String str = "https://www.cafewalk.com/api/store/stock/" + MQTTService.returnID();
        OkHttpUtil.sendOkHttpRequest(str, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                Log.d(TAG, "onResponse: " + response.body().string());
                if (response.code() >= 200 && response.code() < 300){
                    AnalysisBack(response.body().string(), chans);
                }
            }
        });
    }

    /**
     * @ 解析后台库存信息
     * */
    public static void AnalysisBack(String response,String chans){
        Gson gson = new Gson();
        DeviceInven deviceInven = gson.fromJson(response, DeviceInven.class);
        List<ContentInven> contentInven = deviceInven.getContent();
        Collections.sort(contentInven);         // 数据升序
        Log.d(TAG, "AnalysisBack: " + contentInven);
        DeviceSetInven(chans, contentInven);
    }

    /**
     * @ 设备设置商品库存数据打包
     * */
    public static void DeviceSetInven(String chans, List<ContentInven> contentInven){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", "101");
        JSONArray jsonArray = new JSONArray();
        if ("0".equals(chans)){
            for (int i = 0; i < contentInven.size(); i++){
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("chan", contentInven.get(i).getChan());
                jsonObject1.put("goodsId", contentInven.get(i).getGoods().getGoodsId());
                jsonObject1.put("beforeQuantity", contentInven.get(i).getQuantity());
                jsonObject1.put("afterQuantity", "20");
                jsonObject1.put("lock", contentInven.get(i).getLock());
                jsonArray.add(jsonObject1);
            }
        }else {
            for (int i = 0; i < contentInven.size(); i++){
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("chan", contentInven.get(i).getChan());
                jsonObject1.put("goodsId", contentInven.get(i).getGoods().getGoodsId());
                jsonObject1.put("beforeQuantity", contentInven.get(i).getQuantity());
                if (contentInven.get(i).getChan() == Integer.valueOf(chans)){
                    jsonObject1.put("afterQuantity", "20");
                }else {
                    jsonObject1.put("afterQuantity", contentInven.get(i).getQuantity());
                }
                jsonObject1.put("lock", contentInven.get(i).getLock());
                jsonArray.add(jsonObject1);
            }
        }
        jsonObject.put("items", jsonArray);
        Log.d(TAG, "DeviceSetInven: " + jsonObject.toString());
        DeviceSetInvenAppear(jsonObject.toJSONString());
    }

    /**
     * @ 设备设置商品库存上报
     * */
    public static void DeviceSetInvenAppear(String json){
        String str = "https://www.cafewalk.com/api/store/stock/" + MQTTService.returnID();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        OkHttpPost.sendOkHttpRequest(str, requestBody,new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.d(TAG, "onResponse: " + string);
                DeviceInvenAppear(string);

            }
        });
    }

    /**
     * @ 解析设置商品数据
     * */
    public static void DeviceInvenAppear(String string){
        Gson gson = new Gson();
        DeviceData deviceData = gson.fromJson(string, DeviceData.class);
        MainActivity.ReSuccess(deviceData.getMessage());
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
            String strOne = "http://api.heclouds.com/register_de?register_code=h1jS7Vz6ohhoQH4b";
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
    private Handler mHandlerAPK = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            Log.d(TAG, "APKHandler: " + msg.getData().getString("APKUrl"));
            String Url = msg.getData().getString("APKUrl");
            Log.d(TAG, "handleMessage: " + android.net.Uri.fromFile(new java.io.File(Url)));
            Intent intent = new Intent(Intent.ACTION_VIEW);
            assert Url != null;
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(FileProvider.getUriForFile(MainService.this, "com.example.myapplication", new File(Url)),
                    "application/vnd.android.package-archive");
            Log.d(TAG, "handleMessage: " + "1111");
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
     * <p>授权服务获取AccessToken接口.</p>
     * 后台下发
     */
    private static String OAUTH_SERVER_LOGIN_URL = "https://api.dev.cafewalk.com/oauth-service/oauth/token";

    /**
     * 客户端ID
     * 后台下发
     */
    public final static String CLIENT_ID = "670b1376-677a-4909-9859-bef01c0431e2";

    /**
     * 客户端秘钥
     * 后台下发
     */
    public final static String CLIENT_SECRET = "72780234368651019505992566741190641080481032949187";

    /**
     * @ 客户端鉴权信息
     * */
    public static void AuthenticationInformation(){
        HttpUrl httpUrl = HttpUrl.parse(OAUTH_SERVER_LOGIN_URL).newBuilder()
                .addQueryParameter("grant_type", "client_credentials")
                .addQueryParameter("client_id", CLIENT_ID)
                .addQueryParameter("client_secret", CLIENT_SECRET)
                .addQueryParameter("scope", "android_device")
                .build();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf8"), "");
        OkHttpPost.sendOkHttpRequestBody(httpUrl, requestBody, new okhttp3.Callback() {
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

    public static String AccessToken;
    public static String TokenType;

    public static void AccessTokenIn(String token){
        Log.d(TAG, "AccessTokenIn: " + token);
        Gson gson = new Gson();
        AccessToken accessToken = gson.fromJson(token, AccessToken.class);
        AccessToken = accessToken.getAccess_token();
        TokenType = accessToken.getToken_type();
        Log.d(TAG, "AccessTokenIn: " + AccessToken + " " + TokenType);
    }


    public static String ReturnAccessToken(){
        return AccessToken;
    }

    public static String ReturnTokenType(){
        return AccessToken;
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
