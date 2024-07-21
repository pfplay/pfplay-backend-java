package com.pfplaybackend.api.partyroom.domain.entity.data;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "PARTYROOM_SESSION",
        indexes = {
                @Index(name = "paytyroom_session_id_IDX", columnList = "session_id")
        }
)
@Getter
@Setter
public class PartyroomSessionData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sessionId;
    private String destination;
    private UUID uid;

    @Builder
    private PartyroomSessionData(Long id, String sessionId, String destination, UUID uid) {
        this.id = id;
        this.sessionId = sessionId;
        this.destination = destination;
        this.uid = uid;
    }

    public static PartyroomSessionData create(String sessionId, String destination, UUID uid) {
        return PartyroomSessionData.builder()
                .sessionId(sessionId)
                .destination(destination)
                .uid(uid)
                .build();
    }
}
