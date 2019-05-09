-- Data schema

-- !Ups

create table `data` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `objectId` BIGINT NOT NULL,
  `latitude` TEXT NOT NULL,
  `longitude` TEXT NOT NULL,
  `temperature` TEXT NOT NULL,
  `batteryRemaining` TEXT NOT NULL,
  `heartRate` TEXT NOT NULL
)

-- !Downs
drop table `data`