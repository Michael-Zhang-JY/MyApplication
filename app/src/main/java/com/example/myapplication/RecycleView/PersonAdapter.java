package com.example.myapplication.RecycleView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.ArrayListKey.KeyValuePair;
import com.example.myapplication.FontIconView.XRTextView;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class PersonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{

    private static final String TAG = PersonAdapter.class.getCanonicalName();
    private Context context;
    private ArrayList<KeyValuePair> keyValuePairs;
    private static List<Boolean> isClicks;
    private static List<Integer> isOrder;
    private static List<Boolean> isOneDiscount;
    private String chans = "通道  Aisle";

    private static final int TYPE_HEADER = 1;
    private static final int TYPE_CONTENT = 2;

    /**
     * @ 构造函数
     * */
    public PersonAdapter(Context context, ArrayList<KeyValuePair> keyValuePairs){
        this.context = context;
        this.keyValuePairs = keyValuePairs;
        isClicks = new ArrayList<>();
        isOrder = new ArrayList<>();
        isOneDiscount = new ArrayList<>();
        for (int i = 0; i < this.keyValuePairs.size(); i++){
            isClicks.add(false);
            isOrder.add(0);
            isOneDiscount.add(keyValuePairs.get(i).isClick());
        }
    }

    /**
     * @ 返回界面布局
     * */
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item, parent, false));
//    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view;
        switch (viewType){
            default:
            case TYPE_HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_one, parent, false);
                viewHolder = new PersonAdapter.ViewHolderOne(view);
                return  viewHolder;
            case TYPE_CONTENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item, parent, false);
                viewHolder = new PersonAdapter.ViewHolder(view);
                return viewHolder;
        }

//        Log.d(TAG, "onBindViewHolder: " + "3333");
//        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inter_shopping_two, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        if ((position + 1) % 2 == 0) {
            return TYPE_HEADER;
        }
//        Log.d(TAG, "onBindViewHolder: " + "1111");
        return TYPE_CONTENT;
    }

    /**
     * @ 绑定参数
     * */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        Log.d(TAG, "onBindViewHolder position: " + position);
        StringBuilder string = new StringBuilder();
        RecyclerView.ViewHolder viewHolder = holder;
        viewHolder.itemView.setTag(position);
        if (holder instanceof ViewHolder){
            ((ViewHolder) holder).imageView.setTag(position);
            ((ViewHolder) holder).xrtextViewName.setText(keyValuePairs.get(position).getStringName());
//            for (int i = 0; i < keyValuePairs.get(position).getChans().size(); i++){
            string.append(keyValuePairs.get(position).getChans()).append(" ");
//            }
            ((ViewHolder) holder).textViewAisle.setText(string + chans);
            Glide.with(context).load(keyValuePairs.get(position).getStringUrl()).into( ((ViewHolder) holder).imageView);
//        if (isOneDiscount.get(position)){
//            holder.textViewDicount.setVisibility(View.VISIBLE);
//        }else {
//            holder.textViewDicount.setVisibility(View.GONE);
//        }
            if (position != 0){
//            holder.textViewDicount.setText("新品  New");
                ((ViewHolder) holder).textViewHotOne.setText("NEW");
                ((ViewHolder) holder).textViewHotTwo.setText("新品");
            }
            if (keyValuePairs.get(position).isLock()){
//            holder.textViewQuality.setVisibility(View.GONE);
//                ((ViewHolder) holder).imageViewlack.setVisibility(View.GONE);
                ((ViewHolder) holder).imageViewOut.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).viewlack.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).textViewlack.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).textViewlack.setText("停用 not in use");
                ((ViewHolder) holder).imageView.setClickable(false);
                ((ViewHolder) holder).textViewGroup.setVisibility(View.GONE);
//                ((ViewHolder) holder).textViewAisle.setVisibility(View.GONE);
                ((ViewHolder) holder).textViewHotOne.setVisibility(View.GONE);
                ((ViewHolder) holder).textViewHotTwo.setVisibility(View.GONE);
                ((ViewHolder) holder).imageViewHot.setVisibility(View.GONE);
