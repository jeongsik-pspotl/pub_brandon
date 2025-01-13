package com.pspotl.sidebranden.common.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DomainService {

    @Autowired
    private DomainDaoImpl domainDaoImpl;

    @Transactional
    public List<Domain> findByDomainList(){

        return domainDaoImpl.findByDomainList();
    }

    @Transactional
    public List<Domain> findAll(){
        return domainDaoImpl.findAll();
    }

    @Transactional
    public Domain findByID(Long domain_id){
        return domainDaoImpl.findByID(domain_id);
    }

    @Transactional
    public Domain findByDomainName(String domain_name) { return domainDaoImpl.findByDomainName(domain_name); }

    @Transactional
    public void createDomain(Domain domain){
        domain.setCreate_date(LocalDateTime.now());
        domain.setUpdated_date(LocalDateTime.now());
        domainDaoImpl.createDomain(domain);
    }

    @Transactional
    public void  updateDomain(Long domain_id, String domain_name){
        Domain domain = new Domain();

        domain.setDomain_id(domain_id);
        domain.setDomain_name(domain_name);
        domain.setUpdated_date(LocalDateTime.now());

        domainDaoImpl.updateDomain(domain);
    }
}
