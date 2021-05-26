package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
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
import com.example.myapplication.HttpData.HttpCustomerGroupId;
import com.example.myapplication.HttpData.HttpEmployeeInfo;
import com.example.myapplication.HttpData.HttpsAilseData;
import com.example.myapplication.HttpData.HttpsImgUrl;
import com.example.myapplication.HttpData.HttpsMerchandiseNews;
import com.example.myapplication.HttpData.HttpsSerialNo;
import com.example.myapplication.HttpData.Img;
import com.example.myapplication.MQTT.IGetMessageCallBack;
import com.example.myapplication.MQTT.MQTTService;
import com.example.myapplication.MQTT.MyServiceConnection;
import com.example.myapplication.MicrosoftStaff.Microsoft;
import com.example.myapplication.MicrosoftStaff.Token;
import com.example.myapplication.MicrosoftStaff.Users;
import com.example.myapplication.OkHttpUtil.OkHttpPost;
import com.example.myapplication.SerialPort.SerialDataSend;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lztek.toolkit.Lztek;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class MainActivity extends AppCompatActivity implements IGetMessageCallBack {
    public static final String TAG = MainActivity.class.getSimpleName();
//    public Logger logger = LoggerFactory.getLogger(this.getClass());
//    public static final Logger logger = LoggerFactory.getLogger(MainActivity.class);

    public static MainActivity me;
    public static String responseData;
    private MyServiceConnection serviceConnection;
    private ImageView imageView;
    protected TextView textViewInit;
    protected EditText editText;
    protected ConstraintLayout constraintLayout;
    private DBdata dBdata;

    private String PleaseCard = "请 刷 卡 / Please Tag Your Card";
    protected FontIconView fontIconViewTop;

    private Timer timer = new Timer();
    public int countDownTimeOne = 5;
    public int backInt = 0;
    private Timer timerURL = new Timer();
    public static int mark = 0;
    private TimerTask timerTaskURL = null;

    private Timer timerInit = new Timer();
    private TimerTask timerTaskInit = null;

    public static int InitSign = 0;



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
    }

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
            DeviceChannel();
            setEditText();
            StartMicrosoft();
            startTimer();
            MainService.writeData(SerialDataSend.byTesArray());
        }
    }

    private void setEditText(){
        editText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && barr){
                runOnUiThread(()-> fontIconViewTop.setVisibility(View.GONE));
                runOnUiThread(()-> textViewInit.setText("读卡成功，验证卡号/Read the card successfully, verify the card number"));
                Log.d(TAG, "onEditorAction: Card ->" + editText.getText().toString());
                isReturn = true;
            }
            return false;
        });
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

    /**
     * 重启设备
     * */
    @SuppressLint("WrongConstant")
    private void restartApp(){
        Log.d(TAG, "DeviceReboot: " + "4444444");
        Lztek lztek = Lztek.create(this);
        lztek.softReboot();                 // App异常退出重启软件
    }

    public static void RequestData(){
        if (returnStoreID() == 0 || MainService.ReturnCustomerId() == 0){
            me.GetSerialNo();
        }else {
            me.DeviceChannel();
            me.setEditText();               // 监听读卡信息
            me.StartMicrosoft();            // 开启读卡定时器
        }
//        me.setEditText();               // 监听读卡信息
//        me.StartMicrosoft();            // 开启读卡定时器
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
        editText = new EditText(this);
        constraintLayout.addView(editText);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.requestFocusFromTouch();
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
//            mService.releaseService();
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

    public static int storeId = 0;                                      // 设备后台ID

    /**
     * @ 请求设备信息通过流水号
     * */
    private void GetSerialNo(){
        String Str = MQTTService.returnDeviceInfo() + MQTTService.returnSerialNo();
        Log.d(TAG, "GetSerialNo: " + "请求设备信息");
        MainService.BooleanSerialNo = true;
        String Authorization = MainService.ReturnTokenType() + " " + MainService.ReturnAccessToken();
        OkHttpPost.get(Str, Authorization, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body() == null){
                    GetSerialNo();
                }
                assert response.body() != null;
                String string = response.body().string();
                JSONObject jsonObject = JSONObject.parseObject(string);
                Log.d(TAG, "onResponse: SerialNoData -> " + string);
                if (jsonObject.containsKey("code")){
                    if (Integer.parseInt(jsonObject.getString("code")) >= 200 && Integer.parseInt(jsonObject.getString("code")) < 300){
                        MainService.BooleanSerialNo = false;
                        SerialNoData(string);
                    }else{
                        MainService.TimerClose();
                    }
                }

            }
        });
    }

    /**
     * @ 解析设备信息数据
     * */
    private void SerialNoData(String SerialNo){
        Gson gson = new Gson();
        HttpsSerialNo serialNo = gson.fromJson(SerialNo, HttpsSerialNo.class);
        storeId = serialNo.getContent().getStoreId();
        if (serialNo.getContent().getStoreGroup() == null){
            runOnUiThread(()-> textViewInit.setText("设备未配置员工通道，无法使用/The device is not equipped with an employee channel and cannot be used"));
        }else {
            GetCustomerInfo(serialNo.getContent().getStoreGroup().getGroupId());
//            DeviceChannel();
            me.setEditText();               // 监听读卡信息
            if (MicrosoftTimer == null){
                me.StartMicrosoft();            // 开启读卡定时器
            }
        }
//        me.setEditText();               // 监听读卡信息
//        DeviceChannel();
//        me.StartMicrosoft();            // 开启读卡定时器
    }

    /**
     * @ 点位ID获取企业用户信息
     * */
    private void GetCustomerInfo(int GroupId){
        String Str = MQTTService.returnCustomerInfo() + GroupId;
        Log.d(TAG, "GetCustomerInfo: " + "请求企业客户信息");
        MainService.BooleanCustomerInfo = true;
        String Authorization = MainService.ReturnTokenType() + " " + MainService.ReturnAccessToken();
        OkHttpPost.get(Str, Authorization, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body() == null){
                    GetSerialNo();
                }
                assert response.body() != null;
                String string = response.body().string();
                JSONObject jsonObject = JSONObject.parseObject(string);
                Log.d(TAG, "onResponse: GetCustomerInfo -> " + string);
                if (jsonObject.containsKey("code")){
                    if (Integer.parseInt(jsonObject.getString("code")) >= 200 && Integer.parseInt(jsonObject.getString("code")) < 300){
                        CustomerInfoData(string);
                        MainService.BooleanCustomerInfo = false;
                    }else{
                        MainService.TimerClose();
                    }
                }

            }

        });
    }

    /**
     * @ 解析企业用户数据
     * */
    private void CustomerInfoData(String groupID){
        Gson gson = new Gson();
        HttpCustomerGroupId gsonGroupId = gson.fromJson(groupID, HttpCustomerGroupId.class);
        MainService.customerId = gsonGroupId.getContent().getCustomerDTO().getCustomerId();
        DeviceChannel();
    }

    /**
     * @ 通过企业用户ID获取员工信息
     * */
    private void PostCard(String CardNumber){
        String Str = MQTTService.returnEmployeeInfo();
        Log.d(TAG, "GetCustomerInfo: " + "请求企业客户信息");
//        MainService.BooleanSerialNo = true;
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), Card(CardNumber));
        String Authorization = MainService.ReturnTokenType() + " " + MainService.ReturnAccessToken();
        OkHttpPost.post(Str, Authorization, requestBody, new okhttp3.Callback() {
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
                if (response.body() == null){
                    GetSerialNo();
                }
                assert response.body() != null;
                String string = response.body().string();
                JSONObject jsonObject = JSONObject.parseObject(string);
                Log.d(TAG, "onResponse: GetCustomerInfo -> " + jsonObject.toJSONString());
                if (jsonObject.containsKey("code")){
                    if (Integer.parseInt(jsonObject.getString("code")) >= 200 && Integer.parseInt(jsonObject.getString("code")) < 300){
                        MainService.BooleanSerialNo = false;
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("Message", jsonObject.toJSONString());
                        message.setData(bundle);
                        handlerCardData.sendMessage(message);
                    }else {
                        Log.d(TAG, "onFailure: " + "员工信息验证失败");
                        isReturn = true;
                        runOnUiThread(()->textViewInit.setText("验证失败, 请重试/Verification failed. Please try again"));
                        runOnUiThread(()->editText.setText(null));
//                    new Handler().postDelayed(() -> fontIconViewTop.setVisibility(View.GONE), 1000);
//                    new Handler().postDelayed(() -> textViewInit.setText(PleaseCard), 1000);
                        Message message = new Message();
                        handlerMicrosoftOne.sendMessage(message);
                        MainService.TimerClose();
                    }
                }
            }
        });
    }

    /**
     * @ 删除用户名前缀
     * */
    private void CardEmployee(String string){
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

    /**
     * @ 打包员工卡号信息
     * */
    private String Card(String CardNumber){
        String stringCard = CardNumber.trim();     // 去除首尾空格
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("card", stringCard);
        jsonObject.put("customerId", MainService.ReturnCustomerId());
//        Log.d(TAG, "Card: " + jsonObject.toJSONString());
        return jsonObject.toJSONString();
    }

    @SuppressLint("HandlerLeak")
    private Handler handlerCardData = new Handler(Looper.getMainLooper()){
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
//            Log.d(TAG, "handleMessage: " + msg.getData().getString("Message"));
            if (!cardEmployee(msg.getData().getString("Message"))){
                textViewInit.setText("验证失败, 请重试/Verification failed. Please try again");
                editText.setText(null);
                new Handler().postDelayed(() -> fontIconViewTop.setVisibility(View.GONE), 1000);
                new Handler().postDelayed(() -> textViewInit.setText(PleaseCard), 1000);
                isReturn = true;
                Intent intent = new Intent(MainActivity.this, MainMicrosoft.class);
                startActivity(intent);
                finish();
                StopMicrosoft();
            }else {
                textViewInit.setText("验证成功/Successful Authentication");
                CardEmployeeData(msg.getData().getString("Message"));        // 删除用户名前缀
                editText.setText(null);
                Log.d(TAG, "handleMessage: " + editText.getText());
                constraintLayout.removeView(editText);
                Intent intent = new Intent(MainActivity.this, MainInterFace.class);
                intent.putExtra("responseData", returnresponseData());
                startActivityIfNeeded(intent, 1);
                finish();
                StopMicrosoft();
            }
        }
    };

    /**
     * @ 返回所属企业员工信息是否存在
     * */
    private boolean cardEmployee(String cardData){
        Gson gson = new Gson();
        HttpEmployeeInfo httpEmployeeInfo = gson.fromJson(cardData, HttpEmployeeInfo.class);
        if (httpEmployeeInfo.getContent() != null){
            return true;
        }else {
            return false;
        }
    }

    /**
     * @ 解析员工信息
     * */
    private void CardEmployeeData(String cardData){
        Gson gson = new Gson();
        HttpEmployeeInfo httpEmployeeInfo = gson.fromJson(cardData, HttpEmployeeInfo.class);
        MainService.CardNumber = httpEmployeeInfo.getContent().getCard();
        MainService.CardCafeNumber = httpEmployeeInfo.getContent().getEmployeeId();
        String alias = httpEmployeeInfo.getContent().getEmployeeName();
        if ("v-".equals(alias.substring(0,2)) || "V-".equals(alias.substring(0,2))){             // 去除"v—"
            MainService.CardAlias = alias.substring(2);
        }else {
            MainService.CardAlias = alias;
        }
    }

    /**
     * @ 返回StoreId
     * */
    public static int returnStoreID(){
        return storeId;
    }


    /**
     * @ 请求设备通道信息
     * */
    private synchronized void DeviceChannel(){
        try {
            String str = MQTTService.returnAilseData() + returnStoreID();
            Log.d(TAG, "DeviceChannel: " + "请求设备通道信息");
            isComm = true;
            MainService.BooleanAisle = true;
            String Authorization = MainService.ReturnTokenType() + " " + MainService.ReturnAccessToken();
            OkHttpPost.post(str, Authorization, RequestBody.create(null, ""), new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.body() == null){
                        DeviceChannel();
                    }
                    assert response.body() != null;
                    String re =  response.body().string();
                    Log.d(TAG, "onResponse: DeviceChannel -> " + re);
                    if (response.code() >= 200 && response.code() < 300){
                        AnalysisChannel(re);
                        ChansAvailable(re);   // 下发无货通道
                        MainService.BooleanAisle = false;
                    }else if (response.code() == 403){
//                        MainService.StopTimerToken();
//                        MainService.TokenRefresh();
                        MainService.TimerClose();
                    }
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
        ObtainCommodityInformation(httpsAilseData.getContent().get(i).getGoodsId());
    }

    /**
     * @ 获取商品信息
     * */
    private synchronized void ObtainCommodityInformation(int goosId) {
//        Log.d(TAG, "ObtainCommodityInformation: " + "请求通道商品信息");
        String str = MQTTService.returnhomePage() + goosId;
        String Authorization = MainService.ReturnTokenType() + " " + MainService.ReturnAccessToken();
        MainService.BooleanComm = true;
        OkHttpPost.get(str, Authorization, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body() == null){
                    reData = new ArrayList<>();
                    i = 0;
                    DeviceChannel();
                }
                assert response.body() != null;
                String re =  response.body().string();
                Log.d(TAG, "onResponse: ObtainCommodityInformation -> " + re);
                if (response.code() >= 200 && response.code() < 300) {
                    reData.add(re);
                    Log.d(TAG, "onResponse: " + reData.size() + " " + httpsAilseData.getContent().size());
                    if (reData.size() == httpsAilseData.getContent().size()) {
                        AddProductInformation(reData);
                        MainService.BooleanComm = false;
                        reData = new ArrayList<>();
                        i = 0;
                    }else {
                        if (i <= httpsAilseData.getContent().size()){
                            i++;
                            ObtainCommodityInformation(httpsAilseData.getContent().get(i).getGoodsId());
                        }
                    }
                }else if (response.code() == 403){
                    reData = new ArrayList<>();
                    i = 0;
                }
            }
        });
    }

    private int i = 0;
    private ArrayList<String> reData = new ArrayList<>();
    private HttpsAilseData httpsAilseData = new HttpsAilseData();
    private ArrayList<InformationJsonArray> informationJsonArray= new ArrayList<>();      // 保存所有通道商品信息
    private ArrayList<KeyValuePairNews> keyValuePairNews = new ArrayList<>();

    /**
    * @ 添加设备售卖商品信息
    */
    private void AddProductInformation(ArrayList<String> string){
        Gson gson = new Gson();
        Log.d(TAG, "AddProductInformation: " + string);
        for (int i = 0; i < string.size(); i++){
//            Log.d(TAG, "AddProductInformation: " + i);
            HttpsMerchandiseNews httpsMerchandiseNews = gson.fromJson(string.get(i), HttpsMerchandiseNews.class);
            informationJsonArray.add(new InformationJsonArray(httpsMerchandiseNews.getContent().getGoodsId(),
                    httpsMerchandiseNews.getContent().getGoodsName(),
                    httpsMerchandiseNews.getContent().getLabel(),
                    ChannelInformation(httpsMerchandiseNews.getContent().getImgUrl()).get(0).getUrl(),
                    httpsMerchandiseNews.getContent().getCategory().getCategoryName()));
        }
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
//            MainService.openSerialPort(SerialDataSend.byTesArray(6));
            MainService.writeData(SerialDataSend.byTestScmVersions());
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
//        Log.d(TAG, "InformationString: " + jsonObject1.toString());
        responseData = jsonObject1.getString("Information");
    }

    /**
     * @ 字符串转为jsonArray数组
     */
    private List<HttpsImgUrl> ChannelInformation(String img) {
//        Log.d(TAG, "jsonArray: " + img);
        Gson gson = new Gson();
        List<HttpsImgUrl> imgs = gson.fromJson(img, new TypeToken<List<HttpsImgUrl>>() {}.getType());
//        Log.d(TAG, "jsonArray: " + imgs.get(0).getUrl());
        return imgs;
    }

    /**
     * @ 判断每个货道是否有货
     */
    private void  ChansAvailable(String str){
        Gson gson = new Gson();
        httpsAilseData = gson.fromJson(str, HttpsAilseData.class);
        for (int i = 0; i < httpsAilseData.getContent().size(); i++){
//            Log.d(TAG, "ChansAvailable: " + appList.getContent().getGoods().get(i).getQuality());
            if (httpsAilseData.getContent().get(i).getQuantity() <= 1){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MainService.writeData(SerialDataSend.byTesArrayChans(httpsAilseData.getContent().get(i).getChannel()));
            }
        }
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
//        Log.d(TAG, "EditViewMonitor: " + CardNumber.length());
//        Log.d(TAG, "EditViewMonitor: " + editText.getText());
        if (CardNumber.length() == 8){
//            PostMicrosoft(CardNumber);
            PostCard(CardNumber);
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
                if (response.code() >= 200 && response.code() < 300){
                    String  string = response.body().string();
                    Log.i(TAG, "onResponse: PostMicrosoft -> " + string);
                    runOnUiThread(()-> fontIconViewTop.setVisibility(View.GONE));
                    runOnUiThread(()-> textViewInit.setText("验证员工信息/Verify Employee Information"));
                    Gson gson = new Gson();
                    Token tokenInfo = gson.fromJson(string, Token.class);
                    StaffMicrosoft(CardNumber, tokenInfo.getTokenInfo().getToken());
                }else {
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
                }else {
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
        });
     }

    @SuppressLint("HandlerLeak")
    private Handler handlerMicrosoft = new Handler(Looper.getMainLooper()){
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "handleMessage: " + msg.getData().getString("Message"));
            if (StaffMessage(msg.getData().getString("Message")).size() == 0){
                textViewInit.setText("验证失败, 请重试/Verification failed. Please try again");
                editText.setText(null);
                new Handler().postDelayed(() -> fontIconViewTop.setVisibility(View.GONE), 1000);
                new Handler().postDelayed(() -> textViewInit.setText(PleaseCard), 1000);
                isReturn = true;
                Intent intent = new Intent(MainActivity.this, MainMicrosoft.class);
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
                startActivityIfNeeded(intent, 1);
                finish();
                StopMicrosoft();
            }
        }
    };

    /**
     * @ 延时显示
     * */
    @SuppressLint("HandlerLeak")
    private Handler handlerMicrosoftOne = new Handler(Looper.getMainLooper()){
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
     * @ 返回商品信息
     * */
    public static String returnresponseData(){
        return responseData;
    }

    /**
     * @ 异常500处理
     * */
    @SuppressLint("HandlerLeak")
    private Handler handlerABNfive = new Handler(Looper.getMainLooper()){
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
    private Handler handlerABNcustom = new Handler(Looper.getMainLooper()){
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
    private Handler mHandler = new Handler(Looper.getMainLooper()){
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
//        state = "0";
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
        if (message.equals("0")){
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
    private Handler handlerRe = new Handler(Looper.getMainLooper()){
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
    private Handler mHandlerAPK = new Handler(Looper.getMainLooper()){
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
