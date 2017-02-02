  CREATE TABLE `position` (
    `id`    BIGINT(20) NOT NULL AUTO_INCREMENT,
    `title` TEXT       NOT NULL,
    PRIMARY KEY (`id`)
  )
    COLLATE = 'utf8_general_ci'
    ENGINE = InnoDB;

  CREATE TABLE `seniority` (
    `id`    BIGINT(20) NOT NULL AUTO_INCREMENT,
    `title` TEXT       NOT NULL,
    PRIMARY KEY (`id`)
  )
    COLLATE = 'utf8_general_ci'
    ENGINE = InnoDB;
  CREATE TABLE `source_type` (
    `id`    BIGINT(20) NOT NULL AUTO_INCREMENT,
    `title` TEXT       NOT NULL,
    PRIMARY KEY (`id`)
  )
    COLLATE = 'utf8_general_ci'
    ENGINE = InnoDB;

  CREATE TABLE `users` (
    `id`    BIGINT(20) NOT NULL AUTO_INCREMENT,
    `name`  TEXT       NOT NULL,
    `email` TEXT       NOT NULL,
    PRIMARY KEY (`id`)
  )
    COLLATE = 'utf8_general_ci'
    ENGINE = InnoDB;

  CREATE TABLE `candidates` (
    `human_readable_id` VARCHAR(50) NOT NULL,
    `name` TEXT NOT NULL,
    `surname` TEXT NOT NULL,
    `email` TEXT NOT NULL,
    `phone` TEXT NOT NULL,
    `skype` TEXT NOT NULL,
    `seniority_id` BIGINT(20) NOT NULL,
    `position_id` BIGINT(20) NOT NULL,
    `source_type_id` BIGINT(20) NOT NULL,
    `creation_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `comment` TEXT NOT NULL,
    `author_id` BIGINT(20) NOT NULL,
    INDEX `human_readable_id` (`human_readable_id`),
    INDEX `author_candidate_fk` (`author_id`),
    INDEX `position_candidate_fk` (`position_id`),
    INDEX `seniority_candidate_fk` (`seniority_id`),
    INDEX `source_type_candidate_fk` (`source_type_id`),
    CONSTRAINT `author_candidate_fk` FOREIGN KEY (`author_id`) REFERENCES `users` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT `position_candidate_fk` FOREIGN KEY (`position_id`) REFERENCES `position` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT `seniority_candidate_fk` FOREIGN KEY (`seniority_id`) REFERENCES `seniority` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT `source_type_candidate_fk` FOREIGN KEY (`source_type_id`) REFERENCES `source_type` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION
  )
    COLLATE='utf8_general_ci'
    ENGINE=InnoDB
  ;

  CREATE TABLE `cv` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `name` TEXT NOT NULL,
    `candidate_id` VARCHAR(50) NULL DEFAULT NULL,
    `creation_date` TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (`id`),
    INDEX `candidate_cv_fk` (`candidate_id`),
    CONSTRAINT `candidate_cv_fk` FOREIGN KEY (`candidate_id`) REFERENCES `candidates` (`human_readable_id`) ON UPDATE NO ACTION ON DELETE NO ACTION
  )
    COLLATE='utf8_general_ci'
    ENGINE=InnoDB
  ;

  CREATE TABLE `comments` (
    `id` VARCHAR(50) NOT NULL,
    `body` TEXT NOT NULL,
    `is_private` TINYINT(1) NOT NULL,
    `candidate_id` VARCHAR(50) NOT NULL,
    `author_id` BIGINT(20) NOT NULL,
    `creation_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `id` (`id`),
    INDEX `author_comment_fk` (`author_id`),
    INDEX `candidate_comment_fk` (`candidate_id`),
    CONSTRAINT `author_comment_fk` FOREIGN KEY (`author_id`) REFERENCES `users` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT `candidate_comment_fk` FOREIGN KEY (`candidate_id`) REFERENCES `candidates` (`human_readable_id`) ON UPDATE NO ACTION ON DELETE NO ACTION
  )
    COLLATE='utf8_general_ci'
    ENGINE=InnoDB
  ;

  CREATE TABLE `access_token` (
    `value`       TEXT       NOT NULL,
    `expire_date` BIGINT(20) NOT NULL,
    `user_id`     BIGINT(20) NOT NULL,
    INDEX `user_access_token_fk` (`user_id`),
    CONSTRAINT `user_access_token_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
      ON UPDATE NO ACTION
      ON DELETE NO ACTION
  )
    COLLATE = 'utf8_general_ci'
    ENGINE = InnoDB;

  INSERT INTO `source_type` (`id`, `title`) VALUES
    (1, 'Employee recommendation'),
    (2, 'Job sites'),
    (3, 'Recruitment agency'),
    (4, 'Recruiter\'s base');

  INSERT INTO `seniority` (`id`, `title`) VALUES
    (1, 'Trainee'),
    (2, 'Junior'),
    (3, 'Middle'),
    (4, 'Senior'),
    (5, 'Team Lead'),
    (6, 'Architect');

  INSERT INTO `position` (`id`, `title`) VALUES
    (1, '.Net Developer'),
    (2, 'Java Developer'),
    (3, 'Front-end Developer'),
    (4, 'QA Automation'),
    (5, 'QA Manual'),
    (6, 'PHP Developer'),
    (7, 'System Administrator'),
    (8, 'DevOps Engineer'),
    (9, 'Delivery Manager'),
    (10, 'HR'),
    (11, 'Country Manager'),
    (12, 'Python Developer'),
    (13, 'iOS Developer'),
    (14, 'Android Developer')