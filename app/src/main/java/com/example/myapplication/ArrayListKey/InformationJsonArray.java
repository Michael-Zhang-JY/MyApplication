package com.example.myapplication.ArrayListKey;

public class InformationJsonArray {
    private int goodsId;
    private String goodsName;
    private String label;
    private String imgUrl;
    private String categoryName;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public InformationJsonArray(int goodsId, String goodsName, String label, String imgUrl, String categoryName){
        this.goodsId = goodsId;
        this.goodsName = goodsName;
        this.label = label;
        this.imgUrl = imgUrl;
        this.categoryName = categoryName;
    }

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

    @Override
    public String toString() {
        return "InformationJsonArray{" +
                "goodsId=" + goodsId +
                ", goodsName='" + goodsName + '\'' +
                ", label='" + label + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}
