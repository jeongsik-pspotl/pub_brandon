package com.inswave.whive.common.rolecode;

import java.util.List;

public interface RoleCodeDao {

    List<RoleCode> findByCodeTypeList(String coodeType);

}
