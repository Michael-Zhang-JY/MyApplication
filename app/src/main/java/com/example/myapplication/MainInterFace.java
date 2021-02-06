package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.example.myapplication.ArrayListKey.KeyValuePair;
import com.example.myapplication.ArrayListKey.KeyValuePairNews;
import com.example.myapplication.Cafewalk.CafewalkToken;
import com.example.myapplication.FontIconView.FontIconView;
import com.example.myapplication.HttpData.Details;
import com.example.myapplication.HttpData.DeviceData;
import com.example.myapplication.HttpData.Goods;
import com.example.myapplication.HttpData.Img;
import com.example.myapplication.MQTT.MQTTService;
import com.example.myapplication.OkHttpUtil.OkHttpPost;
import com.example.myapplication.RecycleView.PersonAdapter;
import com.example.myapplication.RecycleView.ViewPagerAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Response;

public class MainInterFace extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainInterFace.class.getCanonicalName();
//    public static final Logger logger = LoggerFactory.getLogger(MainInterFace.class);

    protected DeviceData appList;
    private Gson gson = new Gson();
    private ArrayList<String> strURL = new ArrayList<>();
    private ArrayList<String> strName = new ArrayList<>();
    private ArrayList<String> strPrice = new ArrayList<>();
    private ArrayList<String> strOriginalPrice = new ArrayList<>();
    private ArrayList<String> stringArrayListPrice = new ArrayList<>();
    private static ArrayList<KeyValuePair> keyValuePairs = new ArrayList<>();
    protected List<Goods> comGoods;
    protected List<Details> detAils;
    private static ArrayList<String> GroupGoodId = new ArrayList<>();
    public static Integer PriceSign = 0;
    private String endTime;
    private Timer timer = null;
    private Timer timerCountDown = new Timer();
    private Timer timerAnimation = new Timer();
    private TimerTask timerTaskCount = null;
    public int countDownTimeOne = 15;
    private Timer getTimer = new Timer();
    private int getCountDownTimeOne = 1;
    private Timer timerADV = new Timer();
    private int countDownTimeADV = 5;
    private int backInt = 0;            // 轮播标志位

    protected ImageView imageViewADV;
    protected ImageView imageViewOne;
    protected ImageView imageViewTwo;
    protected TextView textViewOneName;
    protected TextView textViewTwoPrice;
    protected TextView textViewThreeName;
    protected TextView textViewFourPrice;
    protected TextView textViewDays;
    protected TextView textViewHours;
    protected TextView textViewMinutes;
    protected TextView textViewSencond;
    protected TextView textViewOrder;
    protected TextView textViewOriginal;
    protected TextView textViewShoppingPrice;
    protected TextView textViewDiscount;
    protected Button buttonAdd;
    protected TextView fontIconViewCurrent;
    protected FontIconView fontIconViewShopping;
    protected ConstraintLayout constraintLayout;
    protected ConstraintLayout constraintLayoutShopping;
    protected FontIconView fontIconViewAdd;
    protected FontIconView fontIconViewReduce;
    protected View viewBackGround;
    protected TextView textViewShow;
    protected ViewPager viewPager;
    protected LinearLayout linearLayout;

    protected LinearLayout linearLayoutOne;
    protected ConstraintLayout constraintLayoutOne;

    private ArrayList<ImageView> listImageView = new ArrayList<>();
    private ViewPagerAdapter viewPagerAdapter;
    private Timer viewPagerTimer;
    private TimerTask viewPagerTimerTask;

    private RecyclerView recyclerView;
    private PersonAdapter adapter;

    private RecyclerView recyclerViewShopping;
    protected ConstraintLayout constraintLayoutShoppingOne;
    protected FontIconView fontIconViewDelete;
    protected TextView textViewDelete;
    protected TextView textView10;
    protected View viewShopping;
    protected FontIconView fontIconViewDown;
    protected TextView textViewfont2;


    private int [] ShipmentGroup = new int[2];
    private int [] ShimentNumber = new int[2];

    private RelativeLayout relativeLayout;
    private int[] RecyViewSite = new int[2];

    private boolean[] isLock = new boolean[2];
    private boolean getIsLock = false;

    protected Button buttonShopping;

    protected TextView textViewTime;
    protected TextView textViewUserName;
    protected TextView textViewCardId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main_inter_face);
        init();
//        initView();

    }

    /**
     * @ 初始化界面 启动RecyclerView
     */
    @SuppressLint("SetTextI18n")
    private void init() {
        CafewalkTokenOne();
        startTimer();               // 启动定时器
        addDestoryActivityToMap(MainInterFace.this, "SubmitTaskResultActivity");
        Intent intent = getIntent();    // 读取商显页下发的数据
        String string = intent.getStringExtra("responseData");
        if (string == null) {    // 空数据弹窗返回商显页
            Log.d(TAG, "init: " + "init异常");
            Popup("设备异常,请重试/Equipment is abnormal. Please try again");
        } else {
//            Log.d(TAG, "init: " + string.length());
            Log.d(TAG, "init:" + string);
//            logger.debug("Interface init: -> " + string);
//            ProductData(string);        // 解析商品数据
            ProductDataNews(string);
        }
//        initViewPager();                // 启动轮播广告
//        textViewTime = findViewById(R.id.TextGreetings);
        textViewUserName = findViewById(R.id.microsoftUserName);
        textViewCardId = findViewById(R.id.microsoftCard);
//        constraintLayoutShopping.setVisibility(View.GONE);
//        constraintLayout.setVisibility(View.GONE);
//        textViewOrder.setVisibility(View.GONE);
//        textViewShoppingPrice.setVisibility(View.GONE);
//        buttonAdd.setOnClickListener(this);
////        fontIconViewShopping.setOnClickListener(this);
//        fontIconViewAdd.setOnClickListener(this);
//        fontIconViewReduce.setOnClickListener(this);
////        textViewShoppingPrice.setOnClickListener(this);
//        fontIconViewDelete.setOnClickListener(this);
//        textViewDelete.setOnClickListener(this);
//        fontIconViewDown.setOnClickListener(this);
//        getDate();
        textViewCardId.setText(MainService.ReturnCard());
        textViewUserName.setText(MainService.ReturnCardName());
        backInt ++;
//        RecycleShopoing();

    }

    public static CafewalkToken cafewalkToken = null;
    public static String Token = null;

    private void CafewalkTokenOne(){
        String Url = MQTTService.returnOrderTokenUrl();
        Log.d(TAG, "CafewalkTokenOne: " + "请求订单Token");
        OkHttpPost.sendOkHttpRequestNull(Url, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() >= 200 && response.code() < 300){
                    Gson gson = new Gson();
                    cafewalkToken = gson.fromJson(response.body().string(), CafewalkToken.class);
                    Token = cafewalkToken.getContent();
                    Log.d(TAG, "onResponse: " + cafewalkToken);
                }
            }
        });
    }

    public static String ReturnToken(){
        return Token;
    }


    @SuppressLint("SetTextI18n")
    private void getDate(){
        Calendar calendar = Calendar.getInstance();
        int Time = calendar.get(Calendar.HOUR_OF_DAY);
        Log.d(TAG, "getDate: " + Time);
        if (Time >= 5 && Time < 12){
            runOnUiThread(()->textViewTime.setText("Good Morning"));
        }else if (Time >= 12 && Time < 18){
            runOnUiThread(()->textViewTime.setText("Good Afternoon"));
        }else {
            runOnUiThread(()->textViewTime.setText("Good Evening"));
        }
    }

    /**
     * @ 初始化轮播数据
     * */
    @SuppressLint("ClickableViewAccessibility")
    private void initViewPager(){
//        viewPager = findViewById(R.id.viewPager);
//        linearLayout = findViewById(R.id.linearLayout_points);
        viewPager.setOnTouchListener((view, motionEvent) -> true);      //禁用左右滑动
        ImageView slide1 = new ImageView(this);
        slide1.setScaleType(ImageView.ScaleType.CENTER_CROP);
        slide1.setImageResource(R.drawable.img_018);
        ImageView slide2 = new ImageView(this);
        slide2.setScaleType(ImageView.ScaleType.CENTER_CROP);
        slide2.setImageResource(R.drawable.img_019);
        ImageView slide3 = new ImageView(this);
        slide3.setScaleType(ImageView.ScaleType.CENTER_CROP);
        slide3.setImageResource(R.drawable.img_020);
        ImageView slide4 = new ImageView(this);
        slide4.setScaleType(ImageView.ScaleType.CENTER_CROP);
        slide4.setImageResource(R.drawable.img_021);
        listImageView.add(slide1);
        listImageView.add(slide2);
        listImageView.add(slide3);
        listImageView.add(slide4);
        viewPagerAdapter = new ViewPagerAdapter(MainInterFace.this, listImageView);
        viewPager.setAdapter(viewPagerAdapter);
        addPoints();
        setPagerListener();
        startautoBannerTimer();                 // 启动自动轮播定时器
    }

    /**
     * @ 自动轮播定时器
     * */
    private void startautoBannerTimer(){
        if (viewPagerTimer == null){
            viewPagerTimer = new Timer();
        }
        if (viewPagerTimerTask == null){
            viewPagerTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if (listImageView.size() > 1) {//多于1个，才循环
                        //子线程不能更新Ui，把主线程请到子线程家里来
                        runOnUiThread(() -> {
                            int index = viewPager.getCurrentItem();
                            index = (index + 1) % listImageView.size();
                            viewPager.setCurrentItem(index,false);      // true为动画效果  false关闭动画效果
                        });
                    }
                }
            };
        }
        if (viewPagerTimer != null){
            viewPagerTimer.schedule(viewPagerTimerTask, 3000, 2500);
        }
    }

    /**
     * @ 暂停广告轮播
     * */
    private void stopautoBannerTimer(){
        if (viewPagerTimer != null){
            viewPagerTimer.cancel();
            viewPagerTimer = null;
        }
        if (viewPagerTimerTask != null){
            viewPagerTimerTask.cancel();
            viewPagerTimerTask = null;
        }
    }


    /**
     * @ 注册监听，当pager变化，切换圆点颜色
     */
    private void setPagerListener() {
        switchPoint(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switchPoint(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * @ 遍历圆点，如果id为选中位置就切换颜色
     * */
    private void switchPoint(int position) {
        ImageView imageView;
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            imageView = (ImageView) linearLayout.getChildAt(i);
            if (i == position) {
                imageView.setImageResource(R.drawable.point);
            } else {
                imageView.setImageResource(R.drawable.point_f);
            }
        }

    }

    /**
     * @ 将小圆点添加到布局中
     * */
    private void addPoints() {
        for (int i = 0; i < listImageView.size(); i++) {
            ImageView imgPoint = new ImageView(this);
            imgPoint.setImageResource(R.drawable.point_f);
            //设置布局属性
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(5, 0, 0, 0);
            imgPoint.setLayoutParams(lp);
            //将添加的小圆点，加到布局中
            linearLayout.addView(imgPoint);
        }
    }

//    /**
//     * @ 添加动画效果 手动添加View 启动动画
//     * @ param:startPostion
//     */
//
//    @SuppressLint("InflateParams")   // startPostion动画开始位置（启动动画）
//    private void startAnimation(int[] startPostion) {
//        relativeLayout = findViewById(R.id.relatvelayout);
//        LayoutInflater layoutInflater = LayoutInflater.from(this);
//        bezierAnimView = (BezierAnimView) layoutInflater.inflate(R.layout.activity_inter_bezier, null);
//        relativeLayout.addView(bezierAnimView);
//        bezierAnimView.startAnimation(startPostion);
//        startTimerAnimation();
//    }
//
//    /**
//     * @ 清除自定义View
//     */
//    // 清除手动添加View
//    @SuppressLint("HandlerLeak")
//    private Handler handlerAnimation = new Handler() {
//        public void handleMessage(Message msg) {
//            relativeLayout.removeView(bezierAnimView);
//        }
//    };

    /**
     * @ 没接收到数弹窗返回主界面
     */
    public void Popup(String str) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainInterFace.this);
        alertDialog.setTitle(str);
        alertDialog.setCancelable(false);               // 禁用外部点击
        alertDialog.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            StartActivity();
//            finish();
            StopOneTimer();
            getTimer.schedule(getTimerTaskCount, 0, 1000);
            EmptyTimer();
        });
        dialogOne = alertDialog.show();
        Message message = new Message();
        handlerOne.sendMessage(message);
