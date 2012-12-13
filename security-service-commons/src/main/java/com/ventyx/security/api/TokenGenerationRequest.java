package com.ventyx.security.api;

import com.ventyx.security.api.model.TokenFormat;

/**
 *  Contains the fields required for a serviced application to procure a token
 */

public class TokenGenerationRequest {

    private String applicationId;
    private String applicationUser;
    private String applicationPassword;
    private String serviceName;
    private TokenFormat tokenFormat;
    private String userPrincipal;
    private String userPassword;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationUser() {
        return applicationUser;
    }

    public void setApplicationUser(String applicationUser) {
        this.applicationUser = applicationUser;
    }

    public String getApplicationPassword() {
        return applicationPassword;
    }

    public void setApplicationPassword(String applicationPassword) {
        this.applicationPassword = applicationPassword;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public TokenFormat getTokenFormat() {
        return tokenFormat;
    }

    public void setTokenFormat(TokenFormat tokenFormat) {
        this.tokenFormat = tokenFormat;
    }

    public String getUserPrincipal() {
        return userPrincipal;
    }

    public void setUserPrincipal(String userPrincipal) {
        this.userPrincipal = userPrincipal;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}
