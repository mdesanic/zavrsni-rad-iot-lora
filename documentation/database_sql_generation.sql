
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`TemperatureSensor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`TemperatureSensor` (
  `temp_sensor_ID` INT NOT NULL AUTO_INCREMENT,
  `sensor_type` VARCHAR(40) NULL,
  `sensor_name` VARCHAR(60) NULL,
  PRIMARY KEY (`temp_sensor_ID`),
  UNIQUE INDEX `temp_sensor_ID_UNIQUE` (`temp_sensor_ID` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Location`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Location` (
  `location_ID` INT NOT NULL AUTO_INCREMENT,
  `location_address` VARCHAR(100) NULL,
  `location_name` VARCHAR(45) NULL,
  PRIMARY KEY (`location_ID`),
  UNIQUE INDEX `location_ID_UNIQUE` (`location_ID` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`DataUploaderDevice`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`DataUploaderDevice` (
  `dud_ID` INT NOT NULL AUTO_INCREMENT,
  `dud_IP_address` VARCHAR(15) NOT NULL,
  `location_ID` INT NOT NULL,
  PRIMARY KEY (`dud_ID`),
  UNIQUE INDEX `dud_ID_UNIQUE` (`dud_ID` ASC) VISIBLE,
  UNIQUE INDEX `dud_IP_address_UNIQUE` (`dud_IP_address` ASC) VISIBLE,
  INDEX `dud_locaiton_fk_idx` (`location_ID` ASC) VISIBLE,
  CONSTRAINT `dud_locaiton_fk`
    FOREIGN KEY (`location_ID`)
    REFERENCES `mydb`.`Location` (`location_ID`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`MeasuringDevice`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`MeasuringDevice` (
  `md_ID` INT NOT NULL AUTO_INCREMENT,
  `dud_ID` INT NOT NULL,
  PRIMARY KEY (`md_ID`),
  UNIQUE INDEX `md_ID_UNIQUE` (`md_ID` ASC) VISIBLE,
  INDEX `dud_md_fk_idx` (`dud_ID` ASC) VISIBLE,
  CONSTRAINT `dud_md_fk`
    FOREIGN KEY (`dud_ID`)
    REFERENCES `mydb`.`DataUploaderDevice` (`dud_ID`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`TemperatureReading`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`TemperatureReading` (
  `reading_ID` INT NOT NULL AUTO_INCREMENT,
  `temperature_value` DECIMAL NOT NULL,
  `timestamp` TIMESTAMP NOT NULL,
  `temp_sensor_ID` INT NOT NULL,
  `md_ID` INT NOT NULL,
  PRIMARY KEY (`reading_ID`),
  UNIQUE INDEX `reading_ID_UNIQUE` (`reading_ID` ASC) VISIBLE,
  INDEX `temp_sensor_reading_fk_idx` (`temp_sensor_ID` ASC) VISIBLE,
  INDEX `md_reading_fk_idx` (`md_ID` ASC) VISIBLE,
  CONSTRAINT `temp_sensor_reading_fk`
    FOREIGN KEY (`temp_sensor_ID`)
    REFERENCES `mydb`.`TemperatureSensor` (`temp_sensor_ID`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `md_reading_fk`
    FOREIGN KEY (`md_ID`)
    REFERENCES `mydb`.`MeasuringDevice` (`md_ID`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;