//        new Thread(() -> {                  // 一秒钟只能弹窗一个
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } finally {
//                temp = true;
//            }
//        }).start();
    }

    private void StartActivity(){
        Intent intent = new Intent(MainInterFace.this, MainActivity.class);
        intent.putExtra("Post", "PostRequest");
        startActivity(intent);
        stopTimer();
        finish();
    }

    @SuppressLint("HandlerLeak")
    private Handler handlerOne = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            StartOneTimer();
        }
    };

    private Timer timerOne = null;
    private TimerTask timerTaskOne = null;
    private int CountDownOne = 9;
    protected AlertDialog dialogOne;

    private void StartOneTimer(){
        if (timerOne == null){
            timerOne = new Timer();
        }
        if (timerTaskOne == null){
            timerTaskOne = new TimerTask() {
                @Override
                public void run() {
                    if (CountDownOne > 0){
                        CountDownOne --;
                    }else {
                        dialogOne.dismiss();
                        StopOneTimer();
                        StartActivity();
                        getTimer.schedule(getTimerTaskCount, 0, 1000);
                        EmptyTimer();
                    }
                }
            };
        }
        timerOne.schedule(timerTaskOne, 1000, 1000);
    }

    private void StopOneTimer(){
        if (timerOne != null){
            timerOne.cancel();
            timerOne = null;
        }
        if (timerTaskOne != null){
            timerTaskOne.cancel();
            timerTaskOne = null;
        }
    }

    /**
     * @ back回调函数
     */
    @Override
    public void onBackPressed() {
//        keyValuePairs.clear();
        EmptyTimer();
//        Intent intent = new Intent();
//        Log.d(TAG, "onActivityResult: " + "1111111");
//        setResult(RESULT_OK, intent);
        Intent intent = new Intent(MainInterFace.this, MainActivity.class);
        intent.putExtra("Post", "PostRequest");
        startActivity(intent);
        finish();
        getTimer.schedule(getTimerTaskCount, 0, 100);
//        keyValuePairs.clear();
//        adapter.notifyDataSetChanged();
    }

    private TimerTask getTimerTaskCount = new TimerTask() {         // 倒计时一秒清掉所有数据
        @Override
        public void run() {
            if (getCountDownTimeOne > 0){
                getCountDownTimeOne --;
            }else {
                keyValuePairs.clear();
//                adapter.notifyDataSetChanged();
                getTimer.cancel();
                getTimer = null;
                getTimerTaskCount = null;
            }
        }
    };

    /**
     * @param data
     * @param requestCode
     * @param resultCode
     * @ 上个活动销毁回调onActivityResult
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
////        countDownTimeADV = 5;
//        backInt = 0;
////        imageViewADV.setImageResource(R.drawable.img_018);
////        constraintLayoutShopping.setVisibility(View.GONE);
//        if (resultCode == RESULT_OK) {
//            String string = data.getStringExtra("PackReturnData");
//            Log.d(TAG, "Interface onActivityResult: -> " + string);
////            logger.debug("Interface onActivityResult: -> " + string);
//            if (string != null) {
//                ParSingPage(string);
//                adapter.notifyDataSetChanged();
//                if (PersonAdapter.NumOrder() > 0) {
//                    RequestPrcice();
////                    constraintLayoutShopping.setVisibility(View.VISIBLE);
//                    textViewOrder.setVisibility(View.VISIBLE);
//                    textViewShoppingPrice.setVisibility(View.VISIBLE);
//                    textViewOrder.setBackgroundResource(rectangle7);
//                    textViewOrder.setText(NumberOrder().toString());
//                }
//                if (PriceSign != 0){
//                    fontIconViewAdd.setVisibility(View.VISIBLE);
//                    fontIconViewReduce.setVisibility(View.VISIBLE);
//                    textViewShow.setVisibility(View.VISIBLE);
//                    viewBackGround.setVisibility(View.VISIBLE);
//                    buttonAdd.setVisibility(View.GONE);
//                    textViewShow.setText(PriceSign.toString());
//                }
////                Log.d(TAG, "Interface onActivityResult: -> " + PriceSign);
////                0logger.debug("Interface onActivityResult: -> " + PriceSign);
//            }
//        }else {
//            keyValuePairsOne.clear();
//            shoppingAdapterOne.notifyDataSetChanged();
//            viewShopping.setVisibility(View.GONE);
//            ClickableStop(true);
//            PriceSign = 0;
////            constraintLayoutShopping.setVisibility(View.GONE);
//            textViewOrder.setVisibility(View.GONE);
//            textViewShoppingPrice.setVisibility(View.GONE);
//            fontIconViewAdd.setVisibility(View.GONE);
//            fontIconViewReduce.setVisibility(View.GONE);
//            constraintLayoutShoppingOne.setVisibility(View.GONE);
//            textViewShow.setVisibility(View.GONE);
//            viewBackGround.setVisibility(View.GONE);
//            buttonAdd.setVisibility(View.VISIBLE);
////            String string = data.getStringExtra("responseData");
////            Log.d(TAG, "onActivityResult: " + string);
//            if (MainActivity.returnresponseData() != null){
//                keyValuePairs.clear();
//                ProductData(MainActivity.returnresponseData());
//            }
//            Log.d(TAG, "onActivityResult: " + "11111111");
//        }
        if (resultCode == RESULT_OK){
            Log.d(TAG, "onActivityResult: " + "111111");
            CafewalkTokenOne();
        }
        Log.d(TAG, "onActivityResult: " + requestCode);
        Log.d(TAG, "onActivityResult: " + "222222");
        countDownTimeOne = 15;
        startTimer();
        startautoBannerTimer();
//        adapter.notifyDataSetChanged();
    }

    /**
     * @param string
     * @ 解析详情页返回的数据
     */
    private void ParSingPage(String string) {
//        Gson gson = new Gson();
//        PackReturnDataClass returnDataClass = gson.fromJson(string, PackReturnDataClass.class);
//        if (MainDetailsPage.returnNumAdd() != 0){
//            keyValuePairs.get(returnDataClass.getPosition()).setCommPrice(returnDataClass.getNumber());
//        }
//        PriceSign = returnDataClass.getPricesign();
    }

    /**
     * @ 启动recycleView 监听item点击事件
     */
    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    private void recyCler() {
        recyclerView = findViewById(R.id.recyclerView_interFace1);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        if (keyValuePairs.size() != 6){
            Log.d(TAG, "recyCler: " + keyValuePairs.toString());
            Log.d(TAG, "recyCler: " + "recyCler异常");
            Popup("设备异常，请重试/Equipment is abnormal. Please try again");
        }
        adapter = new PersonAdapter(this, keyValuePairs);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((view, viewName, position) -> {                  // 用户点击监听
            if (viewName == PersonAdapter.ViewName.DETAILS) {
                countDownTimeOne = 15;
                stopautoBannerTimer();
                stopTimer();
                new Handler().postDelayed(() -> {

                },500);
                Intent intent = new Intent(MainInterFace.this, MainDetailsPage.class);
                intent.putExtra("DetailsPage", PackagingPage(position));
                startActivityForResult(intent, 1);                  // 启动详情页
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {          // 用户滑动监听
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                countDownTimeOne = 15;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                countDownTimeOne = 15;
            }
        });
    }

    /**
     * @ 启动购物车recycleView
     * */
//    @SuppressLint("SetTextI18n")
//    private void RecycleShopoing(){
//        recyclerViewShopping = findViewById(R.id.recyclerViewShoppingOne);
//        recyclerViewShopping.setLayoutManager(new LinearLayoutManager(this));
//        shoppingAdapterOne = new ShoppingAdapterOne(this, keyValuePairsOne);
//        recyclerViewShopping.setAdapter(shoppingAdapterOne);
//        shoppingAdapterOne.setOnItemClickListener((view, viewName, position) -> {
//            switch (viewName){
//                case MINUS:
//                    ShoppingMiuns(position);
//                    break;
//                case SING:
//                    ShoppingSing(position);
//                    break;
//            }
//        });
//    }

//    /**
//     * @ 链接购物车减少按钮
//     * */
//    @SuppressLint("SetTextI18n")
//    private void ShoppingMiuns(int position){
//        countDownTimeOne = 40;
//        for (int i = 0; i < keyValuePairs.size(); i++){
//            if (keyValuePairsOne.get(position).getIntID().equals(keyValuePairs.get(i).getIntID())){
//                keyValuePairs.get(i).setCommPrice(keyValuePairs.get(i).getCommPrice() - 1);
//                PersonAdapter.returnOrder().set(i, PersonAdapter.returnOrder().get(i) - 1);
//            }
//        }
//        for (int i = 0; i < GroupGoodId.size(); i++) {
//            if (GroupGoodId.contains(keyValuePairsOne.get(position).getIntID())) {
//                for (int j = 0; j < keyValuePairsOne.size(); j++) {
//                    if (j == position) {
//                    } else if (GroupGoodId.contains(keyValuePairsOne.get(j).getIntID()) && keyValuePairsOne.get(j).getCommPrice() != 0) {
//                        if (keyValuePairsOne.get(j).getCommPrice().equals(keyValuePairsOne.get(position).getCommPrice())) {
//                            PriceSign = keyValuePairsOne.get(position).getCommPrice();
//                        } else if (keyValuePairsOne.get(j).getCommPrice() > keyValuePairsOne.get(position).getCommPrice()) {
//                            PriceSign = keyValuePairsOne.get(position).getCommPrice();
//                        } else if (keyValuePairsOne.get(j).getCommPrice() < keyValuePairsOne.get(position).getCommPrice()) {
//                            PriceSign = keyValuePairsOne.get(j).getCommPrice();
//                        }
//                        runOnUiThread(() -> textViewShow.setText(PriceSign.toString()));
//                    }
//                }
//            }
//        }
//        if (keyValuePairsOne.get(position).getCommPrice() == 0){
//            for (int i = 0; i < keyValuePairs.size(); i++){
//                if (keyValuePairs.get(i).getIntID().equals(keyValuePairsOne.get(position).getIntID())){
//                    PersonAdapter.returnIsclick().set(i, false);
//                }
//            }
//            keyValuePairsOne.remove(position);
//            ViewGroup.LayoutParams lp = recyclerViewShopping.getLayoutParams();
//            if (keyValuePairsOne.size() > 4) {          // 设定购物车超过四个就固定RecyclerView高度 滑动展示
//                lp.height = DensityUtil.dip2px(this,140 * 4);
//            } else {
//                lp.height = DensityUtil.dip2px(this,140 * keyValuePairsOne.size());
//            }
//            recyclerViewShopping.setLayoutParams(lp);
//        }
////        if (PersonAdapter.returnOrder().get(position) == 0){
////            PersonAdapter.returnIsclick().set(position, false);
////        }
//        if (NumberOrder() == 0) {
////            constraintLayoutShopping.setVisibility(View.GONE);
//            textViewOrder.setVisibility(View.GONE);
//            textViewShoppingPrice.setVisibility(View.GONE);
//            recyclerViewShopping.setVisibility(View.GONE);
//            constraintLayoutShoppingOne.setVisibility(View.GONE);
////            constraintLayoutShopping.setVisibility(View.GONE);
//            viewShopping.setVisibility(View.GONE);
//            ClickableStop(true);
//            PriceSign = 0;
//            for (int i = 0; i < PersonAdapter.returnIsclick().size(); i++){
//                PersonAdapter.returnIsclick().set(i, false);
//            }
//        } else {
//            RequestPrcice();
//            textViewOrder.setText(NumberOrder().toString());
//        }
//        if (PriceSign == 0) {
//            buttonAdd.setVisibility(View.VISIBLE);
//            fontIconViewAdd.setVisibility(View.GONE);
//            fontIconViewReduce.setVisibility(View.GONE);
//            textViewShow.setVisibility(View.GONE);
//            viewBackGround.setVisibility(View.GONE);
//        }
//        textViewOrder.setText(NumberOrder().toString());
//        adapter.notifyDataSetChanged();
//        shoppingAdapterOne.notifyDataSetChanged();
//    }

    /**
     * @ 购物车增加按钮
     * */
//    @SuppressLint("SetTextI18n")
//    private void ShoppingSing(int position) {
//        countDownTimeOne = 40;
//        if (keyValuePairsOne.get(position).getCommPrice() < 4){
//            if (PersonAdapter.NumOrder() < 10){
//                for (int i = 0; i < keyValuePairs.size(); i++){
//                    if (keyValuePairsOne.get(position).getIntID().equals(keyValuePairs.get(i).getIntID())){
//                        keyValuePairs.get(i).setCommPrice(keyValuePairs.get(i).getCommPrice() + 1);
//                        PersonAdapter.returnOrder().set(i, PersonAdapter.returnOrder().get(i) + 1);
//                    }
//                }
//                for (int i = 0; i < GroupGoodId.size(); i++) {
//                    if (GroupGoodId.contains(keyValuePairsOne.get(position).getIntID())) {         // 判断当前点击商品ID是否在组合优惠里面
//                        for (int j = 0; j < keyValuePairsOne.size(); j++) {
//                            if (j == position) {                                    // 当前点击与j值相同不用比较 属于同一个商品
//
//                            } else if (GroupGoodId.contains(keyValuePairsOne.get(j).getIntID()) && keyValuePairsOne.get(j).getCommPrice() != 0) {     // 判断组合优惠商品数量是都不等于0
//                                if (keyValuePairsOne.get(j).getCommPrice().equals(keyValuePairs.get(position).getCommPrice())) {
//                                    PriceSign = keyValuePairsOne.get(position).getCommPrice();
//                                } else if (keyValuePairsOne.get(j).getCommPrice() > keyValuePairsOne.get(position).getCommPrice()) {
//                                    PriceSign = keyValuePairsOne.get(position).getCommPrice();
//                                } else if (keyValuePairsOne.get(j).getCommPrice() < keyValuePairsOne.get(position).getCommPrice()) {
//                                    PriceSign = keyValuePairsOne.get(j).getCommPrice();
//                                }
//                                runOnUiThread(() -> textViewShow.setText(PriceSign.toString()));
//                            }
//                        }
//                    }
//                }
//                RequestPrcice();
//                textViewOrder.setText(NumberOrder().toString());
//                adapter.notifyDataSetChanged();
//                shoppingAdapterOne.notifyDataSetChanged();
//            }else {
//                showOneDialog("商品数已达到最大出货量，无法添加");    // 最大商品弹窗
//            }
//        }else {
//            showOneDialog("单个货道已经到达最大出货量，无法添加");
//        }
//    }

    /**
     * @param position
     * @ 打包详情页数据下发
     */
    private String PackagingPage(int position) {
        JSONObject jsonObject = new JSONObject();
//        try {
        if (keyValuePairs.size() != 6){
            Popup("设备异常，请重试/Equipment is abnormal. Please try again");
            return null;
        }
        jsonObject.put("stringName", keyValuePairs.get(position).getStringName());
        jsonObject.put("stringPrice", keyValuePairs.get(position).getStringPrice());
        jsonObject.put("intID", keyValuePairs.get(position).getIntID());
        jsonObject.put("stringUrl", keyValuePairs.get(position).getStringUrl());
        jsonObject.put("commPrice", keyValuePairs.get(position).getCommPrice());
        jsonObject.put("quality", keyValuePairs.get(position).getQuality());
        jsonObject.put("feature", keyValuePairs.get(position).getFeature());
        jsonObject.put("desc", keyValuePairs.get(position).getDesc());
        jsonObject.put("lock", keyValuePairs.get(position).isLock());
        jsonObject.put("chans", keyValuePairs.get(position).getChans());
        jsonObject.put("position", position);
//        }catch (IndexOutOfBoundsException e){
//            e.printStackTrace();
//            Log.d(TAG, "PackagingPage: " + jsonObject.toString());
//            Popup("数组越界");
//        }
//        Log.i(TAG, "Interface PackagingPage: -> " + jsonObject.toJSONString());
////        logger.info("Interface PackagingPage: -> " + jsonObject.toJSONString());
        return jsonObject.toJSONString();
    }
//
//    /**
//     * @ 请求价格
//     */
//    private void RequestPrcice() {
//        runOnUiThread(() -> buttonShopping.setClickable(false));                // 禁用支付按钮，请求到价格之后开启支付按钮
//        ArrayList<String> goodIdList = new ArrayList<>();
//        ArrayList<String> numList = new ArrayList<>();
//        List<Integer> integers = PersonAdapter.returnOrder();
//        Log.d(TAG, "Interface RequestPrcice: -> " + integers);
////        logger.debug("Interface RequestPrcice: -> " + integers);
//        for (int i = 0; i < integers.size(); i++) {
//            if (integers.get(i) != 0) {
//                goodIdList.add(keyValuePairs.get(i).getIntID());
//                numList.add(integers.get(i).toString());
//            }
//        }
//        String string = JsonBuild.AssemJson(goodIdList, numList);
//        String string1 = MQTTService.returnPriceLink() + "?deviceId=" + MQTTService.returnID();
//        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), string);
//        OkHttpPost.sendOkHttpRequest(string1, requestBody, new okhttp3.Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String string2 = response.body().string();
////                StringUtils.resolve();
//                if (string2.length() != 0) {
//                    runOnUiThread(() -> textViewShoppingPrice.setText(PriceData(string2)));   // 数据返回修改价格
//                    runOnUiThread(() -> buttonShopping.setClickable(true));
//                    Log.i(TAG, "Interface onResponse: -> " + string2);
////                    logger.info("Interface onResponse: -> " + string2);
//                }
//            }
//        });
//    }
//
//    /**
//     * @param str
//     * @ 解析价格数据
//     * @return 商品总价
//     */
//    private String PriceData(String str) {
//        String actualPrice = null;
//        Gson gson1 = new Gson();
//        try {
//            priceJson = gson1.fromJson(str, PriceJson.class);
//            if (priceJson.getCode().equals("200") && priceJson.getContent() != null){
//                actualPrice = priceJson.getContent().getActualPrice();
//            }else {
//                if (priceJson.getCode().equals("500")){
//                    Message message = new Message();
//                    handlerABNfive.sendMessage(message);
//                }else {
//                    Message message = new Message();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("Message", priceJson.getMessage());      // 传递自定义异常数据
//                    message.setData(bundle);
//                    handlerABNcustom.sendMessage(message);
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            Popup("没有请求到价格");
//        }
//
//        return actualPrice;
//    }

    /**
     * @ 启动Item初始化
     */
//    private void RequestInven(int position) {
//        Message message = new Message();
//        message.what = position;
//        handlerRecy.sendMessage(message);   // 启动Item初始化
//    }

    /**
     * @ 启动Item初始化
     * */
//    @SuppressLint("HandlerLeak")
//    private Handler handlerRecy = new Handler() {
//        public void handleMessage(Message msg) {
//            ItemShow(msg.what);      // 传递参数点击item下标， 商品库存数量
//        }
//    };

    /**
     * @ 解析商品库存数据
     * @ return 商品库存量
     */
//    private int AnalysisInven(String str) {
//        Gson gson = new Gson();
//        inventory = gson.fromJson(str, Inventory.class);
//        return inventory.getContent();
//    }
//
//    /**
//     * @ item数据显示
//     */
//    @SuppressLint("SetTextI18n")
//    private void ItemShow(int position) {
//        try {
//            if (PersonAdapter.returnOrder().get(position) < 4){                                         // 限制单个通道只能购买四个商品
//                Log.d(TAG, "ItemShow: " + PersonAdapter.returnOrder().get(position));
//                if (PersonAdapter.returnOrder().get(position) < 10 && PersonAdapter.NumOrder() < 10) {                              // 判断最大出货量
//                    if (Integer.parseInt(keyValuePairs.get(position).getQuality()) > PersonAdapter.returnOrder().get(position)) {        // 判断商品库存量是否小于购物车商品数量
//                        adapter.notifyDataSetChanged();   // 刷新RecyclerView列表
//                        startAnimation(RecyViewSite);                 // 启动动画
//                        PersonAdapter.returnIsclick().set(position, true);       // 指定下标设定Boolean
//                        PersonAdapter.returnOrder().set(position, PersonAdapter.returnOrder().get(position) + 1);   // 添加商品数量
//                        keyValuePairs.get(position).setCommPrice(keyValuePairs.get(position).getCommPrice() + 1);
//                        for (int i = 0; i < GroupGoodId.size(); i++) {
//                            if (GroupGoodId.contains(keyValuePairs.get(position).getIntID())) {         // 判断当前点击商品ID是否在组合优惠里面
//                                for (int j = 0; j < keyValuePairs.size(); j++) {
//                                    if (j == position) {                                    // 当前点击与j值相同不用比较 属于同一个商品
//
//                                    } else if (GroupGoodId.contains(keyValuePairs.get(j).getIntID()) && keyValuePairs.get(j).getCommPrice() != 0) {     // 判断组合优惠商品数量是都不等于0
//                                        if (keyValuePairs.get(j).getCommPrice().equals(keyValuePairs.get(position).getCommPrice())) {
//                                            PriceSign = keyValuePairs.get(position).getCommPrice();
//                                        } else if (keyValuePairs.get(j).getCommPrice() > keyValuePairs.get(position).getCommPrice()) {
//                                            PriceSign = keyValuePairs.get(position).getCommPrice();
//                                        } else if (keyValuePairs.get(j).getCommPrice() < keyValuePairs.get(position).getCommPrice()) {
//                                            PriceSign = keyValuePairs.get(j).getCommPrice();
//                                        }
//                                        //                            Log.d(TAG, "ItemShow: " + i + " " + j + " " + PriceSign);
//                                        buttonAdd.setVisibility(View.GONE);                                     // 隐藏组合优惠界面的点击按钮
//                                        fontIconViewAdd.setVisibility(View.VISIBLE);                            // 显示组合优惠文字图标
//                                        fontIconViewReduce.setVisibility(View.VISIBLE);
//                                        textViewShow.setVisibility(View.VISIBLE);                               // 显示组合优惠数量
//                                        viewBackGround.setVisibility(View.VISIBLE);                             // 边框背景
//                                        runOnUiThread(() -> textViewShow.setText(PriceSign.toString()));        // 组合商品数量显示
//                                    }
//                                }
//                            }
//                        }
//
//                    } else {
//                        showInventoryDialog();      // 库存不足显示
//                    }
//                    RequestPrcice();
////                    constraintLayoutShopping.setVisibility(View.VISIBLE);                       // 购物车界面显示
//                    textViewShoppingPrice.setVisibility(View.VISIBLE);
//                    textViewOrder.setVisibility(View.VISIBLE);
//                    textViewOrder.setBackgroundResource(rectangle7);                            // 购物车背景
//                    textViewOrder.setText(NumberOrder().toString());                            // 商品总数
//                } else {
//                    showOneDialog("商品数已达到最大出货量，无法添加");    // 最大商品弹窗
//                }
//            }else {
//                showOneDialog("单个货道已经到达最大出货量，无法添加");
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            Popup("待定错误");
//        }
//    }

    /**
     * @ 库存不足弹窗显示
     */
    private void showInventoryDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainInterFace.this, R.style.dialog).create();
        alertDialog.setTitle("商品库存不足，无法添加");
        alertDialog.show();
        new Handler().postDelayed(() -> alertDialog.dismiss(), 1000);
    }

