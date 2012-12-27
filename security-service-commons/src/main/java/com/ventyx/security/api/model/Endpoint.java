package com.ventyx.security.api.model;

import javax.persistence.*;

/**
 * Class representing a distinct service endpoint
 *
 */
@Entity(name = "Endpoint")
public class Endpoint {

    private Integer id;
    private String name;
    private String serviceUri;
    private String serviceHost;
    private Integer servicePort;
    private boolean enabled = true;
    private boolean matchPartialUri = true;
    private ServiceConfiguration serviceConfiguration;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(length = 120, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(nullable = false, length = 2000)
    public String getServiceUri() {
        return serviceUri;
    }

    public void setServiceUri(String serviceUri) {
        this.serviceUri = serviceUri;
    }

    @Column(length = 512, nullable = false)
    public String getServiceHost() {
        return serviceHost;
    }

    public void setServiceHost(String serviceHost) {
        this.serviceHost = serviceHost;
    }

    @Column(nullable = false)
    public Integer getServicePort() {
        return servicePort;
    }

    public void setServicePort(Integer servicePort) {
        this.servicePort = servicePort;
    }

    @Column(nullable = false)
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Column(nullable = false)
    public boolean getMatchPartialUri() {
        return matchPartialUri;
    }

    public void setMatchPartialUri(boolean matchPartialUri) {
        this.matchPartialUri = matchPartialUri;
    }

    @ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
    @JoinColumn(name="service_configuration_id")
    public ServiceConfiguration getServiceConfiguration() {
        return serviceConfiguration;
    }

    public void setServiceConfiguration(ServiceConfiguration serviceConfiguration) {
        this.serviceConfiguration = serviceConfiguration;
    }
}
