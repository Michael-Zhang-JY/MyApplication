package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.example.myapplication.ArrayListKey.DetailsPage;
import com.example.myapplication.Cafewalk.CafewalkToken;
import com.example.myapplication.FontIconView.FontIconView;
import com.example.myapplication.FontIconView.XRTextView;
import com.example.myapplication.MQTT.JSONUtils;
import com.example.myapplication.MQTT.MQTTService;
import com.example.myapplication.OkHttpUtil.OkHttpPost;
import com.example.myapplication.OkHttpUtil.OkHttpUtil;
import com.example.myapplication.RecycleView.PersonAdapter;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainDetailsPage extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = MainDetailsPage.class.getCanonicalName();
    //    public static final Logger logger = LoggerFactory.getLogger(MainDetailsPage.class);

    private static MainDetailsPage me;
    protected TextView textViewName;
    protected TextView textViewDescribe;
    protected TextView textViewQuantity;
    protected TextView textViewDrinking;
    protected TextView textViewAcidity;
    protected TextView textViewPrice;
    protected Button buttonReturn;
    protected ImageView imageViewTwo;
    protected TextView xrTextViewOne;
    protected TextView textViewChans;
    protected TextView textViewShipMentOne;
    protected FontIconView fontIconViewStop;
    protected ProgressBar progressBar;
    protected Button buttonPlay;
    protected FontIconView fontIconViewMark;
    protected FontIconView fontIconViewFork;

    public static int chansNum = 0;

    private DetailsPage detailsPage;
    private Gson gson = new Gson();

    private Timer timer = new Timer();
    private int countDowntimer = 15;

    private boolean isPurchase = false;


    private String chans = " 通道 / Aisle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main_details_page);
        Intent intent = getIntent();
        String string = intent.getStringExtra("DetailsPage");
        if (string == null){
            Popup();
        }else {
            Log.i(TAG, "DetailsPage onCreate: -> " + string);
//            logger.info("DetailsPage onCreate: -> " + string);
            AnalyticalData(string);
        }
        me = this;
        initView();
    }

    /**
     * @ 界面初始化
     * */
    private void initView(){
        StartShipMent();
        timer.schedule(timerTask, 1000, 1000);
//        fontIconViewLeft = findViewById(R.id.fontIconViewPage);
        textViewName = findViewById(R.id.textView15);
        textViewDescribe = findViewById(R.id.textView17);
        textViewQuantity = findViewById(R.id.textView18);
        textViewDrinking = findViewById(R.id.textView20);
//        textViewAcidity = findViewById(R.id.textViewPage);
        imageViewTwo = findViewById(R.id.imageViewOne);
        xrTextViewOne = findViewById(R.id.textViewNameOne);
        textViewChans = findViewById(R.id.textViewChans);
        textViewShipMentOne = findViewById(R.id.textViewShipMentOne);
        fontIconViewStop = findViewById(R.id.fontIconViewStop);
        buttonReturn = findViewById(R.id.button);
        progressBar = findViewById(R.id.progressBarOne);
//        fontIconViewLeft.setOnClickListener(this);
        buttonPlay = findViewById(R.id.buttonPlay);
        fontIconViewMark = findViewById(R.id.fontIconViewMark);
        fontIconViewFork = findViewById(R.id.fontIconViewFork);
        buttonReturn.setOnClickListener(this);
//        buttonPlay.setOnClickListener(this);
//        buttonPlay.setClickable(true);
        buttonReturn.setVisibility(View.GONE);
        fontIconViewStop.setVisibility(View.GONE);
        textViewShipMentOne.setText("正在出货 Processing");
        progressBar.setVisibility(View.VISIBLE);

//        Log.d(TAG, "DetailsPage -> handleMessage: " + PackReturnData());
//        CafewalkTokenOne();
    }

    private CafewalkToken cafewalkToken = null;
    private String Token = null;

    private void CafewalkTokenOne(){
        String Url = "https://test.cafewalk.com/api/order/token";
        Log.d(TAG, "CafewalkTokenOne: " + "请求订单Token");
        OkHttpUtil.sendOkHttpRequest(Url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                CafewalkTokenOne();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() >= 200 && response.code() < 300){
                    Gson gson = new Gson();
                    cafewalkToken = gson.fromJson(response.body().string(), CafewalkToken.class);
                    Token = cafewalkToken.getContent();
                    runOnUiThread(()->buttonPlay.setClickable(true));
                    Log.d(TAG, "onResponse: " + cafewalkToken);
                }
            }
        });
    }

    /**
     * @ 无数据显示弹窗返回主界面
     * */
    public void Popup(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainDetailsPage.this);
        alertDialog.setTitle("设备离线请重试");
        alertDialog.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
