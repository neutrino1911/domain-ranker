package ru.test.service;

import ru.test.Domain;

import java.util.Map;

public interface DomainCacheService {

    Domain add(String domain);
    Map<String, Domain> getCache();
}
