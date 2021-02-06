package com.example.myapplication.HttpData;

import java.util.List;

public class ContentInven implements Comparable<ContentInven>{
    private GoodsInven goods;
    private int quantity;
    private int lock;
    private int chan;

    public GoodsInven getGoods() {
        return goods;
    }

    public void setGoods(GoodsInven goods) {
        this.goods = goods;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getLock() {
        return lock;
    }

    public void setLock(int lock) {
        this.lock = lock;
    }

    public int getChan() {
        return chan;
    }

    public void setChan(int chan) {
        this.chan = chan;
    }

    @Override
    public String toString() {
        return "ContentInven{" +
                "goods=" + goods +
                ", quantity=" + quantity +
                ", lock=" + lock +
                ", chan=" + chan +
                '}';
    }

    @Override
    public int compareTo(ContentInven o){
        int i = this.getChan() - o.getChan();
        if (i < 0){
            return -1;
        }
        return 1;
    }
}
