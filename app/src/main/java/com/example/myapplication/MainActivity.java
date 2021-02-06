package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.smartcardio.Card;
import android.smartcardio.CardChannel;
import android.smartcardio.CardTerminal;
import android.smartcardio.CommandAPDU;
import android.smartcardio.ResponseAPDU;
import android.smartcardio.TerminalFactory;
import android.smartcardio.ipc.ICardService;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.ArrayListKey.InformationJsonArray;
import com.example.myapplication.ArrayListKey.KeyValuePairNews;
import com.example.myapplication.DBdata.DBdata;
import com.example.myapplication.FontIconView.FontIconView;
import com.example.myapplication.HttpData.Content;
import com.example.myapplication.HttpData.DeviceData;
import com.example.myapplication.HttpData.Goods;
import com.example.myapplication.HttpData.HttpsAilseData;
import com.example.myapplication.HttpData.HttpsImgUrl;
import com.example.myapplication.HttpData.HttpsMerchandiseNews;
import com.example.myapplication.HttpData.Img;
import com.example.myapplication.MQTT.IGetMessageCallBack;
import com.example.myapplication.MQTT.JSONUtils;
import com.example.myapplication.MQTT.MQTTService;
import com.example.myapplication.MQTT.MyServiceConnection;
import com.example.myapplication.MicrosoftStaff.Microsoft;
import com.example.myapplication.MicrosoftStaff.Token;
import com.example.myapplication.MicrosoftStaff.Users;
import com.example.myapplication.OkHttpUtil.OkHttpPost;
import com.example.myapplication.OkHttpUtil.OkHttpUtil;
import com.example.myapplication.SerialPort.SerialRead;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.lztek.toolkit.Lztek;
import com.lztek.toolkit.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

//import org.slf4j.LoggerFactory;
//import org.slf4j.Logger;

public class MainActivity extends AppCompatActivity implements IGetMessageCallBack {
    public static final String TAG = MainActivity.class.getSimpleName();
//    public static final Logger logger = LoggerFactory.getLogger(MainActivity.class);

    public static MainActivity me;
    private DeviceData appList = null;
    private List<Goods> goods = null;
    private Content content = null;
    private static String ProductData;
    private List<Img> imgs = null;
    private static String clientId;
    private static String passWord;
    public static String responseData;
    private MyServiceConnection serviceConnection;
    private ImageView imageView;
    protected TextView textViewInit;
    protected EditText editText;
    protected ConstraintLayout constraintLayout;
    private DBdata dBdata;

    private String PleaseCard = "请 刷 卡 / Please Tag Your Card";
    protected FontIconView fontIconViewTop;


    private ICardService mService = null;
    private TerminalFactory mFactory = null;

    private static int chansNum = 0;

    private Timer timer = new Timer();
    public int countDownTimeOne = 5;
    public int backInt = 0;
    private Timer timerURL = new Timer();
    public static int mark = 0;
    private TimerTask timerTaskURL = null;

    private Timer timerInit = new Timer();
    private TimerTask timerTaskInit = null;

    public static int orderCode = 0;        // 订单码
    public static SerialPort ser = null;
    public static Lztek lztek = null;
    public static InputStream inPut = null;
    public static OutputStream outPut = null;
    public static boolean isStart = false;

    private int chansSign = 0;
    public static int InitSign = 0;

    private boolean ImeiSign = true;
    private int ImeiInt = 0;
    private static final String MANAGEMENT_PACKAGE =
            "com.hidglobal.cardreadermanager";

//    private String clientId;//客户端标识

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();       // 隐藏状态栏
        }
        setContentView(R.layout.activity_main);
        init();
        GetDisplay();
