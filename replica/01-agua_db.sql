/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19  Distrib 10.11.19-MariaDB, for debian-linux-gnu (aarch64)
--
-- Host: localhost    Database: agua_db
-- ------------------------------------------------------
-- Server version	10.11.19-MariaDB-ubu2204

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `agua_db`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `agua_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;

USE `agua_db`;

--
-- Table structure for table `Tb_Clientes`
--

DROP TABLE IF EXISTS `Tb_Clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Tb_Clientes` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) NOT NULL,
  `dpi` varchar(13) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `direccion_principal` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `dpi_unico` (`dpi`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Tb_Clientes`
--

LOCK TABLES `Tb_Clientes` WRITE;
/*!40000 ALTER TABLE `Tb_Clientes` DISABLE KEYS */;
INSERT INTO `Tb_Clientes` VALUES
(1,'María Fernanda González López','2500000000001','55120001','3a Calle 4-12 Zona 1, El Centro'),
(2,'Carlos Roberto Pérez Hernández','2500000000002','55120002','5a Avenida 2-30 Zona 2, Casco Urbano'),
(3,'Ana Lucía Martínez Ramírez','2500000000003','55120003','Aldea San Isidro, Sector 2'),
(4,'José Manuel Ortiz Castillo','2500000000004','55120004','2a Calle 8-19 Zona 1, El Centro'),
(5,'Gabriela Alejandra Ruiz Morales','2500000000005','55120005','6a Avenida 1-05 Zona 3, Casco Urbano'),
(6,'Luis Fernando Chávez Aguilar','2500000000006','55120006','Aldea San Isidro, Sector 1'),
(7,'Silvia Patricia Mendoza Flores','2500000000007','55120007','4a Calle 10-22 Zona 1, El Centro'),
(8,'Édgar Alexander Reyes Gómez','2500000000008','55120008','1a Avenida 3-08 Zona 2, Casco Urbano'),
(9,'Karla Ivonne Solórzano Vásquez','2500000000009','55120009','Aldea San Isidro, Sector 3'),
(10,'Miguel Ángel Cabrera Rodríguez','2500000000010','55120010','7a Calle 5-14 Zona 1, El Centro'),
(11,'Diana Marisol Juárez Sandoval','2500000000011','55120011','2a Avenida 9-27 Zona 3, Casco Urbano'),
(12,'Pedro Antonio Lemus Barrientos','2500000000012','55120012','Aldea San Isidro, Sector 2');
/*!40000 ALTER TABLE `Tb_Clientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Tb_Contadores`
--

DROP TABLE IF EXISTS `Tb_Contadores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Tb_Contadores` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `codigo_fisico` varchar(30) NOT NULL,
  `direccion_servicio` varchar(255) NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  `cliente_id` int(11) unsigned NOT NULL,
  `tipo_servicio_id` int(11) unsigned NOT NULL,
  `sector_id` int(11) unsigned NOT NULL,
  `fecha_asignacion` date DEFAULT NULL,
  `fecha_desactivacion` date DEFAULT NULL,
  `lectura_inicial` int(11) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo_fisico` (`codigo_fisico`),
  KEY `Tb_Contadores_cliente_id_foreign` (`cliente_id`),
  KEY `Tb_Contadores_tipo_servicio_id_foreign` (`tipo_servicio_id`),
  KEY `Tb_Contadores_sector_id_foreign` (`sector_id`),
  CONSTRAINT `Tb_Contadores_cliente_id_foreign` FOREIGN KEY (`cliente_id`) REFERENCES `Tb_Clientes` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `Tb_Contadores_sector_id_foreign` FOREIGN KEY (`sector_id`) REFERENCES `Tb_Sectores` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `Tb_Contadores_tipo_servicio_id_foreign` FOREIGN KEY (`tipo_servicio_id`) REFERENCES `Tb_Tipos_Servicios` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Tb_Contadores`
--

LOCK TABLES `Tb_Contadores` WRITE;
/*!40000 ALTER TABLE `Tb_Contadores` DISABLE KEYS */;
INSERT INTO `Tb_Contadores` VALUES
(1,'CT-0001','3a Calle 4-12 Zona 1, El Centro',1,1,1,1,'2025-03-10',NULL,0),
(2,'CT-0002','Local 3, 5a Avenida 2-30 Zona 2, Casco Urbano',1,1,2,2,'2025-05-20',NULL,0),
(3,'CT-0003','5a Avenida 2-30 Zona 2, Casco Urbano',1,2,1,2,'2025-01-15',NULL,0),
(4,'CT-0004','5a Avenida 2-31 Zona 2, Casco Urbano',1,2,1,2,'2025-07-02',NULL,0),
(5,'CT-0005','Aldea San Isidro, Sector 2',1,3,1,3,'2025-02-11',NULL,0),
(6,'CT-0006','Aldea San Isidro, Sector 2, terreno anexo',1,3,2,3,'2025-09-01',NULL,0),
(7,'CT-0007','2a Calle 8-19 Zona 1, El Centro',1,4,1,1,'2025-04-18',NULL,0),
(8,'CT-0008','6a Avenida 1-05 Zona 3, Casco Urbano',1,5,2,2,'2025-06-09',NULL,0),
(9,'CT-0009','Aldea San Isidro, Sector 1',1,6,1,3,'2025-03-25',NULL,0),
(10,'CT-0010','4a Calle 10-22 Zona 1, El Centro',1,7,1,1,'2025-05-30',NULL,0),
(11,'CT-0011','1a Avenida 3-08 Zona 2, Casco Urbano',1,8,1,2,'2025-02-27',NULL,0),
(12,'CT-0012','Aldea San Isidro, Sector 3',1,9,2,3,'2025-08-14',NULL,0),
(13,'CT-0013','7a Calle 5-14 Zona 1, El Centro',1,10,1,1,'2025-01-22',NULL,0),
(14,'CT-0014','2a Avenida 9-27 Zona 3, Casco Urbano',1,11,1,2,'2025-04-05',NULL,0),
(15,'CT-0015','Aldea San Isidro, Sector 2',0,12,1,3,'2025-01-05','2026-07-15',0);
/*!40000 ALTER TABLE `Tb_Contadores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Tb_Lecturas`
--

DROP TABLE IF EXISTS `Tb_Lecturas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Tb_Lecturas` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `numero_recibo` varchar(20) NOT NULL,
  `lectura_anterior` int(11) NOT NULL DEFAULT 0,
  `lectura_actual` int(11) NOT NULL,
  `consumo_litros` int(11) NOT NULL,
  `fecha` datetime NOT NULL,
  `contador_id` int(11) unsigned NOT NULL,
  `tarifa_base_id` int(11) unsigned NOT NULL,
  `tarifa_exceso_id` int(11) unsigned DEFAULT NULL,
  `usuario_lector_id` int(11) unsigned NOT NULL,
  `monto_base` decimal(10,2) NOT NULL DEFAULT 0.00,
  `monto_exceso` decimal(10,2) NOT NULL DEFAULT 0.00,
  PRIMARY KEY (`id`),
  UNIQUE KEY `numero_recibo` (`numero_recibo`),
  KEY `Tb_Lecturas_contador_id_foreign` (`contador_id`),
  KEY `Tb_Lecturas_tarifa_base_id_foreign` (`tarifa_base_id`),
  KEY `Tb_Lecturas_tarifa_exceso_id_foreign` (`tarifa_exceso_id`),
  KEY `Tb_Lecturas_usuario_lector_id_foreign` (`usuario_lector_id`),
  CONSTRAINT `Tb_Lecturas_contador_id_foreign` FOREIGN KEY (`contador_id`) REFERENCES `Tb_Contadores` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `Tb_Lecturas_tarifa_base_id_foreign` FOREIGN KEY (`tarifa_base_id`) REFERENCES `Tb_Tarifas` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `Tb_Lecturas_tarifa_exceso_id_foreign` FOREIGN KEY (`tarifa_exceso_id`) REFERENCES `Tb_Tarifas` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `Tb_Lecturas_usuario_lector_id_foreign` FOREIGN KEY (`usuario_lector_id`) REFERENCES `Tb_Usuarios` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Tb_Lecturas`
--

LOCK TABLES `Tb_Lecturas` WRITE;
/*!40000 ALTER TABLE `Tb_Lecturas` DISABLE KEYS */;
INSERT INTO `Tb_Lecturas` VALUES
(1,'R-2026-0001',0,12500,12500,'2026-08-05 09:00:00',1,2,NULL,3,40.00,0.00),
(2,'R-2026-0002',12500,24300,11800,'2026-09-05 09:00:00',1,2,NULL,3,40.00,0.00),
(3,'R-2026-0003',0,14800,14800,'2026-08-06 09:00:00',3,2,NULL,3,40.00,0.00),
(4,'R-2026-0004',14800,28300,13500,'2026-09-06 09:00:00',3,2,NULL,3,40.00,0.00),
(5,'R-2026-0005',0,16200,16200,'2026-08-07 09:00:00',4,2,6,3,40.00,8.00),
(6,'R-2026-0006',16200,28200,12000,'2026-09-07 09:00:00',4,2,NULL,3,40.00,0.00),
(7,'R-2026-0007',0,9800,9800,'2026-08-08 09:00:00',5,2,NULL,3,40.00,0.00),
(8,'R-2026-0008',9800,20000,10200,'2026-09-08 09:00:00',5,2,NULL,3,40.00,0.00),
(9,'R-2026-0009',0,13100,13100,'2026-08-09 09:00:00',7,2,NULL,3,40.00,0.00),
(10,'R-2026-0010',13100,30100,17000,'2026-09-09 09:00:00',7,2,6,3,40.00,8.00),
(11,'R-2026-0011',0,15600,15600,'2026-08-10 09:00:00',9,2,6,3,40.00,4.00),
(12,'R-2026-0012',15600,25100,9500,'2026-09-04 09:00:00',9,2,NULL,3,40.00,0.00),
(13,'R-2026-0013',0,11200,11200,'2026-08-04 09:00:00',10,2,NULL,3,40.00,0.00),
(14,'R-2026-0014',11200,23800,12600,'2026-09-03 09:00:00',10,2,NULL,3,40.00,0.00),
(15,'R-2026-0015',0,14200,14200,'2026-08-03 09:00:00',11,2,NULL,3,40.00,0.00),
(16,'R-2026-0016',14200,28100,13900,'2026-09-02 09:00:00',11,2,NULL,3,40.00,0.00),
(17,'R-2026-0017',0,10500,10500,'2026-08-02 09:00:00',13,2,NULL,3,40.00,0.00),
(18,'R-2026-0018',10500,21500,11000,'2026-09-01 09:00:00',13,2,NULL,3,40.00,0.00),
(19,'R-2026-0019',0,12800,12800,'2026-08-01 09:00:00',14,2,NULL,3,40.00,0.00),
(20,'R-2026-0020',12800,27400,14600,'2026-09-08 09:00:00',14,2,NULL,3,40.00,0.00),
(21,'R-2026-0021',0,48000,48000,'2026-09-05 09:00:00',2,4,NULL,3,80.00,0.00),
(22,'R-2026-0022',0,55500,55500,'2026-09-06 09:00:00',6,4,NULL,3,80.00,0.00),
(23,'R-2026-0023',0,63000,63000,'2026-09-07 09:00:00',8,4,6,3,80.00,12.00),
(24,'R-2026-0024',0,41000,41000,'2026-09-08 09:00:00',12,4,NULL,3,80.00,0.00);
/*!40000 ALTER TABLE `Tb_Lecturas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Tb_Metodos_Pago`
--

DROP TABLE IF EXISTS `Tb_Metodos_Pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Tb_Metodos_Pago` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(30) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Tb_Metodos_Pago`
--

LOCK TABLES `Tb_Metodos_Pago` WRITE;
/*!40000 ALTER TABLE `Tb_Metodos_Pago` DISABLE KEYS */;
INSERT INTO `Tb_Metodos_Pago` VALUES
(3,'Deposito'),
(1,'Efectivo'),
(2,'Transferencia');
/*!40000 ALTER TABLE `Tb_Metodos_Pago` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Tb_Pagos`
--

DROP TABLE IF EXISTS `Tb_Pagos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Tb_Pagos` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `monto` decimal(10,2) NOT NULL,
  `fecha_pago` datetime NOT NULL,
  `lectura_id` int(11) unsigned NOT NULL,
  `metodo_id` int(11) unsigned NOT NULL,
  `usuario_registro_id` int(11) unsigned NOT NULL,
  `anulado` tinyint(1) NOT NULL DEFAULT 0,
  `lectura_id_activa` int(10) unsigned GENERATED ALWAYS AS (case when `anulado` = 0 then `lectura_id` else NULL end) STORED,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_lectura_id_activa` (`lectura_id_activa`),
  KEY `Tb_Pagos_metodo_id_foreign` (`metodo_id`),
  KEY `Tb_Pagos_usuario_registro_id_foreign` (`usuario_registro_id`),
  KEY `idx_lectura_id` (`lectura_id`),
  CONSTRAINT `Tb_Pagos_metodo_id_foreign` FOREIGN KEY (`metodo_id`) REFERENCES `Tb_Metodos_Pago` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `Tb_Pagos_usuario_registro_id_foreign` FOREIGN KEY (`usuario_registro_id`) REFERENCES `Tb_Usuarios` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_pagos_lectura_id` FOREIGN KEY (`lectura_id`) REFERENCES `Tb_Lecturas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Tb_Pagos`
--

LOCK TABLES `Tb_Pagos` WRITE;
/*!40000 ALTER TABLE `Tb_Pagos` DISABLE KEYS */;
INSERT INTO `Tb_Pagos` VALUES
(1,40.00,'2026-08-08 09:00:00',1,3,2,0,1),
(2,40.00,'2026-08-09 09:00:00',3,1,2,0,3),
(3,40.00,'2026-08-11 09:00:00',7,2,2,0,7),
(4,40.00,'2026-08-07 09:00:00',13,3,2,0,13),
(5,40.00,'2026-08-06 09:00:00',15,1,2,0,15),
(6,40.00,'2026-08-05 09:00:00',17,2,2,0,17),
(7,40.00,'2026-08-04 09:00:00',19,3,2,0,19),
(8,80.00,'2026-09-08 09:00:00',21,1,2,0,21),
(9,80.00,'2026-09-09 09:00:00',22,2,2,0,22),
(10,40.00,'2026-09-07 09:00:00',2,3,2,1,NULL),
(11,40.00,'2026-09-09 09:00:00',2,3,2,0,2);
/*!40000 ALTER TABLE `Tb_Pagos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Tb_Roles`
--

DROP TABLE IF EXISTS `Tb_Roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Tb_Roles` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Tb_Roles`
--

LOCK TABLES `Tb_Roles` WRITE;
/*!40000 ALTER TABLE `Tb_Roles` DISABLE KEYS */;
INSERT INTO `Tb_Roles` VALUES
(1,'administrador'),
(3,'lector'),
(2,'secretaria');
/*!40000 ALTER TABLE `Tb_Roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Tb_Sectores`
--

DROP TABLE IF EXISTS `Tb_Sectores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Tb_Sectores` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Tb_Sectores`
--

LOCK TABLES `Tb_Sectores` WRITE;
/*!40000 ALTER TABLE `Tb_Sectores` DISABLE KEYS */;
INSERT INTO `Tb_Sectores` VALUES
(3,'Aldea San Isidro'),
(2,'Casco Urbano'),
(1,'El Centro');
/*!40000 ALTER TABLE `Tb_Sectores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Tb_Tarifas`
--

DROP TABLE IF EXISTS `Tb_Tarifas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Tb_Tarifas` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `precio` decimal(10,2) NOT NULL,
  `vigente_desde` datetime NOT NULL,
  `vigente_hasta` datetime DEFAULT NULL,
  `anulada` tinyint(1) NOT NULL DEFAULT 0,
  `tipo_servicio_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `Tb_Tarifas_tipo_servicio_id_foreign` (`tipo_servicio_id`),
  CONSTRAINT `Tb_Tarifas_tipo_servicio_id_foreign` FOREIGN KEY (`tipo_servicio_id`) REFERENCES `Tb_Tipos_Servicios` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Tb_Tarifas`
--

LOCK TABLES `Tb_Tarifas` WRITE;
/*!40000 ALTER TABLE `Tb_Tarifas` DISABLE KEYS */;
INSERT INTO `Tb_Tarifas` VALUES
(1,35.00,'2025-06-01 00:00:00','2026-06-01 00:00:00',0,1),
(2,40.00,'2026-06-01 00:00:00',NULL,0,1),
(3,70.00,'2025-06-01 00:00:00','2026-06-01 00:00:00',0,2),
(4,80.00,'2026-06-01 00:00:00',NULL,0,2),
(5,3.50,'2025-06-01 00:00:00','2026-06-01 00:00:00',0,3),
(6,4.00,'2026-06-01 00:00:00',NULL,0,3),
(7,999.00,'2026-06-15 00:00:00',NULL,1,1);
/*!40000 ALTER TABLE `Tb_Tarifas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Tb_Tipos_Servicios`
--

DROP TABLE IF EXISTS `Tb_Tipos_Servicios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Tb_Tipos_Servicios` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `codigo` varchar(20) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `volumen_incluido_litros` int(11) DEFAULT NULL,
  `es_servicio` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Tb_Tipos_Servicios`
--

LOCK TABLES `Tb_Tipos_Servicios` WRITE;
/*!40000 ALTER TABLE `Tb_Tipos_Servicios` DISABLE KEYS */;
INSERT INTO `Tb_Tipos_Servicios` VALUES
(1,'CUARTO_PAJA','1/4 de paja',15000,1),
(2,'MEDIA_PAJA','1/2 paja',60000,1),
(3,'EXCESO','Exceso de consumo',1000,0);
/*!40000 ALTER TABLE `Tb_Tipos_Servicios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Tb_Usuarios`
--

DROP TABLE IF EXISTS `Tb_Usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Tb_Usuarios` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  `rol_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `Tb_Usuarios_rol_id_foreign` (`rol_id`),
  CONSTRAINT `Tb_Usuarios_rol_id_foreign` FOREIGN KEY (`rol_id`) REFERENCES `Tb_Roles` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Tb_Usuarios`
--

LOCK TABLES `Tb_Usuarios` WRITE;
/*!40000 ALTER TABLE `Tb_Usuarios` DISABLE KEYS */;
INSERT INTO `Tb_Usuarios` VALUES
(1,'Juan Luis Perez','admin@oficinadelagua.local','$2y$10$xTZUaRpGE3xDTrKVJYPRgubz3txqJ2U1pwPc95OKUlMuVakrJAJfS',1,1),
(2,'Ana Sofia Ramirez','secretaria@oficinadelagua.local','$2y$10$N9RAh/MaUUBsivP/Og51Xe6v66F.XCyar/C3N9rWR7V3rFSBigSD2',1,2),
(3,'Carlos Eduardo Lopez','lector@oficinadelagua.local','$2y$10$JQ4G/.oJrJ2ph5G0wGbsSu7z1TJq91Y46ShC4Ocug/LAAno5Z0CRC',1,3);
/*!40000 ALTER TABLE `Tb_Usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `migrations`
--

DROP TABLE IF EXISTS `migrations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `migrations` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `version` varchar(255) NOT NULL,
  `class` varchar(255) NOT NULL,
  `group` varchar(255) NOT NULL,
  `namespace` varchar(255) NOT NULL,
  `time` int(11) NOT NULL,
  `batch` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `migrations`
--

LOCK TABLES `migrations` WRITE;
/*!40000 ALTER TABLE `migrations` DISABLE KEYS */;
INSERT INTO `migrations` VALUES
(1,'2026-08-31-000001','App\\Database\\Migrations\\CreateTbRoles','default','App',1789240609,1),
(2,'2026-08-31-000002','App\\Database\\Migrations\\CreateTbUsuarios','default','App',1789240609,1),
(3,'2026-08-31-000003','App\\Database\\Migrations\\CreateTbClientes','default','App',1789240609,1),
(4,'2026-08-31-000004','App\\Database\\Migrations\\CreateTbTiposServicios','default','App',1789240609,1),
(5,'2026-08-31-000005','App\\Database\\Migrations\\CreateTbSectores','default','App',1789240609,1),
(6,'2026-08-31-000006','App\\Database\\Migrations\\CreateTbContadores','default','App',1789240609,1),
(7,'2026-08-31-000007','App\\Database\\Migrations\\CreateTbTarifas','default','App',1789240609,1),
(8,'2026-08-31-000008','App\\Database\\Migrations\\CreateTbLecturas','default','App',1789240609,1),
(9,'2026-08-31-000009','App\\Database\\Migrations\\CreateTbMetodosPago','default','App',1789240609,1),
(10,'2026-08-31-000010','App\\Database\\Migrations\\CreateTbPagos','default','App',1789240609,1),
(11,'2026-09-02-000011','App\\Database\\Migrations\\AddMontoALecturas','default','App',1789240609,1),
(12,'2026-09-03-000001','App\\Database\\Migrations\\AddAnuladaToTbTarifas','default','App',1789240609,1),
(13,'2026-09-03-000012','App\\Database\\Migrations\\AddDpiAClientes','default','App',1789240609,1),
(14,'2026-09-03-000013','App\\Database\\Migrations\\AddFechasAContadores','default','App',1789240609,1),
(15,'2026-09-04-233746','App\\Database\\Migrations\\CreateTbRecibos','default','App',1789240609,1),
(16,'2026-09-11-203659','App\\Database\\Migrations\\AddAnuladaToTbPagos','default','App',1789240609,1),
(17,'2026-09-11-205411','App\\Database\\Migrations\\DropUniqueLecturaIdFromTbPagos','default','App',1789240609,1),
(18,'2026-09-11-215958','App\\Database\\Migrations\\DropTbRecibos','default','App',1789240609,1),
(19,'2026-09-15-000001','App\\Database\\Migrations\\AddLecturaInicialATbContadores','default','App',1793346092,2);
/*!40000 ALTER TABLE `migrations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'agua_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-23  8:25:46
