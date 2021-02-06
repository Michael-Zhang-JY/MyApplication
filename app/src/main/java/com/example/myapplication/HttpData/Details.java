package com.example.myapplication.HttpData;

public class Details {
    private String id;
    private String goodsId;
    private String startDate;
    private String endDate;
    private String week;
    private String startTime;
    private String endTime;
    private String tag;
    private String referencePrice;
    private String nowPrice;
    private String nowPreferential;
    private TodayPreferentialInfo todayPreferentialInfo;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getReferencePrice() {
        return referencePrice;
    }

    public void setReferencePrice(String referencePrice) {
        this.referencePrice = referencePrice;
    }

    public String getNowPrice() {
        return nowPrice;
    }

    public void setNowPrice(String nowPrice) {
        this.nowPrice = nowPrice;
    }

    public String getNowPreferential() {
        return nowPreferential;
    }

    public void setNowPreferential(String nowPreferential) {
        this.nowPreferential = nowPreferential;
    }

    public TodayPreferentialInfo getTodayPreferentialInfo() {
        return todayPreferentialInfo;
    }

    public void setTodayPreferentialInfo(TodayPreferentialInfo todayPreferentialInfo) {
        this.todayPreferentialInfo = todayPreferentialInfo;
    }

    @Override
    public String toString() {
        return "Details{" +
                "id='" + id + '\'' +
                ", goodsId='" + goodsId + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", week='" + week + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", tag='" + tag + '\'' +
                ", referencePrice='" + referencePrice + '\'' +
                ", nowPrice='" + nowPrice + '\'' +
                ", nowPreferential='" + nowPreferential + '\'' +
                ", todayPreferentialInfo=" + todayPreferentialInfo +
                '}';
    }
}
