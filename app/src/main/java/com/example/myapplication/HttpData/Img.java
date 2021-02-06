package com.example.myapplication.HttpData;

public class Img {
    private String name;
    private String url;
    private String uid;
    private String status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Img{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", uid='" + uid + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
