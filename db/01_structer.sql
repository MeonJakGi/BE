-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: beshow
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `alarm`
--

DROP TABLE IF EXISTS `alarm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alarm` (
  `alarm_id` bigint NOT NULL AUTO_INCREMENT,
  `stock_id` bigint NOT NULL,
  `alarm_type` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `message` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_read` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`alarm_id`),
  KEY `idx_alarm_stock_id` (`stock_id`),
  KEY `idx_alarm_read_created` (`is_read`,`created_at`),
  CONSTRAINT `fk_alarm_stock` FOREIGN KEY (`stock_id`) REFERENCES `stock` (`stock_id`) ON DELETE CASCADE,
  CONSTRAINT `chk_alarm_type` CHECK (`alarm_type` IN ('SHELF_EMPTY', 'NEED_CHECK'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `camera`
--

DROP TABLE IF EXISTS `camera`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `camera` (
  `camera_id` bigint NOT NULL AUTO_INCREMENT,
  `shelf_id` bigint NOT NULL,
  `camera_status` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`camera_id`),
  KEY `idx_camera_shelf_id` (`shelf_id`),
  CONSTRAINT `fk_camera_shelf` FOREIGN KEY (`shelf_id`) REFERENCES `shelf` (`shelf_id`) ON DELETE CASCADE,
  CONSTRAINT `chk_camera_status` CHECK ((`camera_status` in (_utf8mb4'ACTIVE',_utf8mb4'INACTIVE',_utf8mb4'ERROR')))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `detection_result`
--

DROP TABLE IF EXISTS `detection_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detection_result` (
  `detection_result_id` bigint NOT NULL AUTO_INCREMENT,
  `shelf_image_id` bigint NOT NULL,
  `slot_id` bigint DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  `x` int NOT NULL,
  `y` int NOT NULL,
  `width` int NOT NULL,
  `height` int NOT NULL,
  `confidence` decimal(5,2) NOT NULL,
  `depth_position` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_misplaced` tinyint(1) NOT NULL DEFAULT '0',
  `is_low_confidence` tinyint(1) NOT NULL DEFAULT '0',
  `detected_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`detection_result_id`),
  KEY `idx_detection_result_shelf_image` (`shelf_image_id`),
  KEY `idx_detection_result_slot_product` (`slot_id`,`product_id`),
  KEY `idx_detection_result_product` (`product_id`),
  CONSTRAINT `fk_detection_result_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE SET NULL,
  CONSTRAINT `fk_detection_result_shelf_image` FOREIGN KEY (`shelf_image_id`) REFERENCES `shelf_image` (`shelf_image_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_detection_result_slot` FOREIGN KEY (`slot_id`) REFERENCES `slot` (`slot_id`) ON DELETE SET NULL,
  CONSTRAINT `chk_detection_depth_position` CHECK (((`depth_position` is null) or (`depth_position` in (_utf8mb4'FRONT',_utf8mb4'BACK',_utf8mb4'UNKNOWN'))))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `inventory`
--

DROP TABLE IF EXISTS `inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory` (
  `inventory_id` bigint NOT NULL AUTO_INCREMENT,
  `store_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `total_quantity` int NOT NULL DEFAULT '0',
  `reorder_point` int NOT NULL DEFAULT '0',
  `recommended_order_quantity` int NOT NULL DEFAULT '0',
  `lead_time_days` int DEFAULT NULL,
  `is_order_completed` tinyint(1) NOT NULL DEFAULT '0',
  `order_completed_at` datetime DEFAULT NULL,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`inventory_id`),
  UNIQUE KEY `uk_inventory_store_product` (`store_id`,`product_id`),
  KEY `idx_inventory_store_id` (`store_id`),
  KEY `idx_inventory_product_id` (`product_id`),
  KEY `idx_inventory_order_needed` (`store_id`,`total_quantity`,`reorder_point`),
  CONSTRAINT `fk_inventory_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_inventory_store` FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `planogram`
--

DROP TABLE IF EXISTS `planogram`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `planogram` (
  `planogram_id` bigint NOT NULL AUTO_INCREMENT,
  `slot_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `expected_quantity` int NOT NULL DEFAULT '0',
  `min_front_quantity` int NOT NULL DEFAULT '0',
  `min_display_quantity` int NOT NULL DEFAULT '0',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`planogram_id`),
  UNIQUE KEY `uk_planogram_slot_product` (`slot_id`,`product_id`),
  KEY `idx_planogram_slot_id` (`slot_id`),
  KEY `idx_planogram_product_id` (`product_id`),
  CONSTRAINT `fk_planogram_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_planogram_slot` FOREIGN KEY (`slot_id`) REFERENCES `slot` (`slot_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `product_id` bigint NOT NULL AUTO_INCREMENT,
  `class_id` int NOT NULL,
  `sku_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `product_name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `barcode` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `product_image_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `unit` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`product_id`),
  UNIQUE KEY `uk_product_class_id` (`class_id`),
  UNIQUE KEY `uk_product_sku_code` (`sku_code`),
  UNIQUE KEY `uk_product_barcode` (`barcode`),
  KEY `idx_product_category` (`category`)
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `shelf`
--

DROP TABLE IF EXISTS `shelf`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shelf` (
  `shelf_id` bigint NOT NULL AUTO_INCREMENT,
  `store_id` bigint NOT NULL,
  `front_edge_points` json DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `shelf_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`shelf_id`),
  KEY `idx_shelf_store_id` (`store_id`),
  CONSTRAINT `fk_shelf_store` FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `shelf_image`
--

DROP TABLE IF EXISTS `shelf_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shelf_image` (
  `shelf_image_id` bigint NOT NULL AUTO_INCREMENT,
  `camera_id` bigint DEFAULT NULL,
  `image_s3_key` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `image_width` int DEFAULT NULL,
  `image_height` int DEFAULT NULL,
  `captured_at` datetime NOT NULL,
  `analysis_status` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
  `retry_count` int NOT NULL DEFAULT '0',
  `analysis_message` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_checked_at` datetime DEFAULT NULL,
  `analysis_requested_at` datetime DEFAULT NULL,
  `analysis_completed_at` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`shelf_image_id`),
  KEY `idx_shelf_image_shelf_captured` (`captured_at`),
  KEY `idx_shelf_image_camera_id` (`camera_id`),
  KEY `idx_shelf_image_analysis_status_retry` (`analysis_status`,`retry_count`),
  CONSTRAINT `fk_shelf_image_camera` FOREIGN KEY (`camera_id`) REFERENCES `camera` (`camera_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `slot`
--

DROP TABLE IF EXISTS `slot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `slot` (
  `slot_id` bigint NOT NULL AUTO_INCREMENT,
  `shelf_id` bigint NOT NULL,
  `slot_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `x` int DEFAULT NULL,
  `y` int DEFAULT NULL,
  `width` int DEFAULT NULL,
  `height` int DEFAULT NULL,
  `row_no` int DEFAULT NULL,
  `col_no` int DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`slot_id`),
  UNIQUE KEY `uk_slot_shelf_code` (`shelf_id`,`slot_code`),
  KEY `idx_slot_shelf_id` (`shelf_id`),
  CONSTRAINT `fk_slot_shelf` FOREIGN KEY (`shelf_id`) REFERENCES `shelf` (`shelf_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stock`
--

DROP TABLE IF EXISTS `stock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stock` (
  `stock_id` bigint NOT NULL AUTO_INCREMENT,
  `shelf_image_id` bigint NOT NULL,
  `slot_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `status` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `front_quantity` int NOT NULL DEFAULT '0',
  `back_quantity` int NOT NULL DEFAULT '0',
  `detected_quantity` int NOT NULL DEFAULT '0',
  `estimated_shelf_quantity` int DEFAULT NULL,
  `confidence` decimal(5,2) DEFAULT NULL,
  `status_reason` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `issue_x` int DEFAULT NULL,
  `issue_y` int DEFAULT NULL,
  `issue_width` int DEFAULT NULL,
  `issue_height` int DEFAULT NULL,
  `bbox_source` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_current` tinyint(1) NOT NULL DEFAULT '1',
  `changed_at` datetime NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_misplaced` bit(1) NOT NULL,
  PRIMARY KEY (`stock_id`),
  KEY `fk_stock_product` (`product_id`),
  KEY `idx_stock_current_status` (`is_current`,`status`),
  KEY `idx_stock_slot_product` (`slot_id`,`product_id`),
  KEY `idx_stock_shelf_image_slot_product` (`shelf_image_id`,`slot_id`,`product_id`),
  KEY `idx_stock_changed_at` (`changed_at`),
  CONSTRAINT `fk_stock_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_stock_shelf_image` FOREIGN KEY (`shelf_image_id`) REFERENCES `shelf_image` (`shelf_image_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_stock_slot` FOREIGN KEY (`slot_id`) REFERENCES `slot` (`slot_id`) ON DELETE CASCADE,
  CONSTRAINT `chk_stock_status` CHECK ((`status` in (_utf8mb4'ENOUGH',_utf8mb4'NEED_REFILL',_utf8mb4'ORDER_NEEDED',_utf8mb4'NEED_CHECK')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `store`
--

DROP TABLE IF EXISTS `store`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `store` (
  `store_id` bigint NOT NULL AUTO_INCREMENT,
  `store_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `manager_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`store_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-02  8:38:04
