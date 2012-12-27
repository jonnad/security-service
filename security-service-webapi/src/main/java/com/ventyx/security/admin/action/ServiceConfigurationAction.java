package com.ventyx.security.admin.action;

import com.opensymphony.xwork2.ActionSupport;
import com.ventyx.security.api.model.ServiceConfiguration;
import com.ventyx.security.exception.SecurityServiceException;
import com.ventyx.security.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Lists and maintains service configurations
 */

@Component
public class ServiceConfigurationAction extends ActionSupport {

    public static final String LIST = "list";
    public static final String EDIT = "edit";
    public static final String SAVE = "save";
    public static final String CREATE = "create";

    //transient fields for booleans (bean methods fail for some reason)
    private boolean enabled = false;
    private boolean secured = false;
    private boolean requiresUserAuthentication = false;

    @Autowired
    ConfigurationService configurationService;

    private Integer serviceConfigurationId;
    private List<ServiceConfiguration> serviceConfigurations;
    private ServiceConfiguration serviceConfiguration;

    public String list() {

        this.serviceConfigurations = configurationService.getServiceConfigurations();
        return LIST;

    }

    public String edit() {
        try {
            this.serviceConfiguration = configurationService.getServiceConfiguration(serviceConfigurationId);
            this.setEnabled(serviceConfiguration.isEnabled());
            this.setSecured(serviceConfiguration.isSecured());
            this.setRequiresUserAuthentication(serviceConfiguration.isRequiresUserAuthentication());
        } catch (SecurityServiceException ex) {
            ex.printStackTrace();
        }

        return EDIT;
    }

    public String create() {
        return CREATE;
    }

    public String save() {
        try {
            serviceConfiguration.setEnabled(this.enabled);
            serviceConfiguration.setSecured(this.secured);
            serviceConfiguration.setRequiresUserAuthentication(this.requiresUserAuthentication);

            configurationService.createOrUpdateServiceConfiguration(this.serviceConfiguration);
        } catch (SecurityServiceException ex) {
            ex.printStackTrace();
        }

        return SAVE;
    }

    public List<ServiceConfiguration> getServiceConfigurations() {
        return serviceConfigurations;
    }

    public void setServiceConfigurations(List<ServiceConfiguration> serviceConfigurations) {
        this.serviceConfigurations = serviceConfigurations;
    }

    public ServiceConfiguration getServiceConfiguration() {
        return serviceConfiguration;
    }

    public void setServiceConfiguration(ServiceConfiguration serviceConfiguration) {
        this.serviceConfiguration = serviceConfiguration;
    }

    public Integer getServiceConfigurationId() {
        return serviceConfigurationId;
    }

    public void setServiceConfigurationId(Integer serviceConfigurationId) {
        this.serviceConfigurationId = serviceConfigurationId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

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
}
