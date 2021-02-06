package com.example.myapplication.HttpData;

public class HttpsContentNew {
    private int channel;
    private int quantity;
    private int channelStatus;
    private int goodsId;
    private String price;

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getChannelStatus() {
        return channelStatus;
    }

    public void setChannelStatus(int channelStatus) {
        this.channelStatus = channelStatus;
    }

    public int getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "HttpsContentNew{" +
                "channel=" + channel +
                ", quantity=" + quantity +
                ", channelStatus=" + channelStatus +
                ", goodsId=" + goodsId +
                ", price='" + price + '\'' +
                '}';
    }
}
