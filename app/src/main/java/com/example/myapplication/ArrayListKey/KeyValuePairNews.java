package com.example.myapplication.ArrayListKey;

import java.util.List;

public class KeyValuePairNews {
    private int chans;
    private int intID;
    private String stringName;
    private String stringPrice;
    private String stringUrl;
    private boolean isClick;
    private Integer commPrice;
    private int quality;
    private  int lock;
    private String feature;
    private String desc;

    public int getChans() {
        return chans;
    }

    public void setChans(int chans) {
        this.chans = chans;
    }


    public KeyValuePairNews(int chans, int intID, String stringName, String stringPrice, String stringUrl, boolean isClick, Integer commPrice, int quality, int lock, String feature, String desc) {
        this.chans = chans;
        this.intID = intID;
        this.stringName = stringName;
        this.stringUrl = stringUrl;
        this.stringPrice = stringPrice;
        this.isClick = isClick;
        this.commPrice = commPrice;
        this.quality = quality;
        this.lock = lock;
        this.feature = feature;
        this.desc = desc;
    }


    public boolean isClick() {
        return isClick;
    }

    public int getLock() {
        return lock;
    }

    public void setLock(int lock) {
        this.lock = lock;
    }

    public void setClick(boolean click) {
        isClick = click;
    }

    public Integer getCommPrice() {
        return commPrice;
    }

    public void setCommPrice(Integer commPrice) {
        this.commPrice = commPrice;
    }

    public String getStringPrice() {
        return stringPrice;
    }

    public void setStringPrice(String stringPrice) {
        this.stringPrice = stringPrice;
    }


    public int getIntID() {
        return intID;
    }

    public void setIntID(int intID) {
        this.intID = intID;
    }

    public String getStringName() {
        return stringName;
    }

    public void setStringName(String stringName) {
        this.stringName = stringName;
    }

    public String getStringUrl() {
        return stringUrl;
    }

    public void setStringUrl(String stringUrl) {
        this.stringUrl = stringUrl;
    }

    @Override
    public String toString() {
        return "KeyValuePairNews{" +
                "chans=" + chans +
                ", intID='" + intID + '\'' +
                ", stringName='" + stringName + '\'' +
                ", stringUrl='" + stringUrl + '\'' +
                ", stringPrice='" + stringPrice + '\'' +
                ", isClick=" + isClick +
                ", commPrice=" + commPrice +
                ", quality=" + quality +
                ", lock=" + lock +
                ", feature='" + feature + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    //    // 重构数据排序
//    @Override

//    public List<Integer> getChans() {
//        return chans;
//    }
//    public int compareTo(KeyValuePair o) {
//        int i = this.getIntAisle() - o.getIntAisle();
//        if (i < 0){
//            return -1;
//        }
//        return 1;
//    }
}
