CREATE TABLE IF NOT EXISTS `roles` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`name` TEXT NOT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

INSERT INTO `roles` (`id`, `name`) VALUES
	(1, 'Admin'),
	(2, 'HR'),
	(3, 'Manager'),
	(4, 'Developer'),
	(5, 'Anonymous');

alter table `users` add `role_id` BIGINT(20) NOT NULL;

UPDATE `users` SET `role_id` = 1;

ALTER TABLE `users`	ADD CONSTRAINT `user_role_fk` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

