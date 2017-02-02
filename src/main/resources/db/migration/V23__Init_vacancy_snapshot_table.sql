CREATE TABLE `vacancies_snapshot` (
	`id` VARCHAR(50) NOT NULL,
	`creation_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	`author_id` BIGINT(20) NOT NULL,
	`project` TEXT NOT NULL,
	`description` TEXT NOT NULL,
	`requirements` TEXT NOT NULL,
	`owner_id` BIGINT(20) NOT NULL,
	`quantity` BIGINT(20) NOT NULL,
	`assignee_id` BIGINT(20) NOT NULL,
	`seniority_id` BIGINT(20) NOT NULL,
	`position_id` BIGINT(20) NOT NULL,
	`location_id` INT(1) NOT NULL,
	`status_id` TINYINT(4) NULL DEFAULT NULL,
	INDEX `vacancy_snapshot_id` (`id`),
	INDEX `author_vacancy_snapshot_fk` (`author_id`),
	INDEX `assignee_vacancy_snapshot_fk` (`assignee_id`),
	INDEX `owner_vacancy_snapshot_fk` (`owner_id`),
	INDEX `seniority_vacancy_snapshot_fk` (`seniority_id`),
	INDEX `position_vacancy_snapshot_fk` (`position_id`),
	CONSTRAINT `assignee_vacancy_snapshot_fk` FOREIGN KEY (`assignee_id`) REFERENCES `users` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT `author_vacancy_snapshot_fk` FOREIGN KEY (`author_id`) REFERENCES `users` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT `owner_vacancy_snapshot_fk` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT `position_vacancy_snapshot_fk` FOREIGN KEY (`position_id`) REFERENCES `position` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT `seniority_vacancy_snapshot_fk` FOREIGN KEY (`seniority_id`) REFERENCES `seniority` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
