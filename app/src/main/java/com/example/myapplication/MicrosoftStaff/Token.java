package com.example.myapplication.MicrosoftStaff;

public class Token {
    private TokenInfo tokenInfo;

    public TokenInfo getTokenInfo() {
        return tokenInfo;
    }

    public void setTokenInfo(TokenInfo tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

    @Override
    public String toString() {
        return "Token{" +
                "tokenInfo=" + tokenInfo +
                '}';
    }
}
