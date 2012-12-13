package com.ventyx.security.api.model;

import javax.persistence.*;
import java.util.List;

/**
 *  Defines a service for the proxy to control
 */

@Entity(name = "ServiceDefinition")
public class ServiceDefinition {

    private Integer id;
    private String name;
    private String description;
    private boolean enabled = true;
    private boolean secured = true;
    private boolean requiresUserAuthentication = false;
    private UserAuthenticationScheme userAuthenticationScheme = UserAuthenticationScheme.NONE;

    private List<Endpoint> endpoints;
    private List<Authentication> authentications;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(nullable = false, length = 250, unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(nullable = false, length = 250, unique = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(nullable = false)
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Column(nullable = false)
    public boolean isSecured() {
        return secured;
    }

    public void setSecured(boolean secured) {
        this.secured = secured;
    }

    public boolean isRequiresUserAuthentication() {
        return requiresUserAuthentication;
    }

    public void setRequiresUserAuthentication(boolean requiresUserAuthentication) {
        this.requiresUserAuthentication = requiresUserAuthentication;
    }

    public UserAuthenticationScheme getUserAuthenticationScheme() {
        return userAuthenticationScheme;
    }

    public void setUserAuthenticationScheme(UserAuthenticationScheme userAuthenticationScheme) {
        this.userAuthenticationScheme = userAuthenticationScheme;
    }

    @OneToMany(cascade = CascadeType.ALL ,fetch = FetchType.EAGER, mappedBy = "serviceDefinition")
    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "service_authentications", joinColumns = { @JoinColumn(name = "service_id") }, inverseJoinColumns = { @JoinColumn(name = "authentication_id") })
    public List<Authentication> getAuthentications() {
        return authentications;
    }

    public void setAuthentications(List<Authentication> authentications) {
        this.authentications = authentications;
    }

    public enum UserAuthenticationScheme {
        LDAP,
        ACTIVEDIRECTORY,
        REPOSITORY,
        NONE
    }
}
