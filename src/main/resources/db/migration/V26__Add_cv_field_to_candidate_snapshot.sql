ALTER TABLE `cv` ADD PRIMARY KEY (`id`);
ALTER TABLE `candidates_snapshot` ADD COLUMN `cv` VARCHAR(50) NULL AFTER `location`;
ALTER TABLE `candidates_snapshot`	ADD INDEX `cv_candidate_snapshot_fk` (`cv`);
ALTER TABLE `candidates_snapshot`	ADD CONSTRAINT `cv_candidate_snapshot_fk` FOREIGN KEY (`cv`) REFERENCES `cv` (`id`);