//            holder.textViewLock.setText("货道停用");
//                ((ViewHolder) holder).textViewLock.setVisibility(View.VISIBLE);
            }else if (keyValuePairs.get(position).getQuality() == 0){
                ((ViewHolder) holder).imageView.setClickable(false);
//                ((ViewHolder) holder).textViewLock.setVisibility(View.GONE);
//            holder.imageViewlack.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).imageViewOut.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).viewlack.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).textViewlack.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).textViewGroup.setVisibility(View.GONE);
//                ((ViewHolder) holder).textViewAisle.setVisibility(View.GONE);
                ((ViewHolder) holder).textViewHotOne.setVisibility(View.GONE);
                ((ViewHolder) holder).textViewHotTwo.setVisibility(View.GONE);
                ((ViewHolder) holder).imageViewHot.setVisibility(View.GONE);
//            holder.textViewQuality.setText("库存不足");
//            holder.textViewQuality.setVisibility(View.VISIBLE);
            }else {
//                ((ViewHolder) holder).textViewLock.setVisibility(View.GONE);
//            holder.imageViewlack.setVisibility(View.GONE);
                ((ViewHolder) holder).imageViewOut.setVisibility(View.GONE);
                ((ViewHolder) holder).viewlack.setVisibility(View.GONE);
                ((ViewHolder) holder).textViewlack.setVisibility(View.GONE);
                ((ViewHolder) holder).imageView.setClickable(true);
//                ((ViewHolder) holder).textViewGroup.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).textViewAisle.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).textViewHotOne.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).imageViewHot.setVisibility(View.VISIBLE);
//                ((ViewHolder) holder).textViewHotTwo.setVisibility(View.VISIBLE);
//            holder.textViewQuality.setVisibility(View.GONE);
            }
        }
        if (holder instanceof ViewHolderOne){
            ((ViewHolderOne) holder).imageView.setTag(position);
            ((ViewHolderOne) holder).xrtextViewName.setText(keyValuePairs.get(position).getStringName());
            //for (int i = 0; i < keyValuePairs.get(position).getChans().size(); i++){
            string.append(keyValuePairs.get(position).getChans()).append(" ");
            //}
            ((ViewHolderOne) holder).textViewAisle.setText(string + chans);
            Glide.with(context).load(keyValuePairs.get(position).getStringUrl()).into(((ViewHolderOne) holder).imageView);
//        if (isOneDiscount.get(position)){
//            holder.textViewDicount.setVisibility(View.VISIBLE);
//        }else {
//            holder.textViewDicount.setVisibility(View.GONE);
//        }
            if (position != 0){
//            holder.textViewDicount.setText("新品  New");
                ((ViewHolderOne) holder).textViewHotOne.setText("NEW");
                ((ViewHolderOne) holder).textViewHotTwo.setText("新品");
            }
            if (keyValuePairs.get(position).isLock()){
//            holder.textViewQuality.setVisibility(View.GONE);
                ((ViewHolderOne) holder).imageViewOut.setVisibility(View.VISIBLE);
                ((ViewHolderOne) holder).viewlack.setVisibility(View.VISIBLE);
                ((ViewHolderOne) holder).textViewlack.setVisibility(View.VISIBLE);
                ((ViewHolderOne) holder).textViewlack.setText("停用 not in use");
                ((ViewHolderOne) holder).imageView.setClickable(false);
                ((ViewHolderOne) holder).textViewGroup.setVisibility(View.GONE);
//                ((ViewHolderOne) holder).textViewAisle.setVisibility(View.GONE);
                ((ViewHolderOne) holder).textViewHotOne.setVisibility(View.GONE);
                ((ViewHolderOne) holder).textViewHotTwo.setVisibility(View.GONE);
                ((ViewHolderOne) holder).imageViewHot.setVisibility(View.GONE);
//            holder.textViewLock.setText("货道停用");
                ((ViewHolderOne) holder).textViewLock.setVisibility(View.VISIBLE);
            }else if (keyValuePairs.get(position).getQuality() == 0){
                ((ViewHolderOne) holder).imageView.setClickable(false);
//                ((ViewHolder) holder).textViewLock.setVisibility(View.GONE);
//            holder.imageViewlack.setVisibility(View.VISIBLE);
                ((ViewHolderOne) holder).imageViewOut.setVisibility(View.VISIBLE);
                ((ViewHolderOne) holder).viewlack.setVisibility(View.VISIBLE);
                ((ViewHolderOne) holder).textViewlack.setVisibility(View.VISIBLE);
                ((ViewHolderOne) holder).textViewGroup.setVisibility(View.GONE);
//                ((ViewHolderOne) holder).textViewAisle.setVisibility(View.GONE);
                ((ViewHolderOne) holder).textViewHotOne.setVisibility(View.GONE);
                ((ViewHolderOne) holder).textViewHotTwo.setVisibility(View.GONE);
                ((ViewHolderOne) holder).imageViewHot.setVisibility(View.GONE);
//            holder.textViewQuality.setText("库存不足");
//            holder.textViewQuality.setVisibility(View.VISIBLE);
            }else {
                ((ViewHolderOne) holder).imageViewOut.setVisibility(View.GONE);
                ((ViewHolderOne) holder).textViewLock.setVisibility(View.GONE);
//            holder.imageViewlack.setVisibility(View.GONE);
                ((ViewHolderOne) holder).viewlack.setVisibility(View.GONE);
                ((ViewHolderOne) holder).textViewlack.setVisibility(View.GONE);
                ((ViewHolderOne) holder).imageView.setClickable(true);
//                ((ViewHolderOne) holder).textViewGroup.setVisibility(View.VISIBLE);
                ((ViewHolderOne) holder).textViewAisle.setVisibility(View.VISIBLE);
                ((ViewHolderOne) holder).textViewHotOne.setVisibility(View.VISIBLE);
                ((ViewHolderOne) holder).imageViewHot.setVisibility(View.VISIBLE);
//                ((ViewHolderOne) holder).textViewHotTwo.setVisibility(View.VISIBLE);
//            holder.textViewQuality.setVisibility(View.GONE);
            }
        }
