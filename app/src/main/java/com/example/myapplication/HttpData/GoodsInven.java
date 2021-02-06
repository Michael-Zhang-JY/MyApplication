package com.example.myapplication.HttpData;

import java.util.List;

public class GoodsInven {
    private String goodsId;
    private String chineseName;
    private String englishName;
    private String goodsNo;
    private String goodsType;
    private String attribute;
    private String chineseDesc;
    private String englishDesc;
    private String imgUrl;
    private String price;
    private CategoryInven category;
    private BrandInven brand;

    @Override
    public String toString() {
        return "GoodsInven{" +
                "goodsId='" + goodsId + '\'' +
                ", chineseName='" + chineseName + '\'' +
                ", englishName='" + englishName + '\'' +
                ", goodsNo='" + goodsNo + '\'' +
                ", goodsType='" + goodsType + '\'' +
                ", attribute='" + attribute + '\'' +
                ", chineseDesc='" + chineseDesc + '\'' +
                ", englishDesc='" + englishDesc + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", price='" + price + '\'' +
                ", category=" + category +
                ", brand=" + brand +
                '}';
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getChineseDesc() {
        return chineseDesc;
    }

    public void setChineseDesc(String chineseDesc) {
        this.chineseDesc = chineseDesc;
    }

    public String getEnglishDesc() {
        return englishDesc;
    }

    public void setEnglishDesc(String englishDesc) {
        this.englishDesc = englishDesc;
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

    public CategoryInven getCategory() {
        return category;
    }

    public void setCategory(CategoryInven category) {
        this.category = category;
    }

    public BrandInven getBrand() {
        return brand;
    }

    public void setBrand(BrandInven brand) {
        this.brand = brand;
    }


}
