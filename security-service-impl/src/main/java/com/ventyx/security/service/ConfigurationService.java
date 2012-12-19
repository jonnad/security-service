package com.ventyx.security.service;

import com.ventyx.security.api.model.Authentication;
import com.ventyx.security.api.model.ServiceDefinition;
import com.ventyx.security.exception.SecurityServiceException;
import com.ventyx.security.integration.configuration.ServiceConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Qualifier("configurationService")
public class ConfigurationService {

    @Autowired
    ServiceConfigurationService serviceConfigurationService;

    @Transactional
    public void createOrUpdateServiceConfiguration(ServiceDefinition serviceDefinition) throws SecurityServiceException {

        if (serviceDefinition == null) {
            throw new SecurityServiceException("A serviceDefinition is required");
        }

        if (serviceDefinition.getName() == null) {
            throw new SecurityServiceException("A serviceDefinition name is required");
        }

        serviceConfigurationService.createOrUpdateServiceConfiguration(serviceDefinition);
    }

    public ServiceDefinition getServiceConfiguration(Integer id) throws SecurityServiceException {

        if (id == null) {
            throw new SecurityServiceException("A serviceDefinition name is required");
        }
        return serviceConfigurationService.getServiceConfiguration(id);
    }

    public List<ServiceDefinition> getServiceConfigurations() {
        return serviceConfigurationService.getServiceConfigurations();
    }

    public Authentication getAuthentication(String applicationUser) {
        return serviceConfigurationService.getAuthentication(applicationUser);
    }


}
