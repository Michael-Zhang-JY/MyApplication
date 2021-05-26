package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.example.myapplication.ArrayListKey.DetailsPage;
import com.example.myapplication.FontIconView.FontIconView;
import com.example.myapplication.MQTT.MQTTService;
import com.example.myapplication.OkHttpUtil.OkHttpPost;
import com.example.myapplication.SerialPort.SerialDataSend;
import com.google.gson.Gson;

import java.io.IOException;
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

    public static DetailsPage detailsPage;
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
    }


    /**
     * @ 界面UI赋值
     * */
    @SuppressLint("HandlerLeak")
    private Handler handlerView = new Handler(Looper.getMainLooper()){
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

        }
    }


    public static void CafewalkOrder(){
        MainService.BooleanEsta = true;
        String Url = MQTTService.returnCreateOrderUrl();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), CafewalkJson());
        String Authorization = MainService.ReturnTokenType() + " " + MainService.ReturnAccessToken();
        Log.d(TAG, "CafewalkOrder: ->" + "创建订单");
        OkHttpPost.post(Url, Authorization, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                CafewalkOrder();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body() == null){
                    CafewalkOrder();
                }
                assert response.body() != null;
                String string = response.body().string();
                Log.d(TAG, "onResponse: CafewalkOrder -> " + string);
                if (response.code() >= 200 && response.code() < 300){
                    MainService.BooleanEsta = false;
                }else if (response.code() == 403){
                    MainService.TimerClose();
                }else if (response.code() == 932){
                    MainService.BooleanEsta = false;
                    MainInterFace.Token = null;
                    MainInterFace.CafewalkTokenOne();
                    StartShipMent();
                }
            }
        });
    }

    public static void ShipMentData(){
        MainService.writeData(SerialDataSend.byShipMent());
        MQTTService.arrayDropchan = null;
    }

    private static Timer timerShipMent = null;
    private static TimerTask timerTaskShipMent = null;

    private static void StartShipMent(){
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
//                        MainInterFace.Token = null;
                    }
                }
            };
        }
        timerShipMent.schedule(timerTaskShipMent, 0, 100);
    }

    private static void StopShipMent(){
        if (timerShipMent != null){
            timerShipMent.cancel();
            timerShipMent = null;
        }
        if (timerTaskShipMent != null){
            timerTaskShipMent.cancel();
            timerTaskShipMent = null;
        }
    }


    public static String CafewalkJson(){
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
        jsonObject.put("serialNo", MQTTService.returnSerialNo());
        jsonObject.put("token", MainInterFace.ReturnToken());
        jsonObject.put("tradeType", 1);
        jsonObject.put("userId",  MainService.ReturnCardCafe());
        Log.d(TAG, "CafewalkJson: " + jsonObject.toString());
        return jsonObject.toString();
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
        String infresult = data.substring(12 + SerialDataSend.returnChansNum() * 2 + 3, 12 + (SerialDataSend.returnChansNum() * 2 + 4));
//        Log.d(TAG, "AlterRecycler: " + infresult);
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
                MainInterFace.destoryActivity("SubmitTaskResultActivity");   // 支付成功关闭购买界面
                MainInterFace.returnKeyValuePair().clear();
                Message message = new Message();
                handler.sendMessage(message);
            }
        }
    };


    /**
     * @ Handler回调
     * */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(Looper.getMainLooper()){
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
