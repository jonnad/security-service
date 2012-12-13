package com.ventyx.security.api.model;

/**
 * Class representing data required to construct a valid security token for a user/service
 */
public class Token {

    private String restriction;
    private String issuer;
    private String nameID;
    private String nameQualifier;
    private String sessionId;
    private int maxSessionTimeoutInMinutes = 15; // default is 15 minutes





}
