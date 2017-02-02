ALTER TABLE `candidates` ALTER `human_readable_id` DROP DEFAULT;
ALTER TABLE `candidates` CHANGE COLUMN `human_readable_id` `id` VARCHAR(50) NOT NULL FIRST;
ALTER TABLE `candidates` DROP INDEX `human_readable_id`, ADD INDEX `candidateIdIndex` (`id`);