-- Data schema

-- !Ups

create table `data` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `timestamp` BIGINT NOT NULL,
  `objectId` VARCHAR(25) NOT NULL,
  `latitude` DOUBLE NOT NULL,
  `longitude` DOUBLE NOT NULL,
  `temperature` FLOAT NOT NULL,
  `batteryRemaining` INT NOT NULL,
  `heartRate` FLOAT NOT NULL,
  `state` TEXT NOT NULL,
  `message` TEXT NOT NULL,
  `isAlert` BOOLEAN NOT NULL,
  UNIQUE (objectId, timestamp)
);

-- !Downs
drop table `data`