//            Intent intent = new Intent();
//            Log.d(TAG, "DetailsPage onActivityResult: -> " + "1111111");
//            setResult(RESULT_OK, intent);
            StartActivity();
//            finish();
//            stopTimer();
        });
        alertDialog.show();
//        AlertDialog dialog = alertDialog.create();
//        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(Color.RED);
    }


    /**
     * @ 解析详情页数据
     * */
    private void AnalyticalData(String string){
        detailsPage = gson.fromJson(string, DetailsPage.class);
        Message message = new Message();
        handlerView.sendMessage(message);
    }

    /**
     * @ back回调函数
     */
    @Override
    public void onBackPressed() {
//        keyValuePairs.clear();
        Message message = new Message();
        handler.sendMessage(message);
        MainInterFace.destoryActivity("SubmitTaskResultActivity");   // 支付成功关闭购买界面
        MainInterFace.returnKeyValuePair().clear();
//        keyValuePairs.clear();
//        adapter.notifyDataSetChanged();
    }


    /**
     * @ 界面UI赋值
     * */
    @SuppressLint("HandlerLeak")
    private Handler handlerView = new Handler(){
        @SuppressLint({"ResourceAsColor", "ResourceType", "SetTextI18n"})
        public void handleMessage(Message msg){
            chansNum = detailsPage.getChans();
            textViewName.setText(detailsPage.getStringName());
            textViewDescribe.setText(detailsPage.getDesc().replace("[","").replace("]", "").replaceAll("\"", ""));   // 去除双引号与[]
//            textViewQuantity.setText(detailsPage.getQuality());
            Glide.with(MainDetailsPage.this).load(detailsPage.getStringUrl()).into(imageViewTwo);
            xrTextViewOne.setText(detailsPage.getStringName());
            textViewChans.setText(detailsPage.getChans() + chans);
        }
    };

    /**
     * @ button点击事件监听
     * */
    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.fontIconViewPage:
            case R.id.button:
                if (isPurchase){
                    StartActivity();
                }else {
                    Intent intent = new Intent();
                    intent.putExtra("Data", "DeliveryFailure");
                    setResult(RESULT_OK, intent);
//                StartActivity();
                    finish();
                    stopTimer();
                }
                break;
            case R.id.buttonPlay:
//                countDowntimer = 30;
//                fontIconViewStop.setVisibility(View.GONE);
//                textViewShipMentOne.setText("正在出货 Processing");
//                progressBar.setVisibility(View.VISIBLE);
//                buttonPlay.setClickable(false);
//                buttonReturn.setClickable(false);
//                CafewalkOrder();
////                writeData(byShipMent());
//                break;
        }
    }


    private void CafewalkOrder(){
        String Url = MQTTService.returnCreateOrderUrl() + MQTTService.returnDevId();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), CafewalkJson());
        Log.d(TAG, "CafewalkOrder: " + Url);
