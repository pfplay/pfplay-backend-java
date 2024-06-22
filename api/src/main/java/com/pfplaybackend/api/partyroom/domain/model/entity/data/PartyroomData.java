package com.pfplaybackend.api.partyroom.domain.model.entity.data;

import com.pfplaybackend.api.partyroom.domain.model.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.model.value.Notice;
import com.pfplaybackend.api.partyroom.domain.model.value.PartyroomId;
import com.pfplaybackend.api.user.domain.model.value.UserId;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@DynamicInsert
@DynamicUpdate
@Table(
        name = "PARTYROOM",
        indexes = {
                @Index(name = "paytyroom_host_id_IDX", columnList = "host_id")
        }
)
@Entity
public class PartyroomData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partyroom_id")
    private Long id;

    @Transient
    private PartyroomId partyroomId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "uid", column = @Column(name = "host_id")),
    })
    private UserId hostId;

    @Embedded
    private Notice notice;

    @PostPersist
    public void updatePartyId() {
        this.partyroomId = new PartyroomId(this.id);
    }

    public Partyroom toDomain() {
        return Partyroom.builder().build();
    }
}