//    /**
//     * @ 计算商品总数
//     */
//    private Integer NumberOrder() {
//        int num = 0;
//        List<Integer> list = PersonAdapter.returnOrder();
//        for (int i = 0; i < list.size(); i++) {
//            if (list.get(i) != 0) {
//                num += list.get(i);
//            }
//        }
//        return num;
//    }
    /**
     * @ 解析商品数据（新）
     * */
    private void ProductDataNews(String string){
        Gson gson = new Gson();
        List<KeyValuePairNews> keyValuePairNews = gson.fromJson(string, new TypeToken<List<KeyValuePairNews>>(){}.getType());
        Log.d(TAG, "ProductDataNews: " + keyValuePairNews.toString());
        AisleNumber(keyValuePairNews);
    }

    /**
     * @param str
     * @ 解析商品数据 异常处理
     */
    public void ProductData(String str) {
        Log.d(TAG, "ProductData: one ->" + str);
//        StringUtils.resolve(TAG, "ProductData: one ->", str);
        try {
            appList = gson.fromJson(str, DeviceData.class);
            MainService.deviceId = appList.getContent().getDeviceId();
            if (appList.getCode().equals("200") && appList.getContent() != null){
                comGoods = appList.getContent().getGoods();
                detAils = appList.getContent().getDetails();
//                comGoods = clearData(comGoods);
                Log.d(TAG, "ProductData: " + comGoods.toString());
//                StringUtils.resolve(TAG, "ProductData:", comGoods.toString());
//                AisleNumber(clearData(comGoods));
//                AisleNumber(comGoods);
            }else {
                if (appList.getCode().equals("500")){
                    Message message = new Message();
                    handlerABNfive.sendMessage(message);
                }else {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("Message", appList.getMessage());      // 传递自定义异常数据
                    message.setData(bundle);
                    handlerABNcustom.sendMessage(message);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "ProductData: " + "解析异常");
            if ("821".equals(appList.getCode())){
                Popup("设备被锁定，请解锁/Please unlock the device if it is locked");
            }
        }
    }

    /**
     * @ 删除请求数据相同的数据
     * */
    private List<Goods> clearData(List<Goods> goods){
        for (int i = 0; i < goods.size() - 1; i++){
            for (int j = goods.size() - 1; j > i; j--){
                if (goods.get(j).getId().equals(goods.get(i).getId())){
                    goods.remove(j);
                }
            }
        }
        return goods;
    }

    /**
     * @ 判断组合出货是否有停用货道与无货
     * */
    private boolean judgeLock(ArrayList<String> groupId, List<Goods> comgoods){
        boolean chansLock = false;
        for (int i = 0; i < groupId.size(); i++){
            for (int j = 0; j < comgoods.size(); j++){
//                Log.d(TAG, "judgeLock: " + groupId.get(i));
                if (groupId.get(i).equals(comgoods.get(j).getId())){       // 判断组合优惠里面是否有锁定通道的与无货的
                    if (comgoods.get(j).getLock() || comgoods.get(j).getQuality().equals("0")){
//                        Log.d(TAG, "judgeLock: " + comgoods.get(i).getId() + comgoods.get(j).getQuality());
                        chansLock = true;
                    }
                }
            }
        }
        return chansLock;
    }

    /**
     * @ 异常500处理
     * */
    @SuppressLint("HandlerLeak")
    private Handler handlerABNfive = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            Log.d(TAG, "handleMessage: " +"500异常");
            Popup("设备异常，请重试/Equipment is abnormal. Please try again");
        }
    };

    private static boolean temp = true;

    /**
     * @ 自定义异常处理
     * */
    @SuppressLint("HandlerLeak")
    private Handler handlerABNcustom = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (temp) {
                temp = false;
                PopupCustom(msg.getData().getString("Message"));
            }
        }
    };

    /**
     * @ 弹窗显示自定义异常
     * */
    private void PopupCustom(String message){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainInterFace.this);
        alertDialog.setTitle(message);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
