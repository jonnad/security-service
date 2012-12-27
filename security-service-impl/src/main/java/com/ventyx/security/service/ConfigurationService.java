package com.ventyx.security.service;

import com.ventyx.security.api.model.Authentication;
import com.ventyx.security.api.model.ServiceConfiguration;
import com.ventyx.security.exception.SecurityServiceException;
import com.ventyx.security.integration.configuration.ServiceConfigurationIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Qualifier("configurationService")
public class ConfigurationService {

    @Autowired
    ServiceConfigurationIntegrationService serviceConfigurationIntegrationService;

    @Transactional
    public void createOrUpdateServiceConfiguration(ServiceConfiguration serviceConfiguration) throws SecurityServiceException {

        if (serviceConfiguration == null) {
            throw new SecurityServiceException("A serviceConfiguration is required");
        }

        if (serviceConfiguration.getName() == null) {
            throw new SecurityServiceException("A serviceConfiguration name is required");
        }

        serviceConfigurationIntegrationService.createOrUpdateServiceConfiguration(serviceConfiguration);
    }

    public ServiceConfiguration getServiceConfiguration(Integer id) throws SecurityServiceException {

        if (id == null) {
            throw new SecurityServiceException("A serviceConfiguration name is required");
        }
        return serviceConfigurationIntegrationService.getServiceConfiguration(id);
    }

    public List<ServiceConfiguration> getServiceConfigurations() {
        return serviceConfigurationIntegrationService.getServiceConfigurations();
    }

    public Authentication getAuthentication(String applicationUser) {
        return serviceConfigurationIntegrationService.getAuthentication(applicationUser);
    }


}
