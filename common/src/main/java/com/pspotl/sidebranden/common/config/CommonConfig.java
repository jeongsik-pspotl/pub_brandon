package com.pspotl.sidebranden.common.config;

import com.pspotl.sidebranden.common.branchsetting.BranchSettingDaoImpl;
import com.pspotl.sidebranden.common.build.BuildProjectDaoImpl;
import com.pspotl.sidebranden.common.builderqueue.BuilderQueueManagedgDaoImpl;
import com.pspotl.sidebranden.common.buildhistory.BuildHistoryDaoImpl;
import com.pspotl.sidebranden.common.buildsetting.BuildSettingDaoImpl;
import com.pspotl.sidebranden.common.deploy.DeploySettingDaoImpl;
import com.pspotl.sidebranden.common.deployhistory.DeployHistoryDaoImpl;
import com.pspotl.sidebranden.common.domain.DomainDaoImpl;
import com.pspotl.sidebranden.common.ftpsetting.FTPSettingDaoImpl;
import com.pspotl.sidebranden.common.member.MemberDaoImpl;
import com.pspotl.sidebranden.common.member.MemberLoginDaoImpl;
import com.pspotl.sidebranden.common.menu.MenuDaoImpl;
import com.pspotl.sidebranden.common.pricing.PricingDaoImpl;
import com.pspotl.sidebranden.common.role.RoleDaoImpl;
import com.pspotl.sidebranden.common.rolecode.RoleCodeDaoImpl;
import com.pspotl.sidebranden.common.settings.AllBranchSettingsDaoImpl;
import com.pspotl.sidebranden.common.signingkeysetting.KeySettingDaoImpl;
import com.pspotl.sidebranden.common.usercode.UserKeyCodeImpl;
import com.pspotl.sidebranden.common.vcssetting.VCSSettingDaoImpl;
import com.pspotl.sidebranden.common.workspace.WorkspaceDaoImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:/application.yml")
@RequiredArgsConstructor
public class CommonConfig
{
    @Bean
    public MemberLoginDaoImpl memberLoginDaoImpl(JdbcTemplate jdbcTemplate) {
        MemberLoginDaoImpl memberLogin = new MemberLoginDaoImpl();
        memberLogin.setJdbcTemplate(jdbcTemplate);
        return memberLogin;
    }

    @Bean
    public MemberDaoImpl memberDaoImpl(JdbcTemplate jdbcTemplate) {
        MemberDaoImpl member = new MemberDaoImpl();
        member.setJdbcTemplate(jdbcTemplate);
        return member;
    }

    @Bean
    public WorkspaceDaoImpl workspaceDaoImpl(JdbcTemplate jdbcTemplate) {
        WorkspaceDaoImpl workspace = new WorkspaceDaoImpl();
        workspace.setJdbcTemplate(jdbcTemplate);
        return workspace;
    }

    @Bean
    public BuildProjectDaoImpl buildProjectDaoImpl(JdbcTemplate jdbcTemplate) {
        BuildProjectDaoImpl buildproject = new BuildProjectDaoImpl();
        buildproject.setJdbcTemplate(jdbcTemplate);
        return buildproject;
    }

    @Bean
    public BuildSettingDaoImpl buildSettingDaoImpl(JdbcTemplate jdbcTemplate) {
        BuildSettingDaoImpl buildsetting = new BuildSettingDaoImpl();
        buildsetting.setJdbcTemplate(jdbcTemplate);
        return buildsetting;
    }

    @Bean
    public BuildHistoryDaoImpl buildHistoryDaoImpl(JdbcTemplate jdbcTemplate){
        BuildHistoryDaoImpl buildHistory = new BuildHistoryDaoImpl();
        buildHistory.setJdbcTemplate(jdbcTemplate);
        return buildHistory;
    }

    @Bean
    public AllBranchSettingsDaoImpl allBranchSettingsDaoImpl(JdbcTemplate jdbcTemplate){
        AllBranchSettingsDaoImpl allBranchSettings = new AllBranchSettingsDaoImpl();
        allBranchSettings.setJdbcTemplate(jdbcTemplate);
        return allBranchSettings;
    }