//        if (alreadyInstalled(MANAGEMENT_PACKAGE)){
//            Log.d(TAG, "onCreate: " + "22222");
//            Intent intent = new Intent("com.hidglobal.cardreadermanager.backendipc");
//            intent.setPackage(MANAGEMENT_PACKAGE);
//            Intent serviceIntent = new Intent();
//            serviceIntent.setAction("com.hidglobal.cardreadermanager.backendipc");
//            this.startService(serviceIntent);
//            mService = CardService.getInstance(this);
//        }

    }

    private boolean alreadyInstalled(String packageName) {
        /* To check whether a package is already installed, to PackageManager
         * is used, which can be queried about package information. An
         * exception is thrown if the desired package name is not found.
         * Package names are fully-qualified, e.g.
         * "com.example.anExamplePackage". */

        try {
            PackageManager pm = getPackageManager();
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    private void Card(){
        try {
            if (mFactory == null){
                mFactory = mService.getTerminalFactory();
                List<CardTerminal> terminals = mFactory.terminals().list();
                CardTerminal terminal_1 = terminals.get(0);
                Card card = terminal_1.connect("T=0");
                CardChannel cardChannel = card.getBasicChannel();
                byte[] command = {0x00, (byte) 0xA4, 0x00, 0x0C, 0x02, 0x3F, 0x00};
                ResponseAPDU responseAPDU = cardChannel.transmit(new CommandAPDU(command));
                Log.d(TAG, "Card: " + responseAPDU.toString());
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

//    /**
//     * 跟随系统语言
//     */
//    public static Context attachBaseContext(Context context) {
//        String spLanguage = SPUtils.getInstance().getString(Constants.SP_LANGUAGE , "");
//        String spCountry = SPUtils.getInstance().getString(Constants.SP_COUNTRY,"");
//        if(!TextUtils.isEmpty(spLanguage)&&!TextUtils.isEmpty(spCountry)){
//            Locale  locale = new Locale(spLanguage, spCountry);
//            setAppLanguage(context, locale);
//        }
//        return context;
//    }



    /**
     * @ 界面初始化
     * */
    @SuppressLint({"Range", "SetTextI18n"})
    private void init() {
        me = this;
//        createDevice();
//        startNetwork();
        imageView = findViewById(R.id.mianImageView);
        imageView.setImageResource(R.drawable.img_33);
        textViewInit = findViewById(R.id.textViewInit);
        fontIconViewTop = findViewById(R.id.fontIconViewTop);
//        editText = findViewById(R.id.editText);
        textViewInit.setVisibility(View.VISIBLE);
        backInt++;
        timer.schedule(timerTask, 1000, 1000);
        imageView.setOnClickListener(this::onClick);
        imageView.setClickable(false);
        constraintLayout = findViewById(R.id.constraintLayoutMain);
        editText = new EditText(this);
        constraintLayout.addView(editText);
        editText.setAlpha(0);     // 设置EditView透明
//        editText = findViewById(R.id.edit_Text);
//        editText.setInputType(InputType.TYPE_NULL);
        Intent intent = getIntent();
        String data = intent.getStringExtra("Post");
        if (data != null){
            Log.d(TAG, "init: " + "111111");
            fontIconViewTop.setVisibility(View.GONE);
            textViewInit.setText(PleaseCard);
//            merchandiseNews();
            DeviceChannel();
            startTimer();
            MainService.writeData(byTesArray());
        }
//        editText.setFocusable(true);
//        editText.setFocusableInTouchMode(true);
//        editText.requestFocus();
//        editText.requestFocusFromTouch();
//        startTimerInit();
//        Thread.setDefaultUncaughtExceptionHandler(handler);         // 检测异常  一秒后重启APP
//        dBdata =new DBdata(this,Path() + "/" + "commodity.db" );    // 创建数据库
//        dBdata.getWritableDatabase();
//        Log.d(TAG, "init1: " + dBdata.toString());
//        startTimer();
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && barr){
                    runOnUiThread(()-> fontIconViewTop.setVisibility(View.GONE));
                    runOnUiThread(()-> textViewInit.setText("读卡成功，验证卡号/Read the card successfully, verify the card number"));
                    Log.d(TAG, "onEditorAction: " + editText.getText().toString());
                    isReturn = true;
                }
                return false;
            }
        });
        StartMicrosoft();
    }

    /**
     * @ 实时监听网络状态
     * */
    private Timer timerNetwork = null;
    private TimerTask timerTaskNetwork = null;

    private void startNetwork(){
        if (timerNetwork == null){
            timerNetwork = new Timer();
        }
        if (timerTaskNetwork == null){
            timerTaskNetwork = new TimerTask() {
                @Override
                public void run() {
                    if (isConnectIsNormal()){
                        createDevice();
                        stopNetwork();
                    }
                }
            };
        }
        timerNetwork.schedule(timerTaskNetwork, 0, 500);
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

    /** 判断网络是否连接 */
    private boolean isConnectIsNormal() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
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

    /**
     * @ 修改屏幕dpi
     * */
    private void GetDisplay(){
        Lztek lztek = Lztek.create(this);
//        Log.d(TAG, "GetDisplay: " + lztek.getDisplayDensity());
        if (lztek.getDisplayDensity() != 135){
            lztek.setDisplayDensity(135);
        }
        Log.d(TAG, "GetDisplay: " + lztek.getDisplayDensity());
    }

    private Thread.UncaughtExceptionHandler handler = (t, e) -> {
        try {
            finish();
            Thread.sleep(2000);
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

    public static void RequestData(){
//        me.merchandiseNews();
        me.DeviceChannel();
//        me.AuthenticationTokenformation();
    }

    /**
     * @ 打开串口
     */
    public void openSerialPort(Byte[] data){
        lztek = Lztek.create(this);
        ser = lztek.openSerialPort("dev/ttyS1", 115200);
        outPut = ser.getOutputStream();
        inPut = ser.getInputStream();
        new SerialRead().start();           // 启动read串口线程
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
     * @ 构建十六进制数据下发到下位机 （询问从机门锁状态）
     */
    public Byte [] byTesArray (){
//        chansNum = AnalysisJson(responseData);
        ArrayList<Byte> dataData = new ArrayList<>();
        dataData.add((byte) 0xFF);
        dataData.add((byte) 0x00);
        dataData.add((byte) 0x0D);
        dataData.add((byte) 0x01);
        dataData.add((byte) 0x00);
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
     * @ 转换int数据为byte （两位为一个元素）
     */
    public static byte[] intToDoubleBytes(Integer integer) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (integer / 256);
        bytes[1] = (byte) (integer % 256);
        System.out.println(Arrays.toString(bytes));
        return bytes;
    }

    /**
     * 计算CRC16校验码
     *
     * @ param bytes
     * @ return
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
        System.out.println(Integer.toHexString(CRC));
        return CRC;
    }

    /**
     * @ 上报设定通道是否成功
     * */
    public static void upload_Data (String string){
        JSONObject value = new JSONObject();
        value.put("com", "1");
        value.put("devId", MQTTService.returnDevId());
        value.put("ser", "");
        if (string.equals("00")){
            value.put("chanNum", "0");
        }else{
            value.put("chanNum", String.valueOf(chansNum));
        }
        String str = JSONUtils.createCommandJSONString(MQTTService.SENSORIDONE, value);
        Log.i(TAG, "Activity upload_Data: -> " + str);
//        logger.info("Activity upload_Data: -> " + str);
        MQTTService.publish("$dp", str);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MQTTService.writeData(MQTTService.byTestScmVersions());
//        new Handler().postDelayed(() -> MainService.writeData(MQTTService.byTesArray()), 1000);     // 延时询问门锁状态
    }

    /**
     * @ 返回设定通道数
     * */
    public static int returnChansNum(){
        return chansNum;
    }

    /**
     * @ 获取设备IMEI
     * @param context
     * @return IMEI
     * */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    private String getIMEI(Context context){
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
            assert telephonyManager != null;
            return telephonyManager.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * @ 按钮点击事件
     * */
    private void onClick(View v){
        switch (v.getId()){
            case R.id.mianImageView:
//                Intent intent = new Intent(MainActivity.this, MainInterFace.class);
//                intent.putExtra("responseData", returnresponseData());
//                startActivityForResult(intent, 1);
                EditViewMonitor();
                imageView.setClickable(false);
                break;
        }
    }

    /**
     *@ 上个活动销毁回调onActivityResult
     * */
    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.d(TAG, "onActivityResult: " + "22222");
//        constraintLayout = new ConstraintLayout(this);
//        constraintLayout = findViewById(R.id.constraintLayoutMain);
        editText = new EditText(this);
        constraintLayout.addView(editText);
//        editText.setHeight(100);
//        editText.setWidth(100);
//        editText.setOnFocusChangeListener((view, b) -> {
//
//        });
//        editText.setAlpha(255);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.requestFocusFromTouch();
//        handlerEdit.postDelayed(() -> editText.requestFocus(), 200);
//        editText.dispatchTouchEvent(MotionEvent.ACTION_DOWN);
//        editText.setText(" ");
        Log.d(TAG, "onActivityResult: " + editText.getText().length());
        textViewInit.setText("请刷卡");
//        sendKeyCode();
//        merchandiseNews();
        DeviceChannel();
        countDownTimeOne = 5;
        backInt = 0;
        imageView.setImageResource(R.drawable.img_33);
        backInt ++;
        imageView.setClickable(false);
    }

    private Handler handlerEdit = new Handler();

    private void sendKeyCode(){
        new Thread(() -> {
            Log.d(TAG, "sendKeyCode: " + "11111");
            Instrumentation instrumentation = new Instrumentation();
            instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 10, 10, 0));
            instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 10, 10, 0));
        }).start();
    }

    /**
     * @ 获取SD卡路径
     * */
    private String Path(){
        Lztek lztek = Lztek.create(this);
        String path = lztek.getStorageCardPath();   // 获取SD卡路径
        Log.d(TAG, "Path1: " + path);
//        logger.debug("Path1: " + path);
        return path;
    }

    /**
     * @ 启动MQTT
     * */
    private void initMqttService() {
        serviceConnection = new MyServiceConnection();
        serviceConnection.setIGetMessageCallBack(MainActivity.this);
        Intent intent = new Intent(this, MQTTService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void setMessage(String message) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (serviceConnection != null) {
            Log.d(TAG, "onDestroy: " + "111111111111");
            unbindService(serviceConnection);
            serviceConnection = null;
            stopNetwork();
//            mService.releaseService();
        }
    }
    /**
     * @ 创建产品
     * */
    // 自动创建产品
    private void createDevice(){
        try {
            String strOne = "http://api.heclouds.com/register_de?register_code=FBnW8l4A7moUs7MQ";
            String json = null;
            while (ImeiSign){
                if (ImeiInt == 30){
                    Lztek lztek = Lztek.create(this);
                    lztek.softReboot();
                }
                ImeiInt ++;
                String imei = getIMEI(this);
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
     * @ 后台无数据异常处理
     * */
    @SuppressLint("HandlerLeak")
    private Handler handlerABN = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Popup();
        }
    };

    public void Popup() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("设备离线，请稍后重试");
        alertDialog.setCancelable(false);               // 禁用外部点击
        alertDialog.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
        });
        alertDialog.show();
    }

    /**
     * @ token检测是否过期
     * */
    private void AuthenticationTokenformation(){
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
        OkHttpPost.sendOkHttpRequestHeader(MainService.ReturnAccessToken() + " " + MainService.ReturnTokenType(), MainService.OAUTH_SERVER_CHECK_TOKEN_URL, requestBody, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                MainService.AccessTokenIn(string);
                DeviceChannel();
            }
        });
    }


    /**
     * @ 请求设备通道信息
     * */
    private void DeviceChannel(){
        try {
            String str = MQTTService.returnAilseData() + MQTTService.returnDevId();
//            Log.d(TAG, "DeviceChannel: " + str);
            Log.d(TAG, "DeviceChannel: " + "请求设备通道信息");
            isComm = true;
//            Response request = AgentClient.post(str, null);
//            StartTimerPost();
//            Log.d(TAG, "DeviceChannel: " + request);
//            StartTimerPost(request);
//            MainService.AuthenticationTokenformation();
            OkHttpPost.sendOkHttpRequestNull(str, new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String re =  response.body().string();
                    Log.d(TAG, "onResponse: " + re);
                    AnalysisChannel(re);
            }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @ 解析通道数据
     * */
    private void AnalysisChannel(String channelData){
        Gson gson = new Gson();
        httpsAilseData = gson.fromJson(channelData, HttpsAilseData.class);
//        List<Response> list = httpsAilseData.getContent().parallelStream().map(item -> {
//            String str = MQTTService.returnhomePage() + item.getGoodsId();
//            try {
//                Response response = OkHttpUtil.sendOkHttpRequestNoAsync(str);
//                return response;
//            } catch (IOException e) {
//                e.printStackTrace();
//                return null;
//            }
//        }).collect(Collectors.toList());
//        Log.d(TAG, "AnalysisChannel: " + list.toString());
        for (int i = 0; i < httpsAilseData.getContent().size(); i++){
            ObtainCommodityInformation(httpsAilseData.getContent().get(i).getGoodsId());
        }
//        Log.d(TAG, "AnalysisChannel: " + InformationJsonArray.toString());
    }

    /**
     * @ 获取商品信息
     * */
    private void ObtainCommodityInformation(int goosId){
        String str = MQTTService.returnhomePage() + goosId;
        OkHttpUtil.sendOkHttpRequest(str, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String re =  response.body().string();
                reData.add(re);
                if (ChannelInformationSign ++ == httpsAilseData.getContent().size()){
                    AddProductInformation(reData);
                }
            }
        });
    }

    private ArrayList<String> reData = new ArrayList<>();
    private HttpsAilseData httpsAilseData = new HttpsAilseData();
    private ArrayList<InformationJsonArray> informationJsonArray= new ArrayList<>();      // 保存所有通道商品信息
    private int ChannelInformationSign = 1;                        // 通道信息标志位
    private ArrayList<KeyValuePairNews> keyValuePairNews = new ArrayList<>();

    /**
    * @ 添加设备售卖商品信息
    */
    private void AddProductInformation(ArrayList<String> string){
        Gson gson = new Gson();
        for (int i = 0; i < string.size(); i++){
            HttpsMerchandiseNews httpsMerchandiseNews = gson.fromJson(string.get(i), HttpsMerchandiseNews.class);
//            Log.d(TAG, "AddProductInformation ->: " + ChannelInformation(httpsMerchandiseNews.getContent().getImgUrl()).get(0).getUrl());
            informationJsonArray.add(new InformationJsonArray(httpsMerchandiseNews.getContent().getGoodsId(),
                    httpsMerchandiseNews.getContent().getGoodsName(),
                    httpsMerchandiseNews.getContent().getLabel(),
                    ChannelInformation(httpsMerchandiseNews.getContent().getImgUrl()).get(0).getUrl(),
                    httpsMerchandiseNews.getContent().getCategory().getCategoryName()));
        }
        Log.d(TAG, "AddProductInformation: " + informationJsonArray.toString());
        CommoditySaleInformation();
    }

    /**
     * @ 添加设备完整商品售卖信息
     * */
    private void CommoditySaleInformation(){
        for (int i = 0; i < httpsAilseData.getContent().size(); i++){
            for (int j = 0; j < informationJsonArray.size(); j++){
                if (httpsAilseData.getContent().get(i).getGoodsId() == informationJsonArray.get(j).getGoodsId()){
                    keyValuePairNews.add(new KeyValuePairNews(httpsAilseData.getContent().get(i).getChannel(),
                            httpsAilseData.getContent().get(i).getGoodsId(),
                            informationJsonArray.get(j).getGoodsName(),
                            httpsAilseData.getContent().get(i).getPrice(),
                            informationJsonArray.get(j).getImgUrl(),
                            false,
                            0,
                            httpsAilseData.getContent().get(i).getQuantity(),
                            httpsAilseData.getContent().get(i).getChannelStatus(),
                            informationJsonArray.get(j).getCategoryName(),
                            informationJsonArray.get(j).getLabel()));
                    break;
                }
            }
        }
        Log.d(TAG, "CommoditySaleInformation: " + keyValuePairNews.toString());
        InformationString();
        if(MainService.isChans){
            MainService.openSerialPort(MainService.byTesArray(6));
            MainService.isChans = false;
            InitSign ++;

        }
        runOnUiThread(()-> fontIconViewTop.setVisibility(View.GONE));
        if (barr){
            runOnUiThread(()-> textViewInit.setText(PleaseCard));
        }
    }

    /**
     * @ 商品信息转为字符串
     * */
    private void  InformationString(){
        JSONObject jsonObject1 = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < keyValuePairNews.size(); i++){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("chans", keyValuePairNews.get(i).getChans());
            jsonObject.put("intID", keyValuePairNews.get(i).getIntID());
            jsonObject.put("stringName", keyValuePairNews.get(i).getStringName());
            jsonObject.put("stringUrl", keyValuePairNews.get(i).getStringUrl());
            jsonObject.put("stringPrice", keyValuePairNews.get(i).getCommPrice());
            jsonObject.put("isClick", false);
            jsonObject.put("commPrice", 0);
            jsonObject.put("quality", keyValuePairNews.get(i).getQuality());
            jsonObject.put("lock", keyValuePairNews.get(i).getLock());
            jsonObject.put("feature", keyValuePairNews.get(i).getFeature());
            jsonObject.put("desc", keyValuePairNews.get(i).getDesc());
            jsonArray.add(jsonObject);
        }
        jsonObject1.put("Information", jsonArray);
        Log.d(TAG, "InformationString: " + jsonObject1.toString());
        responseData = jsonObject1.getString("Information");
    }

    /**
     * @ 字符串转为jsonArray数组
     */
    private List<HttpsImgUrl> ChannelInformation(String img) {
//        Log.d(TAG, "jsonArray: " + img);
        Gson gson = new Gson();
        List<HttpsImgUrl> imgs = gson.fromJson(img, new TypeToken<List<HttpsImgUrl>>() {
        }.getType());
//        Log.d(TAG, "jsonArray: " + imgs.get(0).getUrl());
        return imgs;
    }

    /**
 * @ 请求商品信息
     * */
    private void merchandiseNews(){
        if (responseData != null){
            responseData = null;
        }
//        if (MicrosoftTimer != null){
//            StopMicrosoft();
//        }
        try {
            String str = MQTTService.returnhomePage() + "?deviceId=" + MQTTService.returnID();
            isComm = true;
            Log.d(TAG, "merchandiseNews: " + "请求商品信息");
            if (MQTTService.returnhomePage() != null){
                OkHttpUtil.sendOkHttpRequest(str, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: " + "ssl");
                        merchandiseNews();
                        e.printStackTrace();
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        responseData = response.body().string();
                        Log.i(TAG, "Activity onResponse: - > " + responseData);
//                        logger.info("Activity onResponse:  - > " + responseData);
//                    Log.d(TAG, "onResponselen: " + responseData.length());
//                AnalysisJson(responseData);    // 解析json数据1964287
                        if(MainService.isChans){
//                            openSerialPort(byTesArray());    // 打开串口下发设定通道数
                            MainService.openSerialPort(MainService.byTesArray(AnalysisJson(responseData)));
                            MainService.isChans = false;
                            InitSign ++;
//                            MainService.writeData(byTesArray());
                        }
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }

//                    runOnUiThread(()->imageView.setClickable(true));
//                        runOnUiThread(()->textViewInit.setVisibility(View.GONE));
//                        runOnUiThread(()->imageView.setClickable(true));
                        runOnUiThread(()-> fontIconViewTop.setVisibility(View.GONE));
                        if (barr){
                            runOnUiThread(()-> textViewInit.setText(PleaseCard));
                        }
//                        editText.setOnEditorActionListener(new TextWatcher() {
//                            @Override
//                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                            }
//
//                            @Override
//                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                            }
//
//                            @Override
//                            public void afterTextChanged(Editable editable) {
//                                return;
//                            }
//                        });

//                        editText.setOnKeyListener((view, i, keyEvent) -> {
//                            if (i == KeyEvent.KEYCODE_ENTER){
//                                Log.d(TAG, "onKey: " + editText.getText());
//                            }
//                            return false;
//                        });
                        ChansAvailable(responseData);   // 下发无货通道
                    }
                });
            }else {
                stopTimer();
                Message message = new Message();
                handlerABN.sendMessage(message);
            }
        }catch (Exception e){
            Log.d(TAG, "merchandiseNews: " + "2");
            merchandiseNews();
            e.printStackTrace();
        }

    }

    /**
     * @ 判断每个货道是否有货
     */
    private void  ChansAvailable(String str){
        Gson gson = new Gson();
        appList = gson.fromJson(str, DeviceData.class);
        for (int i = 0; i < appList.getContent().getGoods().size(); i++){
//            Log.d(TAG, "ChansAvailable: " + appList.getContent().getGoods().get(i).getQuality());
            if (Integer.valueOf(appList.getContent().getGoods().get(i).getQuality()) <= 1){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MainService.writeData(byTesArrayChans(appList.getContent().getGoods().get(i).getChans().get(0)));
            }
        }
    }

    /**
     * @ 下发货道没货通知
     * */
    public Byte [] byTesArrayChans (int chans){
        ArrayList<Byte> dataData = new ArrayList<>();
        dataData.add((byte) 0xFF);
        dataData.add((byte) 0x00);
        dataData.add((byte) 0x0F);
        dataData.add((byte) 0x01);
        dataData.add((byte) chans);
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

    private boolean isReturn = false;     // 刷卡回车键判断
    private boolean isComm = false;        // 商品请求判断

    /**
     * @ 判断卡号是否符合标准
     * */
    private void EditViewMonitor(){
        String CardNumber = editText.getText().toString();
//        String CardNumber = "1964550 ";
//        StopMicrosoft();
        Log.d(TAG, "EditViewMonitor: " + CardNumber.length());
        Log.d(TAG, "EditViewMonitor: " + editText.getText());
        if (CardNumber.length() == 8){
            PostMicrosoft(CardNumber);
        }
    }

    /***
     * 请求token
     * */
    private void PostMicrosoft(String CardNumber){
        String string = "https://ref.chinanorth.cloudapp.chinacloudapi.cn/sso/api/token";
        Log.d(TAG, "PostMicrosoft: " + "请求MicrosoftToken");
        FormBody requestBody = new FormBody.Builder()
                .add("UserName", "refVendor")
                .add("UserPwd", "1qaz2wsx!QAZ@WSX")
                .build();
        OkHttpPost.sendOkHttpRequest(string, requestBody, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                PostMicrosoft(editText.getText().toString());
                Log.d(TAG, "onFailure: " + "token请求失败");
                isReturn = true;
                runOnUiThread(()->textViewInit.setText("验证失败, 请重试/Verification failed. Please try again"));
                runOnUiThread(()->editText.setText(null));
//                new Handler().postDelayed(() -> fontIconViewTop.setVisibility(View.GONE), 1000);
//                new Handler().postDelayed(() -> textViewInit.setText(PleaseCard), 1000);
                Message message = new Message();
                handlerMicrosoftOne.sendMessage(message);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String  string = response.body().string();
                Log.i(TAG, "Activity onResponse: - >  + 1 " + string);
                runOnUiThread(()-> fontIconViewTop.setVisibility(View.GONE));
                runOnUiThread(()-> textViewInit.setText("验证员工信息/Verify Employee Information"));
                Gson gson = new Gson();
                Token tokenInfo = gson.fromJson(string, Token.class);
                StaffMicrosoft(CardNumber, tokenInfo.getTokenInfo().getToken());
            }
        });
    }

    /**
     * @ 请求员工信息
     * */
    private void StaffMicrosoft(String cardNumber, String token){
        String stringCard = cardNumber.trim();     // 去除首尾空格
        Log.d(TAG, "StaffMicrosoft: " + "请求MicrosoftStaff");
        String string = "https://ref.chinanorth.cloudapp.chinacloudapi.cn/sso/api/UserInfo/GetUserInfoByBn";
        RequestBody requestBody = new FormBody.Builder()
                .add("bn", stringCard)
                .build();
        OkHttpPost.sendOkHttpRequestMicrosoft(string, token, requestBody, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (e instanceof SocketTimeoutException){
//                    StaffMicrosoft(cardNumber, token);
                    Log.d(TAG, "onFailure: " + "员工信息验证失败");
                    isReturn = true;
                    runOnUiThread(()->textViewInit.setText("验证失败, 请重试/Verification failed. Please try again"));
                    runOnUiThread(()->editText.setText(null));
//                    new Handler().postDelayed(() -> fontIconViewTop.setVisibility(View.GONE), 1000);
//                    new Handler().postDelayed(() -> textViewInit.setText(PleaseCard), 1000);
                    Message message = new Message();
                    handlerMicrosoftOne.sendMessage(message);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code() >= 200 && response.code() < 300) {
                    String string1 = response.body().string();
//                    runOnUiThread(()-> textViewInit.setText("验证成功"));
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("Message", string1);
                    message.setData(bundle);
                    handlerMicrosoft.sendMessage(message);
                    Log.d(TAG, "onResponse: " + string1);
                }
            }
        });
     }

    @SuppressLint("HandlerLeak")
    private Handler handlerMicrosoft = new Handler(){
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (StaffMessage(msg.getData().getString("Message")).size() == 0){
                textViewInit.setText("验证失败, 请重试/Verification failed. Please try again");
                editText.setText(null);
                new Handler().postDelayed(() -> fontIconViewTop.setVisibility(View.GONE), 1000);
                new Handler().postDelayed(() -> textViewInit.setText(PleaseCard), 1000);
                isReturn = true;
                Intent intent = new Intent(MainActivity.this, MainMicrosoft.class);
//                intent.putExtra("responseData", returnresponseData());
//                startActivityForResult(intent, 1);
                startActivity(intent);
                finish();
                StopMicrosoft();
//                StartMicrosoft();
            }else {
                textViewInit.setText("验证成功/Successful Authentication");
//                MainService.CardNumber = editText.getText().toString().trim();
                CardMessage(msg.getData().getString("Message"));        // 删除用户名前缀
                editText.setText(null);
                Log.d(TAG, "handleMessage: " + editText.getText());
                constraintLayout.removeView(editText);
                Intent intent = new Intent(MainActivity.this, MainInterFace.class);
                intent.putExtra("responseData", returnresponseData());
                startActivityForResult(intent, 1);
                finish();
                StopMicrosoft();
            }
        }
    };

    /**
     * @ 延时显示
     * */
    @SuppressLint("HandlerLeak")
    private Handler handlerMicrosoftOne = new Handler(){
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            new Handler().postDelayed(() -> fontIconViewTop.setVisibility(View.GONE), 1000);
            new Handler().postDelayed(() -> textViewInit.setText(PleaseCard), 1000);
        }
    };

    /**
     * @ 删除用户名前缀
     * */
    private void CardMessage(String string){
        Gson gson = new Gson();
        Microsoft microsoft = gson.fromJson(string, Microsoft.class);
        MainService.CardNumber = microsoft.getUsers().get(0).getBadgeNumber();
        MainService.MicrosoftCard = microsoft.getUsers().get(0).getEmployeeNO();
        String alias = microsoft.getUsers().get(0).getAlias();
        if ("v-".equals(alias.substring(0,2)) || "V-".equals(alias.substring(0,2))){             // 去除"v—"
            MainService.CardAlias = alias.substring(2);
        }else {
            MainService.CardAlias = alias;
        }

    }

    private List<Users> StaffMessage(String message){
        Gson gson = new Gson();
        Microsoft microsoft = gson.fromJson(message, Microsoft.class);
        return microsoft.getUsers();
    }

    private TimerTask MicrosoftTimerTask = null;
    private Timer MicrosoftTimer = null;
    private int MicrosoftDown = 50;


    private void StartMicrosoft(){
        if (MicrosoftTimer == null){
            MicrosoftTimer = new Timer();
        }
        if(MicrosoftTimerTask == null){
            MicrosoftTimerTask = new TimerTask() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    if (editText.getText().length() == 8 && isReturn){
                        isReturn = false;
                        EditViewMonitor();
                    }
                    if (editText.getText().length() != 8 && isReturn){
                        isReturn = false;
                        runOnUiThread(()-> textViewInit.setText("读卡失败,请重新刷卡/Please Recharge Your Card"));
                        runOnUiThread(()-> editText.setText(null));
                    }
//                    Log.d(TAG, "run: 0" + "等待刷卡");
                }
            };
        }
        if (MicrosoftTimer != null){
            MicrosoftTimer.schedule(MicrosoftTimerTask, 0, 100);
        }
    }

    private void StopMicrosoft(){
        if (MicrosoftTimer != null){
            MicrosoftTimer.cancel();
            MicrosoftTimer = null;
        }
        if (MicrosoftTimerTask != null){
            MicrosoftTimerTask.cancel();
            MicrosoftTimerTask = null;
        }
    }

    /**
     * @ 开启定时器
     * */
    private void startTimer(){
        if (timerURL == null) {
            timerURL = new Timer();
        }

        if (timerTaskURL == null) {
            timerTaskURL = new TimerTask() {
                @Override
                public void run() {
                    if (!isComm){         // 请求一次商品信息
//                        merchandiseNews();
                        DeviceChannel();
                        isComm = true;
                        stopTimer();
                    }
                }
            };
        }

        if(timerURL != null)
            timerURL.schedule(timerTaskURL, 1000, 5000);
    }

    /**
     * @ 暂停定时器
     * */
    private void stopTimer(){
        if (timerURL != null) {
            timerURL.cancel();
            timerURL = null;
        }
        if (timerTaskURL != null) {
            timerTaskURL.cancel();
            timerTaskURL = null;
            mark = 0;
        }

    }
    // 生成随机数openId
