-- pfplay.music_list definition

CREATE TABLE `music_list` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `play_list_id` int unsigned NOT NULL,
  `order_number` int unsigned NOT NULL COMMENT '순서',
  `name` varchar(250) NOT NULL COMMENT '곡명',
  `duration` varchar(50) DEFAULT NULL COMMENT '총 재생 시간',
  `url` varchar(500) DEFAULT NULL COMMENT 'url',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_music_list_play_list_id` (`play_list_id`),
  CONSTRAINT `fk_music_list_play_list_id` FOREIGN KEY (`play_list_id`) REFERENCES `play_list` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;