//        viewHolder.imageView.setTag(position);
//        holder.xrtextViewName.setText(keyValuePairs.get(position).getStringName());
//        for (int i = 0; i < keyValuePairs.get(position).getChans().size(); i++){
//            string.append(keyValuePairs.get(position).getChans().get(i).toString()).append(" ");
//        }
//        holder.textViewAisle.setText(string + chans);
//        Glide.with(context).load(keyValuePairs.get(position).getStringUrl()).into(holder.imageView);
////        if (isOneDiscount.get(position)){
////            holder.textViewDicount.setVisibility(View.VISIBLE);
////        }else {
////            holder.textViewDicount.setVisibility(View.GONE);
////        }
//        if (position != 0){
////            holder.textViewDicount.setText("新品  New");
//            holder.textViewHotOne.setText("NEW");
//            holder.textViewHotTwo.setText("新品");
//        }
//        if((position + 1) % 2 == 0){
//
//        }
//        if (keyValuePairs.get(position).isLock()){
////            holder.textViewQuality.setVisibility(View.GONE);
//            holder.imageViewlack.setVisibility(View.GONE);
//            holder.viewlack.setVisibility(View.GONE);
//            holder.textViewlack.setVisibility(View.GONE);
//            holder.imageView.setClickable(false);
////            holder.textViewLock.setText("货道停用");
//            holder.textViewLock.setVisibility(View.VISIBLE);
//        }else if (keyValuePairs.get(position).getQuality().equals("0")){
//            holder.imageView.setClickable(false);
//            holder.textViewLock.setVisibility(View.GONE);
////            holder.imageViewlack.setVisibility(View.VISIBLE);
//            holder.viewlack.setVisibility(View.VISIBLE);
//            holder.textViewlack.setVisibility(View.VISIBLE);
////            holder.textViewQuality.setText("库存不足");
////            holder.textViewQuality.setVisibility(View.VISIBLE);
//        }else {
//            holder.textViewLock.setVisibility(View.GONE);
////            holder.imageViewlack.setVisibility(View.GONE);
//            holder.viewlack.setVisibility(View.GONE);
//            holder.textViewlack.setVisibility(View.GONE);
//            holder.imageView.setClickable(true);
////            holder.textViewQuality.setVisibility(View.GONE);
//        }
//        if (keyValuePairs.get(position).getQuality().equals("0")){
//            holder.fontIconView.setVisibility(View.GONE);
//            holder.textViewQuality.setText("库存不足");
//            holder.textViewQuality.setVisibility(View.VISIBLE);
//        }else {
//            holder.fontIconView.setVisibility(View.VISIBLE);
//            holder.textViewQuality.setVisibility(View.GONE);
//        }
    }



    /**
     * @ item个数
     * @return item数量
     * */
    @Override
    public int getItemCount() {
//        Log.d(TAG, "PersonAdapter: " + keyValuePairs.size());
        return keyValuePairs.size();
    }


    /**
     * @ 界面布局
     * */
    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textViewAisle;
        TextView textViewOrder;
        TextView textViewDicount;
        TextView textViewLock;
        TextView textViewQuality;
        XRTextView xrtextViewName;
        ImageView imageViewlack;
        TextView textViewlack;
        View viewlack;
        View viewShopoing;
        ImageView imageViewOut;
        ImageView imageViewHot;
        ConstraintLayout constraintLayout;

        TextView textViewHotOne;
        TextView textViewHotTwo;
        TextView textViewGroup;



        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView9);
            xrtextViewName = itemView.findViewById(R.id.textView2);
            textViewAisle = itemView.findViewById(R.id.textView8);
            textViewDicount = itemView.findViewById(R.id.textView11);
