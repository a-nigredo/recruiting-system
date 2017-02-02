ALTER TABLE `vacancies` ADD `project` TEXT NOT NULL;
ALTER TABLE `vacancies` ADD `description` TEXT NOT NULL;
ALTER TABLE `vacancies` ADD `requirements` TEXT NOT NULL;
ALTER TABLE `vacancies` ADD `owner_id` BIGINT(20) NOT NULL;
ALTER TABLE `vacancies` ADD `quantity` BIGINT(20) NOT NULL;
ALTER TABLE `vacancies` ADD `assignee_id` BIGINT(20) NOT NULL;
ALTER TABLE `vacancies` ADD `seniority_id` BIGINT(20) NOT NULL;
ALTER TABLE `vacancies` ADD `position_id` BIGINT(20) NOT NULL;
ALTER TABLE `vacancies` ADD `location_id` INT(1) NOT NULL AFTER `position_id`;

ALTER TABLE `vacancies` ADD CONSTRAINT `assignee_vacancy_fk` FOREIGN KEY (`assignee_id`) REFERENCES `users` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `vacancies` ADD CONSTRAINT `owner_vacancy_fk` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `vacancies` ADD CONSTRAINT `seniority_vacancy_fk` FOREIGN KEY (`seniority_id`) REFERENCES `seniority` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `vacancies` ADD CONSTRAINT `position_vacancy_fk` FOREIGN KEY (`position_id`) REFERENCES `position` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION;
