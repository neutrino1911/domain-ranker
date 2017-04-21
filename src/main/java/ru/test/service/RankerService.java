package ru.test.service;

import ru.test.Domain;

import java.util.List;

public interface RankerService {

    boolean add(String URI);
    List<Domain> get(int count);
}
