package com.ventyx.security.api.model;

import javax.persistence.*;
import java.util.List;

/**
 * Maintains service-level credentials, which are not for end users, rather
 * applications for server-to-server back channel communication
 */
@Entity(name = "Authentication")
public class Authentication {

    private Integer id;
    private String applicationUser;
    private String applicationPassword; //todo encrypt
    private List<ServiceConfiguration> serviceConfigurations;
    private boolean enabled = true;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(nullable = false, length = 120)
    public String getApplicationUser() {
        return applicationUser;
    }

    public void setApplicationUser(String applicationUser) {
        this.applicationUser = applicationUser;
    }

    @Column(nullable = false, length = 120)
    public String getApplicationPassword() {
        return applicationPassword;
    }

    public void setApplicationPassword(String applicationPassword) {
        this.applicationPassword = applicationPassword;
    }

    @ManyToMany(mappedBy = "authentications")
    public List<ServiceConfiguration> getServiceConfigurations() {
        return serviceConfigurations;
    }

    public void setServiceConfigurations(List<ServiceConfiguration> serviceConfiguration) {
        this.serviceConfigurations = serviceConfiguration;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
