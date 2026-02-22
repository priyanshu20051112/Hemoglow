-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: bloodbank
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `donations`
--

DROP TABLE IF EXISTS `donations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `donations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `center_name` varchar(255) NOT NULL,
  `blood_group` varchar(3) NOT NULL,
  `donation_date` date NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `center` varchar(255) DEFAULT NULL,
  `date` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `donations_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `donations`
--

LOCK TABLES `donations` WRITE;
/*!40000 ALTER TABLE `donations` DISABLE KEYS */;
/*!40000 ALTER TABLE `donations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_participants`
--

DROP TABLE IF EXISTS `event_participants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event_participants` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `event_id` bigint NOT NULL,
  `joined_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`,`event_id`),
  KEY `event_id` (`event_id`),
  CONSTRAINT `event_participants_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `event_participants_ibfk_2` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_participants`
--

LOCK TABLES `event_participants` WRITE;
/*!40000 ALTER TABLE `event_participants` DISABLE KEYS */;
INSERT INTO `event_participants` VALUES (1,52,1,'2025-10-09 20:19:34'),(2,49,2,'2025-10-09 20:32:33'),(3,53,2,'2025-10-09 20:32:57'),(6,59,6,'2025-10-09 22:11:26'),(7,59,5,'2025-10-09 22:16:51');
/*!40000 ALTER TABLE `event_participants` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `events`
--

DROP TABLE IF EXISTS `events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `events` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `event_date` varchar(255) NOT NULL,
  `location` varchar(255) NOT NULL,
  `organization_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `details` text,
  PRIMARY KEY (`id`),
  KEY `FKo5pg8uyvf27066bj8bl97d4b1` (`organization_id`),
  KEY `FKat8p3s7yjcp57lny4udqvqncq` (`user_id`),
  CONSTRAINT `FKat8p3s7yjcp57lny4udqvqncq` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKo5pg8uyvf27066bj8bl97d4b1` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `events`
--