//            Intent intent = new Intent();
//            setResult(RESULT_OK, intent);
            StartActivity();
//            finish();
            EmptyTimer();
            getTimer.schedule(getTimerTaskCount, 0, 1000);
        });
        alertDialog.show();
        new Thread(() -> {                  // 一秒钟只能弹窗一个
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                temp = true;
            }
        }).start();

//        dialog = alertDialog.create();
    }

//    @Override
//    protected void onDestroy() {
//        if (dialog != null) {
//            dialog.dismiss();
//        }
//        super.onDestroy();
//    }

//    /**
//     * @ 判断组合优惠商品是否在商品列表里面
//     */
//    private boolean judgeGoods(List<Details> details, List<Goods> goods) {
//        ArrayList<String> strings = new ArrayList<>();
//        ArrayList<String> strings1 = new ArrayList<>();
//        for (int i = 0; i < details.size(); i++) {
//            strings.add(details.get(i).getGoodsId());
//        }
//        for (int i = 0; i < goods.size(); i++) {
//            strings1.add(goods.get(i).getId());
//        }
//        if (!strings1.containsAll(strings)) {
//            return false;
//        }
//        return true;
//    }

    /**
     * @ 隐藏组合优惠
     */
//    @SuppressLint("HandlerLeak")
//    private Handler Concealhandler = new Handler() {
//        public void handleMessage(Message msg) {
//            constraintLayout.setVisibility(View.GONE);
//        }
//    };

    /**
     * @ 字符串转为jsonArray数组
     */
    private List<Img> jsonArray(String img) {
//        Log.d(TAG, "jsonArray: " + img);
        Gson gson = new Gson();
        List<Img> imgs = gson.fromJson(img, new TypeToken<List<Img>>() {
        }.getType());
//        Log.d(TAG, "jsonArray: " + imgs.get(0).getUrl());
        return imgs;
    }

    /**
     * @param string
     * @ 添加商品列表数据
     */
    public void AisleNumber(List<KeyValuePairNews> string) {
//        List<Integer> integers = Arrays.asList(3, 4, 5);
        boolean lock = false;
        for (int i = 0; i < string.size(); i++) {
//            List<Img> imgs = jsonArray(string.get(i).getImg());
//            for (int j = 0; j < string.get(i).getChans().size(); j++){
//                Log.d(TAG, "AisleNumber: " + string.size());
            if (string.get(i).getLock() == 0){
                lock = false;
            }else {
                lock = true;
            }
            keyValuePairs.add(new KeyValuePair(string.get(i).getChans(),
                    string.get(i).getIntID(),
                    string.get(i).getStringName(),
                    string.get(i).getStringPrice(),
                    string.get(i).getStringUrl(),
                    false,
                    0,
                    string.get(i).getQuality(),
                    lock,
                    string.get(i).getFeature(),
                    string.get(i).getDesc()
            ));

//            }
        }
//        for (KeyValuePair pair : keyValuePairs) {
//            Log.d(TAG, "Interface -> AisleNumber: "
//                    + pair.getChans() + " "
//                    + pair.getIntID() + " "
//                    + pair.getStringName() + " "
//                    + pair.getStringPrice() + " "
//                    + pair.getStringUrl());
//        }
        recyCler();
        Log.i(TAG, "Interface AisleNumber: -> " + keyValuePairs.toString());
//        logger.info("Interface AisleNumber: -> " + keyValuePairs.toString());
    }

