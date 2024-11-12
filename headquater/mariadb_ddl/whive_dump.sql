/*mariadb sql dump */

DROP TABLE IF EXISTS `build_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `build_history` (
  `id` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
  `build_id` bigint(10) unsigned DEFAULT NULL,
  `platform_type` varchar(10) NOT NULL,
  `target_server_id` varchar(100) NOT NULL,
  `status` varchar(10) DEFAULT NULL,
  `status_log` varchar(255) DEFAULT NULL,
  `log_path` varchar(100) DEFAULT NULL,
  `result` varchar(100) DEFAULT NULL,
  `platform_build_file_path` varchar(255) DEFAULT NULL,
  `qrcode` varchar(255) DEFAULT NULL,
  `build_start_date` datetime NOT NULL,
  `build_end_date` datetime DEFAULT NULL,
  `project_name` varchar(255) NOT NULL,
  `platform_build_file_name` varchar(255) DEFAULT NULL,
  `logfile_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=330 DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS `build_project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `build_project` (
  `id` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
  `workspace_id` bigint(10) unsigned DEFAULT NULL,
  `project_name` varchar(255) NOT NULL,
  `platform` varchar(255) NOT NULL,
  `target_server` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `status` tinyint(4) NOT NULL,
  `template_version` varchar(50) NOT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime NOT NULL,
  `build_etc1` varchar(100) DEFAULT NULL,
  `build_etc2` varchar(255) DEFAULT NULL,
  `build_etc3` varchar(255) DEFAULT NULL,
  `project_dir_path` varchar(300) DEFAULT NULL,
  `branch_id` int(11) DEFAULT NULL,
  `vcs_id` int(11) DEFAULT NULL,
  `ftp_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=196 DEFAULT CHARSET=utf8
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `build_project`
--


DROP TABLE IF EXISTS `build_setting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `build_setting` (
  `id` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
  `build_id` bigint(10) unsigned DEFAULT NULL,
  `platform_type` varchar(10) NOT NULL,
  `target` varchar(10) NOT NULL,
  `app_id` varchar(255) NOT NULL,
  `app_name` varchar(255) NOT NULL,
  `app_version` varchar(10) NOT NULL,
  `app_version_code` varchar(10) NOT NULL,
  `server_URL` varchar(100) NOT NULL,
  `package_name` varchar(255) NOT NULL,
  `icon_image_path` varchar(255) DEFAULT NULL,
  `screenshot_image_path` varchar(255) DEFAULT NULL,
  `all_keyfile_path` varchar(255) DEFAULT NULL,
  `all_keyfile_password` varchar(255) DEFAULT NULL,
  `provisioning_profile_path` varchar(255) DEFAULT NULL,
  `key_alias` varchar(100) DEFAULT NULL,
  `key_password` varchar(100) DEFAULT NULL,
  `test_url` varchar(255) NOT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime DEFAULT NULL,
  `build_setting_etc1` varchar(100) DEFAULT NULL,
  `build_setting_etc2` varchar(100) DEFAULT NULL,
  `build_setting_etc3` varchar(100) DEFAULT NULL,
  `keystore_file_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `build_setting`
--