LOCK TABLES `events` WRITE;
/*!40000 ALTER TABLE `events` DISABLE KEYS */;
INSERT INTO `events` VALUES (1,'sdfja;osd','2025-10-25','dsfs',10,52,'sadfs'),(2,'khoon bhari maang','2025-10-22','mulund',8,49,'mulund west'),(3,'donate','2025-10-18','mulund',11,55,'mulund west'),(5,'new era','2025-10-15','mulund',12,57,'mulund west'),(6,'new ','2025-10-29','mulund',12,57,'mulund west');
/*!40000 ALTER TABLE `events` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventory`
--

DROP TABLE IF EXISTS `inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `blood_group` varchar(255) DEFAULT NULL,
  `liters` int NOT NULL,
  `organization_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3ukmw9v23ffqhltuu6m393gu2` (`organization_id`),
  KEY `FK6s70ikopm646wy54vwowsnp6d` (`user_id`),
  CONSTRAINT `FK3ukmw9v23ffqhltuu6m393gu2` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`),
  CONSTRAINT `FK6s70ikopm646wy54vwowsnp6d` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory`
--

LOCK TABLES `inventory` WRITE;
/*!40000 ALTER TABLE `inventory` DISABLE KEYS */;
INSERT INTO `inventory` VALUES (1,'A+',10,NULL,27),(2,'A+',8,NULL,24),(3,'A+',5,NULL,28),(4,'O+',10,NULL,29),(5,'O+',5,NULL,30),(6,'O+',6,NULL,31),(7,'O+',6,NULL,32),(8,'O+',8,NULL,33),(9,'O+',25,NULL,34),(10,'AB-',5,NULL,37),(11,'AB-',7,NULL,36),(12,'B-',10,NULL,40),(13,'B-',2,2,NULL),(14,'B-',5,NULL,44),(15,'O-',7,NULL,47),(16,'A+',9,8,49),(17,'B-',6,NULL,53),(18,'O-',1,10,52),(19,'A+',4,11,55),(20,'O-',5,12,57),(21,'AB-',5,NULL,58),(22,'AB-',3,12,57),(23,'B-',3,12,57),(24,'B+',8,12,57);
/*!40000 ALTER TABLE `inventory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `organization`
--

DROP TABLE IF EXISTS `organization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `organization` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `blood_liters` int NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `open_for_donations` bit(1) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `organization_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_organization_user` (`user_id`),
  CONSTRAINT `fk_organization_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `organization`
--

LOCK TABLES `organization` WRITE;
/*!40000 ALTER TABLE `organization` DISABLE KEYS */;
INSERT INTO `organization` VALUES (1,0,'testorg@example.com','Test Organization',_binary '','$2a$10$ArXhmtgjzviGvR1J82lN8.qvSuq.OHA.UvbDABb3Xa8q.X5Ez6axK','testorg',NULL,NULL,NULL,''),(2,0,'queen4@gmail.com','queen4',_binary '\0','$2a$10$dPLoy2QXd/CVw4Cm/TpN5uIK6n/5lx1jv9aMdgQ5MDAz6Mg1Vk9xm','queen4',NULL,NULL,34,''),(3,0,'org@example.com','My Org',_binary '\0','$2a$10$UFIKve5rbnWznBmBkj3Cq.A.Tru0Z1IcH0ER1Blsr.c7uJ7qq3/Om','myorg',NULL,NULL,35,''),(4,0,'org2@example.com','City Blood Center',_binary '\0','$2a$10$8VFjWsOpCXwLWuaA5t/elOZ3jTT8UtOs7ZT1vPsCP4PnA7tzaG882','org_test2',NULL,NULL,38,''),(5,0,'queen11@gmail.com','queen pandey',_binary '\0','$2a$10$MJ1U3MboKWDV25DO/Kcp4Od5eq18ividI0qJ1V7tyInzLntpmmjwW','queen11','thane','9967261464',44,'ORG-000044-20251010'),(6,0,'testorg123@bloodbank.org','Test Blood Bank',_binary '\0','$2a$10$aRwVsjDuArfLjfT0SOru9.xJ2kawynG8fBDViYtAS.XxxAI/lI6pm','testorg123','456 Hospital Ave','9876543210',46,'ORG-000046-20251010'),(7,0,'queen12@gmail.com','queen upadhyay',_binary '\0','$2a$10$ZriQu1DidSg67qY4EHXKDOHId8xv1WTioR6lP3RM5LW2Px58KnxZS','queen12','mira road','3333333333',47,'ORG-000047-20251010'),(8,0,'aastha@gmail.com','aastha blood bank',_binary '\0','$2a$10$FZW1rdz/w81gbXM1cxAjO.zEwR4IFsfvvSaXreQrnrVz4oVrpdyu.','aastha','mulund','9999999999',49,'ORG-000049-20251010'),(9,0,'autoorg_1760037600233@example.com','Auto Org',_binary '\0','$2a$10$ztHlEtAgEUiVSSJ5ovzF4emkVy4n.8T/x4Ak.MHv.5I7r8fAMAo4m','autoorg_1760037600233',NULL,NULL,51,'ORG-000051-20251010'),(10,0,'queen13@gmail.com','queen tripathi',_binary '\0','$2a$10$rJvWTsZb2WYtSCnElQAovubV.RzGC4fxoXPi96tbEV3ynXK7Y01h6','queen13','thane','2222222222',52,'ORG-000052-20251010'),(11,0,'annanay022@gmail.com','annanay bank of blood',_binary '\0','$2a$10$m4Zdq0UJbXsmSRJgwWKAUuHhHvtT5a0vhu.Wnm8nZmkfYMZEm4eN.','annanay022','mira road','9863241578',55,'ORG-000055-20251010'),(12,0,'simrun027@gmail.com','simrun blood bank',_binary '\0','$2a$10$22Lnd9rWXj16r8aTNDGTQ.4B6eIhnORBUQPWcDckOQDOXVm0PwNf2','simrun027','mira road','7568245619',57,'ORG-000057-20251010');
/*!40000 ALTER TABLE `organization` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `requests`
--

DROP TABLE IF EXISTS `requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `requests` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `blood_group` varchar(255) NOT NULL,
  `hospital` varchar(255) NOT NULL,
  `contact_number` varchar(255) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `patient` varchar(255) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `organization_id` bigint DEFAULT NULL,
  `amount` double NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `FKk8d7yyojoqctf3qpoouw59r3c` (`organization_id`),
  CONSTRAINT `FKk8d7yyojoqctf3qpoouw59r3c` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`),
  CONSTRAINT `requests_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `requests`
--

LOCK TABLES `requests` WRITE;
/*!40000 ALTER TABLE `requests` DISABLE KEYS */;
INSERT INTO `requests` VALUES (2,24,'A+','test4','9029874792','2025-10-05 19:08:06','sim','REJECTED',1,0),(3,27,'O+','test6','9029874782','2025-10-05 19:09:48','test4',NULL,1,0),(4,29,'O+','test6','9029874792','2025-10-05 19:15:00','test6',NULL,1,0),(5,24,'O+','test6','9029874792','2025-10-05 19:15:59','sim',NULL,1,0),(6,29,'O+','test6','9029874792','2025-10-05 19:25:26','test6',NULL,1,1),(7,29,'O+','test6','9029874792','2025-10-05 19:31:01','test6',NULL,1,1),(8,24,'O+','test6','9999999999','2025-10-05 19:41:36','sim',NULL,1,1),(9,18,'O+','test6','9999999999','2025-10-05 19:42:59','nandita05',NULL,1,1),(10,18,'O+','test6','6969696969','2025-10-05 19:50:45','nandita05',NULL,1,2),(11,30,'O+','queen','9029874792','2025-10-08 15:15:22','queen',NULL,NULL,1),(12,35,'A+','City Hospital','1234567890','2025-10-08 18:40:45','John Doe',NULL,3,2),(13,35,'A+','City Hospital','1234567890','2025-10-08 18:44:06','John Doe',NULL,3,2),(14,36,'AB-','queen5','5555555555','2025-10-08 18:48:13','queen5',NULL,NULL,1),(15,44,'B-','queen11','9029874792','2025-10-09 18:47:12','queen11',NULL,5,1),(16,47,'O-','queen12','8888888888','2025-10-09 18:57:23','queen12','REJECTED',7,1),(17,52,'O-','queen13','6565656565','2025-10-09 19:48:45','queen13','ACCEPTED',10,2),(18,49,'A+','aastha','9898989898','2025-10-09 20:31:51','aastha','ACCEPTED',8,1),(19,55,'A+','annanay022','9029874792','2025-10-09 21:11:56','annanay022','ACCEPTED',11,1),(20,57,'O-','simrun027','9029874792','2025-10-09 21:23:48','simrun027','ACCEPTED',12,1),(21,59,'AB-','simrun027','9999999999','2025-10-09 22:08:39','annanay034','ACCEPTED',12,1),(22,59,'B-','simrun027','9852361476','2025-10-09 22:11:16','annanay034','ACCEPTED',12,2),(23,59,'B+','simrun027','9029874792','2025-10-09 22:16:42','annanay034','ACCEPTED',12,2);
/*!40000 ALTER TABLE `requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `user_role` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'priyanshu@example.com','$2a$10$8U67BAAEOFllY6Ko6RLgIO0n2.2LT9ETMPEl9YW1bJXa8uipgCavW.','USER','priyanshu',NULL,NULL,NULL),(3,'priyanshu2@example.com','$2a$10$Te9k2WLvNWz8d3w0Bc0oouHPIc7Iyp7swYqCteYDbCTldg.mqE.ia','USER','priyanshu1',NULL,NULL,NULL),(18,'nandita1@gmail.com','$2a$10$YHkkejr24Jrp0vZ/WbgmXO7zLC8qIfbj3t3MphCE4hwqKLJigjk7G','USER','nandita05',NULL,NULL,NULL),(19,'simrun@gmail.com','$2a$10$RBDO3JSnoyvGDVJC7aXwfOaCX2PDf3rr.pqlExPlCM.3I98HRG7JS','USER','simrun',NULL,NULL,NULL),(20,'lilavati@gmail.com','$2a$10$SgJlttnR58QLLxmHgJ6at./DkehPHfFxvI9hCEximC8xfv.OIlqaa','ORGANISATION','lilavati hospital',NULL,NULL,NULL),(21,'city@gmail.com','$2a$10$rPMhYyPL0pRHaRgLax0ZN.Xq1fCkfxO2nXl8hbqrpX50iWx5qw8zC','ORGANISATION','city',NULL,NULL,NULL),(22,'test@gmail.com','$2a$10$ffjUbBMZSeyzGqC67/pafuVwW.cinRQwjNBjapehjkyl.1o5ZuJUq','ORGANISATION','test',NULL,NULL,NULL),(23,'test2@gmail.com','$2a$10$h.RKyPXfKEGgDque4uSdeOhxvti/7ygcc0nfz6FC/HunBBA.xeMna','ORGANISATION','test2',NULL,NULL,NULL),(24,'sim@gmail.com','$2a$10$ZXoH9wTIv71YdW3OBQLtNOdGoiBPVQTHdBFZUTqjtwfLlX.d.YNPS','USER','sim',NULL,NULL,NULL),(25,'king@gmail.com','$2a$10$fLZxIkgrQT/QLsID/92FXuRDhWY5/yzKTlZEzDP6fKJGt2qs0N9L6','USER','king',NULL,NULL,NULL),(26,'test3@gmail.com','$2a$10$JIM/aWnOdItVtg6W50W/NOWPZmoSIvzEXZRtCzJWynw752F.i0fTm','ORGANISATION','test3',NULL,NULL,NULL),(27,'test4@gmail.com','$2a$10$5W0WIst8hvUZWPxWfqa9LOO.d8ckDVHKAG0U4vnYqCd.g9t2LfSZG','ORGANISATION','test4',NULL,NULL,NULL),(28,'test5@gmail.com','$2a$10$sKIx0Gvt2s9tRyApj8N6j.jAe7UdMOrh3GC7X9zrpaIR4tnIOSlbe','ORGANISATION','test5',NULL,NULL,NULL),(29,'test6@gmail.com','$2a$10$yHy8vov0cBaTKiAu8IuK1eAj4CYhHMHCblCWM6.x0hasrH05Ib1CW','ORGANISATION','test6',NULL,NULL,NULL),(30,'queen@gmail.com','$2a$10$Sk1CprCwYYpLvyBP/Junqe8VxDzl3d75KOR4oePyIa5e3A9I2lDxe','ORGANISATION','queen',NULL,NULL,NULL),(31,'queen1@gmail.com','$2a$10$RU9t0nOeuCQjD9M4WMm.m.qAFSrhPDRvPLpdnYYf05oguuRTTFjky','ORGANISATION','queen1',NULL,NULL,NULL),(32,'queen2@gmail.com','$2a$10$ggXLKpH8hFEt9IB5JlyTse5IGHsLTlo09UsVDp2TWOgNBBBPEeMJK','ORGANISATION','queen2',NULL,NULL,NULL),(33,'queen3@gmail.com','$2a$10$aRLoJMAyTJyttLhffJmjY.iNZbVig/6BLlpHBK7yUlWAJ0dXhzTM.','ORGANISATION','queen3',NULL,NULL,NULL),(34,'queen4@gmail.com','$2a$10$dPLoy2QXd/CVw4Cm/TpN5uIK6n/5lx1jv9aMdgQ5MDAz6Mg1Vk9xm','ORGANIZATION','queen4',NULL,NULL,NULL),(35,'org@example.com','$2a$10$UFIKve5rbnWznBmBkj3Cq.A.Tru0Z1IcH0ER1Blsr.c7uJ7qq3/Om','ORGANIZATION','myorg',NULL,NULL,NULL),(36,'queen5@gmail.com','$2a$10$acASHqplzs5bjsD/Gdj7we/9IGgx7pDX4nACvJk9jSOu6QDXddel.','ORGANISATION','queen5',NULL,NULL,NULL),(37,'king2@gmail.com','$2a$10$34AWBUIgrCFUd/aAq3q.lezlzkOlNxYyU5CQs3T4SCXGAfG6AbONm','USER','king2',NULL,NULL,NULL),(38,'org2@example.com','$2a$10$8VFjWsOpCXwLWuaA5t/elOZ3jTT8UtOs7ZT1vPsCP4PnA7tzaG882','ORGANIZATION','org_test2',NULL,NULL,NULL),(39,'queen6@gmail.com','$2a$10$DCBTDmsMnUxCBHRolIAMxuQ42RZMQHV.NBVjA1c4ZSxI27oLl0bqq','ORGANISATION','queen6',NULL,NULL,NULL),(40,'king3@gmail.com','$2a$10$HwopefQi9C9ZOyrVTqudTua6KTi613s82/w.Qg7EATeQShyh6js8a','USER','king3',NULL,NULL,NULL),(41,'queen10@gmail.com','$2a$10$7pr1hA6.uAUu32.iDomFHeGqYoL86otheHkJEfZqp/8wFS6609yme','ORGANISATION','queen10',NULL,NULL,NULL),(42,'king10@gmail.com','$2a$10$l1l50fhYp4Ygk2FwRscEquMFyPFOJSD0H5HvzrM1hrowjr5TF2dd2','USER','king10',NULL,NULL,NULL),(43,'king11@gmail.com','$2a$10$bp7iaAoA3YQ411I.pOPbaOy02NeQVlvay5G7iTStMc/ZpE7iLuhH2','USER','king11','thane','king mishra','9029874792'),(44,'queen11@gmail.com','$2a$10$MJ1U3MboKWDV25DO/Kcp4Od5eq18ividI0qJ1V7tyInzLntpmmjwW','ORGANIZATION','queen11','thane','queen pandey','9967261464'),(45,'testuser123@example.com','$2a$10$PexjVjFgwHIp9nbI7b4zzeQ6VLtf4kaYIPFAmISEI/OTCDvOnjbha','USER','testuser123','123 Test St','Test User','1234567890'),(46,'testorg123@bloodbank.org','$2a$10$aRwVsjDuArfLjfT0SOru9.xJ2kawynG8fBDViYtAS.XxxAI/lI6pm','ORGANIZATION','testorg123','456 Hospital Ave','Test Blood Bank','9876543210'),(47,'queen12@gmail.com','$2a$10$ZriQu1DidSg67qY4EHXKDOHId8xv1WTioR6lP3RM5LW2Px58KnxZS','ORGANIZATION','queen12','mira road','queen upadhyay','3333333333'),(48,'king12@gmail.com','$2a$10$FSwB6qaOx2NvlPba0DEiYeDGA.zP.MG1NKdRrJMjqmBJi8epvh65K','USER','king12','mumbai','king yadav','8888888888'),(49,'aastha@gmail.com','$2a$10$FZW1rdz/w81gbXM1cxAjO.zEwR4IFsfvvSaXreQrnrVz4oVrpdyu.','ORGANIZATION','aastha','mulund','aastha blood bank','9999999999'),(50,'priyanshu@gmail.com','$2a$10$gBFbj0VN772GR.6.MbJTHeV2lsCKygf1q0rnq0VSCRT2subjrQHt.','USER','priyanshu2005','thane','priyanshu upadhyay','9870831231'),(51,'autoorg_1760037600233@example.com','$2a$10$ztHlEtAgEUiVSSJ5ovzF4emkVy4n.8T/x4Ak.MHv.5I7r8fAMAo4m','ORGANIZATION','autoorg_1760037600233',NULL,NULL,NULL),(52,'queen13@gmail.com','$2a$10$rJvWTsZb2WYtSCnElQAovubV.RzGC4fxoXPi96tbEV3ynXK7Y01h6','ORGANIZATION','queen13','thane','queen tripathi','2222222222'),(53,'king13@gmail.com','$2a$10$Sdqd3GUvm6z09JL9LgZQGOob3nyE1Fjq3oW3fiZx0K3mrtOBYVOiO','USER','king13','thane','king shinde','6565656565'),(54,'priyanshu012@gmail.com','$2a$10$8.fAfbgzYHofUfAj4pNVmeYlfJguh1abeYJ8Cprn7gpWUJDdjK69S','USER','priyanshu012','thane','priyanshu upadhyay','9029874792'),(55,'annanay022@gmail.com','$2a$10$m4Zdq0UJbXsmSRJgwWKAUuHhHvtT5a0vhu.Wnm8nZmkfYMZEm4eN.','ORGANIZATION','annanay022','mira road','annanay bank of blood','9863241578'),(56,'priyanshu011@gmail.com','$2a$10$5rExtlhw3RFuQ3To9VnOZ.CPkxixjbF7oOpOuIMNWIcqiExfSV8h.','USER','priyanshu011','thane','priyanshu upadhyay','9029874792'),(57,'simrun027@gmail.com','$2a$10$22Lnd9rWXj16r8aTNDGTQ.4B6eIhnORBUQPWcDckOQDOXVm0PwNf2','ORGANIZATION','simrun027','mira road','simrun blood bank','7568245619'),(58,'annanay033@gmail.com','$2a$10$OaLHjVJMx7pZGDk.cHjO3eMtvoX29g1x5iVnGNF3VtyAtLLMQCetu','USER','annanay033','mira road','annanay vyas','9854736124'),(59,'annanay034@gmail.com','$2a$10$lkJvNJ3i.Ii0782vYr/yzOReNat04bDLien1MeLprgcpbXU4SfYe.','USER','annanay034','mira road','annanay vyas','9854736124');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-10  3:49:18