//    // 添加单个优惠数据
//    public void OneSingleDiscount(List<Details> details) {
//        for (int i = 0; i < details.size(); i++) {
//            keyDetails.add(new KeyDetail(details.get(i).getGoodsId()));
//            for (int j = 0; j < keyValuePairs.size(); j++) {
//                if (keyDetails.get(i).goodId.equals(keyValuePairs.get(j).getIntID())) {
//                    PersonAdapter.returnIsOneDiscount().set(j, true);
//                    adapter.notifyItemChanged(j);
//                }
//            }
//        }
//    }
//
//    /**
//     * @param details
//     * @ 添加组合优惠数据
//     */
//    public void CombinationPreferentialData(List<Details> details) {
////        Log.d(TAG, "CombinationPreferentialData: " + keyValuePairs.toString());
//        for (int i = 0; i < details.size(); i++) {
//            keyDetails.add(new KeyDetail(details.get(i).getGoodsId()));
//            Log.d(TAG, "Interface CombinationPreferentialData: -> " + keyDetails.toString());
////            logger.debug("Interface CombinationPreferentialData: -> " + keyDetails.toString());
//            strOriginalPrice.add(details.get(i).getNowPrice());
//            for (int j = 0; j < keyValuePairs.size(); j++) {
//                if (keyDetails.get(i).goodId.equals(keyValuePairs.get(j).getIntID())) {
//                    strName.add(keyValuePairs.get(j).getStringName());
//                    strURL.add(keyValuePairs.get(j).getStringUrl());
//                    strPrice.add(keyValuePairs.get(j).getStringPrice());
//                }
//            }
//
//        }
//        if (keyValuePairs.size() != MainActivity.returnChansNum()){     // 请求数据与设定通道数不相等退出详情页重新请求数据
//            Popup("请求得到的数据与通道数不相等");
//        }else {
//            endTime = details.get(0).getTodayPreferentialInfo().getEndTime();
//            if (timer == null){
//                startTimerOne();
//                Message message = new Message();
//                handler.sendMessage(message);
//            }
//        }
//    }

    /**
     * @ 加载显示商品列表
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {
            Log.d(TAG, "handleMessage: " + strName.toString());
            if (strName.size() != 0) {
                SpecialDiscount();
                textViewTwoPrice.setText(strPrice.get(0));
                textViewOneName.setText(strName.get(0));
                textViewThreeName.setText(strName.get(1));
                textViewFourPrice.setText(strPrice.get(1));
                Glide.with(MainInterFace.this).load(strURL.get(0)).into(imageViewOne);
                Glide.with(MainInterFace.this).load(strURL.get(1)).into(imageViewTwo);
                textViewOriginal.setText(stringArrayListPrice.get(0));
                fontIconViewCurrent.setText(stringArrayListPrice.get(1));
                textViewDiscount.setText(stringArrayListPrice.get(2));
                Log.d(TAG, "Interface handleMessage: -> " + strPrice.toString());
//                logger.debug("Interface handleMessage: -> " + strPrice.toString());
            }
        }
    };

    /**
     * @ 计算价格优惠折扣
     */
    private void SpecialDiscount() {
        String stringOneOriginal = strPrice.get(0);
        String stringTwoOriginal = strPrice.get(1);
        String stringOneCurrent = strOriginalPrice.get(0);
        String stringTwoCurrent = strOriginalPrice.get(1);
        if (stringOneCurrent != null){
            double original = Double.valueOf(stringOneOriginal) + Double.valueOf(stringTwoOriginal);
            double current = Double.valueOf(stringOneCurrent) + Double.valueOf(stringTwoCurrent);
            double Discount = current / original;
            DecimalFormat decimalFormat = new DecimalFormat("#.00");
            String DiscountEarned = decimalFormat.format(Discount).replaceAll("[.]", "");
            DecimalFormat decimalFormat1 = new DecimalFormat("0.00");           // 保留两位小数
            String stringOriginal = decimalFormat1.format(original);
            String stringCurrent = decimalFormat1.format(current);
            Log.d(TAG, "Interface SpecialDiscount: -> " + stringOriginal + " " + stringCurrent);
//            logger.debug("Interface SpecialDiscount: -> " + stringOriginal + " " + stringCurrent);
            stringArrayListPrice.add(stringOriginal);
            stringArrayListPrice.add(stringCurrent);
            stringArrayListPrice.add(DiscountEarned + "%");
            Log.d(TAG, "Interface SpecialDiscount: -> " + stringArrayListPrice.toString());
//            logger.debug("Interface SpecialDiscount: -> " + stringArrayListPrice.toString());
        }else {                 // 优惠价格为空  设备离线  组合优惠没有设置价格
            Message message = new Message();
            handlerABNfive.sendMessage(message);
        }

    }

    /**
     * @ 计算显示组合优惠时间
     */
    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    private void TimeDiff() {
        DateFormat dateFormat = new SimpleDateFormat("kk:mm:ss"); // 24小时制
        String date = dateFormat.format(new java.util.Date());
        try {
            Date startTimeOne = dateFormat.parse(date);
            Date endTime1 = dateFormat.parse(endTime);
//            boolean flag = belongCalendar(startTimeOne,endTime1);
//            boolean bool = WeekContrast();
            Message message = new Message();
            handlerGroup.sendMessage(message);
            long diff = endTime1.getTime() - startTimeOne.getTime();
            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
            long sencond = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / 1000;
//            textViewDays.setText(days + "");
            textViewHours.setText(hours + "");
            textViewMinutes.setText(minutes + "");
            textViewSencond.setText(sencond + "");


        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     * @ 显示组合优惠
     */
    @SuppressLint("HandlerLeak")
    private Handler handlerGroup = new Handler() {
        public void handleMessage(Message message) {
            constraintLayout.setVisibility(View.VISIBLE);
        }
    };


    /**
     * @ 优惠倒计时
     */
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Message message = new Message();
            getHandler.sendMessage(message);
        }
    };

    /**
     * @ 组合商品优惠倒计时
     * */
    @SuppressLint("HandlerLeak")
    private Handler getHandler = new Handler() {
        public void handleMessage(Message msg) {
            TimeDiff();
        }
    };

    /**
     * @ 对比商品库存（组合优惠商品）（点击按钮不同启动的不同的UI变化）
//     */
//    private void RequestGroupShipMent() {
//        for (int i = 0; i < keyValuePairs.size(); i ++){        // 取出组合优惠的商品库存
//            for (int j = 0; j < detAils.size(); j++){
//                Log.d(TAG, "RequestGroupShipMent: " + detAils.size() + " " + j);
//                if (keyValuePairs.get(i).getIntID().equals(detAils.get(j).getGoodsId())){
//                    ShipmentGroup[j] = Integer.parseInt(keyValuePairs.get(i).getQuality());
//                    isLock[j] = keyValuePairs.get(i).isLock();
//                }
//            }
//        }
//        for (int i = 0; i < isLock.length; i++){
//            if (isLock[i]){
//                getIsLock = true;
//            }
//        }
//        ShimentNumber[0] = TakeOutShipMent(detAils.get(detAils.size() - detAils.size()).getGoodsId());
//        ShimentNumber[1] = TakeOutShipMent(detAils.get(detAils.size() - 1).getGoodsId());
//        Message message = new Message();
//        handlerShipMent.sendMessage(message);
//    }

