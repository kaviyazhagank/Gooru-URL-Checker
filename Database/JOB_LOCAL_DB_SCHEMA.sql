CREATE TABLE `domain_list` (
  `resource_source_id` int(11) NOT NULL,
  `confidence_level` int(11) DEFAULT '0',
  `domain_name` varchar(255) DEFAULT NULL,
  `fail_count` int(11) DEFAULT '0',
  `last_checked_count` int(11) DEFAULT '0',
  `last_checked_date` datetime DEFAULT NULL,
  `ok_count` int(11) DEFAULT '0',
  `resource_count` int(11) DEFAULT '0',
  `total_checked` int(11) DEFAULT '0',
  `transient_count` int(11) DEFAULT '0',
  PRIMARY KEY (`resource_source_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `job_config_settings` (
  `constant_name` varchar(100) NOT NULL,
  `constant_value` varchar(100) NOT NULL,
  PRIMARY KEY (`constant_name`),
  KEY `constant_name` (`constant_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8; 