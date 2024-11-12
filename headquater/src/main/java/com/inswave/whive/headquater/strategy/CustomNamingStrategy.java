package com.inswave.whive.headquater.strategy;

import com.inswave.whive.headquater.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;

import java.util.Locale;

@Slf4j
public class CustomNamingStrategy extends SpringPhysicalNamingStrategy {

    @Override public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {

        Identifier superIdentifier = super.toPhysicalTableName(name, jdbcEnvironment);
        String tableName = superIdentifier.getText();
        if(tableName.toLowerCase(Locale.ROOT).contains("${yyyymm}")) {
            tableName = tableName.replace("${yyyymm}", DateUtil.getCurrentDate("yyyyMM"));
            tableName = tableName.toUpperCase(Locale.ROOT);
            return new Identifier(tableName, name.isQuoted());
        } else if(tableName.equalsIgnoreCase("V_IPLIST")) {
            tableName = tableName.toUpperCase(Locale.ROOT);
            return new Identifier(tableName, name.isQuoted());
        }
        return this.getIdentifier(tableName, name.isQuoted(), jdbcEnvironment);
    }

}
