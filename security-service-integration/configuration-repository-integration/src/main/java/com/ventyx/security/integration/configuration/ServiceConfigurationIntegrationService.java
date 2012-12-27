package com.ventyx.security.integration.configuration;

import com.ventyx.security.api.model.Authentication;
import com.ventyx.security.api.model.ServiceConfiguration;
import com.ventyx.security.api.model.Token;
import com.ventyx.security.integration.configuration.dao.AuthenticationDao;
import com.ventyx.security.integration.configuration.dao.ServiceConfigurationDao;
import com.ventyx.security.integration.configuration.dao.TokenDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServiceConfigurationIntegrationService {

    @Autowired
    ServiceConfigurationDao serviceConfigurationDao;

    @Autowired
    AuthenticationDao authenticationDao;

    @Autowired
    TokenDao tokenDao;

    @Transactional
    public void createOrUpdateServiceConfiguration(ServiceConfiguration serviceConfiguration) {
        serviceConfigurationDao.saveOrUpdate(serviceConfiguration);
    }

    public ServiceConfiguration getServiceConfiguration(Integer id) {
        return serviceConfigurationDao.find(id);
    }

    public List<ServiceConfiguration> getServiceConfigurations() {
        return serviceConfigurationDao.findAll();
    }

    public Authentication getAuthentication(String applicationUser) {
        return authenticationDao.findByApplicationUser(applicationUser);
    }

    public void createOrUpdateToken(Token token) {
        tokenDao.saveOrUpdate(token);
    }

}
