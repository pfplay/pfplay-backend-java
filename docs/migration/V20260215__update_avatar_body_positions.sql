-- 아바타 body 좌표 갱신 (AVATAR_BODY_RESOURCE 테이블)
-- AvatarResourceInitializeService에서 수정된 좌표값을 기존 DB 레코드에 반영

UPDATE AVATAR_BODY_RESOURCE SET combine_position_y = 41 WHERE name = 'ava_body_basic_001';
UPDATE AVATAR_BODY_RESOURCE SET combine_position_y = 39 WHERE name = 'ava_body_djing_003';
UPDATE AVATAR_BODY_RESOURCE SET combine_position_y = 45 WHERE name = 'ava_body_djing_004';
UPDATE AVATAR_BODY_RESOURCE SET combine_position_y = 40 WHERE name = 'ava_body_djing_005';
UPDATE AVATAR_BODY_RESOURCE SET combine_position_y = 39 WHERE name = 'ava_body_djing_006';
UPDATE AVATAR_BODY_RESOURCE SET combine_position_y = 40 WHERE name = 'ava_body_djing_007';
UPDATE AVATAR_BODY_RESOURCE SET combine_position_y = 40 WHERE name = 'ava_body_djing_008';
UPDATE AVATAR_BODY_RESOURCE SET combine_position_y = 42 WHERE name = 'ava_body_djing_009';
UPDATE AVATAR_BODY_RESOURCE SET combine_position_y = 43 WHERE name = 'ava_body_djing_010';
UPDATE AVATAR_BODY_RESOURCE SET combine_position_y = 44 WHERE name = 'ava_body_djing_011';
UPDATE AVATAR_BODY_RESOURCE SET combine_position_y = 38 WHERE name = 'ava_body_djing_012';

-- 기존 사용자 프로필의 좌표도 갱신
UPDATE USER_PROFILE up
JOIN AVATAR_BODY_RESOURCE abr ON up.avatar_body_uri = abr.resource_uri
SET up.combine_position_x = abr.combine_position_x,
    up.combine_position_y = abr.combine_position_y;

-- 프로필 introduction 컬럼 길이 변경 (TASK-09)
ALTER TABLE USER_PROFILE MODIFY introduction VARCHAR(50);
