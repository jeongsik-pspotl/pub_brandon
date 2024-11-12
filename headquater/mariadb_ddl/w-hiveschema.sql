use whive;

create table deploy_history
(
    deploy_history_id int auto_increment
        primary key,
    project_id        int          not null,
    deploy_id         int          not null,
    status_log        varchar(300) null,
    log_path          varchar(255) null,
    result            varchar(50)  null,
    logfile_name      varchar(100) null,
    deploy_start_date datetime     null,
    deploy_end_date   datetime     null
);

create table deploy_setting
(
    deploy_id         int auto_increment
        primary key,
    build_id          int          not null,
    all_package_name  varchar(255) null,
    apple_key_id      varchar(255) null,
    apple_issuer_id   varchar(255) null,
    created_date      datetime     null,
    updated_date      datetime     null,
    all_signingkey_id int          null
);

create table builder_setting
(
    builder_id            int auto_increment
        primary key,
    role_code_id          varchar(5)   null,
    builder_user_id       varchar(50)  null,
    builder_platform_type varchar(8)   null,
    builder_name          varchar(255) null,
    session_status        varchar(20)  null,
    session_type          varchar(100) null,
    builder_url           varchar(100) null,
    created_date          datetime     null,
    updated_date          datetime     null,
    last_date             datetime     null,
    builder_password      varchar(200) null
);

create table domain
(
    domain_id    int auto_increment
        primary key,
    domain_name  varchar(255) not null,
    create_date  datetime     not null,
    updated_date datetime     null
);

create table ftp_settings
(
    ftp_id       int auto_increment
        primary key,
    ftp_name     varchar(255) not null,
    ftp_url      varchar(255) not null,
    ftp_type     varchar(20)  null,
    ftp_etc1     varchar(100) null,
    ftp_etc2     varchar(100) null,
    create_date  datetime     null,
    update_date  datetime     null,
    ftp_ip       varchar(20)  not null,
    ftp_port     int(9)       not null,
    ftp_user_id  varchar(100) not null,
    ftp_user_pwd varchar(255) not null
);

create table key_android_setting
(
    key_id                     int          not null
        primary key,
    android_key_type           varchar(8)   null,
    android_key_path           varchar(255) null,
    android_key_password       varchar(255) null,
    android_key_alias          varchar(255) null,
    android_key_store_password varchar(255) null,
    android_deploy_key_path    varchar(200) null
);

create table key_group_role
(
    key_group_role_id     int auto_increment
        primary key,
    role_id               int not null,
    key_build_android_id  int null,
    key_build_ios_id      int null,
    key_deploy_android_id int null,
    key_deploy_ios_id     int null
);

create table key_ios_setting
(
    key_id                       int                          not null
        primary key,
    ios_key_type                 varchar(8)                   null,
    ios_key_path                 varchar(255)                 null,
    ios_debug_profile_path       varchar(255)                 null,
    ios_release_profile_path     varchar(255)                 null,
    ios_key_password             varchar(255)                 null,
    ios_issuer_id                varchar(100)                 null,
    ios_key_id                   varchar(100)                 null,
    ios_release_type             varchar(15)                  null,
    ios_unlock_keychain_password varchar(255)                 null,
    ios_profiles_json            longtext collate utf8mb4_bin null,
    ios_certificates_json        longtext collate utf8mb4_bin null
);

create table key_setting
(
    key_id       int auto_increment
        primary key,
    builder_id   int          not null,
    admin_id     int          not null,
    key_name     varchar(255) not null,
    platform     varchar(8)   not null,
    created_date datetime     null,
    updated_date datetime     null
);

create table member_role
(
    id           bigint(10) unsigned auto_increment
        primary key,
    member_id    bigint(10) unsigned null,
    workspace_id bigint(10) unsigned null,
    role_code_id varchar(3)          null,
    domain_id    int                 null
);

create table project_group_role
(
    project_group_role_id   int auto_increment
        primary key,
    workspace_group_role_id int        not null,
    project_id              int        not null,
    read_yn                 varchar(3) null,
    update_yn               varchar(3) null,
    delete_yn               varchar(3) null,
    build_yn                varchar(3) null,
    deploy_yn               varchar(3) null,
    export_yn               varchar(3) null
);

create table project_history
(
    project_history_id       int auto_increment
        primary key,
    project_id               int          not null,
    platform                 varchar(8)   not null,
    status                   varchar(10)  null,
    status_log               varchar(50)  null,
    log_path                 varchar(300) null,
    logfile_name             varchar(255) null,
    platform_build_file_path varchar(255) null,
    platform_build_file_name varchar(255) null,
    result                   varchar(100) null,
    project_name             varchar(255) null,
    qrcode                   varchar(300) null,
    history_started_date     datetime     null,
    history_ended_date       datetime     null,
    build_number             varchar(100) null,
    ios_builded_target_or_bundle_id       varchar(256)          null comment 'WMatrix는 빌드한 iOS 타겟 이름, 일반앱을 빌드한 bundleId'
);
/*
    ios_builded_target_or_bundle_id 컬럼
    1. Onpremise
        - W-Matrix
            - iOS : bundle id
            - Android : package name
        - 일반 앱 : 지원 X

    2. Service
        - W-Matrix
            - iOS : target name
            - Android : Null
        - 일반 앱
            - iOS : bundle id
            - Android: Null
 */


