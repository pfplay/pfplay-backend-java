package com.pfplaybackend.api.common.entity;

import com.pfplaybackend.api.common.domain.event.DomainEvent;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class BaseEntity {

    @CreatedDate
    @Column(updatable = false, columnDefinition = "datetime default current_timestamp")
    protected LocalDateTime createdAt;

    @LastModifiedDate
    @Column(columnDefinition = "datetime default current_timestamp on update current_timestamp")
    protected LocalDateTime updatedAt;

    @Transient
    private final transient List<DomainEvent> domainEvents = new ArrayList<>();

    protected void registerEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    public List<DomainEvent> pollDomainEvents() {
        List<DomainEvent> events = Collections.unmodifiableList(new ArrayList<>(domainEvents));
        domainEvents.clear();
        return events;
    }
}
