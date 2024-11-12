drop table if exists oauth_client_details;
CREATE TABLE oauth_client_details (
  client_id varchar(255) NOT NULL,
  resource_ids varchar(255) DEFAULT NULL,
  client_secret varchar(255) DEFAULT NULL,
  scope varchar(255) DEFAULT NULL,
  authorized_grant_types varchar(255) DEFAULT NULL,
  web_server_redirect_uri varchar(255) DEFAULT NULL,
  authorities varchar(255) DEFAULT NULL,
  access_token_validity int(11) DEFAULT NULL,
  refresh_token_validity int(11) DEFAULT NULL,
  additional_information varchar(4096) DEFAULT NULL,
  autoapprove varchar(255) DEFAULT NULL,
  PRIMARY KEY (client_id)
);

LOCK TABLES `oauth_client_details` WRITE;
/*!40000 ALTER TABLE `oauth_client_details` DISABLE KEYS */;
INSERT INTO oauth_client_details VALUES("admin",NULL,"{bcrypt}$2a$10$M3Ae6zFGtkO.A11jOiJxA.c2zcpoAuaicxJwulN7EbmmOcbgurFwa",NULL,"authorization_code,password,refresh_token,implicit","",NULL,300,3600,NULL,NULL);
/*!40000 ALTER TABLE `oauth_client_details` ENABLE KEYS */;
UNLOCK TABLES;

drop table if exists member;
CREATE TABLE member (
  id bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  email varchar(50) NOT NULL,
  password varchar(255) NOT NULL,
  user_name varchar(50) NOT NULL,
  team varchar(50),
  company varchar(50),
  enabled bit(1) NOT NULL,
  accountNonLocked bit(1) NOT NULL,
  accountNonExpired bit(1) NOT NULL,
  credentialsNonExpired bit(1) NOT NULL,
  created_date datetime NOT NULL,
  updated_date datetime,
  last_login_date datetime,
  PRIMARY KEY (id)
);


drop table if exists member_config;
CREATE TABLE member_config (
  user_id bigint unsigned NOT NULL,
  id bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  icon blob,
  google_account varchar(50),
  google_password varchar(255),
  apple_account varchar(50),
  apple_password varchar(255),
  created_date datetime NOT NULL,
  updated_Date datetime,
  PRIMARY KEY (id)
);

drop table if exists member_role;
CREATE TABLE member_role (
  member_id bigint(20) unsigned NOT NULL,
  role_id bigint(20) unsigned NOT NULL
  PRIMARY KEY (member_id,role_id)
);

drop table if exists role;
CREATE TABLE role (
  id bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  role varchar(50) NOT NULL,
  primary key (id)
);

drop table if exists workspace;
CREATE TABLE workspace (
  user_id bigint(20) unsigned NOT NULL,
  id bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  name varchar(50) NOT NULL,
  status TINYINT NOT NULL,
  favorite bit(1),
  created_date datetime NOT NULL,
  updated_date datetime,
  primary key (id)
);


drop table if exists build_project;
CREATE TABLE build_project (
  workspace_id bigint(20) unsigned NOT NULL,
  id bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  project_name varchar(255),
  platform varchar(255),
  target_server varchar(255),
  description varchar(255),
  status TINYINT,
  template_version varchar(255),
  created_date datetime,
  updated_date datetime,
  primary key (id)
);

drop table if exists build_setting;
CREATE TABLE build_setting (
  build_project_id bigint(20) unsigned NOT NULL,
  id bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  build_project_path varchar(50) NOT NULL,
  app_id varchar(50) NOT NULL,
  server_url varchar(50) NOT NULL,
  target varchar(30) NOT NULL,
  app_name varchar(50) NOT NULL,
  app_version varchar(30) NOT NULL,
  app_version_code varchar(30) NOT NULL,
  keychain_path varchar(255) NOT NULL,
  keychain_password varchar(255) NOT NULL,
  certificate_path varchar(255) NOT NULL,
  certificate_password varchar(255) NOT NULL,
  provisioning_profile_path varchar(255) NOT NULL,
  primary key (id)
);

drop table if exists deploy_project;
CREATE TABLE deploy_project (
  workspace_id bigint(20) unsigned NOT NULL,
  build_project_id bigint(20) unsigned NOT NULL,
  id bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  project_name varchar(50) NOT NULL,
  status TINYINT NOT NULL,
  platform varchar(30),
  created_date datetime NOT NULL,
  updated_date datetime,
  primary key (id)
);

drop table if exists deploy_setting;
CREATE TABLE deploy_setting (
  deploy_project_id bigint(20) unsigned NOT NULL,
  id bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  upload_type varchar(50) NOT NULL,
  upload_server varchar(50) NOT NULL,
  upload_id varchar(50) NOT NULL,
  upload_password varchar(255) NOT NULL,
  upload_target varchar(50) NOT NULL,
  upload_path varchar(50) NOT NULL,
  created_date datetime NOT NULL,
  updated_date datetime,
  primary key (id)
);

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
    queue_etc_1 varchar(10),
    queue_etc_2 varchar(10),
    queue_etc_3 varchar(10),
    create_date datetime NOT NULL,
    update_date datetime,
    primary key (id)
);