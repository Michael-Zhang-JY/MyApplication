package com.example.myapplication.HttpData;

import java.util.List;

public class HttpsMerchandiseContent {
    private int goodsId;
    private String goodsName;
    private String description;
    private String label;
    private String imgUrl;
    private String price;
    private int goodsState;
    private String gmtCreate;
    private String gmtModified;
    private HttpsCategory category;
    private int tenantId;

    public int getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getGoodsState() {
        return goodsState;
    }

    public void setGoodsState(int goodsState) {
        this.goodsState = goodsState;
    }

    public String getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(String gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(String gmtModified) {
        this.gmtModified = gmtModified;
    }

    public HttpsCategory getCategory() {
        return category;
    }

    public void setCategory(HttpsCategory category) {
        this.category = category;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String toString() {
        return "HttpsMerchandiseContent{" +
                "goodsId=" + goodsId +
                ", goodsName='" + goodsName + '\'' +
                ", description='" + description + '\'' +
                ", label=" + label +
                ", imgUrl=" + imgUrl +
                ", price='" + price + '\'' +
                ", goodsState=" + goodsState +
                ", gmtCreate='" + gmtCreate + '\'' +
                ", gmtModified='" + gmtModified + '\'' +
                ", category=" + category +
                ", tenantId=" + tenantId +
                '}';
    }
}