//    /**
//     * 取出组合优惠商品数量
//     * */
//    private int TakeOutShipMent(String GoodId){
//        int num = 0;
//        for (int i = 0; i < keyValuePairs.size(); i++){
//            if (GoodId.equals(keyValuePairs.get(i).getIntID())){
//                num = keyValuePairs.get(i).getCommPrice();
//            }
//        }
//        return num;
//    }

    /**
     * @ 请求商品库存（组合优惠商品）（点击按钮不同启动的不同的UI变化）
     */
//    private void RequestGroupShipMentOne() {
//        for (int i = 0; i < keyValuePairs.size(); i ++){            // 取出组合优惠的商品库存
//            for (int j = 0; j < detAils.size(); j++){
//                if (keyValuePairs.get(i).getIntID().equals(detAils.get(j).getGoodsId())){
//                    ShipmentGroup[j] = Integer.parseInt(keyValuePairs.get(i).getQuality());
//                }
//            }
//        }
//        ShimentNumber[0] = TakeOutShipMent(detAils.get(detAils.size() - detAils.size()).getGoodsId());
//        ShimentNumber[1] = TakeOutShipMent(detAils.get(detAils.size() - 1).getGoodsId());
//        Message message = new Message();
//        handlerShipMentOne.sendMessage(message);
//    }

    /**
     * @ 启动弹窗提示
     */
//    @SuppressLint("HandlerLeak")
//    private Handler handlerShipMent = new Handler() {
//        public void handleMessage(Message msg) {
//            Combination();
//        }
//    };

    /**
     * @ 启动弹窗显示
     */
//    @SuppressLint("HandlerLeak")
//    private Handler handlerShipMentOne = new Handler() {
//        public void handleMessage(Message msg) {
//            CombinationOne();
//        }
//    };

    /**
     * @ 链接buttonAdd点击事件
     */
    @SuppressLint("SetTextI18n")
//    private void Combination() {
//        Log.d(TAG, "Combination: " + getIsLock);
//        if (!getIsLock){
//            if (PriceSign < 4 && ShimentNumber[0] < 4 && ShimentNumber[1] < 4){
//                if (NumberOrder() < 9) {
//                    if (ShipmentGroup[0] > ShimentNumber[0] &&
//                            ShipmentGroup[1] > ShimentNumber[1]) {   // 判断商品库存
//                        int[] startPostion = new int[2];
//                        buttonAdd.getLocationInWindow(startPostion);  // 计算按钮位置
//                        startAnimation(startPostion);
//                        Log.d(TAG, "Interface -> Combination PriceSign: " + PriceSign);
//                        for (int i = 0; i < detAils.size(); i++) {
//                            for (int j = 0; j < keyValuePairs.size(); j++) {
//                                if (keyDetails.get(i).goodId.equals(keyValuePairs.get(j).getIntID())) {
//                                    PersonAdapter.returnIsclick().set(j, true);
//                                    PersonAdapter.returnOrder().set(j, PersonAdapter.returnOrder().get(j) + 1);
//                                    keyValuePairs.get(j).setCommPrice(keyValuePairs.get(j).getCommPrice() + 1);
////                            adapter.notifyItemChanged(j);
//                                    adapter.notifyDataSetChanged();
//                                }
//                            }
//                        }
//                        PriceSign++;
////                    constraintLayoutShopping.setVisibility(View.VISIBLE);
//                        textViewShoppingPrice.setVisibility(View.VISIBLE);
//                        textViewOrder.setVisibility(View.VISIBLE);
//                        buttonAdd.setVisibility(View.GONE);
//                        fontIconViewAdd.setVisibility(View.VISIBLE);
//                        fontIconViewReduce.setVisibility(View.VISIBLE);
//                        viewBackGround.setVisibility(View.VISIBLE);
//                        textViewShow.setVisibility(View.VISIBLE);
//                        textViewShow.setText(PriceSign.toString());
//                        textViewOrder.setBackgroundResource(rectangle7);
//                        RequestPrcice();   // 请求价格
//                        textViewOrder.setText(NumberOrder().toString());
//                    } else {
//                        showInventoryDialog();          // 库存不足显示
//                    }
//                } else {
//                    showOneDialog("商品数已达到最大出货量，无法添加");                    // 最大出货量
//                }
//            }else {
//                showOneDialog("单个货道已经到达最大出货量，无法添加");
//            }
//
//        }else {
//            buttonAdd.setClickable(false);
//        }
//    }

    /**
     * @ 链接fontIconViewAdd点击事件
     */
//    @SuppressLint("SetTextI18n")
//    private void CombinationOne() {
//        if (PriceSign < 4 && ShimentNumber[0] < 4 && ShimentNumber[1] < 4){
//            if (NumberOrder() < 9) {           // 判断最大出货量
//                if (ShipmentGroup[0] > ShimentNumber[0] &&
//                        ShipmentGroup[1] > ShimentNumber[1]) {          // 判断商品库存
//                    int[] startPostion = new int[2];
//                    fontIconViewAdd.getLocationInWindow(startPostion);  // 计算按钮位置
//                    startAnimation(startPostion);
//                    PriceSign++;
//                    textViewShow.setText(PriceSign.toString());
//                    for (int i = 0; i < detAils.size(); i++) {
//                        for (int j = 0; j < keyValuePairs.size(); j++) {
//                            if (keyDetails.get(i).goodId.equals(keyValuePairs.get(j).getIntID())) {
//                                PersonAdapter.returnIsclick().set(j, true);
//                                PersonAdapter.returnOrder().set(j, PersonAdapter.returnOrder().get(j) + 1);
//                                keyValuePairs.get(j).setCommPrice(keyValuePairs.get(j).getCommPrice() + 1);
////                            adapter.notifyItemChanged(j);
//                                adapter.notifyDataSetChanged();
//                            }
//                        }
//                    }
//                } else {
//                    showInventoryDialog();          // 库存不足显示
//                }
//                RequestPrcice();   // 请求价格
//                textViewOrder.setText(NumberOrder().toString());
//            }else {
//                showOneDialog("商品数已达到最大出货量，无法添加");                // 最大出货量
//            }
//        }else {
//            showOneDialog("单个货道已经到达最大出货量，无法添加");
//        }
//
//    }

    /**
     * @ 链接fontIconViewReduce点击事件
     * */
//    @SuppressLint("SetTextI18n")
//    private void CombinationTwo(){
//        if (PriceSign != 0){
//            PriceSign --;
//            textViewShow.setText(PriceSign.toString());
//            for (int i = 0; i < detAils.size(); i++) {
//                for (int j = 0; j < keyValuePairs.size(); j++) {
//                    if (keyDetails.get(i).goodId.equals(keyValuePairs.get(j).getIntID())) {
//                        PersonAdapter.returnIsclick().set(j, true);
//                        PersonAdapter.returnOrder().set(j, PersonAdapter.returnOrder().get(j) - 1);
//                        keyValuePairs.get(j).setCommPrice(keyValuePairs.get(j).getCommPrice() - 1);
//                        adapter.notifyDataSetChanged();
//                    }
//                }
//            }
//            if (PriceSign == 0){
//                for (int i = 0; i < detAils.size(); i++) {
//                    for (int j = 0; j < keyValuePairs.size(); j++) {
//                        if (keyDetails.get(i).goodId.equals(keyValuePairs.get(j).getIntID()) && keyValuePairs.get(j).getCommPrice().equals(0)) {
//                            PersonAdapter.returnIsclick().set(j, false);
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                }
//                buttonAdd.setVisibility(View.VISIBLE);
//                fontIconViewAdd.setVisibility(View.GONE);
//                fontIconViewReduce.setVisibility(View.GONE);
//                viewBackGround.setVisibility(View.GONE);
//                textViewShow.setVisibility(View.GONE);
//                if (NumberOrder() == 0){
////                    constraintLayoutShopping.setVisibility(View.GONE);
//                    textViewShoppingPrice.setVisibility(View.GONE);
//                    textViewOrder.setVisibility(View.GONE);
//                }else {
//                    RequestPrcice();   // 请求价格
//                    textViewOrder.setText(NumberOrder().toString());
//                }
//            }else {
//                RequestPrcice();   // 请求价格
//                textViewOrder.setText(NumberOrder().toString());
//            }
//        }
//    }

    /**
     * @ 购物车点击事件
     * */
