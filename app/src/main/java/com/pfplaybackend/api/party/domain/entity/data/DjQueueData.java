package com.pfplaybackend.api.party.domain.entity.data;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.party.domain.exception.DjException;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@DynamicInsert
@DynamicUpdate
@Table(name = "DJ_QUEUE")
@Entity
public class DjQueueData extends BaseEntity {

    @Id
    @Column(name = "partyroom_id")
    private Long partyroomId;

    private boolean isClosed;

    public DjQueueData() {}

    private DjQueueData(Long partyroomId) {
        this.partyroomId = partyroomId;
        this.isClosed = false;
    }

    // ── Factory Method ──

    public static DjQueueData createFor(Long partyroomId) {
        return new DjQueueData(partyroomId);
    }

    // ── Business Methods ──

    public void open() {
        this.isClosed = false;
    }

    public void close() {
        this.isClosed = true;
    }

    public void validateOpen() {
        if (isClosed) {
            throw ExceptionCreator.create(DjException.QUEUE_CLOSED);
        }
    }
}