//    public void RandomNumber(){
//        Random random = new Random();
//        HashSet<Integer> integerHashSet = new HashSet<>();
//        for (int i = 0; i < 8; i++){
//            integerHashSet.add(random.nextInt(10));
//        }
//        Log.d(TAG, "RandomNumber: " + integerHashSet.toString());
//    }

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

    /**
     * @ 返回商品信息
     * */
    public static String returnresponseData(){
        return responseData;
    }

    /**
     * @ 返回通道数
     * */
    public int AnalysisJson(String str) {
        int chansNum = 0;
        Gson gson = new Gson();
        try {
            appList = gson.fromJson(str, DeviceData.class);
            if (appList.getCode().equals("200") && appList.getContent() != null){
                content = appList.getContent();
                goods = content.getGoods();
                for (int i = 0; i < goods.size(); i++){
                    List<Integer> chans = goods.get(i).getChans();
                    chansNum += chans.size();
                }
            }else {
                if (appList.getCode().equals("500")){
                    Message message = new Message();
                    handlerABNfive.sendMessage(message);
                }else {
                    if ("821".equals(appList.getCode())){
                        chansNum = 6;
                    }
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("code", appList.getCode());      // 传递自定义异常数据
                    message.setData(bundle);
                    Log.d(TAG, "AnalysisJson: " + "11111123456");
                    handlerABNcustom.sendMessage(message);
                }
            }
        }catch (IllegalStateException e){
            e.printStackTrace();
            Popup();
        }
//        Log.d(TAG, "AnalysisJson: " + chansNum);
        return chansNum;
    }

    /**
     * @ 异常500处理
     * */
    @SuppressLint("HandlerLeak")
    private Handler handlerABNfive = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            Popup();
        }
    };

    /**
     * @ 自定义异常处理
     * */
    @SuppressLint("HandlerLeak")
    private Handler handlerABNcustom = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "handleMessage: " + msg.getData().getString("code"));
            if ("821".equals(msg.getData().getString("code"))){
                PopupCustom("设备被锁定，请解锁/Please unlock the device if it is locked");
            }
        }
    };

    private AlertDialog alertDialogOne;
    /**
     * @ 弹窗显示自定义异常
     * */
    private void PopupCustom(String message){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle(message);
        alertDialog.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
        });
        alertDialogOne = alertDialog.create();
        alertDialogOne.show();
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                alertDialogOne.dismiss();
            }
        }, 5000);
    }

    /**
     * @ 字符串转为jsonArray数组
     * */
    private List<Img> jsonArray(String img){
        Gson gson1 = new Gson();
        List<Img> imgs = gson1.fromJson(img, new TypeToken<List<Img>>(){}.getType());
//        Log.d(TAG, "jsonArray: " + imgs.get(0).getUrl());
        return imgs;
    }

    /**
     * @ 设备故障显示
     * */
