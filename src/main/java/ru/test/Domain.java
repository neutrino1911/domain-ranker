package ru.test;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "domains")
public class Domain implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String domain;
    private long count;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public synchronized void incCount() {
        count++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Domain domain1 = (Domain) o;

        return domain.equals(domain1.domain);
    }

    @Override
    public int hashCode() {
        return domain.hashCode();
    }
}
