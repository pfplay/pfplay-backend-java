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

INSERT INTO pfplay.avatar (`type`,name,image,`point`,is_uniform) VALUES
	 ('BASIC','도깨비불','https://postfiles.pstatic.net/MjAyMzA3MjlfMTE3/MDAxNjkwNjE0MTc3MjUz.owpzAVyLyeWQNKejvnYsd7g4Qv9SPvwwzl6voUCAeZ0g.Re2hKthxs8iV4NJr2Ofd-4_DfiXe46GzvPfhrjftX3Eg.PNG.sylviuss/avatar_empty.png?type=w773',0,1),
	 ('BASIC','유령','https://postfiles.pstatic.net/MjAyMzA3MjlfMTE3/MDAxNjkwNjE0MTc3MjUz.owpzAVyLyeWQNKejvnYsd7g4Qv9SPvwwzl6voUCAeZ0g.Re2hKthxs8iV4NJr2Ofd-4_DfiXe46GzvPfhrjftX3Eg.PNG.sylviuss/avatar_empty.png?type=w773',0,1),
	 ('BASIC','기본1','https://postfiles.pstatic.net/MjAyMzA3MjlfMTE3/MDAxNjkwNjE0MTc3MjUz.owpzAVyLyeWQNKejvnYsd7g4Qv9SPvwwzl6voUCAeZ0g.Re2hKthxs8iV4NJr2Ofd-4_DfiXe46GzvPfhrjftX3Eg.PNG.sylviuss/avatar_empty.png?type=w773',0,0),
	 ('DJ','디제잉 아바타1','https://postfiles.pstatic.net/MjAyMzA3MjlfMTE3/MDAxNjkwNjE0MTc3MjUz.owpzAVyLyeWQNKejvnYsd7g4Qv9SPvwwzl6voUCAeZ0g.Re2hKthxs8iV4NJr2Ofd-4_DfiXe46GzvPfhrjftX3Eg.PNG.sylviuss/avatar_empty.png?type=w773',1,0),
	 ('DJ','디제잉 아바타2','https://postfiles.pstatic.net/MjAyMzA3MjlfMTE3/MDAxNjkwNjE0MTc3MjUz.owpzAVyLyeWQNKejvnYsd7g4Qv9SPvwwzl6voUCAeZ0g.Re2hKthxs8iV4NJr2Ofd-4_DfiXe46GzvPfhrjftX3Eg.PNG.sylviuss/avatar_empty.png?type=w773',5,0),
	 ('DJ','디제잉 아바타3','https://postfiles.pstatic.net/MjAyMzA3MjlfMTE3/MDAxNjkwNjE0MTc3MjUz.owpzAVyLyeWQNKejvnYsd7g4Qv9SPvwwzl6voUCAeZ0g.Re2hKthxs8iV4NJr2Ofd-4_DfiXe46GzvPfhrjftX3Eg.PNG.sylviuss/avatar_empty.png?type=w773',10,0),
	 ('DJ','디제잉 아바타4','https://postfiles.pstatic.net/MjAyMzA3MjlfMTE3/MDAxNjkwNjE0MTc3MjUz.owpzAVyLyeWQNKejvnYsd7g4Qv9SPvwwzl6voUCAeZ0g.Re2hKthxs8iV4NJr2Ofd-4_DfiXe46GzvPfhrjftX3Eg.PNG.sylviuss/avatar_empty.png?type=w773',100,0),
	 ('DJ','디제잉 아바타5','https://postfiles.pstatic.net/MjAyMzA3MjlfMTE3/MDAxNjkwNjE0MTc3MjUz.owpzAVyLyeWQNKejvnYsd7g4Qv9SPvwwzl6voUCAeZ0g.Re2hKthxs8iV4NJr2Ofd-4_DfiXe46GzvPfhrjftX3Eg.PNG.sylviuss/avatar_empty.png?type=w773',300,0),
	 ('DJ','디제잉 아바타6','https://postfiles.pstatic.net/MjAyMzA3MjlfMTE3/MDAxNjkwNjE0MTc3MjUz.owpzAVyLyeWQNKejvnYsd7g4Qv9SPvwwzl6voUCAeZ0g.Re2hKthxs8iV4NJr2Ofd-4_DfiXe46GzvPfhrjftX3Eg.PNG.sylviuss/avatar_empty.png?type=w773',600,0),
	 ('DJ','디제잉 아바타7','https://postfiles.pstatic.net/MjAyMzA3MjlfMTE3/MDAxNjkwNjE0MTc3MjUz.owpzAVyLyeWQNKejvnYsd7g4Qv9SPvwwzl6voUCAeZ0g.Re2hKthxs8iV4NJr2Ofd-4_DfiXe46GzvPfhrjftX3Eg.PNG.sylviuss/avatar_empty.png?type=w773',1000,0);
