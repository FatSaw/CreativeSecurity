CREATE TABLE `creativesecurity_version` (
  `property`  enum('version') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
  `value`  tinyint(4) NOT NULL ,
  PRIMARY KEY (`property`)
)
  ENGINE=MyISAM
  DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
  CHECKSUM=0
  ROW_FORMAT=FIXED
  DELAY_KEY_WRITE=0
;

CREATE TABLE `creativesecurity_worlds` (
  `world_id`  binary(16) NOT NULL ,
  `world_folder`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
  PRIMARY KEY (`world_id`),
  UNIQUE INDEX `world_folder` (`world_folder`) USING BTREE
)
  ENGINE=InnoDB
  DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
  ROW_FORMAT=COMPACT
;

CREATE TABLE `creativesecurity_blocks` (
  `world_id`  binary(16) NOT NULL ,
  `x`  int(11) NOT NULL ,
  `y`  int(11) NOT NULL ,
  `z`  int(11) NOT NULL ,
  `region_x`  int(11) NOT NULL ,
  `region_z`  int(11) NOT NULL ,
  `uuid`  binary(16) NOT NULL ,
  `date`  date NOT NULL ,
  PRIMARY KEY (`world_id`, `x`, `y`, `z`),
  FOREIGN KEY (`world_id`) REFERENCES `creativesecurity_worlds` (`world_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  INDEX `region` (`world_id`, `region_x`, `region_z`) USING BTREE
)
  ENGINE=InnoDB
  DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
  ROW_FORMAT=COMPACT
;

CREATE TABLE `creativesecurity_playerdata` (
  `player_id`  binary(16) NOT NULL ,
  `current_survival`  tinyint(2) NOT NULL ,
  `current_creative`  tinyint(2) NOT NULL ,
  `last_creative`  tinyint(1) NOT NULL ,
  `survival_inventories`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
  `creative_inventories`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
  PRIMARY KEY (`player_id`)
)
  ENGINE=InnoDB
  DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
  ROW_FORMAT=COMPACT
;

INSERT INTO creativesecurity_version VALUES ('version', 1);
