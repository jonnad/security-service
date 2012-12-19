package com.ventyx.security.admin.action;

import com.opensymphony.xwork2.ActionSupport;
import com.ventyx.security.api.model.ServiceDefinition;
import com.ventyx.security.exception.SecurityServiceException;
import com.ventyx.security.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Lists and maintains service definitions
 */

@Component
public class ServiceDefinitionAction extends ActionSupport {

    public static final String LIST = "list";
    public static final String EDIT = "edit";
    public static final String SAVE = "save";

    @Autowired
    ConfigurationService configurationService;

    private Integer serviceDefinitionId;
    private List<ServiceDefinition> serviceDefinitions;
    private ServiceDefinition serviceDefinition;

    public String list() {

        this.serviceDefinitions = configurationService.getServiceConfigurations();
        return LIST;

    }

    public String edit() {
        try {
            this.serviceDefinition = configurationService.getServiceConfiguration(serviceDefinitionId);
        } catch (SecurityServiceException ex) {
            ex.printStackTrace();
        }

        return EDIT;
    }

    public String save() {
        try {
            configurationService.createOrUpdateServiceConfiguration(this.serviceDefinition);
        } catch (SecurityServiceException ex) {
            ex.printStackTrace();
        }

        return SAVE;
    }

    public List<ServiceDefinition> getServiceDefinitions() {
        return serviceDefinitions;
    }

    public void setServiceDefinitions(List<ServiceDefinition> serviceDefinitions) {
        this.serviceDefinitions = serviceDefinitions;
    }

    public ServiceDefinition getServiceDefinition() {
        return serviceDefinition;
    }

    public void setServiceDefinition(ServiceDefinition serviceDefinition) {
        this.serviceDefinition = serviceDefinition;
    }

    public Integer getServiceDefinitionId() {
        return serviceDefinitionId;
    }

    public void setServiceDefinitionId(Integer serviceDefinitionId) {
        this.serviceDefinitionId = serviceDefinitionId;
    }
}
