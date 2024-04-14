package com.pfplaybackend.api.user.repository.profile;


import com.pfplaybackend.api.user.model.domain.profile.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvatarRepository extends JpaRepository<Avatar, Integer> {

}
