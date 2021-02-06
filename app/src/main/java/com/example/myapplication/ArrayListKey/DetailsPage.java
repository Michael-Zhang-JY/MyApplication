package com.example.myapplication.ArrayListKey;

import java.util.List;

public class DetailsPage {
    private String stringName;
    private String stringPrice;
    private String intID;
    private String stringUrl;
    private Integer number;
    private String quality;
    private String feature;
    private String desc;
    private int chans;
    private boolean lock;
    private Integer position;

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
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

    public String getIntID() {
        return intID;
    }

    public void setIntID(String intID) {
        this.intID = intID;
    }

    public String getStringUrl() {
        return stringUrl;
    }

    public void setStringUrl(String stringUrl) {
        this.stringUrl = stringUrl;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
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

    @Override
    public String toString() {
        return "DetailsPage{" +
                "stringName='" + stringName + '\'' +
                ", stringPrice='" + stringPrice + '\'' +
                ", intID='" + intID + '\'' +
                ", stringUrl='" + stringUrl + '\'' +
                ", number=" + number +
                ", quality='" + quality + '\'' +
                ", feature='" + feature + '\'' +
                ", desc='" + desc + '\'' +
                ", chans=" + chans +
                ", lock=" + lock +
                ", position=" + position +
                '}';
    }

    public int getChans() {
        return chans;
    }

    public void setChans(int chans) {
        this.chans = chans;
    }
}