//            imageViewlack = itemView.findViewById(R.id.imageViewlack);
            viewlack = itemView.findViewById(R.id.imageViewlack);
            textViewlack = itemView.findViewById(R.id.textViewlack);
            textViewLock = itemView.findViewById(R.id.textViewlock);
            constraintLayout = itemView.findViewById(R.id.constraintLayoutFace);
            viewShopoing = itemView.findViewById(R.id.imageViewlackview);
            itemView.setOnClickListener(PersonAdapter.this);
            imageView.setOnClickListener(PersonAdapter.this);
            textViewHotOne = itemView.findViewById(R.id.textViewHot);
            textViewHotTwo = itemView.findViewById(R.id.textHotSaleTwo);
            textViewGroup = itemView.findViewById(R.id.TextbackGroundOne);
            imageViewOut = itemView.findViewById(R.id.imageOut);
            imageViewHot = itemView.findViewById(R.id.imageView10);
        }
    }

    /**
     * @ 界面布局
     * */
    class ViewHolderOne extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textViewAisle;
        TextView textViewOrder;
        TextView textViewDicount;
        TextView textViewLock;
        TextView textViewQuality;
        XRTextView xrtextViewName;
        ImageView imageViewlack;
        TextView textViewlack;
        View viewlack;
        View viewShopoing;
        ImageView imageViewHot;
        ImageView imageViewOut;
        ConstraintLayout constraintLayout;

        TextView textViewHotOne;
        TextView textViewHotTwo;
        TextView textViewGroup;


        ViewHolderOne(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView9);
            xrtextViewName = itemView.findViewById(R.id.textView2);
            textViewAisle = itemView.findViewById(R.id.textView8);
            textViewDicount = itemView.findViewById(R.id.textView11);
//            imageViewlack = itemView.findViewById(R.id.imageViewlack);
            viewlack = itemView.findViewById(R.id.imageViewlack);
            textViewlack = itemView.findViewById(R.id.textViewlack);
            textViewLock = itemView.findViewById(R.id.textViewlock);
            constraintLayout = itemView.findViewById(R.id.constraintLayoutFace);
            viewShopoing = itemView.findViewById(R.id.imageViewlackview);
            itemView.setOnClickListener(PersonAdapter.this);
            imageView.setOnClickListener(PersonAdapter.this);
            textViewHotOne = itemView.findViewById(R.id.textViewHot);
            textViewHotTwo = itemView.findViewById(R.id.textHotSaleTwo);
            textViewGroup = itemView.findViewById(R.id.TextbackGroundOne);
            imageViewOut = itemView.findViewById(R.id.imageOut);
            imageViewHot = itemView.findViewById(R.id.imageView10);
        }
    }


    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    /**
     * @ 点击事件监听回调
     * */
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    /**
     * @ item里面有多个控件可以点击
     *  */
    public enum ViewName {
        INCREASE,
        REDUCE,
        DETAILS,
        ITEM
    }

    /**
     * @ 点击事件接口
     * */
    public interface OnRecyclerViewItemClickListener {
        void onClick(View view, ViewName viewName, int position);
    }


    public static List<Integer> returnOrder(){
        return isOrder;
    }

    public static List<Boolean> returnIsclick(){
        return isClicks;
    }

    public static List<Boolean> returnIsOneDiscount(){
        return isOneDiscount;
    }

    /**
     * @ 计算购物车货品数
     * */
    public static Integer NumOrder(){
        int num = 0;
        for (int i = 0; i < isOrder.size(); i++){
            if (isOrder.get(i) != 0){
                num += isOrder.get(i);
            }
        }
        return num;
    }

    /**
     * @ item点击监听
     * */
    @Override
    public void onClick(View v){
        int position = (int) v.getTag();
        if (mOnItemClickListener != null) {
            if (!keyValuePairs.get(position).isLock() && keyValuePairs.get(position).getQuality() != 0) {
                mOnItemClickListener.onClick(v, ViewName.DETAILS, position);
            }

        }
    }

}


