package com.ventyx.security.integration.configuration;

import com.ventyx.security.api.model.Authentication;
import com.ventyx.security.api.model.ServiceDefinition;
import com.ventyx.security.integration.configuration.dao.AuthenticationDao;
import com.ventyx.security.integration.configuration.dao.ServiceConfigurationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServiceConfigurationService {

    @Autowired
    ServiceConfigurationDao serviceConfigurationDao;

    @Autowired
    AuthenticationDao authenticationDao;

    @Transactional
    public void createServiceConfiguration(ServiceDefinition serviceDefinition) {
        serviceConfigurationDao.saveOrUpdate(serviceDefinition);
    }

    public ServiceDefinition getServiceConfiguration(Long id) {
        return serviceConfigurationDao.find(id);
    }

    public List<ServiceDefinition> getServiceConfigurations() {
        return serviceConfigurationDao.findAll();
    }

    public Authentication getAuthentication(String applicationUser) {
        return authenticationDao.findByApplicationUser(applicationUser);
    }
}