//    public void onClickShopping(View v){
//        switch (v.getId()){
//            case R.id.buttonShopping:
//                if (PersonAdapter.NumOrder() != 0){
//                    stopautoBannerTimer();
//                    stopTimer();
//                    Intent intent = new Intent(MainInterFace.this, MainInterShopping.class);
//                    intent.putExtra("KeyValuePairs", AssemblyData());
////                Log.d(TAG, "onClick: " + AssemblyData());
//                    startActivityForResult(intent, 1);
//                }else {
//                    showOneDialogAdd("请先添加商品");
//                }
//                break;
//            default:
//                for (int i = 0; i < keyValuePairs.size(); i++){
//                    if (keyValuePairs.get(i).getCommPrice() != 0){
//                        keyValuePairsOne.add(keyValuePairs.get(i));
//                        shoppingAdapterOne.notifyDataSetChanged();
//                    }
//                }
//                ViewGroup.LayoutParams lp = recyclerViewShopping.getLayoutParams();
//                if (keyValuePairsOne.size() > 4) {          // 设定购物车超过四个就固定RecyclerView高度 滑动展示
//                    lp.height = DensityUtil.dip2px(this,140 * 4);
//                } else {
//                    lp.height = DensityUtil.dip2px(this,140 * keyValuePairsOne.size());
//                }
//                if (keyValuePairsOne.size() != 0){
//                    recyclerViewShopping.setLayoutParams(lp);
//                    recyclerView.setNestedScrollingEnabled(false);
//                    recyclerViewShopping.setVisibility(View.VISIBLE);
//                    constraintLayoutShoppingOne.setVisibility(View.VISIBLE);
//                    viewShopping.setVisibility(View.VISIBLE);
////                    textViewShoppingPrice.setClickable(false);
//                    ClickableStop(false);
//                }else {
//                    showOneDialogAdd("请先添加商品");
//                }
//                break;
//        }
//
//    }

    /**
     * @ 商品添加提示
     * */
//    @SuppressLint("ResourceAsColor")
//    private void showOneDialogAdd(String string){
//        AlertDialog alertDialog = new AlertDialog.Builder(MainInterFace.this, R.style.dialogTwo).create();
////        alertDialog.setTitle("商品已添加到购物车，请回主页查看");
//        LayoutInflater inflater = LayoutInflater.from(this);
//        View v = inflater.inflate(R.layout.update_manage_dialog, null);
//        TextView textView = v.findViewById(R.id.textViewdialog);
//        textView.setText(string);
//        alertDialog.show();
//        Objects.requireNonNull(alertDialog.getWindow()).setContentView(v);
//        new Handler().postDelayed(alertDialog::dismiss, 1000);
//
//    }

    /**
     * @ 禁用(打开)点击事件
     * */
    private void ClickableStop(boolean item){
        constraintLayoutShopping.setClickable(item);
        fontIconViewShopping.setClickable(item);
        textViewOrder.setClickable(item);
        textViewfont2.setClickable(item);
        textView10.setClickable(item);
        textViewShoppingPrice.setClickable(item);
    }

    /**
     * @ */

    /**
     * @ 点击监听事件
     * */
    @SuppressLint({"SetTextI18n", "ResourceType"})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.buttonAdd:
//                countDownTimeOne = 40;
//                RequestGroupShipMent();
//                break;
//            case R.id.fontIconViewAdd:
//                countDownTimeOne = 40;
//                RequestGroupShipMentOne();
//                break;
//            case R.id.fontIconViewReduce:
//                countDownTimeOne = 40;
//                CombinationTwo();
//                break;
//            case R.id.textViewDelete:
//            case R.id.DeletefontIcon:
//                countDownTimeOne = 40;
//                for (int i = 0; i < keyValuePairs.size(); i++){
//                    keyValuePairs.get(i).setCommPrice(0);
//                    PersonAdapter.returnIsclick().set(i, false);
//                    PersonAdapter.returnOrder().set(i, 0);
//                }
////                for (int j = 0; j < PersonAdapter.returnIsclick().size(); j++){
////                    PersonAdapter.returnIsclick().set(j, false);
////                }
//                keyValuePairsOne.clear();
////                for (int z = 0; z < PersonAdapter.returnOrder().size(); z++){
//////                    PersonAdapter.returnOrder().set(z, 0);
//////                }
//                recyclerViewShopping.setVisibility(View.GONE);
//                constraintLayoutShoppingOne.setVisibility(View.GONE);
////                constraintLayoutShopping.setVisibility(View.GONE);
//                textViewShoppingPrice.setVisibility(View.GONE);
//                textViewOrder.setVisibility(View.GONE);
//                viewShopping.setVisibility(View.GONE);
//                fontIconViewAdd.setVisibility(View.GONE);
//                fontIconViewReduce.setVisibility(View.GONE);
//                textViewShow.setVisibility(View.GONE);
//                viewBackGround.setVisibility(View.GONE);
//                buttonAdd.setVisibility(View.VISIBLE);
//                ClickableStop(true);
//                PriceSign = 0;
//                adapter.notifyDataSetChanged();
//                break;
//            case R.id.fontIconViewDown:
//                countDownTimeOne = 40;
//                recyclerViewShopping.setVisibility(View.GONE);
//                constraintLayoutShoppingOne.setVisibility(View.GONE);
////                constraintLayoutShopping.setVisibility(View.GONE);
//                viewShopping.setVisibility(View.GONE);
//                keyValuePairsOne.clear();
//                ClickableStop(true);
//                break;
        }
    }

    /**
     * @ 商品无法添加弹窗显示
     * */
//    private void showOneDialog(String str){
//        AlertDialog alertDialog = new AlertDialog.Builder(MainInterFace.this, R.style.dialogTwo).create();
//        LayoutInflater inflater = LayoutInflater.from(this);
//        View v = inflater.inflate(R.layout.update_manage_dialog, null);
//        TextView textView = v.findViewById(R.id.textViewdialog);
//        textView.setText(str);
//        alertDialog.show();
//        Objects.requireNonNull(alertDialog.getWindow()).setContentView(v);
//        new Handler().postDelayed(alertDialog::dismiss, 1000);
//    }
//
//    private String AssemblyDataOne(){
//        JSONObject jsonObject = new JSONObject();
//        JSONArray jsonArray = new JSONArray();
//        JSONArray jsonArray1 = new JSONArray();
//
//        return jsonObject.toJSONString();
//    }

    /**
     * @ 打包支付页数据下发
     * */
