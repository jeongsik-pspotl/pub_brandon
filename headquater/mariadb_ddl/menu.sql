-- whive.menu definition
CREATE TABLE `menu` (
                        `menu_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '메뉴 아이디 PK',
                        `menu_page_name` varchar(200) DEFAULT NULL COMMENT '메뉴 페이지 이름',
                        `menu_code` varchar(10) NOT NULL COMMENT '메뉴 코드 ex: m010000 (m대분류중분류소분류, 각2자리)',
                        `menu_profile_type` varchar(50) NOT NULL COMMENT '메뉴 프로파일 타입 ex: service, onpremise, all(service+onpremiss)',
                        `menu_role_type` varchar(50) NOT NULL COMMENT '메뉴 사용자 권한 ex: user, admin, superadmin',
                        `menu_lang_type` varchar(10) NOT NULL COMMENT '메뉴 언어 타입',
                        `text_label` varchar(200) NOT NULL COMMENT '메뉴 이름',
                        `menu_key` varchar(200) NOT NULL COMMENT '메뉴 key 값',
                        `depth` int(11) NOT NULL COMMENT '메뉴 분류 값 ex: 1,2',
                        `icon` varchar(200) DEFAULT NULL COMMENT '메뉴 아이콘',
                        `create_date` datetime DEFAULT NULL COMMENT '생성일자',
                        `update_date` datetime DEFAULT NULL COMMENT '수정일자',
                        PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='W-Hive 메뉴 관리 태이블';

INSERT INTO whive.menu (menu_page_name,menu_code,menu_profile_type,menu_role_type,menu_lang_type,text_label,menu_key,`depth`,icon,create_date,update_date) VALUES
                                                                                                                                                               ('workspace','m010000','all','ADMIN','ko','Workspace','Workspace',1,'/images/menu_ico/ico_mn01_on.png','2023-03-03 10:15:38','2023-03-03 10:15:38'),
                                                                                                                                                               ('history','m020000','all','ADMIN','ko','History','History',1,'/images/menu_ico/ico_mn02_on.png','2023-03-03 10:35:43','2023-03-03 10:35:43'),
                                                                                                                                                               ('build_history','m020100','all','ADMIN','ko','Build History','Build History',2,NULL,'2023-03-03 10:37:22','2023-03-03 10:37:22'),
                                                                                                                                                               ('deploy_history','m020200','all','ADMIN','ko','Deploy History','Deploy History',2,NULL,'2023-03-03 10:38:08','2023-03-03 10:38:08'),
                                                                                                                                                               ('settings','m030000','all','ADMIN','ko','Settings','Settings',1,'/images/menu_ico/ico_mn03_on.png','2023-03-03 10:38:39','2023-03-03 10:38:39'),
                                                                                                                                                               ('builder','m030100','onpremise','ADMIN','ko','Builder','Builder',2,NULL,'2023-03-03 10:39:09','2023-03-03 10:39:09'),
                                                                                                                                                               ('vcs','m030200','all','ADMIN','ko','VCS','VCS',2,NULL,'2023-03-03 10:39:47','2023-03-03 10:39:47'),
                                                                                                                                                               ('ftp_server','m030300','onpremise','ADMIN','ko','FTP Server','FTP Seerver',2,NULL,'2023-03-03 10:40:17','2023-03-03 10:40:17'),
                                                                                                                                                               ('certificate','m030400','all','ADMIN','ko','인증서','인증서',2,NULL,'2023-03-03 10:40:53','2023-03-03 10:40:53'),
                                                                                                                                                               ('role_manager','m040000','all','ADMIN','ko','Role Manager','Role Manager',1,'/images/menu_ico/ico_mn04_on.png','2023-03-03 10:41:28','2023-03-03 10:41:28');
INSERT INTO whive.menu (menu_page_name,menu_code,menu_profile_type,menu_role_type,menu_lang_type,text_label,menu_key,`depth`,icon,create_date,update_date) VALUES
                                                                                                                                                               ('domain','m040100','onpremise','ADMIN','ko','Domain','Domain',2,NULL,'2023-03-03 10:42:51','2023-03-03 10:42:51'),
                                                                                                                                                               ('workspace_manage','m040200','all','ADMIN','ko','Workspace 관리','Workspace 관리',2,NULL,'2023-03-03 10:44:45','2023-03-03 10:44:45'),
                                                                                                                                                               ('authority_manage','m040300','onpremise','ADMIN','ko','권한관리','권한관리',2,NULL,'2023-03-03 10:45:32','2023-03-03 10:45:32'),
                                                                                                                                                               ('user_list','m040400','onpremise','ADMIN','ko','사용자 목록','사용자 목록',2,NULL,'2023-03-03 10:46:13','2023-03-03 10:46:13'),
                                                                                                                                                               ('guide','m050000','all','ADMIN','ko','Guide','Guide',1,NULL,'2023-03-03 10:46:38','2023-03-03 10:46:38');
