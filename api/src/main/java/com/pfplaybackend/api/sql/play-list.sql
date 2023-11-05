-- pfplay.play_list definition

CREATE TABLE `play_list` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int unsigned NOT NULL,
  `order_number` int unsigned NOT NULL COMMENT '플레이리스트 순서',
  `name` varchar(100) NOT NULL COMMENT '플레이리스트 명',
  `type` varchar(50) NOT NULL COMMENT '플레이리스트 or 그랩리스트',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `play_list_user_id_IDX` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;