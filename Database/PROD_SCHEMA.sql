CREATE TABLE `content` (
  `content_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `account_uid` varchar(36) DEFAULT NULL,
  `gooru_oid` varchar(36) NOT NULL DEFAULT '',
  `type_name` varchar(20) NOT NULL,
  `user_id` int(10) unsigned DEFAULT NULL,
  `creator_id` int(11) unsigned DEFAULT NULL,
  `sharing` enum('public','private','anyonewithlink') NOT NULL,
  `created_on` datetime DEFAULT NULL,
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `last_updated_user_uid` varchar(36) DEFAULT NULL,
  `revision_history_uid` varchar(36) DEFAULT NULL,
  `user_uid` varchar(36) DEFAULT NULL,
  `status_type` int(11) DEFAULT NULL,
  `creator_uid` varchar(36) DEFAULT NULL,
  `organization_uid` varchar(36) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`content_id`),
  UNIQUE KEY `idx_unique_gooru_oid` (`gooru_oid`) USING HASH,
  KEY `FK_content_content_type` (`type_name`),
  KEY `FK_content_user` (`user_id`),
  KEY `idx_gooru_content_id` (`gooru_oid`),
  KEY `organization_uid` (`account_uid`),
  KEY `creator_id` (`creator_id`),
  KEY `user_uid` (`user_uid`),
  KEY `creator_uid` (`creator_uid`),
  KEY `organization_uid_2` (`organization_uid`),
  KEY `status_type` (`status_type`),
  CONSTRAINT `FK_content_content_type` FOREIGN KEY (`type_name`) REFERENCES `content_type` (`name`) ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=18793180 DEFAULT CHARSET=utf8;

CREATE TABLE `resource` (
  `content_id` bigint(20) unsigned NOT NULL,
  `type_name` varchar(20) NOT NULL,
  `category` varchar(100) DEFAULT NULL,
  `url` varchar(2000) DEFAULT NULL,
  `resource_source_id` int(11) DEFAULT NULL,
  `title` varchar(1000) DEFAULT '',
  `has_frame_breaker` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`content_id`),
  UNIQUE KEY `content_id` (`content_id`,`type_name`),
  KEY `url` (`url`(255)),
  KEY `type_name` (`type_name`,`in_use`),
  KEY `resource_source_id` (`resource_source_id`),
  KEY `resource_info_id` (`resource_info_id`),
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




