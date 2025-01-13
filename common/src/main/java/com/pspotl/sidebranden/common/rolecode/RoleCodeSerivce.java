package com.pspotl.sidebranden.common.rolecode;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class RoleCodeSerivce {

    @Autowired
    private RoleCodeDaoImpl roleCodeDaoImpl;

    @Transactional
    public List<RoleCode> findByIdDetail(String codeType){
        return roleCodeDaoImpl.findByCodeTypeList(codeType);
    }

}
