package com.pfplaybackend.api.entity.audit;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class BaseTime {

    @CreatedDate
    @Column(updatable = false, columnDefinition = "datetime default current_timestamp")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(columnDefinition = "datetime default current_timestamp on update current_timestamp")
    private LocalDateTime updatedAt;

}
