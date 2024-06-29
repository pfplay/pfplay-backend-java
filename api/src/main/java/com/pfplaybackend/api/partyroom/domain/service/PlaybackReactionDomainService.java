package com.pfplaybackend.api.partyroom.domain.service;

import com.pfplaybackend.api.partyroom.domain.entity.data.history.PlaybackReactionHistoryData;
import com.pfplaybackend.api.partyroom.domain.enums.ReactionType;
import org.springframework.stereotype.Service;

@Service
public class PlaybackReactionDomainService {
    
    // TODO 연산 최적화 필요
    public PlaybackReactionHistoryData determine(PlaybackReactionHistoryData playbackReactionHistoryData, ReactionType reactionType) throws Exception {
        boolean liked = playbackReactionHistoryData.isLiked();
        boolean disliked = playbackReactionHistoryData.isDisliked();
        boolean grabbed = playbackReactionHistoryData.isGrabbed();

        // 레코드는 존재하지만 사실상 무효한 선택 정보인 경우
        if(!liked && !disliked && !grabbed) {
            // X X X → 히스토리 id는 유지하되, 선택한 것만 체크토록 반영
        }

        if(reactionType.equals(ReactionType.LIKE)) {
            if(liked) {
                // O X X → X X X
                // O X O → X X O
                cancelLiked(); // 아무것도 선택하지 않게 된다.
                // 결과적으로 기존 상태에서 좋아요 취소하기(OFF)
            }
            if(disliked) {
                // X O X → O X X
                cancelDislike(); // 싫어요 취소
                pushLiked(); // 좋아요 버튼 누르기
                // 결과적으로 배타적으로 '좋아요'
                // TODO 댄스 시작
                // TODO 점수 +1
            }
            if(!liked && grabbed) {
                // X X O → O X O
                pushLiked();
                // 결과적으로 기존 상태에서 좋아요 누르기(ON)
                // TODO 댄스 시작
                // TODO 점수 +1
            }
        }
        if(reactionType.equals(ReactionType.DISLIKE)) {
            if(disliked) {
                // X O X → X X X
                cancelLiked();
                // 결과적으로 모두 OFF
            }
            if(liked) {
                // O X X → X O X
                // O X O → X O O
                cancelLiked(); // 좋아요 취소
                // TODO 댄스 중단
                // TODO 점수 -1
            }
        }
        if(reactionType.equals(ReactionType.GRAB)) {
            if(liked && !grabbed) {
                // O X X → O X O
                // 결과적으로 기존 상태에서 그랩만 누르기(ON)
            }
            if(disliked) {
                // X O X → X X O
                cancelDislike();
                // 결과적으로 배타적으로 '그랩'(ON)
            }
            if(grabbed) {
                // X X 'O' → X X 'X'
                // O X 'O' → O X 'X'
                // 결과적으로 기존 상태에서 그랩만 취소하기(OFF)
            }
        }

        // 리턴시킬 데이터 포맷은 다음과 같다.
        // 1. 갱신할 레코드 객체 PlaybackReactionHistoryData
        // 2. 후속 조치: NONE, Grab 취소, Grab 수행
        throw new Exception();
    }

    public void cancelLiked() {

    }

    public void cancelDislike() {

    }

    public void cancelGrabbed() {

    }

    public void pushLiked() {

    }

}
