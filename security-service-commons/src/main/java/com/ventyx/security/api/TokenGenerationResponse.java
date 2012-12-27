package com.ventyx.security.api;

import com.ventyx.security.api.model.Token;

/**
 * Represents a token generation response
 */
public class TokenGenerationResponse {

    private Token token;
    private String errorCode = "0";
    private String errorMessage = "";

    public TokenGenerationResponse() {
        super();
    }

    public TokenGenerationResponse(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