DROP TABLE IF EXISTS `member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member` (
  `id` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
  `email` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `member_role` varchar(50) NOT NULL,
  `member_name` varchar(50) NOT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime DEFAULT NULL,
  `last_login_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member`
--

LOCK TABLES `member` WRITE;
/*!40000 ALTER TABLE `member` DISABLE KEYS */;
INSERT INTO `member` VALUES (1,'admin@inswave.com','admin1234!','ADMIN','관리자','2019-12-26 00:33:00',NULL,NULL);
/*!40000 ALTER TABLE `member` ENABLE KEYS */;
UNLOCK TABLES;


DROP TABLE IF EXISTS `member_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member_role` (
  `id` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
  `member_id` bigint(10) unsigned DEFAULT NULL,
  `workspace_id` bigint(10) unsigned DEFAULT NULL,
  `role_code_id` varchar(3) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member_role`
--


DROP TABLE IF EXISTS `role_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_code` (
  `role_code_id` varchar(3) NOT NULL,
  `role_code_name` varchar(50) NOT NULL,
  PRIMARY KEY (`role_code_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_code`
--

LOCK TABLES `role_code` WRITE;
/*!40000 ALTER TABLE `role_code` DISABLE KEYS */;
INSERT INTO `role_code` VALUES ('01','build'),('02','deploy'),('03','all');
/*!40000 ALTER TABLE `role_code` ENABLE KEYS */;
UNLOCK TABLES;


DROP TABLE IF EXISTS `workspace`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workspace` (
  `id` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
  `member_id` bigint(10) unsigned DEFAULT NULL,
  `workspace_name` varchar(50) NOT NULL,
  `status` tinyint(4) NOT NULL,
  `favorite_flag` varchar(1) DEFAULT NULL,
  `workspace_path` varchar(50) DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime DEFAULT NULL,
  `workspace_etc1` varchar(100) DEFAULT NULL,
  `workspace_etc2` varchar(100) DEFAULT NULL,
  `workspace_etc3` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `workspace`
--

DROP TABLE IF EXISTS `all_branch_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `all_branch_settings` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `branch_id` varchar(50) NOT NULL,
  `branch_name` varchar(100) NOT NULL,
  `session_status` varchar(3) DEFAULT NULL,
  `session_type` varchar(30) DEFAULT NULL,
  `last_date` datetime DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `all_branch_settings`
--

LOCK TABLES `all_branch_settings` WRITE;
/*!40000 ALTER TABLE `all_branch_settings` DISABLE KEYS */;
INSERT INTO `all_branch_settings` VALUES (1,'000001','buildserver','Y','BRANCH','2020-08-04 04:56:57','2020-08-04 04:56:57','2020-08-04 04:56:57');
/*!40000 ALTER TABLE `all_branch_settings` ENABLE KEYS */;
UNLOCK TABLES;


CREATE TABLE `branch_settings` (
  `branch_id` int(11) NOT NULL AUTO_INCREMENT,
  `role_code_id` varchar(5) NOT NULL,
  `branch_user_id` varchar(10) NOT NULL,
  `branch_name` varchar(255) NOT NULL,
  `session_status` varchar(20) DEFAULT NULL,
  `session_type` varchar(100) DEFAULT NULL,
  `create_date` datetime NOT NULL,
  `update_date` datetime DEFAULT NULL,
  `branch_url` varchar(100) DEFAULT NULL,
  `last_date` datetime DEFAULT NULL,
  PRIMARY KEY (`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8

CREATE TABLE `vcs_settings` (
  `vcs_id` int(11) NOT NULL AUTO_INCREMENT,
  `role_code_id` varchar(5) NOT NULL,
  `vcs_type` varchar(20) DEFAULT NULL,
  `vcs_url` varchar(255) DEFAULT NULL,
  `vcs_user_id` varchar(255) DEFAULT NULL,
  `vcs_user_pwd` varchar(255) DEFAULT NULL,
  `create_date` datetime NOT NULL,
  `update_date` datetime DEFAULT NULL,
  `vcs_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`vcs_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8


CREATE TABLE `ftp_settings` (
  `ftp_id` int(11) NOT NULL AUTO_INCREMENT,
  `role_code_id` varchar(10) NOT NULL,
  `ftp_name` varchar(255) NOT NULL,
  `ftp_url` varchar(255) NOT NULL,
  `ftp_type` varchar(20) DEFAULT NULL,
  `ftp_etc1` varchar(100) DEFAULT NULL,
  `ftp_etc2` varchar(100) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `ftp_ip` varchar(20) NOT NULL,
  `ftp_port` int(9) NOT NULL,
  `ftp_user_id` varchar(100) NOT NULL,
  `ftp_user_pwd` varchar(255) NOT NULL,
  PRIMARY KEY (`ftp_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8






