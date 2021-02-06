package com.example.myapplication.ArrayListKey;

import java.util.List;

public class KeyValuePair {
    private int chans;
    private int intID;
    private String stringName;
    private String stringPrice;
    private String stringUrl;
    private boolean isClick;
    private Integer commPrice;
    private int quality;
    private boolean lock;
    private String feature;
    private String desc;

    public KeyValuePair(int chans, int intID, String stringName, String stringPrice, String stringUrl, boolean isClick, Integer commPrice, int quality, boolean lock, String feature, String desc) {
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




    @Override
    public String toString() {
        return "KeyValuePair{" +
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

    public int getChans() {
        return chans;
    }

    public void setChans(int chans) {
        this.chans = chans;
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

    public String getStringPrice() {
        return stringPrice;
    }

    public void setStringPrice(String stringPrice) {
        this.stringPrice = stringPrice;
    }

    public String getStringUrl() {
        return stringUrl;
    }

    public void setStringUrl(String stringUrl) {
        this.stringUrl = stringUrl;
    }

    public boolean isClick() {
        return isClick;
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

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
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
