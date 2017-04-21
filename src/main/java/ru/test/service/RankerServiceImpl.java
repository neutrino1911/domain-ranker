package ru.test.service;

import ru.test.Domain;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Stateless
public class RankerServiceImpl implements RankerService {

    @EJB
    private DomainCacheService domainCacheService;

    private final static String URL_VALIDATION_TEMPLATE = "^(?:(?:https?|ftp)://)(?:\\S+(?::\\S*)?@)?(?:(?!10(?:\\.\\d{1,3}){3})(?!127(?:\\.\\d{1,3}){3})(?!169\\.254(?:\\.\\d{1,3}){2})(?!192\\.168(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\x{00a1}-\\x{ffff}0-9]+-?)*[a-z\\x{00a1}-\\x{ffff}0-9]+)(?:\\.(?:[a-z\\x{00a1}-\\x{ffff}0-9]+-?)*[a-z\\x{00a1}-\\x{ffff}0-9]+)*(?:\\.(?:[a-z\\x{00a1}-\\x{ffff}]{2,})))(?::\\d{2,5})?(?:/[^\\s]*)?$";
    private final static String URL_TRIMER = "^[^/]+//([^/]+)/?.*$";

    @Override
    public boolean add(String URI) {
        Matcher m1 = Pattern.compile(URL_VALIDATION_TEMPLATE).matcher(URI);
        if (!m1.matches())
            throw new IllegalArgumentException();
        Matcher m2 = Pattern.compile(URL_TRIMER).matcher(URI);
        if (!m2.matches())
            throw new IllegalArgumentException();
        String sDomain = m2.group(1);
        Domain domain = domainCacheService.getCache().get(sDomain);
        if (domain == null) {
            return domainCacheService.add(sDomain) != null;
        }
        domain.incCount();
        return true;
    }

    @Override
    public List<Domain> get(int count) {
        List<Domain> list = new ArrayList<>(domainCacheService.getCache().values());
        Collections.sort(list, new Comparator<Domain>() {
            @Override
            public int compare(Domain d1, Domain d2) {
                return (int) (d2.getCount() - d1.getCount());
            }
        });
        if (count < domainCacheService.getCache().size()) {
            list = list.subList(0, count);
        }
        return list;
    }
}