//    public String AssemblyData() {
//        int remainder = 0;
//        String string = null;
//        ArrayList<String> GoodId = new ArrayList<>();
//        ArrayList<String> GoodIdGroup = new ArrayList<>();
//        JSONObject jsonObject = new JSONObject();
//        JSONArray jsonArray = new JSONArray();
//        JSONArray jsonArray1 = new JSONArray();
//        JSONObject jsonObjectOneGroup = new JSONObject();
//        try {
//            jsonObject.put("ShoppingGroup", jsonObjectOneGroup);
//            jsonObjectOneGroup.put("discountGroup", jsonArray);
//            jsonObject.put("preferentialPrice", priceJson.getContent().getPreferentialPrice());
//            jsonObject.put("actualPrice", priceJson.getContent().getActualPrice());
//            jsonObject.put("originalPrice", priceJson.getContent().getOriginalPrice());
//            for (int i = 0; i < detAils.size(); i++){
//                GoodId.add(detAils.get(i).getGoodsId());
//                for (int j = 0; j < keyValuePairs.size(); j++){
//                    if (GoodId.contains(keyValuePairs.get(j).getIntID()) && keyValuePairs.get(j).getCommPrice() != 0 ){
//                        GoodIdGroup.add(keyValuePairs.get(j).getIntID());
//                    }
//                }
//            }
//            if (GoodId.containsAll(GoodIdGroup) && GoodIdGroup.containsAll(GoodId)) {
//                for (int j = 0; j < detAils.size(); j++) {
//                    for (int i = 0; i < keyValuePairs.size(); i++) {
//                        if (detAils.get(j).getGoodsId().equals(keyValuePairs.get(i).getIntID())) {
//                            JSONObject jsonObject1 = new JSONObject();
//                            jsonObject1.put("PriceGroup", keyValuePairs.get(i).getStringPrice());
//                            jsonObject1.put("GoodIdGroup", keyValuePairs.get(i).getIntID());
//                            jsonObject1.put("NameGroup", keyValuePairs.get(i).getStringName());
//                            jsonObject1.put("UrlGroup", keyValuePairs.get(i).getStringUrl());
//                            jsonObject1.put("intNumGroup", keyValuePairs.get(i).getCommPrice());
//                            jsonObject1.put("chans", keyValuePairs.get(i).getChans());
//                            jsonArray.add(jsonObject1);
//                        }
//                    }
//                }
//            }
//            if (jsonArray.size() != 0 && "B".equals(detAils.get(detAils.size() - detAils.size()).getTag())){
//                JSONObject jsonObjectOne = (JSONObject) jsonArray.get(jsonArray.size() - jsonArray.size());
//                JSONObject jsonObjectTwo = (JSONObject) jsonArray.get(jsonArray.size() - 1);
//                if (jsonObjectOne.getInteger("intNumGroup") != jsonObjectTwo.getInteger("intNumGroup")){
//                    if (jsonObjectOne.getInteger("intNumGroup") > jsonObjectTwo.getInteger("intNumGroup")){
//                        string = jsonObjectOne.getString("GoodIdGroup");
//                        remainder = jsonObjectOne.getInteger("intNumGroup") - jsonObjectTwo.getInteger("intNumGroup");
//                        jsonObjectOne.put("intNumGroup", jsonObjectOne.getInteger("intNumGroup") - remainder);
//                    }else {
//                        string = jsonObjectTwo.getString("GoodIdGroup");
//                        remainder = jsonObjectTwo.getInteger("intNumGroup") - jsonObjectOne.getInteger("intNumGroup");
//                        jsonObjectTwo.put("intNumGroup", jsonObjectTwo.getInteger("intNumGroup") - remainder);
//                    }
//                }
//                jsonObjectOneGroup.put("Original", GroupPriceSale(jsonObjectOne, jsonObjectTwo).get(GroupPriceSale(jsonObjectOne, jsonObjectTwo).size() -
//                        GroupPriceSale(jsonObjectOne, jsonObjectTwo).size()));
//                jsonObjectOneGroup.put("CurrEnt", GroupPriceSale(jsonObjectOne, jsonObjectTwo).get(GroupPriceSale(jsonObjectOne, jsonObjectTwo).size() - 1));
//            }else {
//                for (int i = 0; i < keyValuePairs.size(); i++){
//                    if (keyValuePairs.get(i).getCommPrice() != 0 && GoodId.contains(keyValuePairs.get(i).getIntID())){
//                        JSONObject jsonObject1 = new JSONObject();
//                        jsonObject1.put("PriceGroup", keyValuePairs.get(i).getStringPrice());
//                        jsonObject1.put("GoodIdGroup", keyValuePairs.get(i).getIntID());
//                        jsonObject1.put("NameGroup", keyValuePairs.get(i).getStringName());
//                        jsonObject1.put("UrlGroup", keyValuePairs.get(i).getStringUrl());
//                        jsonObject1.put("intNumGroup", keyValuePairs.get(i).getCommPrice());
//                        jsonObject1.put("chans", keyValuePairs.get(i).getChans());
//                        jsonArray1.add(jsonObject1);
//                    }
//                }
//            }
//            for (int i = 0; i < keyValuePairs.size(); i++){
//                if (keyValuePairs.get(i).getCommPrice() != 0 && !GoodId.contains(keyValuePairs.get(i).getIntID())){
////                    for (int j = 0; j < detAils.size(); j++){
////                        if (!GoodId.contains(keyValuePairs.get(i).intID)){
////                        Log.d(TAG, "AssemblyData: " + "33333");
//                    JSONObject jsonObject1 = new JSONObject();
//                    jsonObject1.put("PriceGroup", keyValuePairs.get(i).getStringPrice());
//                    jsonObject1.put("GoodIdGroup", keyValuePairs.get(i).getIntID());
//                    jsonObject1.put("NameGroup", keyValuePairs.get(i).getStringName());
//                    jsonObject1.put("UrlGroup", keyValuePairs.get(i).getStringUrl());
//                    jsonObject1.put("intNumGroup", keyValuePairs.get(i).getCommPrice());
//                    jsonObject1.put("chans", keyValuePairs.get(i).getChans());
//                    jsonArray1.add(jsonObject1);
////                            break;
//                }
//            }
//            for (int i = 0; i < keyValuePairs.size(); i++){
//                if (keyValuePairs.get(i).getIntID().equals(string) && remainder != 0){
//                    JSONObject jsonObject1 = new JSONObject();
//                    jsonObject1.put("PriceGroup", keyValuePairs.get(i).getStringPrice());
//                    jsonObject1.put("GoodIdGroup", keyValuePairs.get(i).getIntID());
//                    jsonObject1.put("NameGroup", keyValuePairs.get(i).getStringName());
//                    jsonObject1.put("UrlGroup", keyValuePairs.get(i).getStringUrl());
//                    jsonObject1.put("intNumGroup", remainder);
//                    jsonObject1.put("chans", keyValuePairs.get(i).getChans());
//                    jsonArray1.add(jsonObject1);
//                    break;
//                }
//            }
//            jsonObject.put("ShoppingSingle", jsonArray1);
//            Log.i(TAG, "Interface AssemblyData: -> " + jsonObject.toString());
////            logger.info("Interface AssemblyData: -> " + jsonObject.toString());
//        }catch (NullPointerException e){
//            e.printStackTrace();
////            logger.error("Interface Error: -> " + e.toString());
//            Popup("价格列表为空");
//        }
//        return jsonObject.toJSONString();
//    }

    /**
     * @ 计算出货界面组合优惠价格
     * @param jsonObjectOne
     * @param jsonObjectTwo
     * */
    private ArrayList<String> GroupPriceSale(JSONObject jsonObjectOne, JSONObject jsonObjectTwo){
        ArrayList<String> string = new ArrayList<>();
        String oriGinalcommOne = jsonObjectOne.getString("PriceGroup");
        String oriGinalcommTwo = jsonObjectTwo.getString("PriceGroup");
        String currentcomOne = strOriginalPrice.get(strOriginalPrice.size() - strOriginalPrice.size());
        String currentcomTwo = strOriginalPrice.get(strOriginalPrice.size() - 1);
        double oriGinal = (Double.valueOf(oriGinalcommOne) + Double.valueOf(oriGinalcommTwo)) * jsonObjectOne.getInteger("intNumGroup");
        double currEnt = (Double.valueOf(currentcomOne) + Double.valueOf(currentcomTwo)) * jsonObjectOne.getInteger("intNumGroup");
        DecimalFormat decimalFormat1 = new DecimalFormat("0.00");           // 保留两位小数
        String stringOriginal = decimalFormat1.format(oriGinal);
        String stringCurrent = decimalFormat1.format(currEnt);
        string.add(stringOriginal);
        string.add(stringCurrent);
        Log.i(TAG, "Interface GroupPriceSale: -> " + string);
//        logger.info("Interface GroupPriceSale: -> " + string);
        return string;
    }

    @SuppressLint("HandlerLeak")
    private Handler handlerCount = new Handler(){
        public void handleMessage(Message msg){
            if (countDownTimeOne == 1){
//                Intent intent = new Intent();
//                setResult(RESULT_OK, intent);
                Log.d(TAG, "handleMessage: " + "MainFace11111");
                StartActivity();
//                finish();
                EmptyTimer();
                getTimer.schedule(getTimerTaskCount, 0,1000);
//                keyValuePairs.clear();              // 清空数据集立马更新数据
//                adapter.notifyDataSetChanged();
            }
        }
    };

    /**
     * @ 开启定时器
     * */
    private void startTimer(){
        if (timerCountDown == null) {
            timerCountDown = new Timer();
        }

        if (timerTaskCount == null) {
            timerTaskCount = new TimerTask() {
                @Override
                public void run() {
//                    Log.d(TAG, "run1: " + countDownTimeOne);
                    if (countDownTimeOne > 1){
                        countDownTimeOne--;
                        Log.d(TAG, "run: " + countDownTimeOne);
                        Message message = new Message();
                        handlerCount.sendMessage(message);
                    }
                }
            };
        }

        if(timerCountDown != null){
            timerCountDown.schedule(timerTaskCount, 1000, 1000);
        }
    }

    /**
     * @ 暂停定时器
     * */
    private void stopTimer(){
        if (timerCountDown != null) {
            timerCountDown.cancel();
            timerCountDown = null;
        }
        if (timerTaskCount != null) {
            timerTaskCount.cancel();
            timerTaskCount = null;
        }

    }

    /**
     * @ 开启定时器清空动画圆圈
     * */
//    private void startTimerAnimation(){
//        stopTimerAnimation();
//        timerAnimation = new Timer();
//        timerAnimation.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Message message = new Message();
//                handlerAnimation.sendMessage(message);
//            }
//        }, 1500);
//
//    }

    /**
     * @ 清空定时器
     * */
    private void stopTimerAnimation(){
        if (timerAnimation != null) {
            timerAnimation.cancel();
            timerAnimation = null;
        }
    }

    /**
     * @ 返回item通道数据
     * */
    public static ArrayList<KeyValuePair> returnKeyValuePair(){
        return keyValuePairs;
    }

    /**
     * @ 返回组合优惠数据
     * */
    public static ArrayList<String> returnGroupGoodId(){
        return GroupGoodId;
    }

    public static Integer returnPrice(){
        return PriceSign;
    }


    /**
     * @ 五秒更换一次广告
     * */
//    private TimerTask timerTaskADV = new TimerTask() {
//        @Override
//        public void run() {
//            if (countDownTimeADV > 1){
//                countDownTimeADV --;
//                Message message = new Message();
//                handlerADV.sendMessage(message);
//            }else {
//                countDownTimeADV = 5;
//                Message message = new Message();
//                handlerADV.handleMessage(message);
//            }
//        }
//    };

//    @SuppressLint("HandlerLeak")
//    private Handler handlerADV = new Handler(){
//        public void handleMessage(Message msg){
//            super.handleMessage(msg);
//            if (countDownTimeADV == 5){
//                setImageView();
//            }
//        }
//    };
//
//    /**
//     * @ 更换背景图片*/
//    private void setImageView() {
////        Log.d(TAG, "setImageView: " + backInt);
//        switch (backInt){
//            case 0:
//                runOnUiThread(() -> {
//                    imageViewADV.setImageResource(R.drawable.img_018);
//                    backInt++;
//                });
//                break;
//            case 1:
//                runOnUiThread(() -> {
//                    imageViewADV.setImageResource(R.drawable.img_019);
//                    backInt++;
//                });
//                break;
//            case 2:
//                runOnUiThread(() -> {
//                    imageViewADV.setImageResource(R.drawable.img_020);
//                    backInt++;
//                });
//                break;
//            case 3:
//                runOnUiThread(() -> {
//                    imageViewADV.setImageResource(R.drawable.img_021);
//                    backInt = 0;
//                });
//                break;
//            default:
//                break;
//        }
//
//    }

    /**
     * @ 清空本活动所有的定时器
     * */
    private void EmptyTimer(){
        stopautoBannerTimer();
        stopTimer();
        if (timer != null){
            timer.cancel();
            timer = null;
        }
        if (timerTask != null){
            timerTask.cancel();
            timerTask = null;
        }
    }

    /**
     * @ 开启定时器
     * */
    private void startTimerOne(){
        if (timer == null) {
            timer = new Timer();
        }

        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
//                    Log.d(TAG, "run1: " + countDownTimeOne);
                    if (countDownTimeOne > 1){
                        countDownTimeOne--;
                        Message message = new Message();
                        handlerCount.sendMessage(message);
                    }
                }
            };
        }

        if(timer != null){
            timer.schedule(timerTask, 1000, 1000);
        }
    }

    /**
     * @ 暂停定时器
     * */
    private void stopTimerOne(){
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

    }

    private static Map<String, Activity> destoryMap = new HashMap<>();

    //将Activity添加到队列中
    public static void addDestoryActivityToMap(Activity activity, String activityName) {
        destoryMap.put(activityName, activity);
    }

    //根据名字销毁制定Activity
    public static void destoryActivity(String activityName) {
        Set<String> keySet = destoryMap.keySet();
//        Log.d(TAG, "destoryActivity: " + keySet.size());
//        LogUtil.i(keySet.size());
        if (keySet.size() > 0) {
            for (String key : keySet) {
                if (activityName.equals(key)) {
                    destoryMap.get(key).finish();
                }
            }
        }
    }

}