    @Bean
    public BranchSettingDaoImpl branchSettingDao(JdbcTemplate jdbcTemplate){
        BranchSettingDaoImpl branchSettingDao = new BranchSettingDaoImpl();
        branchSettingDao.setJdbcTemplate(jdbcTemplate);
        return branchSettingDao;
    }

    @Bean
    public VCSSettingDaoImpl vcsSettingDao(JdbcTemplate jdbcTemplate){
        VCSSettingDaoImpl vcsSettingDao = new VCSSettingDaoImpl();
        vcsSettingDao.setJdbcTemplate(jdbcTemplate);
        return vcsSettingDao;
    }

    @Bean
    public FTPSettingDaoImpl ftpSettingDao(JdbcTemplate jdbcTemplate){
        FTPSettingDaoImpl ftpSettingDao = new FTPSettingDaoImpl();
        ftpSettingDao.setJdbcTemplate(jdbcTemplate);
        return ftpSettingDao;
    }

    @Bean
    public KeySettingDaoImpl signingKeySettingDao(JdbcTemplate jdbcTemplate){
        KeySettingDaoImpl keySettingDao = new KeySettingDaoImpl();
        keySettingDao.setJdbcTemplate(jdbcTemplate);
        return keySettingDao;
    }

    @Bean
    public RoleCodeDaoImpl roleCodeDao(JdbcTemplate jdbcTemplate){
        RoleCodeDaoImpl roleCodeDao = new RoleCodeDaoImpl();
        roleCodeDao.setJdbcTemplate(jdbcTemplate);
        return roleCodeDao;
    }

    @Bean
    public DomainDaoImpl domainDao(JdbcTemplate jdbcTemplate){
        DomainDaoImpl domainDao = new DomainDaoImpl();
        domainDao.setJdbcTemplate(jdbcTemplate);
        return domainDao;
    }

    @Bean
    public DeploySettingDaoImpl deploySettingDao(JdbcTemplate jdbcTemplate){
        DeploySettingDaoImpl deploySettingDao = new DeploySettingDaoImpl();
        deploySettingDao.setJdbcTemplate(jdbcTemplate);
        return deploySettingDao;
    }

    @Bean
    public RoleDaoImpl roleDao(JdbcTemplate jdbcTemplate){
        RoleDaoImpl roleDao = new RoleDaoImpl();
        roleDao.setJdbcTemplate(jdbcTemplate);
        return roleDao;
    }

    @Bean
    public DeployHistoryDaoImpl deployHistoryDao(JdbcTemplate jdbcTemplate){
        DeployHistoryDaoImpl deployHistoryDao = new DeployHistoryDaoImpl();
        deployHistoryDao.setJdbcTemplate(jdbcTemplate);
        return deployHistoryDao;
    }

    @Bean
    public UserKeyCodeImpl userKeyCodeDao(JdbcTemplate jdbcTemplate){
        UserKeyCodeImpl userKeyCodeDao = new UserKeyCodeImpl();
        userKeyCodeDao.setJdbcTemplate(jdbcTemplate);
        return userKeyCodeDao;
    }

    @Bean
    public MenuDaoImpl menuDao(JdbcTemplate jdbcTemplate) {
        MenuDaoImpl menuDao = new MenuDaoImpl();
        menuDao.setJdbcTemplate(jdbcTemplate);
        return menuDao;
    }

    @Bean
    public BuilderQueueManagedgDaoImpl builderQueueManagedgDao(JdbcTemplate jdbcTemplate){
        BuilderQueueManagedgDaoImpl builderQueueManagedgDao = new BuilderQueueManagedgDaoImpl();
        builderQueueManagedgDao.setJdbcTemplate(jdbcTemplate);
        return builderQueueManagedgDao;

    }

    @Bean
    public PricingDaoImpl pricingDao(JdbcTemplate jdbcTemplate){
        PricingDaoImpl pricingDao = new PricingDaoImpl();
        pricingDao.setJdbcTemplate(jdbcTemplate);
        return pricingDao;
    }

//    @Bean
//    public DataSource dataSource() throws Exception {
//        DataSource dataSource = new HikariDataSource(hikariConfig());
//        System.out.println(dataSource.toString());
//        return dataSource;
//    }

//    @Bean
//    @ConfigurationProperties(prefix="spring.datasource.hikari")
//    public HikariConfig hikariConfig() {
//        return new HikariConfig();
//    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
