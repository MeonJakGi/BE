SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS alarm;
DROP TABLE IF EXISTS stock;
DROP TABLE IF EXISTS detection_result;
DROP TABLE IF EXISTS image_log;
DROP TABLE IF EXISTS shelf_image;
DROP TABLE IF EXISTS planogram;
DROP TABLE IF EXISTS inventory;
DROP TABLE IF EXISTS slot;
DROP TABLE IF EXISTS camera;
DROP TABLE IF EXISTS shelf;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS store;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE store (
  store_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  store_name VARCHAR(100) NOT NULL,
  address VARCHAR(255),
  phone VARCHAR(30),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE product (
  product_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_name VARCHAR(100) NOT NULL,
  category VARCHAR(50) NOT NULL,
  barcode VARCHAR(50),
  brand VARCHAR(100),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_product_name (product_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE shelf (
  shelf_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  store_id BIGINT NOT NULL,
  shelf_name VARCHAR(100) NOT NULL,
  shelf_location VARCHAR(100),
  shelf_status ENUM('ACTIVE','INACTIVE','MAINTENANCE') NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_shelf_store FOREIGN KEY (store_id) REFERENCES store(store_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE camera (
  camera_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  shelf_id BIGINT NOT NULL,
  camera_name VARCHAR(100) NOT NULL,
  camera_url VARCHAR(255),
  camera_status ENUM('ACTIVE','INACTIVE','ERROR') NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_camera_shelf (shelf_id),
  CONSTRAINT fk_camera_shelf FOREIGN KEY (shelf_id) REFERENCES shelf(shelf_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE slot (
  slot_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  shelf_id BIGINT NOT NULL,
  slot_name VARCHAR(100) NOT NULL,
  row_no INT NOT NULL,
  col_no INT NOT NULL,
  x_min FLOAT NOT NULL,
  y_min FLOAT NOT NULL,
  x_max FLOAT NOT NULL,
  y_max FLOAT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_slot_position (shelf_id, row_no, col_no),
  CONSTRAINT fk_slot_shelf FOREIGN KEY (shelf_id) REFERENCES shelf(shelf_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE planogram (
  planogram_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  slot_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  display_order INT,
  max_display_qty INT NOT NULL,
  min_display_qty INT NOT NULL,
  standard_width FLOAT,
  standard_height FLOAT,
  standard_depth FLOAT,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_planogram_slot FOREIGN KEY (slot_id) REFERENCES slot(slot_id),
  CONSTRAINT fk_planogram_product FOREIGN KEY (product_id) REFERENCES product(product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE inventory (
  inventory_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  store_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  current_qty INT NOT NULL,
  safety_qty INT NOT NULL,
  reorder_qty INT NOT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_inventory_store_product (store_id, product_id),
  CONSTRAINT fk_inventory_store FOREIGN KEY (store_id) REFERENCES store(store_id),
  CONSTRAINT fk_inventory_product FOREIGN KEY (product_id) REFERENCES product(product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE shelf_image (
  shelf_image_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  camera_id BIGINT NOT NULL,
  shelf_id BIGINT NOT NULL,
  image_url VARCHAR(255) NOT NULL,
  captured_at DATETIME NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_shelf_image_camera FOREIGN KEY (camera_id) REFERENCES camera(camera_id),
  CONSTRAINT fk_shelf_image_shelf FOREIGN KEY (shelf_id) REFERENCES shelf(shelf_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE image_log (
  image_log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  shelf_image_id BIGINT NOT NULL,
  model_name VARCHAR(100),
  model_version VARCHAR(50),
  process_status ENUM('PENDING','PROCESSING','SUCCESS','FAILED') NOT NULL DEFAULT 'PENDING',
  detected_count INT NOT NULL DEFAULT 0,
  error_message TEXT,
  started_at DATETIME,
  finished_at DATETIME,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_image_log_shelf_image FOREIGN KEY (shelf_image_id) REFERENCES shelf_image(shelf_image_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE detection_result (
  detection_result_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  image_log_id BIGINT NOT NULL,
  product_id BIGINT,
  slot_id BIGINT,
  confidence FLOAT NOT NULL,
  bbox_x_min FLOAT NOT NULL,
  bbox_y_min FLOAT NOT NULL,
  bbox_x_max FLOAT NOT NULL,
  bbox_y_max FLOAT NOT NULL,
  is_front_row BOOLEAN,
  detected_class_name VARCHAR(100),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_detection_result_image_log FOREIGN KEY (image_log_id) REFERENCES image_log(image_log_id),
  CONSTRAINT fk_detection_result_product FOREIGN KEY (product_id) REFERENCES product(product_id),
  CONSTRAINT fk_detection_result_slot FOREIGN KEY (slot_id) REFERENCES slot(slot_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE stock (
  stock_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  shelf_id BIGINT NOT NULL,
  slot_id BIGINT,
  product_id BIGINT NOT NULL,
  image_log_id BIGINT,
  detected_qty INT NOT NULL DEFAULT 0,
  estimated_display_qty INT,
  status ENUM('NORMAL','REPLENISH_REQUIRED','NEEDS_CHECK','ORDER_REQUIRED') NOT NULL,
  reason VARCHAR(255),
  checked_at DATETIME NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_stock_shelf FOREIGN KEY (shelf_id) REFERENCES shelf(shelf_id),
  CONSTRAINT fk_stock_slot FOREIGN KEY (slot_id) REFERENCES slot(slot_id),
  CONSTRAINT fk_stock_product FOREIGN KEY (product_id) REFERENCES product(product_id),
  CONSTRAINT fk_stock_image_log FOREIGN KEY (image_log_id) REFERENCES image_log(image_log_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE alarm (
  alarm_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  stock_id BIGINT NOT NULL,
  store_id BIGINT NOT NULL,
  shelf_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  alarm_type ENUM('REPLENISH_REQUIRED','NEEDS_CHECK','ORDER_REQUIRED') NOT NULL,
  alarm_status ENUM('UNREAD','READ','RESOLVED') NOT NULL DEFAULT 'UNREAD',
  message VARCHAR(255) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  read_at DATETIME,
  resolved_at DATETIME,
  CONSTRAINT fk_alarm_stock FOREIGN KEY (stock_id) REFERENCES stock(stock_id),
  CONSTRAINT fk_alarm_store FOREIGN KEY (store_id) REFERENCES store(store_id),
  CONSTRAINT fk_alarm_shelf FOREIGN KEY (shelf_id) REFERENCES shelf(shelf_id),
  CONSTRAINT fk_alarm_product FOREIGN KEY (product_id) REFERENCES product(product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
