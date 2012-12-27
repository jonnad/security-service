package com.ventyx.security.api.model;

import javax.persistence.*;

/**
 * Class representing a unique instance of a token, mainly for logging and verification purposes
 */

@Entity(name = "Token")
public class Token {

    private long id;
    private String tokenId;
    private ServiceConfiguration serviceConfiguration;
    private String value;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(nullable = false, length = 512)
    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }


    @ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
    @JoinColumn(name="service_configuration_id")
    public ServiceConfiguration getServiceConfiguration() {
        return serviceConfiguration;
    }

    public void setServiceConfiguration(ServiceConfiguration serviceConfiguration) {
        this.serviceConfiguration = serviceConfiguration;
    }

    @Transient
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
