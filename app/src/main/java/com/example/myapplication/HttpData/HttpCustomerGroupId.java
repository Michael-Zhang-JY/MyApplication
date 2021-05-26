package com.example.myapplication.HttpData;

public class HttpCustomerGroupId {
    private int code;
    private String message;
    private CustomerContent content;
    private String timestamp;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CustomerContent getContent() {
        return content;
    }

    public void setContent(CustomerContent content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "HttpCustomerGroupId{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", content=" + content +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
