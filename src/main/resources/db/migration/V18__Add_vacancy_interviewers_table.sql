CREATE TABLE `vacancy_interviewers` (
	`vacancy_id` VARCHAR(50) NULL,
	`interviewer_id` VARCHAR(50) NULL
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

ALTER TABLE `vacancy_interviewers`
	ALTER `vacancy_id` DROP DEFAULT,
	ALTER `interviewer_id` DROP DEFAULT;
ALTER TABLE `vacancy_interviewers`
	CHANGE COLUMN `vacancy_id` `vacancy_id` VARCHAR(50) NOT NULL FIRST,
	CHANGE COLUMN `interviewer_id` `interviewer_id` BIGINT(20) NOT NULL AFTER `vacancy_id`,
	ADD CONSTRAINT `vacancy_id_fk` FOREIGN KEY (`vacancy_id`) REFERENCES `vacancies` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
	ADD CONSTRAINT `vacancy_interviewer_fk` FOREIGN KEY (`interviewer_id`) REFERENCES `users` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

