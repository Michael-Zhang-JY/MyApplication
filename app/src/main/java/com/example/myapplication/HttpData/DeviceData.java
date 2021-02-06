package com.example.myapplication.HttpData;

public class DeviceData {
    private String code;
    private String message;
    private Content content;
    private String timestamp;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "DeviceData{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", content=" + content +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp(){
        return timestamp;
    }

    public void setTimestamp(String timestamp){
        this.timestamp = timestamp;
    }

    public Content getContent(){
        return content;
    }

    public void setContent(Content content){
        this.content = content;
    }

}

