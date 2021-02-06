package com.example.myapplication.HttpData;

import java.util.List;

public class HttpsAilseData {
    private String code;
    private String message;
    private List<HttpsContentNew> content;
    private String timestamp;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<HttpsContentNew> getContent() {
        return content;
    }

    public void setContent(List<HttpsContentNew> content) {
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
        return "HttpsAilseData{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", content=" + content +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
