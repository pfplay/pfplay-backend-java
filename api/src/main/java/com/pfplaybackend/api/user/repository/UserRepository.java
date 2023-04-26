package com.pfplaybackend.api.user.repository;

import com.pfplaybackend.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<String, User> {

}
