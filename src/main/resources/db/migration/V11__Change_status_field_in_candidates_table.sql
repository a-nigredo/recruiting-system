ALTER TABLE `candidates` ALTER `status_id` DROP DEFAULT;
ALTER TABLE `candidates` CHANGE COLUMN `status_id` `status` ENUM('1','2','3','4','5','6') NOT NULL DEFAULT '1' AFTER `author_id`;
