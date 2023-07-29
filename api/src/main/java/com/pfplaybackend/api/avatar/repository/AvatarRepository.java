package com.pfplaybackend.api.avatar.repository;

import com.pfplaybackend.api.entity.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {
}