INSERT INTO pfplay.avatar (`type`,name,image,`point`,is_uniform) VALUES
	 ('DJ','디제잉 아바타8','https://postfiles.pstatic.net/MjAyMzA3MjlfMTE3/MDAxNjkwNjE0MTc3MjUz.owpzAVyLyeWQNKejvnYsd7g4Qv9SPvwwzl6voUCAeZ0g.Re2hKthxs8iV4NJr2Ofd-4_DfiXe46GzvPfhrjftX3Eg.PNG.sylviuss/avatar_empty.png?type=w773',3000,0),
	 ('DJ','디제잉 아바타9','https://postfiles.pstatic.net/MjAyMzA3MjlfMTE3/MDAxNjkwNjE0MTc3MjUz.owpzAVyLyeWQNKejvnYsd7g4Qv9SPvwwzl6voUCAeZ0g.Re2hKthxs8iV4NJr2Ofd-4_DfiXe46GzvPfhrjftX3Eg.PNG.sylviuss/avatar_empty.png?type=w773',5000,0),
	 ('DJ','디제잉 아바타10','https://postfiles.pstatic.net/MjAyMzA3MjlfMTE3/MDAxNjkwNjE0MTc3MjUz.owpzAVyLyeWQNKejvnYsd7g4Qv9SPvwwzl6voUCAeZ0g.Re2hKthxs8iV4NJr2Ofd-4_DfiXe46GzvPfhrjftX3Eg.PNG.sylviuss/avatar_empty.png?type=w773',10000,0),
	 ('DJ','디제잉 아바타11','https://postfiles.pstatic.net/MjAyMzA3MjlfMTE3/MDAxNjkwNjE0MTc3MjUz.owpzAVyLyeWQNKejvnYsd7g4Qv9SPvwwzl6voUCAeZ0g.Re2hKthxs8iV4NJr2Ofd-4_DfiXe46GzvPfhrjftX3Eg.PNG.sylviuss/avatar_empty.png?type=w773',20000,0),
	 ('DJ','디제잉 아바타12','https://postfiles.pstatic.net/MjAyMzA3MjlfMTE3/MDAxNjkwNjE0MTc3MjUz.owpzAVyLyeWQNKejvnYsd7g4Qv9SPvwwzl6voUCAeZ0g.Re2hKthxs8iV4NJr2Ofd-4_DfiXe46GzvPfhrjftX3Eg.PNG.sylviuss/avatar_empty.png?type=w773',30000,0),
	 ('REF','레퍼럴 아바타1','https://postfiles.pstatic.net/MjAyMzA3MjlfMTE3/MDAxNjkwNjE0MTc3MjUz.owpzAVyLyeWQNKejvnYsd7g4Qv9SPvwwzl6voUCAeZ0g.Re2hKthxs8iV4NJr2Ofd-4_DfiXe46GzvPfhrjftX3Eg.PNG.sylviuss/avatar_empty.png?type=w773',1,0),
	 ('REF','레퍼럴 아바타2','https://postfiles.pstatic.net/MjAyMzA3MjlfMTE3/MDAxNjkwNjE0MTc3MjUz.owpzAVyLyeWQNKejvnYsd7g4Qv9SPvwwzl6voUCAeZ0g.Re2hKthxs8iV4NJr2Ofd-4_DfiXe46GzvPfhrjftX3Eg.PNG.sylviuss/avatar_empty.png?type=w773',5,0),
	 ('REF','레퍼럴 아바타3','https://postfiles.pstatic.net/MjAyMzA3MjlfMTE3/MDAxNjkwNjE0MTc3MjUz.owpzAVyLyeWQNKejvnYsd7g4Qv9SPvwwzl6voUCAeZ0g.Re2hKthxs8iV4NJr2Ofd-4_DfiXe46GzvPfhrjftX3Eg.PNG.sylviuss/avatar_empty.png?type=w773',10,0),
	 ('REF','레퍼럴 아바타4','https://postfiles.pstatic.net/MjAyMzA3MjlfMTE3/MDAxNjkwNjE0MTc3MjUz.owpzAVyLyeWQNKejvnYsd7g4Qv9SPvwwzl6voUCAeZ0g.Re2hKthxs8iV4NJr2Ofd-4_DfiXe46GzvPfhrjftX3Eg.PNG.sylviuss/avatar_empty.png?type=w773',100,0),
	 ('ROOM','누적 DJ 아바타1','https://postfiles.pstatic.net/MjAyMzA3MjlfMTE3/MDAxNjkwNjE0MTc3MjUz.owpzAVyLyeWQNKejvnYsd7g4Qv9SPvwwzl6voUCAeZ0g.Re2hKthxs8iV4NJr2Ofd-4_DfiXe46GzvPfhrjftX3Eg.PNG.sylviuss/avatar_empty.png?type=w773',1,0);
INSERT INTO pfplay.avatar (`type`,name,image,`point`,is_uniform) VALUES
	 ('ROOM','누적 DJ 아바타2','https://postfiles.pstatic.net/MjAyMzA3MjlfMTE3/MDAxNjkwNjE0MTc3MjUz.owpzAVyLyeWQNKejvnYsd7g4Qv9SPvwwzl6voUCAeZ0g.Re2hKthxs8iV4NJr2Ofd-4_DfiXe46GzvPfhrjftX3Eg.PNG.sylviuss/avatar_empty.png?type=w773',5,0),
	 ('ROOM','누적 DJ 아바타3','https://postfiles.pstatic.net/MjAyMzA3MjlfMTE3/MDAxNjkwNjE0MTc3MjUz.owpzAVyLyeWQNKejvnYsd7g4Qv9SPvwwzl6voUCAeZ0g.Re2hKthxs8iV4NJr2Ofd-4_DfiXe46GzvPfhrjftX3Eg.PNG.sylviuss/avatar_empty.png?type=w773',10,0),
	 ('ROOM','누적 DJ 아바타4','https://postfiles.pstatic.net/MjAyMzA3MjlfMTE3/MDAxNjkwNjE0MTc3MjUz.owpzAVyLyeWQNKejvnYsd7g4Qv9SPvwwzl6voUCAeZ0g.Re2hKthxs8iV4NJr2Ofd-4_DfiXe46GzvPfhrjftX3Eg.PNG.sylviuss/avatar_empty.png?type=w773',100,0);
