CREATE TABLE `candidates_snapshot` (
	`id` VARCHAR(50) NOT NULL,
	`name` TEXT NOT NULL,
	`surname` TEXT NULL,
	`email` TEXT NOT NULL,
	`phone` TEXT NOT NULL,
	`skype` TEXT NOT NULL,
	`seniority_id` BIGINT(20) NOT NULL,
	`position_id` BIGINT(20) NOT NULL,
	`source_type_id` BIGINT(20) NOT NULL,
	`creation_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	`author_id` BIGINT(20) NOT NULL,
	`status` ENUM('1','2','3','4','5','6','7') NOT NULL DEFAULT '1',
	`vacancy_id` VARCHAR(50) NULL DEFAULT NULL,
	`location` TINYINT(4) NOT NULL,
	INDEX `author_candidate_snapshot_fk` (`author_id`),
	INDEX `position_candidate_snapshot_fk` (`position_id`),
	INDEX `seniority_candidate_snapshot_fk` (`seniority_id`),
	INDEX `source_type_candidate_snapshot_fk` (`source_type_id`),
	INDEX `candidateSnapshotIdIndex` (`id`),
	INDEX `vacancy_id_candidate_snapshot_fk` (`vacancy_id`),
	CONSTRAINT `author_candidate_snapshot_fk` FOREIGN KEY (`author_id`) REFERENCES `users` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT `position_candidate_snapshot_fk` FOREIGN KEY (`position_id`) REFERENCES `position` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT `seniority_candidate_snapshot_fk` FOREIGN KEY (`seniority_id`) REFERENCES `seniority` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT `source_type_candidate_snapshot_fk` FOREIGN KEY (`source_type_id`) REFERENCES `source_type` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT `vacancy_id_candidate_snapshot_fk` FOREIGN KEY (`vacancy_id`) REFERENCES `vacancies` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
