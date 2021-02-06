package com.example.myapplication.HttpData;

import java.util.List;

public class Content {
    private String id;
    private String type;
    private String deviceId;
    private String preferential;
    private List<Details> details;
    private List<Goods> goods;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType(){
        return type;
    }

    public void setType (String type){
        this.type = type;
    }

    public String getDeviceId(){
        return deviceId;
    }

    public void setDeviceId (String deviceId){
        this.deviceId = deviceId;
    }

    public String getPreferential(){
        return preferential;
    }

    public void setPreferential (String preferential){
        this.preferential = preferential;
    }

    public List<Details> getDetails(){
        return details;
    }


    public void setDetails(List<Details> details){
        this.details = details;
    }

    public List<Goods> getGoods(){
        return goods;
    }

    public void setGoods(List<Goods> goods){
        this.goods = goods;
    }

    @Override
    public String toString() {
        return "Content{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", preferential='" + preferential + '\'' +
                ", details=" + details +
                ", goods=" + goods +
                '}';
    }
}
