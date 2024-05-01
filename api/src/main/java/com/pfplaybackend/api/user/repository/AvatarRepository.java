package com.pfplaybackend.api.user.repository;


import com.pfplaybackend.api.user.model.entity.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvatarRepository extends JpaRepository<Avatar, Integer> {

}
