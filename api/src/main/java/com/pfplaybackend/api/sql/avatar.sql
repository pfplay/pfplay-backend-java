-- pfplay.avatar definition

CREATE TABLE `avatar` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '점수 타입',
  `name` varchar(50) NOT NULL,
  `image` varchar(500) NOT NULL COMMENT 'body template image url',
  `point` int unsigned NOT NULL DEFAULT '0' COMMENT '해금 충족 점수',
  `is_uniform` tinyint(1) DEFAULT '0' COMMENT '일체형 아바타 여부',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;