create table project_setting
(
    project_setting_id   int auto_increment
        primary key,
    project_id           int          not null,
    app_id               varchar(255) not null,
    app_name             varchar(255) not null,
    app_version          varchar(20)  not null,
    app_version_code     varchar(10)  not null,
    package_name         varchar(255) not null,
    min_target_version   varchar(8)   not null,
    icon_image_path      varchar(255) null,
    zip_file_name        varchar(255) null,
    created_date         datetime     null,
    updated_date         datetime     null,
    project_setting_etc1 varchar(100) null,
    project_setting_etc2 varchar(100) null,
    project_setting_etc3 varchar(100) null
);

create table project
(
    project_id        int auto_increment
        primary key,
    workspace_id      int          not null,
    project_name      varchar(255) not null,
    platform          varchar(8)   null,
    description       text         null,
    template_version  varchar(50)  null,
    project_dir_path  varchar(300) null,
    platform_language varchar(8)   null,
    ftp_id            int          not null,
    builder_id        int          null,
    vcs_id            int          null,
    key_id            int          null,
    created_date      datetime     null,
    updated_date      datetime     null,
    project_etc1      varchar(100) null,
    project_etc2      varchar(100) null,
    project_etc3      varchar(100) null,
    delete_yn         varchar(3)   null,
    product_type      varchar(30)  null
);

create table workspace
(
    workspace_id   bigint(10) unsigned auto_increment
        primary key,
    member_id      bigint(10) unsigned null,
    workspace_name varchar(50)         not null,
    status         varchar(4)          not null,
    favorite_flag  varchar(1)          null,
    created_date   datetime            not null,
    updated_date   datetime            null,
    workspace_etc1 varchar(100)        null,
    workspace_etc2 varchar(100)        null,
    workspace_etc3 varchar(100)        null,
    delete_yn      varchar(3)          null
);

create table role_code
(
    role_code_id   varchar(3)  not null
        primary key,
    role_code_name varchar(50) not null,
    role_code_type varchar(10) null
);

INSERT INTO role_code (role_code_id, role_code_name, role_code_type) VALUES ('01', 'build', 'project');
INSERT INTO role_code (role_code_id, role_code_name, role_code_type) VALUES ('02', 'deploy', 'project');
INSERT INTO role_code (role_code_id, role_code_name, role_code_type) VALUES ('03', 'all', 'project');
INSERT INTO role_code (role_code_id, role_code_name, role_code_type) VALUES ('10', 'SUPERADMIN', 'member');
INSERT INTO role_code (role_code_id, role_code_name, role_code_type) VALUES ('11', 'ADMIN', 'member');
INSERT INTO role_code (role_code_id, role_code_name, role_code_type) VALUES ('12', 'USER', 'member');
INSERT INTO role_code (role_code_id, role_code_name, role_code_type) VALUES ('20', 'Java', 'Andprogram');
INSERT INTO role_code (role_code_id, role_code_name, role_code_type) VALUES ('21', 'Kotlin', 'Andprogram');
INSERT INTO role_code (role_code_id, role_code_name, role_code_type) VALUES ('22', 'Objc', 'iosprogram');
INSERT INTO role_code (role_code_id, role_code_name, role_code_type) VALUES ('23', 'Swift', 'iosprogram');
INSERT INTO role_code (role_code_id, role_code_name, role_code_type) VALUES ('24', 'C#', 'winprogram');
INSERT INTO role_code (role_code_id, role_code_name, role_code_type) VALUES ('30', 'Android', 'platform');
INSERT INTO role_code (role_code_id, role_code_name, role_code_type) VALUES ('31', 'iOS', 'platform');

create table role
(
    role_id      int auto_increment
        primary key,
    role_name    varchar(255) null,
    created_date datetime     null,
    updated_date datetime     null
);

create table user
(
    user_id         int auto_increment
        primary key,
    domain_id       int          not null,
    role_id         int          null,
    email           varchar(255) null,
    password        varchar(255) null,
    user_role       varchar(20)  null,
    build_yn        varchar(8)   null,
    created_date    datetime     null,
    updated_date    datetime     null,
    last_login_date datetime     null,
    user_etc1       varchar(100) null,
    user_etc2       text         null,
    user_etc3       varchar(100) null,
    user_name       varchar(255) null,
    user_login_id   varchar(255) null,
    phone_number    varchar(15)  null
);

