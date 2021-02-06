package com.example.myapplication.HttpData;

import java.util.List;

public class Goods {
    private String id;
    private String goodName;
    private String feature;
    private String tag;
    private String quality;
    private String desc;
    private String referencePrice;
    private String nowPrice;
    private String preferential;
    private boolean lock;
    private List<Integer> chans;
    private String img;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public String getPreferential() {
        return preferential;
    }

    public void setPreferential(String preferential) {
        this.preferential = preferential;
    }

    public boolean getLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public List<Integer> getChans() {
        return chans;
    }

    public void setChans(List<Integer> chans) {
        this.chans = chans;
    }


    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "id='" + id + '\'' +
                ", goodName='" + goodName + '\'' +
                ", feature='" + feature + '\'' +
                ", tag='" + tag + '\'' +
                ", quality='" + quality + '\'' +
                ", desc='" + desc + '\'' +
                ", referencePrice='" + referencePrice + '\'' +
                ", nowPrice='" + nowPrice + '\'' +
                ", preferential='" + preferential + '\'' +
                ", lock='" + lock + '\'' +
                ", chans=" + chans +
                ", img=" + img +
                '}';
    }
}