//        Log.d(TAG, "CafewalkOrder: " + CafewalkJson());
        Log.d(TAG, "CafewalkOrder: " + "创建订单");
        OkHttpPost.sendOkHttpRequest(Url, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                CafewalkOrder();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() >= 200 && response.code() < 300){
                    assert response.body() != null;
                    Log.d(TAG, "onResponse: order" + response.body().string());
//                    StartShipMent();
                }
            }
        });
    }

    public static void ShipMentData(){
        MainService.writeData(byShipMent());
        MQTTService.arrayDropchan = null;
    }

    private Timer timerShipMent = null;
    private TimerTask timerTaskShipMent = null;

    private void StartShipMent(){
        if (timerShipMent == null){
            timerShipMent = new Timer();
        }
        if (timerTaskShipMent == null){
            timerTaskShipMent = new TimerTask() {
                @Override
                public void run() {
                    if (MainInterFace.ReturnToken() != null){
                        CafewalkOrder();
                        StopShipMent();
                        MainInterFace.Token = null;
                    }
                }
            };
        }
        timerShipMent.schedule(timerTaskShipMent, 0, 100);
    }

    private void StopShipMent(){
        if (timerShipMent != null){
            timerShipMent.cancel();
            timerShipMent = null;
        }
        if (timerTaskShipMent != null){
            timerTaskShipMent.cancel();
            timerTaskShipMent = null;
        }
        Log.d(TAG, "StopShipMent: " + "11111111");
    }

    private String CafewalkJson(){
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObjectDescripTion = new JSONObject();
        jsonObjectDescripTion.put("Alias", MainService.ReturnCardName());
        jsonObjectDescripTion.put("EmployeeNo", MainService.ReturnMicrosoftCard());
        jsonObjectDescripTion.put("BadgeNumber", MainService.ReturnCard());
        jsonObject.put("description", jsonObjectDescripTion.toJSONString());
        jsonObject.put("deviceSaleOrderType", 1);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObjectItem = new JSONObject();
        jsonObjectItem.put("num", 1);
        jsonObjectItem.put("channel", detailsPage.getChans());
        jsonArray.add(jsonObjectItem);
        jsonObject.put("items", jsonArray);
        jsonObject.put("payType", 1);
        jsonObject.put("storeId", MQTTService.returnDevId());
        jsonObject.put("token", MainInterFace.ReturnToken());
        jsonObject.put("tradeType", 1);
        jsonObject.put("userId", "");
        Log.d(TAG, "CafewalkJson: " + jsonObject.toString());
        return jsonObject.toString();
    }

    /**
     * @ 库存不足弹窗显示
     */
    private void showInventoryDialog(String string) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainDetailsPage.this, R.style.dialog).create();
        alertDialog.setTitle(string);
        alertDialog.show();
        new Handler().postDelayed(alertDialog::dismiss, 1000);
    }

    /**
     * @ 商品数量无法添加弹窗
     * */
    private void showOneDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(MainDetailsPage.this, R.style.dialog).create();
        alertDialog.setTitle("商品数已达到最大出货量，无法添加");
        alertDialog.show();
        new Handler().postDelayed(alertDialog::dismiss, 1000);
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
     * 打包串口需要下发的出货数据
     * */
    private static Byte [] byShipMent (){
        MainService.orderCode ++;
        if (MainService.orderCode == 10){
            MainService.orderCode ++;
        }
        if (MainService.orderCode == 65535){
            MainService.orderCode = 0;
        }
        byte byteCode = (byte)(MainService.orderCode / 256);
        byte bytecodeONE = (byte)(MainService.orderCode % 256);
//        OrderCode = byteCode;
        ArrayList<Byte> shipMent = new ArrayList<>();
        shipMent.add((byte) 0xFF);
        shipMent.add((byte) 0x00);
        shipMent.add((byte) 0x02);
        shipMent.add((byte) ((byte) stringByte().length + 2));
        shipMent.add(byteCode);
        shipMent.add(bytecodeONE);
        for (int i = 0; i < stringByte().length; i++){
            shipMent.add(stringByte()[i]);          // 循环赋值后台下发的出货数据
        }
        Byte [] data = new Byte[shipMent.size()];
        shipMent.toArray(data);         // 复制数据给data
        Integer crc = MainActivity.getCRC(data);
        byte [] danum = MainActivity.intToDoubleBytes(crc);
        shipMent.add(danum[0]);
        shipMent.add(danum[1]);
        shipMent.add((byte) 0x0D);
        shipMent.add((byte) 0x0A);
        Byte [] dataArray =  new Byte[shipMent.size()];
        shipMent.toArray(dataArray);
        Log.i(TAG, "Shipment byShipMent: -> " + Arrays.toString(dataArray));
//        logger.info("Shipment byShipMent: -> " + Arrays.toString(dataArray));
        return dataArray;
    }

    /**
     * @ 赋值后台下发的出货通道
     * */
    public static byte [] stringByte (){
        String dropchan = MQTTService.returnArrayDropchan();
        Log.d(TAG, "stringByte: " + dropchan);
        String[] split = dropchan.split("");
        byte[] bytes = new byte[split.length - 1];
        for (int i = 1 ; i < split.length; i++) {
//            if (i == chansNum){
//                Log.d(TAG, "stringByte: " + chansNum);
//                bytes[i - 1] = (byte) 1;
//            }else {
                bytes[i - 1] = (byte) Integer.parseInt(split[i]);
//            }
        }
        Log.d(TAG, "stringByte: " + Arrays.toString(bytes));
        return bytes;
    }

    /**
     * @ 出货数据回调
     * */
    public static void ALterRecyclerCall(String data){
        me.AlterRecycler(data);
    }

    /**
     * @ 修改recycler数据
     * */
    @SuppressLint("SetTextI18n")
    private void AlterRecycler(String data){
        String infresult = data.substring(12 + MainService.returnChansNum() * 2 + 3, 12 + (MainService.returnChansNum() * 2 + 4));
        if (infresult.equals("0")){
            runOnUiThread(() -> progressBar.setVisibility(View.GONE));
            runOnUiThread(() -> textViewShipMentOne.setTextColor(this.getResources().getColor(R.color.colorPrimary)));
            runOnUiThread(() -> textViewShipMentOne.setText("出货成功 Succeed"));
            runOnUiThread(() -> fontIconViewMark.setVisibility(View.VISIBLE));
            isPurchase = true;
            MainInterFace.destoryActivity("SubmitTaskResultActivity");   // 支付成功关闭购买界面
            MainInterFace.returnKeyValuePair().clear();
        }else {
            runOnUiThread(() -> progressBar.setVisibility(View.GONE));
            runOnUiThread(() -> textViewShipMentOne.setTextColor(this.getResources().getColor(R.color.light_red)));
            runOnUiThread(() -> textViewShipMentOne.setText("出货失败 DeliveryFailure"));
            runOnUiThread(() -> fontIconViewFork.setVisibility(View.VISIBLE));
        }
        runOnUiThread(()->buttonReturn.setClickable(true));
        runOnUiThread(()->buttonReturn.setVisibility(View.VISIBLE));
    }

    public static void uploadShipMentData(String data){
//        String string = data.substring(10 + MainActivity.returnChansNum() * 2 + 1, 10 + MainActivity.returnChansNum()* 2 + 2);
//        if (data.substring(10 + MainActivity.returnChansNum() * 2 + 1, 10 + MainActivity.returnChansNum()* 2 + 2).equals("A")){
//            string = "10";
//        }3
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("com", "2");
        jsonObject.put("devId", MQTTService.returnDevId());
        jsonObject.put("order", MQTTService.returnOrder());
        jsonObject.put("ser", MQTTService.returnJsonoBject().getString("ser"));
        jsonObject.put("chan", data.substring(12 + MainService.returnChansNum() * 2 + 1, 12 + MainService.returnChansNum()* 2 + 2));   // 固定10位 + n * 2 + 1 || n * 2 + 2
        jsonObject.put("infresult", data.substring(12 + MainService.returnChansNum() * 2 + 3, 12 + (MainService.returnChansNum() * 2 + 4)));           //8 位 + n * 2 + 3 || n * 2 + 4
        String str = JSONUtils.createCommandJSONString(MQTTService.SENSORIDTHREE, jsonObject);
        Log.d(TAG, "uploadShipMentData: " + str + " " + MQTTService.returnOrder());
        MQTTService.publish("$dp", str);
    }


    /**
     * @ 定时器
     * */
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if (countDowntimer > 1){
                countDowntimer --;
                Log.d(TAG, "run: " + countDowntimer);
            }else {
                Log.d(TAG, "run: " + "MainDeta1111");
                MainInterFace.destoryActivity("SubmitTaskResultActivity");   // 支付成功关闭购买界面
                MainInterFace.returnKeyValuePair().clear();
//                stopTimer();
                Message message = new Message();
                handler.sendMessage(message);
            }
        }
    };


    /**
     * @ Handler回调
     * */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
//            Intent intent = new Intent();
//            setResult(RESULT_OK, intent);
            StartActivity();
//            finish();
        }
    };

    private void StartActivity(){
        Intent intent = new Intent(MainDetailsPage.this, MainActivity.class);
        intent.putExtra("Post", "PostRequest");
        startActivity(intent);
        finish();
        stopTimer();
    }

    private void stopTimer(){
        if (timer != null){
            timer.cancel();
            timer = null;
        }
        if (timerTask != null){
            timerTask.cancel();
            timerTask = null;
        }
    }

}
