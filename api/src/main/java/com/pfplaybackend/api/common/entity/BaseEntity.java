package com.pfplaybackend.api.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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
}
