package com.ventyx.security.integration.configuration.dao;

import com.ventyx.security.api.model.Token;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Token DAO
 */
@Repository
public class TokenDao extends HibernateDaoSupport {

    @Autowired
    public void init(SessionFactory factory) {
        setSessionFactory(factory);
    }

    public void saveOrUpdate(Token token) {
        getHibernateTemplate().saveOrUpdate(token);
    }

    public void delete(Token token) {
        getHibernateTemplate().delete(token);
    }

    public Token find(Long id) {
        return (Token) getHibernateTemplate().load(Token.class, id);
    }

    public List findAll() {
        return getHibernateTemplate().find("from " + Token.class.getName());
    }

    public Token findByTokenId(String tokenId) {

        String hql = "from Token toke where toke.tokenId = :tokenId";
        Query query = getSession().createQuery(hql);
        query.setString("tokenId", tokenId);

        List<Token> tokens = query.list();

        if (tokens != null && !tokens.isEmpty()) {
            return tokens.get(0);
        }
        return null;
    }

}
