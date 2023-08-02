package com.pfplaybackend.api.avatar.repository;

import com.pfplaybackend.api.entity.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {

    <R> List<R> findBy(Class<R> R);
}