INSERT INTO user (user_id, domain_id, role_id, email, password, user_role, build_yn, created_date, updated_date, last_login_date, user_etc1, user_etc2, user_etc3, user_name, user_login_id, phone_number) VALUES (1, 1, 0, 'admin@inswave.com', '$2a$10$Wr0eI42gJTXQE0Cpy3zR.OzqkSVfA/huZ9jV6W2CrTykCn7E1Fi2m', 'SUPERADMIN', 'N', '2019-12-26 00:33:00.0', '2022-07-19 16:52:23.0', '2022-07-21 16:40:46.0', null, null, null, '관리자', 'admin', null);

create table vcs_settings
(
    vcs_id           int auto_increment
        primary key,
    vcs_type         varchar(20)  null,
    vcs_url          varchar(255) null,
    vcs_user_id      varchar(255) null,
    vcs_user_pwd     varchar(255) null,
    create_date      datetime     not null,
    update_date      datetime     null,
    vcs_name         varchar(255) null,
    vcs_network_mode varchar(3)   null,
    admin_id         int          null
);


create table workspace_group_role
(
    workspace_group_role_id int auto_increment
        primary key,
    role_id                 int not null,
    workspace_id            int not null
)
    engine = InnoDB;

CREATE TABLE `menu` (
    `menu_id` int(12) NOT NULL AUTO_INCREMENT COMMENT '메뉴 아이디 PK',
    `menu_page_name` varchar(200) DEFAULT NULL COMMENT '메뉴 페이지 이름',
    `menu_code` varchar(20) NOT NULL COMMENT '메뉴 코드 ex: m0100000000 (m 분류1 분류2 분류3 분류4 분류5, 각2자리)',
    `menu_profile_type` varchar(50) NOT NULL COMMENT '메뉴 프로파일 타입 ex: service, onpremise, all(service+onpremiss)',
    `menu_role_type` varchar(50) NOT NULL COMMENT '메뉴 사용자 권한 ex: user, admin, superadmin',
    `menu_pay_type` int(12) NOT NULL COMMENT '결제 정보에 따른 메뉴 권한, 100: Free User, 300: Pro User, 900: 관리자',
    `menu_lang_type` varchar(10) NOT NULL COMMENT '메뉴 언어 타입',
    `text_label` varchar(200) NOT NULL COMMENT '메뉴 이름',
    `menu_key` varchar(200) NOT NULL COMMENT '메뉴 key 값',
    `depth` int(12) NOT NULL COMMENT '메뉴 분류 값 ex: 1,2',
    `icon` varchar(200) DEFAULT NULL COMMENT '메뉴 아이콘',
    `url` varchar(500) DEFAULT NULL COMMENT '메뉴 URL',
    `create_date` datetime DEFAULT NULL COMMENT '생성일자',
    `update_date` datetime DEFAULT NULL COMMENT '수정일자',
    `menu_etc1` varchar(100) DEFAULT NULL COMMENT 'etc1',
    `menu_etc2` varchar(100) DEFAULT NULL COMMENT 'etc2',
    `menu_etc3` varchar(100) DEFAULT NULL COMMENT 'etc3',
    PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='W-Hive 메뉴 관리 테이블';

INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(1, 'workspace', 'm0100000000', 'all', 'all', 100, 'ko', 'Workspace', 'Workspace', 1, '/images/menu_ico/ico_mn01_on.png', '/ui/works/workspace.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(3, 'history', 'm0200000000', 'all', 'all', 100, 'ko', 'History', 'History', 1, '/images/menu_ico/ico_mn02_on.png', NULL, NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(4, 'build_history', 'm0201000000', 'all', 'all', 100, 'ko', 'Build History', 'Build History', 2, NULL, '/ui/history/history_list.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(5, 'deploy_history', 'm0202000000', 'all', 'all', 300, 'ko', 'Deploy History', 'Deploy History', 2, NULL, '/ui/history/deploy_history_list.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(6, 'settings', 'm0300000000', 'all', 'ADMIN', 300, 'ko', 'Settings', 'Settings', 1, '/images/menu_ico/ico_mn03_on.png', NULL, NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(7, 'builder', 'm0301000000', 'onpremise', 'ADMIN', 900, 'ko', 'Builder', 'Builder', 2, NULL, '/ui/settings/setting_branch_list.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(8, 'vcs', 'm0302000000', 'all', 'ADMIN', 300, 'ko', 'VCS', 'VCS', 2, NULL, '/ui/settings/setting_vcs_list.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(9, 'ftp_server', 'm0303000000', 'onpremise', 'ADMIN', 900, 'ko', 'FTP Server', 'FTP Server', 2, NULL, '/ui/settings/setting_ftp_list.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(10, 'certificate', 'm0304000000', 'all', 'ADMIN', 100, 'ko', '인증서', '인증서', 2, NULL, '/ui/settings/setting_signingkey_list.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(11, 'role_manager', 'm0400000000', 'onpremise', 'ADMIN', 900, 'ko', 'Role Manager', 'Role Manager', 1, '/images/menu_ico/ico_mn04_on.png', NULL, NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(12, 'domain', 'm0401000000', 'onpremise', 'ADMIN', 900, 'ko', 'Domain', 'Domain', 2, NULL, '/ui/manager/userManager_domain_list.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(13, 'workspace_manage', 'm0402000000', 'all', 'ADMIN', 100, 'ko', 'Workspace 관리', 'Workspace 관리', 2, NULL, '/ui/manager/userManager_workspace_role_detail.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(14, 'authority_manage', 'm0403000000', 'onpremise', 'ADMIN', 900, 'ko', '권한관리', '권한관리', 2, NULL, '/ui/manager/userManager_user_role_detail.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(15, 'user_list', 'm0404000000', 'onpremise', 'ADMIN', 900, 'ko', '사용자 목록', '사용자 목록', 2, NULL, '/ui/manager/userManager_admin_list.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(16, 'guide', 'm0500000000', 'all', 'all', 100, 'ko', 'Guide', 'Guide', 1, NULL, NULL, NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(17, 'project_add', 'm0100010000', 'all', 'all', 100, 'ko', '프로젝트 생성', '프로젝트 생성', 3, NULL, '/ui/works/project_add.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(18, 'build', 'm0101000000', 'all', 'all', 100, 'ko', 'Build', 'Build', 3, NULL, '/ui/build/build.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(19, 'project_add_01', 'm0100010100', 'all', 'all', 100, 'ko', '프로젝트 생성 01', '프로젝트 생성 01', 4, NULL, '/ui/works/project_add_step01.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(20, 'project_add_02', 'm0100010200', 'all', 'all', 100, 'ko', '프로젝트 생성 02', '프로젝트 생성 02', 4, NULL, '/ui/works/project_add_step02.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(21, 'project_add_03', 'm0100010300', 'all', 'all', 100, 'ko', '프로젝트 생성 03', '프로젝트 생성 03', 4, NULL, '/ui/works/project_add_step03.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(22, 'project_add_04', 'm0100010400', 'all', 'all', 100, 'ko', '프로젝트 생성 04', '프로젝트 생성 04', 4, NULL, '/ui/works/project_add_step04.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(23, 'project_add_05', 'm0100010500', 'all', 'all', 100, 'ko', '프로젝트 생성 05', '프로젝트 생성 05', 4, NULL, '/ui/works/project_add_step05.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(24, 'project_setting_01', 'm0100020100', 'all', 'all', 100, 'ko', '프로젝트 설정 01', '프로젝트 설정 01', 4, NULL, '/ui/works/project_setting_step01.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(25, 'project_setting_02', 'm0100020200', 'all', 'all', 100, 'ko', '프로젝트 설정 02', '프로젝트 설정 02', 4, NULL, '/ui/works/project_setting_step02.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(26, 'project_setting_03', 'm0100020300', 'all', 'all', 100, 'ko', '프로젝트 설정 03', '프로젝트 설정 03', 4, NULL, '/ui/works/project_setting_step03.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(27, 'project_setting_04', 'm0100020400', 'all', 'all', 100, 'ko', '프로젝트 설정 04', '프로젝트 설정 04', 4, '', '/ui/works/project_setting_step04.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(28, 'project_setting_05', 'm0100020500', 'all', 'all', 100, 'ko', '프로젝트 설정 05', '프로젝트 설정 05', 4, NULL, '/ui/works/project_setting_step05.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(29, 'history_detail', 'm0201010000', 'all', 'all', 100, 'ko', '히스토리 상세', '히스토리 상세', 3, NULL, '/ui/history/history_detail.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(30, 'deploy_history_detail', 'm0202010000', 'all', 'all', 300, 'ko', '배포 히스토리 상세', '배포 히스토리 상세', 3, NULL, '/ui/history/deploy_history_detail.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(31, 'setting_branch_detail', 'm0301010000', 'onpremise', 'ADMIN', 900, 'ko', '빌더 상세', '빌더 상세', 3, NULL, '/ui/settings/setting_branch_detail.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(32, 'setting_vcs_detail', 'm0302010000', 'all', 'ADMIN', 300, 'ko', '버전관리 상세', '버전관리 상세', 3, NULL, '/ui/settings/setting_vcs_detail.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(33, 'setting_ftp_detail', 'm0303010000', 'onpremise', 'ADMIN', 900, 'ko', 'FTP 상세', 'FTP 상세', 3, NULL, '/ui/settings/setting_ftp_detail.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(34, 'setting_certificate_detail', 'm0304010000', 'all', 'ADMIN', 100, 'ko', '인증서 상세', '인증서 상세', 3, NULL, '/ui/settings/setting_signingkey_detail.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(35, 'manager_domain_detail', 'm0401010000', 'onpremise', 'ADMIN', 900, 'ko', '도메인 상세', '도메인 상세', 3, NULL, '/ui/manager/userManager_domain_detail.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(36, 'manager_user_detail', 'm0404010000', 'all', 'ADMIN', 100, 'ko', '사용자 상세', '사용자 상세', 3, NULL, '/ui/manager/userManager_admin_detail.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(37, 'setting_signingkey_android_build_detail', 'm0304010100', 'all', 'ADMIN', 100, 'ko', 'Build', 'Build', 4, NULL, '/ui/settings/setting_signingkey_android_build_detail.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(38, 'setting_signingkey_android_deploy_detail', 'm0304010200', 'all', 'ADMIN', 100, 'ko', 'Deploy', 'Deploy', 4, NULL, '/ui/settings/setting_signingkey_android_deploy_detail.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(39, 'setting_signingkey_ios_profiles_detail', 'm0304010300', 'all', 'ADMIN', 100, 'ko', 'Profiles', 'Profiles', 4, NULL, '/ui/settings/setting_signingkey_ios_profiles_detail.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(40, 'setting_signingkey_ios_certificate_detail', 'm0304010400', 'all', 'ADMIN', 100, 'ko', 'Certificate', 'Certificate', 4, NULL, '/ui/settings/setting_signingkey_ios_certificate_detail.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(41, 'setting_signingkey_ios_deploy_detail', 'm0304010500', 'all', 'ADMIN', 100, 'ko', 'Deploy', 'Deploy', 4, NULL, '/ui/settings/setting_signingkey_ios_deploy_detail.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(42, 'project_setting', 'm0100020000', 'all', 'all', 100, 'ko', '프로젝트 설정', '프로젝트 설정', 3, NULL, '/ui/works/project_setting.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(43, 'deploy', 'm0102000000', 'all', 'all', 300, 'ko', '배포', '배포', 3, NULL, '/ui/deploy/deploy.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(44, 'authority_manage_detail', 'm0403010000', 'all', 'all', 900, 'ko', '권한관리 상세', '권한관리 상세', 3, NULL, '/ui/manager/userManager_user_role_detail.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(45, 'project_deploy_task_setting', 'm0100030000', 'all', 'all', 300, 'ko', '프로젝트 배포 설정', '프로젝트 배포 설정', 3, NULL, '/ui/works/project_deploy_task_setting.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(46, 'project_deploy_task_setting_step1', 'm0100030100', 'all', 'all', 300, 'ko', '프로젝트 배포 설정 1', '프로젝트 배포 설정 1', 4, NULL, '/ui/works/project_deploy_task_setting_step01.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(47, 'project_deploy_task_setting_step2', 'm0100030200', 'all', 'all', 300, 'ko', '프로젝트 배포 설정 2', '프로젝트 배포 설정 2', 4, NULL, '/ui/works/project_deploy_task_setting_step02.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(48, 'project_deploy_task_setting_step3', 'm0100030300', 'all', 'all', 300, 'ko', '프로젝트 배포 설정 3', '프로젝트 배포 설정 3', 4, NULL, '/ui/works/project_deploy_task_setting_step03.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(49, 'project_import', 'm0100040000', 'all', 'all', 100, 'ko', '프로젝트 가져오기', '프로젝트 가져오기', 3, NULL, '/ui/works/project_import.xml', NOW(), NOW());
INSERT INTO whive.menu
(menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, `depth`, icon, url, create_date, update_date)
VALUES(50, 'project_import_01', 'm0100040100', 'all', 'all', 100, 'ko', '프로젝트 가져오기 01', '프로젝트 가져오기 01', 4, NULL, '/ui/works/project_import_step01.xml', NOW(), NOW());


INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (52, 'workspace', 'm0100000000', 'all', 'all', 100, 'ja', 'Workspace', 'Workspace', 1, '/images/menu_ico/ico_mn01_on.png', '/ui/works/workspace.xml', '2023-11-24 17:36:17', '2023-11-24 17:36:17', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (53, 'history', 'm0200000000', 'all', 'all', 100, 'ja', 'History', 'History', 1, '/images/menu_ico/ico_mn02_on.png', null, '2023-11-24 17:36:17', '2023-11-24 17:36:17', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (54, 'build_history', 'm0201000000', 'all', 'all', 100, 'ja', 'Build History', 'Build History', 2, null, '/ui/history/history_list.xml', '2023-11-24 17:36:17', '2023-11-24 17:36:17', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (55, 'deploy_history', 'm0202000000', 'all', 'all', 300, 'ja', 'Deploy History', 'Deploy History', 2, null, '/ui/history/deploy_history_list.xml', '2023-11-24 17:36:17', '2023-11-24 17:36:17', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (56, 'settings', 'm0300000000', 'all', 'ADMIN', 100, 'ja', 'Settings', 'Settings', 1, '/images/menu_ico/ico_mn03_on.png', null, '2023-11-24 17:36:17', '2023-11-24 17:36:17', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (57, 'builder', 'm0301000000', 'onpremise', 'ADMIN', 900, 'ja', 'Builder', 'Builder', 2, null, '/ui/settings/setting_branch_list.xml', '2023-11-24 17:36:17', '2023-11-24 17:36:17', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (58, 'vcs', 'm0302000000', 'all', 'ADMIN', 300, 'ja', 'VCS', 'VCS', 2, null, '/ui/settings/setting_vcs_list.xml', '2023-11-24 17:36:17', '2023-11-24 17:36:17', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (59, 'ftp_server', 'm0303000000', 'onpremise', 'ADMIN', 900, 'ja', 'FTP Server', 'FTP Server', 2, null, '/ui/settings/setting_ftp_list.xml', '2023-11-24 17:36:17', '2023-11-24 17:36:17', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (60, 'certificate', 'm0304000000', 'all', 'ADMIN', 100, 'ja', '証明書', '証明書', 2, null, '/ui/settings/setting_signingkey_list.xml', '2023-11-24 17:36:17', '2023-11-24 17:36:17', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (61, 'role_manager', 'm0400000000', 'onpremise', 'ADMIN', 100, 'ja', 'Role Manager', 'Role Manager', 1, '/images/menu_ico/ico_mn04_on.png', null, '2023-11-24 17:36:17', '2023-11-24 17:36:17', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (62, 'domain', 'm0401000000', 'onpremise', 'ADMIN', 900, 'ja', 'Domain', 'Domain', 2, null, '/ui/manager/userManager_domain_list.xml', '2023-11-24 17:36:17', '2023-11-24 17:36:17', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (63, 'workspace_manage', 'm0402000000', 'all', 'ADMIN', 100, 'ja', 'Workspace 管理', 'Workspace 管理', 2, null, '/ui/manager/userManager_workspace_role_detail.xml', '2023-11-24 17:36:17', '2023-11-24 17:36:17', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (64, 'authority_manage', 'm0403000000', 'onpremise', 'ADMIN', 900, 'ja', '権限管理', '権限管理', 2, null, '/ui/manager/userManager_user_role_list.xml', '2023-11-24 17:36:17', '2023-11-24 17:36:17', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (65, 'user_list', 'm0404000000', 'onpremise', 'ADMIN', 900, 'ja', 'ユーザーリスト', 'ユーザーリスト', 2, null, '/ui/manager/userManager_admin_list.xml', '2023-11-24 17:36:18', '2023-11-24 17:36:18', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (66, 'guide', 'm0500000000', 'all', 'all', 100, 'ja', 'Guide', 'Guide', 1, null, 'https://docs1.inswave.com/whive-guide', '2023-11-24 17:36:18', '2023-11-24 17:36:18', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (67, 'project_add', 'm0100010000', 'all', 'all', 100, 'ja', 'プロジェクト作成', 'プロジェクト作成', 3, null, '/ui/works/project_add.xml', '2023-11-24 17:36:18', '2023-11-24 17:36:18', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (68, 'build', 'm0101000000', 'all', 'all', 100, 'ja', 'Build', 'Build', 3, null, '/ui/build/build.xml', '2023-11-24 17:36:18', '2023-11-24 17:36:18', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (69, 'project_add_01', 'm0100010100', 'all', 'all', 100, 'ja', 'プロジェクト作成 01', 'プロジェクト作成 01', 4, null, '/ui/works/project_add_step01.xml', '2023-11-24 17:36:18', '2023-11-24 17:36:18', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (70, 'project_add_02', 'm0100010200', 'all', 'all', 100, 'ja', 'プロジェクト作成 02', 'プロジェクト作成 02', 4, null, '/ui/works/project_add_step02.xml', '2023-11-24 17:36:18', '2023-11-24 17:36:18', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (71, 'project_add_03', 'm0100010300', 'all', 'all', 100, 'ja', 'プロジェクト作成 03', 'プロジェクト作成 03', 4, null, '/ui/works/project_add_step03.xml', '2023-11-24 17:36:18', '2023-11-24 17:36:18', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (72, 'project_add_04', 'm0100010400', 'all', 'all', 100, 'ja', 'プロジェクト作成 04', 'プロジェクト作成 04', 4, null, '/ui/works/project_add_step04.xml', '2023-11-24 17:36:18', '2023-11-24 17:36:18', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (73, 'project_add_05', 'm0100010500', 'all', 'all', 100, 'ja', 'プロジェクト作成 05', 'プロジェクト作成 05', 4, null, '/ui/works/project_add_step05.xml', '2023-11-24 17:36:18', '2023-11-24 17:36:18', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (74, 'project_setting_01', 'm0100020100', 'all', 'all', 100, 'ja', 'プロジェクト設定 01', 'プロジェクト設定 01', 4, null, '/ui/works/project_setting_step01.xml', '2023-11-24 17:36:18', '2023-11-24 17:36:18', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (75, 'project_setting_02', 'm0100020200', 'all', 'all', 100, 'ja', 'プロジェクト設定 02', 'プロジェクト設定 02', 4, null, '/ui/works/project_setting_step02.xml', '2023-11-24 17:36:18', '2023-11-24 17:36:18', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (76, 'project_setting_03', 'm0100020300', 'all', 'all', 100, 'ja', 'プロジェクト設定 03', 'プロジェクト設定 03', 4, null, '/ui/works/project_setting_step03.xml', '2023-11-24 17:36:18', '2023-11-24 17:36:18', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (77, 'project_setting_04', 'm0100020400', 'all', 'all', 100, 'ja', 'プロジェクト設定 04', 'プロジェクト設定 04', 4, '', '/ui/works/project_setting_step04.xml', '2023-11-24 17:36:18', '2023-11-24 17:36:18', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (78, 'project_setting_05', 'm0100020500', 'all', 'all', 100, 'ja', 'プロジェクト設定 05', 'プロジェクト設定 05', 4, null, '/ui/works/project_setting_step05.xml', '2023-11-24 17:36:18', '2023-11-24 17:36:18', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (79, 'history_detail', 'm0201010000', 'all', 'all', 100, 'ja', '履歴詳細', '履歴詳細', 3, null, '/ui/history/history_detail.xml', '2023-11-24 17:36:18', '2023-11-24 17:36:18', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (80, 'deploy_history_detail', 'm0202010000', 'all', 'all', 300, 'ja', 'デプロイ履歴の詳細', 'デプロイ履歴の詳細', 3, null, '/ui/history/deploy_history_detail.xml', '2023-11-24 17:36:18', '2023-11-24 17:36:18', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (81, 'setting_branch_detail', 'm0301010000', 'onpremise', 'ADMIN', 900, 'ja', 'ビルダーの詳細', 'ビルダーの詳細', 3, null, '/ui/settings/setting_branch_detail.xml', '2023-11-24 17:36:18', '2023-11-24 17:36:18', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (82, 'setting_vcs_detail', 'm0302010000', 'all', 'ADMIN', 300, 'ja', 'バージョン管理の詳細', 'バージョン管理の詳細', 3, null, '/ui/settings/setting_vcs_detail.xml', '2023-11-24 17:36:18', '2023-11-24 17:36:18', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (83, 'setting_ftp_detail', 'm0303010000', 'onpremise', 'ADMIN', 900, 'ja', 'FTP 詳細', 'FTP 詳細', 3, null, '/ui/settings/setting_ftp_detail.xml', '2023-11-24 17:36:18', '2023-11-24 17:36:18', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (84, 'setting_certificate_detail', 'm0304010000', 'all', 'ADMIN', 100, 'ja', '証明書の詳細', '証明書の詳細', 3, null, '/ui/settings/setting_signingkey_detail.xml', '2023-11-24 17:36:18', '2023-11-24 17:36:18', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (85, 'manager_domain_detail', 'm0401010000', 'onpremise', 'ADMIN', 900, 'ja', 'ドメインの詳細', 'ドメインの詳細', 3, null, '/ui/manager/userManager_domain_detail.xml', '2023-11-24 17:36:18', '2023-11-24 17:36:18', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (86, 'manager_user_detail', 'm0404010000', 'all', 'ADMIN', 100, 'ja', 'ユーザーの詳細', 'ユーザーの詳細', 3, null, '/ui/manager/userManager_admin_detail.xml', '2023-11-24 17:36:18', '2023-11-24 17:36:18', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (87, 'setting_signingkey_android_build_detail', 'm0304010100', 'all', 'ADMIN', 100, 'ja', 'Build', 'Build', 4, null, '/ui/settings/setting_signingkey_android_build_detail.xml', '2023-11-24 17:36:19', '2023-11-24 17:36:19', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (88, 'setting_signingkey_android_deploy_detail', 'm0304010200', 'all', 'ADMIN', 100, 'ja', 'Deploy', 'Deploy', 4, null, '/ui/settings/setting_signingkey_android_deploy_detail.xml', '2023-11-24 17:36:19', '2023-11-24 17:36:19', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (89, 'setting_signingkey_ios_profiles_detail', 'm0304010300', 'all', 'ADMIN', 100, 'ja', 'Profiles', 'Profiles', 4, null, '/ui/settings/setting_signingkey_ios_profiles_detail.xml', '2023-11-24 17:36:19', '2023-11-24 17:36:19', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (90, 'setting_signingkey_ios_certificate_detail', 'm0304010400', 'all', 'ADMIN', 100, 'ja', 'Certificate', 'Certificate', 4, null, '/ui/settings/setting_signingkey_ios_certificate_detail.xml', '2023-11-24 17:36:19', '2023-11-24 17:36:19', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (91, 'setting_signingkey_ios_deploy_detail', 'm0304010500', 'all', 'ADMIN', 100, 'ja', 'Deploy', 'Deploy', 4, null, '/ui/settings/setting_signingkey_ios_deploy_detail.xml', '2023-11-24 17:36:19', '2023-11-24 17:36:19', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (92, 'project_setting', 'm0100020000', 'all', 'all', 100, 'ja', 'プロジェクト設定', 'プロジェクト設定', 3, null, '/ui/works/project_setting.xml', '2023-11-24 17:36:19', '2023-11-24 17:36:19', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (93, 'deploy', 'm0102000000', 'all', 'all', 300, 'ja', 'デプロイ', 'デプロイ', 3, null, '/ui/deploy/deploy.xml', '2023-11-24 17:36:19', '2023-11-24 17:36:19', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (94, 'authority_manage_detail', 'm0403010000', 'all', 'all', 900, 'ja', '権限管理の詳細', '権限管理の詳細', 3, null, '/ui/manager/userManager_user_role_detail.xml', '2023-11-24 17:36:19', '2023-11-24 17:36:19', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (95, 'project_deploy_task_setting', 'm0100030000', 'all', 'all', 300, 'ja', 'プロジェクトのデプロイ設定', 'プロジェクトのデプロイ設定', 3, null, '/ui/works/project_deploy_task_setting.xml', '2023-11-24 17:36:19', '2023-11-24 17:36:19', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (96, 'project_deploy_task_setting_step1', 'm0100030100', 'all', 'all', 300, 'ja', 'プロジェクトのデプロイ設定 1', 'プロジェクトのデプロイ設定 1', 4, null, '/ui/works/project_deploy_task_setting_step01.xml', '2023-11-24 17:36:19', '2023-11-24 17:36:19', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (97, 'project_deploy_task_setting_step2', 'm0100030200', 'all', 'all', 300, 'ja', 'プロジェクトのデプロイ設定 2', 'プロジェクトのデプロイ設定 2', 4, null, '/ui/works/project_deploy_task_setting_step02.xml', '2023-11-24 17:36:19', '2023-11-24 17:36:19', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (98, 'project_deploy_task_setting_step3', 'm0100030300', 'all', 'all', 300, 'ja', 'プロジェクトのデプロイ設定 3', 'プロジェクトのデプロイ設定 3', 4, null, '/ui/works/project_deploy_task_setting_step03.xml', '2023-11-24 17:36:19', '2023-11-24 17:36:19', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (99, 'project_import', 'm0100040000', 'all', 'all', 100, 'ja', 'プロジェクトの取り込み', 'プロジェクトの取り込み', 3, null, '/ui/works/project_import.xml', '2023-11-24 17:36:19', '2023-11-24 17:36:19', null, null, null);
INSERT INTO whive.menu (menu_id, menu_page_name, menu_code, menu_profile_type, menu_role_type, menu_pay_type, menu_lang_type, text_label, menu_key, depth, icon, url, create_date, update_date, menu_etc1, menu_etc2, menu_etc3) VALUES (100, 'project_import_01', 'm0100040100', 'all', 'all', 100, 'ja', 'プロジェクトの取り込み 01', 'プロジェクトの取り込み 01', 4, null, '/ui/works/project_import_step01.xml', '2023-11-24 17:36:19', '2023-11-24 17:36:19', null, null, null);


drop table if exists builder_queue_managed;
CREATE TABLE builder_queue_managed (
 builder_id bigint(20) unsigned NOT NULL,
 queue_managed_id bigint(20) unsigned NOT NULL AUTO_INCREMENT,
 project_queue_cnt bigint(4) NOT NULL,
 build_queue_cnt bigint(4) NOT NULL,
 deploy_queue_cnt bigint(4) NOT NULL,
 etc_queue_cnt bigint(4) NOT NULL,
 project_queue_status_cnt bigint(4) NOT NULL,
 build_queue_status_cnt bigint(4) NOT NULL,
 deploy_queue_status_cnt bigint(4) NOT NULL,
 etc_queue_status_cnt bigint(4) NOT NULL,
 queue_etc_1 varchar(100),
 queue_etc_2 varchar(100),
 queue_etc_3 varchar(100),
 create_date datetime NOT NULL,
 update_date datetime,
 primary key (queue_managed_id)
);

create table user_key_code
(
    user_key_id int auto_increment,
    user_key_email varchar(255) not null,
    user_key_code_value varchar(50) null,
    user_key_expired_date datetime null,
    constraint user_key_code_pk
        primary key (user_key_id)
);

create table pricing
(
    pricing_id   bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    user_id bigint(20) not null,
    order_id     varchar(100) not null,
    customer_uid varchar(100) not null,
    imp_uid varchar(50) not null,
    pay_type_cd  varchar(10) null,
    pay_change_yn varchar(3) not null ,
    pricing_etc1 varchar(100) null,
    pricing_etc2 varchar(100) null,
    create_date datetime NOT NULL,
    update_date datetime,
    constraint pricing_pk
        primary key (pricing_id)
);

alter table user
    add appid_json longtext null comment 'appid json';

create table whive_approval
(
    approval_id   bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    git_uuid varchar(45) not null,
    approval_pk     bigint(20) not null,
    platform varchar(10) not null,
    approval_type varchar(50) not null,
    BSWR_DVLP_NO  varchar(20) null,
    BSWR_DVLP_STTS_DCD varchar(8) not null ,
    EMP_NO varchar(10) not null,
    SNLN_ATST_CD bigint(3) null,
    create_date datetime NOT NULL,
    update_date datetime,
    constraint allow_id
        primary key (approval_id)
);


