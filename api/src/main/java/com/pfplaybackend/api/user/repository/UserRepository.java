package com.pfplaybackend.api.user.repository;

import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.user.dto.UserSaveDto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);

}
