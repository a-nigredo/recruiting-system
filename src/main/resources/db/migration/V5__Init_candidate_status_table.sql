  CREATE TABLE IF NOT EXISTS `candidate_status` (
    `id`    BIGINT(20) NOT NULL AUTO_INCREMENT,
    `title` TEXT       NOT NULL,
    PRIMARY KEY (`id`)
  )
    COLLATE = 'utf8_general_ci'
    ENGINE = InnoDB;

INSERT INTO `candidate_status` (`id`, `title`) VALUES
	(1, 'On hold'),
	(2, 'Screening'),
	(3, 'Not a fit'),
	(4, 'Offered'),
	(5, 'Accepted'),
	(6, 'Rejected');

alter table `candidates` add `status_id` BIGINT(20) NOT NULL;

UPDATE `candidates` SET status_id = 1;

ALTER TABLE `candidates`
	ADD CONSTRAINT `status_fk` FOREIGN KEY (`status_id`) REFERENCES `candidate_status` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION;
