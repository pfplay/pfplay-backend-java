package com.pfplaybackend.api.user.application;

import com.pfplaybackend.api.user.repository.AvatarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AvatarService {
    private final AvatarRepository avatarRepository;
    private final ActivityService activityService;

    public void sample() {
    }

//    public AvatarBodyDto getAvatarBody(Integer bodyId) {
//        Avatar avatar = avatarRepository.findById(bodyId).orElseThrow();
//        return AvatarBodyDto.builder()
//                .id(avatar.getId())
//                .type(avatar.getType())
//                .name(avatar.getName())
//                .image(avatar.getImage())
//                .requiredPoint(avatar.getPoint())
//                .isUniform(avatar.getIsUniform())
//                .build();
//    }

//    public List<AvatarBodyDto> getAvatarBodies(Long userId) {
//        List<Avatar> result = avatarRepository.findAll();
//        List<AvatarBodyDto> dtoList = new ArrayList<>();
//
//        List<UserActivityGroupByDto> pointHistory = userActivityService.getPointHistory(userId);
//        for (Avatar avatar : result) {
//            boolean isAvailable = false;
//            long myPoint = 0L;
//            for (UserActivityGroupByDto pointHistoryDto : pointHistory) {
//                if (avatar.getType().name().equals(pointHistoryDto.getType().name())) {
//                    if (avatar.getPoint() <= pointHistoryDto.getPoint()) {
//                        isAvailable = true;
//                    }
//                    myPoint = pointHistoryDto.getPoint();
//                }
//            }
//
//            if (avatar.getType().equals(AvatarType.BASIC)) {
//                isAvailable = true;
//            }
//
//            AvatarBodyDto dto = AvatarBodyDto.builder()
//                    .id(avatar.getId())
//                    .type(avatar.getType())
//                    .name(avatar.getName())
//                    .image(avatar.getImage())
//                    .myPoint(myPoint)
//                    .requiredPoint(avatar.getPoint())
//                    .isAvailable(isAvailable)
//                    .isUniform(avatar.getIsUniform())
//                    .build();
//            dtoList.add(dto);
//        }
//
//        return dtoList;
//    }
}
