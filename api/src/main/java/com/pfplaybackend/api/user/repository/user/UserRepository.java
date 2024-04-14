package com.pfplaybackend.api.user.repository.user;

import com.pfplaybackend.api.user.model.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    User findByNickname(String nickname);

    // Optional<User> findByEmail(String email);
    Optional<User> findByRefreshToken(String refreshToken);
}
