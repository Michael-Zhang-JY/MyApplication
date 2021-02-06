package com.example.myapplication.HttpData;

public class HttpsMerchandiseNews {
    private String code;
    private String message;
    private HttpsMerchandiseContent content;
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

    public HttpsMerchandiseContent getContent() {
        return content;
    }

    public void setContent(HttpsMerchandiseContent content) {
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
        return "HttpsMerchandiseNews{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", content=" + content +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
