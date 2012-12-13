package com.ventyx.security.integration.configuration.dao;

import com.ventyx.security.api.model.Authentication;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: USJOHAM
 * Date: 12/6/12
 * Time: 2:21 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class AuthenticationDao extends HibernateDaoSupport {

    @Autowired
    public void init(SessionFactory factory) {
        setSessionFactory(factory);
    }

    public void saveOrUpdate(Authentication authentication) {
        getHibernateTemplate().saveOrUpdate(authentication);
    }

    public void delete(Authentication authentication) {
        getHibernateTemplate().delete(authentication);
    }

    public Authentication find(Long id) {
        return (Authentication) getHibernateTemplate().load(Authentication.class, id);
    }

    public List findAll() {
        return getHibernateTemplate().find("from " + Authentication.class.getName());
    }

    public Authentication findByApplicationUser(String applicationUser) {

        String hql = "from Authentication auth where auth.applicationUser = :applicationUser";
        Query query = getSession().createQuery(hql);
        query.setString("applicationUser", applicationUser);

        List<Authentication> authentications = query.list();

        if (authentications != null && !authentications.isEmpty()) {
            return authentications.get(0);
        }
        return null;
    }

}
