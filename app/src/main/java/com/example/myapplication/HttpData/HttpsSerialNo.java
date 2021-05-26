package com.example.myapplication.HttpData;

public class HttpsSerialNo {
    private String code;
    private String message;
    private SerialNoContent content;
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

    public SerialNoContent getContent() {
        return content;
    }

    public void setContent(SerialNoContent content) {
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
        return "HttpsSerialNo{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", content=" + content +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
