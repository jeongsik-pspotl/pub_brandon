package com.inswave.whive.common.domain;

import java.util.List;

public interface DomainDao {

    Domain findByID(Long domain_id);

    List<Domain> findByDomainList();

    List<Domain> findAll();

    Domain findByDomainName(String domain_name);

    void createDomain(Domain domain);

    void updateDomain(Domain domain);

}