//    private List<Goods> GoodsArray(){
//        Gson gsonArray = new Gson();
//        List<Goods> goods = gsonArray.fromJson(appList.getContent().getGoods())
//    }


    /**
     * @ 数据库存储数据
     * */
    private void StorageData(){
        SQLiteDatabase sqLiteDatabase = dBdata.getWritableDatabase();
        Log.d(TAG, "onClick: " + sqLiteDatabase);
        ContentValues values = new ContentValues();
        values.put("name", imgs.get(0).getName());
        values.put("url", imgs.get(0).getUrl());
        values.put("uid", imgs.get(0).getUid());
        values.put("status", imgs.get(0).getStatus());
        Log.d(TAG, "StorageData: " + values);
        sqLiteDatabase.insert("commodity1", null, values);
        Cursor cursor = sqLiteDatabase.query("commodity1", new String[]{"name", "url", "uid", "status"}, "id=?", new String[]{"1"}, null, null, null);
        Log.i(TAG, "StorageData: " + cursor.toString());
    }

    /**
     * @ 定时器轮询背景图
     * */
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if (countDownTimeOne > 1){
                countDownTimeOne--;
                Message message = new Message();
                message.what = countDownTimeOne;
                mHandler.sendMessage(message);
            } else {
                countDownTimeOne = 5;
                Message message = new Message();
                message.what = countDownTimeOne;
                mHandler.sendMessage(message);
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if (countDownTimeOne == 5){
                setImageView();
            }
        }
    };


    /**
     * @ 更换背景图片*/
    public void setImageView() {
        switch (backInt){
            case 0:
            case 1:
            case 2:
            case 3:
                imageView.setImageResource(R.drawable.img_33);
                backInt++;
                break;
            case 4:
                imageView.setImageResource(R.drawable.img_33);
                backInt = 0;
                break;
            default:
                break;
        }

    }

    private void startTimerInit(){
        if (timerInit == null){
            timerInit = new Timer();
        }
        if (timerTaskInit == null){
            timerTaskInit = new TimerTask() {
                @Override
                public void run() {
                    if (InitSign > 0){
                        runOnUiThread(()->textViewInit.setVisibility(View.GONE));
                        runOnUiThread(()->imageView.setClickable(true));
//                        textViewInit.setVisibility(View.GONE);
                        stopTimerInit();
                    }
                }
            };
        }
        timerInit.schedule(timerTaskInit, 1000, 100);
    }

    private void stopTimerInit(){
        if (timerInit != null){
            timerInit.cancel();
            timerInit = null;
        }
        if (timerTaskInit != null){
            timerTaskInit.cancel();
            timerTaskInit = null;
        }
    }

    /**
     * @ 设备开锁  禁用购买
     * */
    public static void BFB(String state){
        me.BarredFromBuying(state);
    }

    private boolean barr = true;   // 标记是否打开舱门

    private void BarredFromBuying(String state){
        Log.d(TAG, "BarredFromBuying: " + state);
        if (state.equals("0")){
            runOnUiThread(()-> editText.setText(null));
            barr = true;
            if (MicrosoftTimer == null){
                StartMicrosoft();
            }
            runOnUiThread(()->textViewInit.setText(PleaseCard));
        }else {
            barr = false;
            StopMicrosoft();
            runOnUiThread(()->textViewInit.setText("停用 / Block up"));
        }
    }

    /**
     * @ 设备开锁  禁用购买
     * */
    public static void ReSuccess(String message){
        me.ReplenishmentSuccess(message);
    }

    private void ReplenishmentSuccess(String message){
        Log.d(TAG, "ReplenishmentSuccess: " + message);
        if (message.equals("success")){
            runOnUiThread(()->textViewInit.setText("补货成功 / Replenishment success"));
            Message messageRe = new Message();
            handlerRe.sendMessage(messageRe);
        }else {
            runOnUiThread(()->textViewInit.setText("当前操作补货数量为零 / The current operation replenishment quantity is zero"));
            Message messageRe = new Message();
            handlerRe.sendMessage(messageRe);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handlerRe = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            new Handler().postDelayed(() -> textViewInit.setText("停用 / Block up"), 1000);
        }
    };

    /**
     * @ 主机主动读取从机门锁状态
     * */

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
//            Log.d(TAG, "APKHandler: " + msg.getData().getString("APKUrl"));
            String Url = msg.getData().getString("APKUrl");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            assert Url != null;
            intent.setDataAndType(android.net.Uri.fromFile(new java.io.File(Url)),
                    "application/vnd.android.package-archive");
            intent.putExtra("IMPLUS_INSTALL", "SILENT_INSTALL");
            MainActivity.this.startActivity(intent);
        }
    };
}
