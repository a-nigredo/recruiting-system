CREATE TABLE `vacancies` (
	`id` VARCHAR(50) NOT NULL,
	`title` VARCHAR(250) NULL DEFAULT NULL,
	`creation_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	`author_id` BIGINT(20) NOT NULL,
	INDEX `vacancy_id` (`id`),
	INDEX `author_vacancy_fk` (`author_id`),
	CONSTRAINT `author_vacancy_fk` FOREIGN KEY (`author_id`) REFERENCES `users` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;