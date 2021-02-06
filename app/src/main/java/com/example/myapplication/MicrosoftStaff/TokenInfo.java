package com.example.myapplication.MicrosoftStaff;

public class TokenInfo {
    private boolean Success;
    private String Token;
    private String Message;

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean success) {
        Success = success;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    @Override
    public String toString() {
        return "TokenInfo{" +
                "Success=" + Success +
                ", Token='" + Token + '\'' +
                ", Message='" + Message + '\'' +
                '}';
    }
}
