package ru.test.service;

import ru.test.Domain;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@Startup
public class DomainCacheServiceImpl implements DomainCacheService {

    @PersistenceContext(unitName = "ejb-test-unit")
    private EntityManager entityManager;
    private ConcurrentHashMap<String, Domain> cache;

    @Override
    public Domain add(String sDomain) {
        Query query= entityManager.createQuery("SELECT D FROM Domain D WHERE D.domain=:domain");
        query.setParameter("domain", sDomain);
        List<Domain> list = query.getResultList();
        Domain domain;
        if (list.size() > 0) {
            domain = list.get(0);
            domain.incCount();
        } else {
            domain = new Domain();
            domain.setDomain(sDomain);
            domain.setCount(1);
        }
        return cache.put(sDomain, domain);
    }

    @Override
    public Map<String, Domain> getCache() {
        return cache;
    }

    @PostConstruct
    @Schedule(hour = "*", minute = "*/10", persistent = false)
    private void loadCache() {
        if (cache != null)
            saveCache();
        Query query = entityManager.createQuery("SELECT D FROM Domain D ORDER BY D.count DESC");
        List<Domain> list = query.setMaxResults(100).getResultList();
        ConcurrentHashMap<String, Domain> newMap = new ConcurrentHashMap<>(100);
        for (Domain domain : list) {
            newMap.put(domain.getDomain(), domain);
        }
        cache = newMap;
    }

    @PreDestroy
    private void saveCache() {
        for (Domain domain : cache.values()) {
            entityManager.merge(domain);
        }
    }
}
