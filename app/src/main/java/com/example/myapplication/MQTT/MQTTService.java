package com.example.myapplication.MQTT;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.APKdownload.DownloadUtil;
import com.example.myapplication.MainActivity;
import com.example.myapplication.MainDetailsPage;
import com.example.myapplication.MainService;
import com.lztek.toolkit.Lztek;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MQTTService extends Service {
    public static final String TAG = MQTTService.class.getSimpleName();

    private static MqttAndroidClient client;
    private MqttConnectOptions conOpt;
    private Lztek lztek = Lztek.create(this);
    public static MQTTService me;

    public static final String SENSORIDONE = "DATAser";            // 数据流名称
    public static final String SENSORIDTHREE = "DATAchanInfresult";     // 出货数据
    public static final String SENSORFOUR = "DATAVersion";       // 版本号
    public static final String SENSORURL = "DATAUrl";           // 主动上报上线通知
    public static final String SENSORCHANS = "DATAmendChans";   // 上报设备补货信息
    public static final String SENSORICCID = "DATAsimimsi";   // 上报设备补货信息

    private String host = "tcp://183.230.40.39:6002";
    private String userName = "400841";
    private String passWord = "MZRCNcTwVfYm6sI9fD=T=8TtcG4=";
    private static String myTopic = UUID.randomUUID().toString();      //要订阅的主题
    private String clientId = "604951928";//客户端标识
    private IGetMessageCallBack IGetMessageCallBack;
    private String com = "/dev/ttyUSB1";            // 4G模块USB接口地址

    public static String arrayDropchan = null;      // 出货通道
    public static JSONObject jsonObject = null;     // 平台下发的数据
    public static String id = null;                 // 请求URLID
    public static String homePage = null;           // 扫码首页URL
    public static String PriceLink = null;          // 请求价格URL
    public static String QRcode = null;             // 请求支付二维码
    public static String Order = null;              // 订单号
    public static String DevId = null;              // 设备ID
    public static String Ailsedata = null;          // 设备通道信息
    public static String OrderTokenUrl = null;         // 订单tokenUrl
    public static String CreateOrderUrl = null;         // 创建订单Url

    private final static int QOS = 0;

    private Timer timerURL = new Timer();
    private TimerTask timerTaskURL = null;
    private int countDownTimerURL = 2;
    private boolean URL = false;

    private Timer timerDevice = new Timer();
    private TimerTask timerTaskDevice = null;
    private int countDownTimerDevice = 120;
    private boolean Device = false;
    private boolean DeviceNot = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(getClass().getName(), "onCreate");
        Log.d(TAG, "onCreate: " + myTopic);
        init();
    }

    /**
     * @ 上报数据给平台
     */
    public static void publish(String topic, String msg) {
        int qos = 1;
        try {
            if (client != null) {
                byte[] bytes = msg.getBytes();
                byte[] data = new byte[bytes.length + 3];
                System.arraycopy(bytes, 0, data, 3, bytes.length);   // 数组复制
                data[0] = 0x01;
                data[1] = 0x00;
                data[2] = (byte) msg.length();
                IMqttDeliveryToken publish = client.publish(topic, data, qos, false);
//                Log.d(TAG, "publish msg: " + publish.getMessage());
            }
        } catch (MqttException e) {
            Log.e(TAG, "exception: " + e.getMessage());
        }
    }

    private void init() {
        me = this;
        String passWord1 = MainService.returnKey();
//        String clientId1 = MainActivity.returnDeviceId();
//        Log.d(TAG, "init1: " + clientId1);
        // 服务器地址（协议+地址+端口号）
        String uri = host;
//        client = new MqttAndroidClient(this, host, clientId);
        client = new MqttAndroidClient(this, uri, MainService.returnDeviceId());
        // 设置MQTT监听并且接受消息
        client.setCallback(mqttCallback);
        conOpt = new MqttConnectOptions();
        // 清除缓存
        conOpt.setCleanSession(true);
        // 设置超时时间，单位：秒
        conOpt.setConnectionTimeout(10);
        // 心跳包发送间隔，单位：秒
        conOpt.setKeepAliveInterval(20);
        // 用户名
//        Log.d(TAG, "init1: " + userName);
        conOpt.setUserName(userName);
        // 密码
//        Log.d(TAG, "init1: " + passWord1);
//        Log.d(TAG, "init1: " + passWord);
        conOpt.setPassword(MainService.returnKey().toCharArray());     //将字符串转换为字符串数组
//        conOpt.setPassword(passWord.toCharArray());
        /*断线重连*/
        conOpt.setAutomaticReconnect(true);
        conOpt.setCleanSession(true);           //  设置客户端和服务器是否应该记住重新启动和重新连接之间的状态。

        Log.d(TAG, "init: " + userName + " " + MainService.returnDeviceId() + " " + MainService.returnKey());
//        Log.d(TAG, "init: " + userName + " " + clientId + " " + passWord);
        // last will message
        boolean doConnect = true;
        String message = "{\"terminal_uid\":\"" + MainService.returnDeviceId() + "\"}";
        Log.e(getClass().getName(), "message是:" + message);
        String topic = myTopic;
        //int qos = 1;
        if ((!message.equals("")) || (!topic.equals(""))) {
            // 最后的遗嘱
            // MQTT本身就是为信号不稳定的网络设计的，所以难免一些客户端会无故的和Broker断开连接。
            //当客户端连接到Broker时，可以指定LWT，Broker会定期检测客户端是否有异常。
            //当客户端异常掉线时，Broker就往连接时指定的topic里推送当时指定的LWT消息。

            try {
                conOpt.setWill(topic, message.getBytes(), QOS, false);
            } catch (Exception e) {
                Log.i(TAG, "Exception Occured", e);
                doConnect = false;
                iMqttActionListener.onFailure(null, e);
            }
        }

        if (doConnect) {
            doClientConnection();
        }

    }


    @Override
    public void onDestroy() {
        stopSelf();
        try {
            client.disconnect();
            client.unregisterResources();
//            client.close();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    /**
     * 连接MQTT服务器
     */
    private void doClientConnection() {
        startTimerDevice();   // 开启设备是否上线检测
        if (!client.isConnected() && isConnectIsNormal()) {
            try {
                client.connect(conOpt, null, iMqttActionListener);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

    }

    // MQTT是否连接成功
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            Log.i(TAG, "连接成功 ");
            startTimerURL();
            Device = true;
            try {
                // 订阅myTopic话题
                client.subscribe(myTopic, QOS);

            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            Log.d(TAG, "onFailure: " + "2222222");
//            DeviceNot = true;
            // 连接失败，重连
        }
    };


    // MQTT监听并且接受消息
    private MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String str1 = new String(message.getPayload());
            String str2 = topic + ";qos:" + message.getQos() + ";retained:" + message.isRetained();
//            Log.d(TAG, "messageArrived:" + str1);
//            Log.d(TAG, "topic - >: " + topic);
            Log.d(TAG, "messageArrived: " + message);
            String substring = topic.substring(0, 6);
            if ("$creq/".equals(substring)) {
                topic = "$crsp/" + topic.substring(6);
//                Log.d(TAG, "topic: " + topic);
//                Log.d(TAG, "message : " + message);
                client.publish(topic, message);
            }
//            Log.i(TAG, "substring: " + substring);
//            Log.i(TAG, str2);
            if (IGetMessageCallBack != null) {
//                Log.d(TAG, "messageArrived1: " + str1);
                IGetMessageCallBack.setMessage(str1);
            }
//            Log.d(TAG, "messageArrived: " + jsonObject);
            jsonObject = JSONObject.parseObject(str1);
//            Log.d(TAG, "messageArrived: " + jsonObject.toString());
            data_Analysis(jsonObject);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {

        }

        @Override
        public void connectionLost(Throwable arg0) {
            // 失去连接，重连
        }
    };

    /**
     * 判断网络是否连接
     */
    private boolean isConnectIsNormal() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            Log.i(TAG, "MQTT当前网络名称：" + name);
            return true;
        } else {
            Log.i(TAG, "MQTT 没有可用网络");
            return false;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.e(getClass().getName(), "onBind");
        return new CustomBinder();
    }

    public void setIGetMessageCallBack(IGetMessageCallBack IGetMessageCallBack) {
        this.IGetMessageCallBack = IGetMessageCallBack;
    }

    public class CustomBinder extends Binder {
        public MQTTService getService() {
            return MQTTService.this;
        }
    }

    private boolean Versions = true;

    /*接收到数据进行解析*/
    private void data_Analysis(JSONObject jsonObject) {
        if (jsonObject.getString("com").equals("1")) {
            if (Versions){
                writeData(byTestScmVersions());
                Versions = false;
                new Handler().postDelayed(() -> MainService.writeData(byTesArray()), 1000);     // 延时询问门锁状态
            }
        } else if (jsonObject.getString("com").equals("2")) {
            String dropChanStr = jsonObject.getString("dropchan");
            arrayDropchan = dropChanStr;
            Order = jsonObject.getString("order");
//            MainInterShopping.TimerBuffCountDown++;
            MainDetailsPage.ShipMentData();
        } else if (jsonObject.getString("com").equals("24")) {
            Log.d(TAG, "data_Analysis: " + "改过版本 + 4");
            URL = true;
            homePage = jsonObject.getString("goods");
            PriceLink = jsonObject.getString("PriceLink");
            QRcode = jsonObject.getString("QRcode");
            id = jsonObject.getString("id");
            DevId = jsonObject.getString("devId");
            Ailsedata = jsonObject.getString("AisleData");
            OrderTokenUrl = jsonObject.getString("OrderToke");
            CreateOrderUrl = jsonObject.getString("CreateOrder");
//            MainService.AuthenticationInformation();
//            AgentClient.init();
//            Send(com,"at+qccid");
//            String ICCID = Receive(com);
            AppearSimICCID();
            MainActivity.RequestData();
//            if (Versions){
//                writeData(byTestScmVersions());
//                Versions = false;
//                new Handler().postDelayed(() -> MainService.writeData(byTesArray()), 1000);     // 延时询问门锁状态
//            }
        } else if (jsonObject.getString("com").equals("25")) {
            writeData(byShipMent(jsonObject.getString("chans")));
        } else if (jsonObject.getString("com").equals("26")) {
//            Log.d(TAG, "data_Analysis: " + "新版本");
            if (jsonObject.getString("app").equals("scm")){
                //                    writebyteData(readLocalFile());
//                writeData(byTestScmBoot());
                APKupgrade(jsonObject.getString("Scmurl"), "PCU800CAV.bin");
            }else if (jsonObject.getString("app").equals("android")){
                APKupgrade(jsonObject.getString("Androidurl"), "Microsoft.apk");
            }
//            else if (jsonObject.getString("app").equals("upgrade")){
//
//            }
        } else if (jsonObject.getString("com").equals("27")){
            DeviceHardReboot(Integer.valueOf(jsonObject.getString("second")));
        } else if (jsonObject.getString("com").equals("29")){
//            Send(com,"at+qccid");
//            String ICCID = Receive(com);
            AppearSimICCID();

        }
    }


    public static Byte[] ByteInbyte(byte[] data){
        Byte[] arr = new Byte[data.length];
        for (int i = 0; i < data.length; i++) {
            arr[i] = data[i];           // 循环赋值
        }
        return arr;
    }

    /**
     * @ 串口写数据
     */
    public static void writeData(Byte[] data) {
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
     * @ 构建十六进制数据下发到下位机 （询问从机门锁状态）
     */
    public static Byte [] byTesArray (){
//        chansNum = AnalysisJson(responseData);
        ArrayList<Byte> dataData = new ArrayList<>();
        dataData.add((byte) 0xFF);
        dataData.add((byte) 0x00);
        dataData.add((byte) 0x0D);
        dataData.add((byte) 0x01);
        dataData.add((byte) 0x00);
        Byte [] data = new Byte[dataData.size()];
        dataData.toArray(data);         // 复制数据给data
        Integer crc = MainService.getCRC(data);
        byte [] danum = MainService.intToDoubleBytes(crc);
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
     * @ 通知Scm进入bootloader模式 进行固件更新
     * */
    public static Byte[] byTestScmBoot(){
        ArrayList<Byte> dataData = new ArrayList<>();
        dataData.add((byte) 0xFF);
        dataData.add((byte) 0x00);
        dataData.add((byte) 0x11);
        dataData.add((byte) 0x01);
        dataData.add((byte) 0x00);
        Byte [] data = new Byte[dataData.size()];
        dataData.toArray(data);         // 复制数据给data
        Integer crc = MainService.getCRC(data);
        byte [] danum = MainService.intToDoubleBytes(crc);
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
     * @ 通知Scm从板固件发送完成
     * */
    public static Byte[] byTestScmOver(){
        ArrayList<Byte> dataData = new ArrayList<>();
        dataData.add((byte) 0xFF);
        dataData.add((byte) 0x00);
        dataData.add((byte) 0x14);
        dataData.add((byte) 0x01);
        dataData.add((byte) 0x00);
        Byte [] data = new Byte[dataData.size()];
        dataData.toArray(data);         // 复制数据给data
        Integer crc = MainService.getCRC(data);
        byte [] danum = MainService.intToDoubleBytes(crc);
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
     * @ 读取SCM固件版本号
     * */
    public static Byte[] byTestScmVersions(){
        ArrayList<Byte> dataData = new ArrayList<>();
        dataData.add((byte) 0xFF);
        dataData.add((byte) 0x00);
        dataData.add((byte) 0x10);
        dataData.add((byte) 0x01);
        dataData.add((byte) 0x00);
        Byte [] data = new Byte[dataData.size()];
        dataData.toArray(data);         // 复制数据给data
        Integer crc = MainService.getCRC(data);
        byte [] danum = MainService.intToDoubleBytes(crc);
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
     * @ SCM 固件更新
     * */
    public static void writeSCM(String SCM){
        me.writebyteSCMData(SCM);
    }


    private boolean SCMOver = false;
    /**
     * @ 串口写数据(单片机远程升级)
     * @ 串口发送数据不能超过4k 需分开发送  延时需要500ms
     */
    private void writebyteSCMData(String SCM) {
        try {
            byte [] data = readLocalFile();
            int SCMDATASIZE = data.length / 4096;
            if (data.length % 4096 != 0){
                SCMDATASIZE  += 1;
            }
            Log.d(TAG, "writebyteSCMData: " + SCMDATASIZE);
            int SCMNum = Integer.valueOf(SCM, 16);
            Log.d(TAG, "writeData: " + data.length);
            if (data.length > 4096){
                if (SCMNum + 1 < SCMDATASIZE){
                    byte[] datanum = new byte[4096];
                    System.arraycopy(data, (SCMNum * 4096), datanum, 0, 4096);
                    MainService.writeData(byteAdd(datanum, SCMNum));
                    Log.d(TAG, "writeData: " + datanum.length);
                }else {
                    if (SCMNum + 1 == SCMDATASIZE){
                        byte[] datanum = new byte[data.length - SCMNum*4096];
                        Log.d(TAG, "writebyteSCMData: " + (data.length - SCMNum*4096));
                        System.arraycopy(data, (SCMNum * 4096), datanum, 0, (data.length - SCMNum*4096));
                        MainService.writeData(byteAdd(datanum, SCMNum));
                        Log.d(TAG, "writeData: " + datanum.length);
                        SCMOver = true;
                    }
                }
            }else {
                MainService.writeData(byteAdd(data, SCMNum));
                SCMOver = true;
            }
            if (SCMOver){
                if (SCMNum + 1 > SCMDATASIZE){
                    writeData(byTestScmOver());
                    SCMOver = false;
                    StartTimerVersions();
                }
            }
//            if (data.length > 4096){
//
//                int num = 0;
//                int numTwo = data.length;
//                int numThree = 0;
//                for (int i = 0; i < data.length; i++) {
//                    num ++;
//                    if (numThree < data.length / 4096){
//                        if (num == 4096){
//                            byte[] datanum = new byte[num];
//                            System.arraycopy(data, (numThree * 4096), datanum, 0, num);
////                            dataData.add(datanum);
//                            MainService.outPut.write(byteAdd(datanum, numThree));
//                            numThree++;
//                            numTwo = numTwo - 4096;
//                            num = 0;
//                            Thread.sleep(1000);
////                            Log.d(TAG, "writebyteData: " + numThree);
////                            Log.d(TAG, "writebyteData: " + numTwo);
//                        }
//                    }else {
//                        if (num == numTwo){
//                            byte[] datanum = new byte[num];
//                            System.arraycopy(data, (numThree * 4096), datanum, 0, num);
//                            MainService.outPut.write(byteAdd(datanum, numThree));
////                            Thread.sleep(1000);
//                        }
//                    }
//
//                }
//            }else {
//                MainService.outPut.write(data);
//            }

//            MainService.outPut.write(data);
//            Thread.sleep(1);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    private Timer Timerversions = null;
    private TimerTask TimerTaskversions = null;

    private void StartTimerVersions(){
        if (Timerversions == null){
            Timerversions = new Timer();
        }
        if (TimerTaskversions == null){
            TimerTaskversions = new TimerTask() {
                @Override
                public void run() {
                    writeData(byTestScmVersions());
                    StopTimerVersions();
                }
            };
        }
        Timerversions.schedule(TimerTaskversions, 5000);
    }

    private void StopTimerVersions(){
        if (Timerversions != null){
            Timerversions.cancel();
            Timerversions = null;
        }
        if (TimerTaskversions != null){
            TimerTaskversions.cancel();
            TimerTaskversions = null;
        }
    }

    /**
     * @ 串口写数据(单片机远程升级)
     * @ 串口发送数据不能超过4k 需分开发送  延时需要500ms
     */
//    public static void writebyteData(byte[] data) {
//        try {
//            Log.d(TAG, "writeData: " + Arrays.toString(data));
//            if (data.length > 4096){
//                int num = 0;
//                int numTwo = data.length;
//                int numThree = 0;
//                for (int i = 0; i < data.length; i++) {
//                    num ++;
//                    if (numThree < data.length / 4096){
//                        if (num == 4096){
//                            byte[] datanum = new byte[num];
//                            System.arraycopy(data, (numThree * 4096), datanum, 0, num);
////                            dataData.add(datanum);
//                            MainService.outPut.write(byteAdd(datanum, numThree));
//                            numThree++;
//                            numTwo = numTwo - 4096;
//                            num = 0;
//                            Thread.sleep(1000);
////                            Log.d(TAG, "writebyteData: " + numThree);
////                            Log.d(TAG, "writebyteData: " + numTwo);
//                        }
//                    }else {
//                        if (num == numTwo){
//                            byte[] datanum = new byte[num];
//                            System.arraycopy(data, (numThree * 4096), datanum, 0, num);
//                            MainService.outPut.write(byteAdd(datanum, numThree));
////                            Thread.sleep(1000);
//                        }
//                    }
//
//                }
//            }else {
//                MainService.outPut.write(data);
//            }
//
////            MainService.outPut.write(data);
////            Thread.sleep(1);
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * @ SCM单片机更新分包添加头尾
     * */
    public static Byte[] byteAdd(byte[] data, int num){
        ArrayList<Byte> dataData = new ArrayList<>();
        byte byteNum = (byte)(num / 256);
        byte byteNumONE = (byte)(num % 256);
        byte byteCode = (byte)(data.length / 256);
        byte bytecodeONE = (byte)(data.length % 256);
        Byte[] datanum = new Byte[data.length + 11];
//        Log.d(TAG, "byteAdd: " + byteCode + " " + bytecodeONE);
        dataData.add((byte) 0x4B);
        dataData.add((byte) 0x54);
        dataData.add(byteNum);
        dataData.add(byteNumONE);
        dataData.add(byteCode);
        dataData.add(bytecodeONE);
        for (int i = 0; i < data.length; i++){
            dataData.add(data[i]);
        }
        Byte [] DATA = new Byte[dataData.size()];
        dataData.toArray(DATA);         // 复制数据给data
        Integer crc = MainService.getCRC(DATA);
        byte [] danum = MainService.intToDoubleBytes(crc);
        dataData.add(danum[0]);
        dataData.add(danum[1]);
        dataData.add((byte) 0x43);
        dataData.add((byte) 0x43);
        dataData.add((byte) 0x44);
        Byte [] dataArray =  new Byte[dataData.size()];
//        Log.d(TAG, "byteAdd: " + dataData.size());
        dataData.toArray(dataArray);
        for (int i = 0; i < dataArray.length; i++){
            datanum[i] = dataArray[i];
        }
        Log.d(TAG, "byteAdd: " + Arrays.toString(datanum));
        return datanum;
    }

    /**
     * 打包串口需要下发的出货数据
     */
    private Byte[] byShipMent(String chans) {
        if (chans.length() == 1) {
            chans = "0" + chans;
        }
        ArrayList<Byte> shipMent = new ArrayList<>();
        shipMent.add((byte) 0xFF);
        shipMent.add((byte) 0x00);
        shipMent.add((byte) 0x0A);
        shipMent.add((byte) 0x01);
        shipMent.add((byte) (Integer.parseInt(chans)));
        Byte[] data = new Byte[shipMent.size()];
        shipMent.toArray(data);         // 复制数据给data
        Integer crc = MainActivity.getCRC(data);
        byte[] danum = MainActivity.intToDoubleBytes(crc);
        shipMent.add(danum[0]);
        shipMent.add(danum[1]);
        shipMent.add((byte) 0x0D);
        shipMent.add((byte) 0x0A);
        Byte[] dataArray = new Byte[shipMent.size()];
        shipMent.toArray(dataArray);
        Log.i(TAG, "Shipment byShipMent: -> " + Arrays.toString(dataArray));
//        logger.info("Shipment byShipMent: -> " + Arrays.toString(dataArray));
        return dataArray;
    }


    /**
     * @ 下载apk && SCM 固件更新
     * @ 远程升级
     */
    private void APKupgrade(String url, String name) {
        Log.d(TAG, "APKupgrade: " + Path());
        DownloadUtil.get().download(url, Path(), name, new DownloadUtil.OnDownloadListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onDownloadSuccess() {
                Log.d(TAG, "onDownloadSuccess: " + "下载完成");
                String Url = Path() + "/" + name;
                Log.d(TAG, "onDownloadSuccess: " + Url);
                if (name.equals("PCU800CAV.bin")){
                    writeData(byTestScmBoot());                 // SCM 远程升级

                }else {
                    MainService.APK(Url);                       // APK 远程升级
                }
            }

            @Override
            public void onDownloading(int progress) {
//                Log.d(TAG, "onDownloading: " + "下载中");
            }

            @Override
            public void onDownloadFailed() {

            }
        });
    }

    /**
     * @ 获取SD卡路径
     */
    private String Path() {
        Lztek lztek = Lztek.create(this);
        String path = Objects.requireNonNull(getExternalFilesDir("APK")).getAbsolutePath();
//        String path = lztek.getStorageCardPath();   // 获取SD卡路径
//        Log.d(TAG, "Path1: " + path);
//        logger.debug("Path1: " + path);
//        return path + "/APK";
        return path;
    }


    public static String returnArrayDropchan() {
//        Log.d(TAG, "returnArrayDropchan: " + arrayDropchan);
        return arrayDropchan;
    }

    /*返回平台下发的数据*/
    public static JSONObject returnJsonoBject() {
        return jsonObject;
    }

    /**
     * @ 返回扫码主页URL
     */
    public static String returnhomePage() {
        return homePage;
    }

    /**
     * @ 返回订单token
     */
    public static String returnOrderTokenUrl() {
        return OrderTokenUrl;
    }

    /**
     * @ 返回设备ID
     */
    public static String returnCreateOrderUrl() {
        return CreateOrderUrl;
    }

    /**
     * @ 返回价格URL
     */
    public static String returnPriceLink() {
        return PriceLink;
    }

    /**
     * @ 返回二维码URL
     */
    public static String returnQRcode() {
        return QRcode;
    }

    /**
     * @ 返回请求后台ID
     */
    public static String returnID() {
        return id;
    }

    /**
     * @ 返回订单号
     */
    public static String returnOrder() {
        return Order;
    }

    /**
     * @ 返回设备通道信息
     */
    public static String returnAilseData() {
        return Ailsedata;
    }

    /**
     * @ 返回设备ID
     */
    public static String returnDevId() {
        return DevId;
    }


    /**
     * @ 设备上线通知
     */
    private void startTimerURL() {
        if (timerURL == null) {
            timerURL = new Timer();
        }

        if (timerTaskURL == null) {
            timerTaskURL = new TimerTask() {
                @Override
                public void run() {
                    if (countDownTimerURL > 0) {
                        countDownTimerURL--;
                    } else {             // 一分钟之后判断是否下发URL
                        if (!URL) {
                            AppearDataUrl();
                            stopTimerURL();
                        }
                    }
                }
            };
        }
        timerURL.schedule(timerTaskURL, 1000, 1000);
    }

    private void stopTimerURL() {
        if (timerURL != null) {
            timerURL.cancel();
            timerURL = null;
        }
        if (timerTaskURL != null) {
            timerTaskURL.cancel();
            timerTaskURL = null;
        }
    }

    /**
     * @ 检测设备是否链接成功
     */
    private void startTimerDevice() {
        if (timerDevice == null) {
            timerDevice = new Timer();
        }
        if (timerTaskDevice == null) {
            timerTaskDevice = new TimerTask() {
                @Override
                public void run() {
                    if (countDownTimerDevice > 0) {
                        countDownTimerDevice--;
                        if (Device) {
                            stopTimerDevice();
                            Device = false;
                            Log.d(TAG, "run: " + "456");
                        }
                        if (DeviceNot) {
                            Log.d(TAG, "DeviceReboot: " + "22222222");
                            stopTimerDevice();
                            DeviceSoftReboot();
                        }
                    } else {
                        stopTimerDevice();
                        Log.d(TAG, "DeviceReboot: " + "33333333");
                        DeviceSoftReboot();
                    }
                }
            };
        }
        timerDevice.schedule(timerTaskDevice, 1000, 1000);
    }

    private void stopTimerDevice() {
        if (timerDevice != null) {
            timerDevice.cancel();
            timerDevice = null;
        }
        if (timerTaskDevice != null) {
            timerTaskDevice.cancel();
            timerTaskDevice = null;
        }
    }

    /**
     * @ 设备软件重启
     */
    private void DeviceSoftReboot() {
        Log.d(TAG, "DeviceReboot: " + "11111");
        Lztek lztek = Lztek.create(this);
        lztek.softReboot();
    }

    private void DeviceHardReboot(int Second) {
        Log.d(TAG, "DeviceReboot: " + "11111");
        Lztek lztek = Lztek.create(this);
        lztek.alarmPoweron(Second);
    }

    /**
     * @ 获取设备卡ICCID
     * @param context
     * @return ICCID
     * */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    private String getICCID(Context context){
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            assert telephonyManager != null;
//            Log.d(TAG, "getIMEI: " + telephonyManager.getLine1Number());
            Log.d(TAG, "getIMSI: " + telephonyManager.getSubscriberId());
//            SubscriptionManager sm = SubscriptionManager.from(context);
//            List sis = sm.getActiveSubscriptionInfoList();
//            SubscriptionInfo si = (SubscriptionInfo) sis.get(0);
//            String iccId = si.getIccId();
            return telephonyManager.getSubscriberId();
//            String iccid2 = null;
//            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//            try { Method method = tm.getClass().getDeclaredMethod("getSubscriberInfo");
//                try { method.setAccessible(true);
//                    Object obj = method.invoke(tm);
//                    Method method2 = obj.getClass().getDeclaredMethod("getPhone",int.class);
//                    method2.setAccessible(true);
//                    Object obj2 = method2.invoke(obj,0);
//                    Method method3 = obj2.getClass().getMethod("getFullIccSerialNumber");
//                    iccid2 = (String) method3.invoke(obj2);
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                } } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            }
//            Log.d(TAG, "getIMEI: " + iccid2);
//            return iccid2;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * @ 通过AT指令获取SIMICCID（接收）
     * */
    private String Receive(String file) {
        RandomAccessFile localRandomAccessFile = null;
        try {
            localRandomAccessFile = new RandomAccessFile(file, "r");
            byte[] arrayOfByte = new byte[1024];
            int readSize = 0;
            while ((readSize = localRandomAccessFile.read(arrayOfByte)) == -1) {

            }
            String response = new String(arrayOfByte).substring(0, readSize);
            Log.d(TAG, "Receive: " + response);
//            LogUtils.e("re:"+response);
            response = replaceBlank(response.substring(10));
            Log.d(TAG, "Receive: " + response);
//            localRandomAccessFile.close();
            return response;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @ 通过AT指令获取SIMICCID（发送）
     * */
    void Send(String file, String cmd){
        RandomAccessFile localRandomAccessFile = null;
        try {
            localRandomAccessFile = new RandomAccessFile(file, "rw");
            localRandomAccessFile.writeBytes(cmd + "\r\n");
            localRandomAccessFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @ 去除\r\n
     * */
    public static String replaceBlank(String src) {
        String dest = "";
        if (src != null) {
            Pattern pattern = Pattern.compile("K|O\r|\n*");
            Matcher matcher = pattern.matcher(src);
            dest = matcher.replaceAll("");
        }
        return dest;
    }

    /**
     * @ 上报SIMIMSI
     * */
    private void AppearSimICCID(){
        JSONObject value = new JSONObject();
        value.put("com", "29");
        value.put("devId", MQTTService.returnDevId());
        if (getICCID(this) == null){
            value.put("simimsi", "");
//            Log.d(TAG, "AppearSimICCID: " + "111111");
        }else {
//            Log.d(TAG, "AppearSimICCID: " + "222222");
            value.put("simimsi", getICCID(this));
        }
        String str = JSONUtils.createCommandJSONString(MQTTService.SENSORICCID, value);
        Log.i(TAG, "Activity upload_Data: -> " + str);
//        logger.info("Activity upload_Data: -> " + str);
        MQTTService.publish("$dp", str);
    }


    /**
     * @ 主动上报上线通知
     */
    private void AppearDataUrl() {
        JSONObject value = new JSONObject();
        value.put("com", "24");
        value.put("set", "");
        String string = JSONUtils.createCommandJSONString(SENSORURL, value);
        Log.i(TAG, "Activity upload_Data: -> " + string);
        MQTTService.publish("$dp", string);
    }

    /**
     * @ 上报App版本与SCM版本
     * */
    public static void AppVersion(String str){
        me.AppversionCode(str);
    }

    /**
     * @ 上报当前app版本与scm版本号
     */
    private void AppversionCode(String str) {
        String ScmversionCode = HexadecimalToDecimal(str);
        JSONObject value = new JSONObject();
        value.put("com", "26");
        value.put("devId", MQTTService.returnDevId());
        value.put("AndroidVersionCode", String.valueOf(MainService.packageCode(this)));
        value.put("AndroidVersionName", MainService.packageName(this));
        value.put("ScmVersion", ScmversionCode);
        String string = JSONUtils.createCommandJSONString(SENSORFOUR, value);
        Log.i(TAG, "Activity upload_Data: -> " + string);
        MQTTService.publish("$dp", string);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MainService.writeData(MQTTService.byTesArray());
    }

    /**
     * @ SCM十六进制字符串转十进制字符串
     * */
    private String HexadecimalToDecimal(String str){
        String string = null;
        int a = Integer.valueOf(str.substring(0, 2), 16);
        int b = Integer.valueOf(str.substring(2, 4), 16);
        String stringOne = String.valueOf(a);
        String stringTwo = String.valueOf(b);
        if (stringOne.length() < 2){
            stringOne = "0" + stringOne;
        }
        if (stringTwo.length() < 2){
            stringTwo = "0" + stringTwo;
        }
        string = stringOne + stringTwo;
        return string;
    }

    /**
     * @ 读取bin文件
     * */
    private byte[] readLocalFile() throws IOException {
        Lztek lztek = Lztek.create(MQTTService.this);
        String path = Objects.requireNonNull(getExternalFilesDir("APK")).getAbsolutePath();
//        String path = lztek.getStorageCardPath();   // 获取SD卡路径+
        String fileName = path + "/PCU800CAV.bin";
        InputStream inputStream = new FileInputStream(fileName);
//        Log.d(TAG, "readLocalFile: " + inputStream);
        byte[] data = toByteArray(inputStream);
        inputStream.close();
//        Log.d(TAG, "readLocalFile: " + data.length);

        return data;
    }
    private byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }
}