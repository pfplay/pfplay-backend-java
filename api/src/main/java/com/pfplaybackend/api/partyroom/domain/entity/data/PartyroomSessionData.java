package com.pfplaybackend.api.partyroom.domain.entity.data;

import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.PartyroomSession;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Getter
@Table(
        name = "PARTYROOM_SESSION",
        indexes = {
                @Index(name = "paytyroom_session_id_IDX", columnList = "session_id")
        }
)

@Entity
public class PartyroomSessionData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="partyroom_session_id")
    private Long id;

    private String sessionId;
    private UserId userId;
    private PartyroomId partyroomId;

    @Builder
    private PartyroomSessionData(Long id, String sessionId, UserId userId, PartyroomId partyroomId) {
        this.id = id;
        this.sessionId = sessionId;
        this.userId = userId;
        this.partyroomId = partyroomId;
    }

    public static PartyroomSessionData create(String sessionId, UserId userId, PartyroomId partyroomId) {
        return PartyroomSessionData.builder()
                .sessionId(sessionId)
                .userId(userId)
                .partyroomId(partyroomId)
                .build();
    }

    public PartyroomSession toDomain() {
        return PartyroomSession.builder()
                .id(this.getId())
                .sessionId(this.getSessionId())
                .userId(this.getUserId())
                .partyroomId(this.getPartyroomId())
                .build();
    }
}
