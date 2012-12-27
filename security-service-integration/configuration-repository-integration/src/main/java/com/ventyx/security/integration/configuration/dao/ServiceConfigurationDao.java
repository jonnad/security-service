package com.ventyx.security.integration.configuration.dao;

import com.ventyx.security.api.model.ServiceConfiguration;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: USJOHAM
 * Date: 12/6/12
 * Time: 2:21 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class ServiceConfigurationDao extends HibernateDaoSupport {


    @Autowired
    public void init(SessionFactory factory) {
        setSessionFactory(factory);
    }

    public void saveOrUpdate(ServiceConfiguration serviceConfiguration) {
        getHibernateTemplate().saveOrUpdate(serviceConfiguration);
    }

    public void delete(ServiceConfiguration serviceConfiguration) {
        getHibernateTemplate().delete(serviceConfiguration);
    }

    public ServiceConfiguration find(Integer id) {
        return (ServiceConfiguration) getHibernateTemplate().get(ServiceConfiguration.class, id);
    }

    public List findAll() {
        return getHibernateTemplate().find("from " + ServiceConfiguration.class.getName());
    }


}
