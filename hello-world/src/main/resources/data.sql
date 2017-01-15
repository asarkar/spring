DROP TABLE IF EXISTS `countrylanguage`;
DROP TABLE IF EXISTS `city`;
DROP TABLE IF EXISTS `country`;
CREATE TABLE `country` (
  `code` char(3) NOT NULL DEFAULT '',
  `name` char(52) NOT NULL DEFAULT '',
  `continent` enum('Asia','Europe','North America','Africa','Oceania','Antarctica','South America') NOT NULL DEFAULT 'Asia',
  `region` char(26) NOT NULL DEFAULT '',
  `surfacearea` float(10,2) NOT NULL DEFAULT '0.00',
  `indepyear` smallint(6) DEFAULT NULL,
  `population` int(11) NOT NULL DEFAULT '0',
  `lifeexpectancy` float(3,1) DEFAULT NULL,
  `gnp` float(10,2) DEFAULT NULL,
  `gnpold` float(10,2) DEFAULT NULL,
  `localname` char(45) NOT NULL DEFAULT '',
  `governmentform` char(45) NOT NULL DEFAULT '',
  `headofstate` char(60) DEFAULT NULL,
  `capital` int(11) DEFAULT NULL,
  `code2` char(2) NOT NULL DEFAULT '',
  PRIMARY KEY (`code`)
);
INSERT INTO `country` VALUES ('AFG','Afghanistan','Asia','Southern and Central Asia',652090.00,1919,22720000,45.9,5976.00,NULL,'Afganistan/Afqanestan','Islamic Emirate','Mohammad Omar',1,'AF');
INSERT INTO `country` VALUES ('NLD','Netherlands','Europe','Western
Europe',41526.00,1581,15864000,78.3,371362.00,360478.00,'Nederland','Constitutional Monarchy','Beatrix',5,'NL');
CREATE TABLE `city` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` char(35) NOT NULL DEFAULT '',
  `countrycode` char(3) NOT NULL DEFAULT '',
  `district` char(20) NOT NULL DEFAULT '',
  `population` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `k_countrycode_city` (`countrycode`),
  CONSTRAINT `fk_countrycode_city` FOREIGN KEY (`countrycode`) REFERENCES `country` (`code`)
);
INSERT INTO `city` VALUES (1,'Kabul','AFG','Kabol',1780000);
INSERT INTO `city` VALUES (2,'Qandahar','AFG','Qandahar',237500);
INSERT INTO `city` VALUES (3,'Herat','AFG','Herat',186800);
INSERT INTO `city` VALUES (4,'Mazar-e-Sharif','AFG','Balkh',127800);
INSERT INTO `city` VALUES (5,'Amsterdam','NLD','Noord-Holland',731200);
INSERT INTO `city` VALUES (6,'Rotterdam','NLD','Zuid-Holland',593321);
INSERT INTO `city` VALUES (7,'Haag','NLD','Zuid-Holland',440900);
INSERT INTO `city` VALUES (8,'Utrecht','NLD','Utrecht',234323);
INSERT INTO `city` VALUES (9,'Eindhoven','NLD','Noord-Brabant',201843);
INSERT INTO `city` VALUES (10,'Tilburg','NLD','Noord-Brabant',193238);
INSERT INTO `city` VALUES (11,'Groningen','NLD','Groningen',172701);
INSERT INTO `city` VALUES (12,'Breda','NLD','Noord-Brabant',160398);
INSERT INTO `city` VALUES (13,'Apeldoorn','NLD','Gelderland',153491);
INSERT INTO `city` VALUES (14,'Nijmegen','NLD','Gelderland',152463);
INSERT INTO `city` VALUES (15,'Enschede','NLD','Overijssel',149544);
INSERT INTO `city` VALUES (16,'Haarlem','NLD','Noord-Holland',148772);
INSERT INTO `city` VALUES (17,'Almere','NLD','Flevoland',142465);
INSERT INTO `city` VALUES (18,'Arnhem','NLD','Gelderland',138020);
INSERT INTO `city` VALUES (19,'Zaanstad','NLD','Noord-Holland',135621);
INSERT INTO `city` VALUES (20,'s-Hertogenbosch','NLD','Noord-Brabant',129170);
CREATE TABLE `countrylanguage` (
  `countrycode` char(3) NOT NULL DEFAULT '',
  `language` char(30) NOT NULL DEFAULT '',
  `isofficial` enum('T','F') NOT NULL DEFAULT 'F',
  `percentage` float(4,1) NOT NULL DEFAULT '0.0',
  PRIMARY KEY (`countrycode`,`language`),
  KEY `k_countrycode_countrylanguage` (`countrycode`),
  CONSTRAINT `fk_countrycode_countrylanguage` FOREIGN KEY (`countrycode`) REFERENCES `country` (`code`)
);
INSERT INTO `countrylanguage` VALUES ('AFG','Balochi','F',0.9);
INSERT INTO `countrylanguage` VALUES ('AFG','Dari','T',32.1);
INSERT INTO `countrylanguage` VALUES ('AFG','Pashto','T',52.4);
INSERT INTO `countrylanguage` VALUES ('AFG','Turkmenian','F',1.9);
INSERT INTO `countrylanguage` VALUES ('AFG','Uzbek','F',8.8);
INSERT INTO `countrylanguage` VALUES ('NLD','Fries','F',3.7);
INSERT INTO `countrylanguage` VALUES ('NLD','Turkish','F',0.8);
COMMIT;