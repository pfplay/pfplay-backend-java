package com.pfplaybackend.api.user.service.user;

import com.pfplaybackend.api.config.mapper.ObjectMapperConfig;
import com.pfplaybackend.api.config.external.WebClientConfig;
import com.pfplaybackend.api.user.model.entity.user.User;
import com.pfplaybackend.api.user.presentation.user.request.ProfileUpdateRequest;
import com.pfplaybackend.api.user.repository.user.UserRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final WebClientConfig web;
    private final ObjectMapperConfig om;

    public UserService(UserRepository userRepository,
                       WebClientConfig web,
                       ObjectMapperConfig om) {
        this.userRepository = userRepository;
        this.web = web;
        this.om = om;
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUser(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public void setProfile(User user, ProfileUpdateRequest request) {
        User getUser = userRepository.findByNickname(request.getNickname());
        if (getUser != null) {
            throw new DuplicateKeyException("이미 존재하는 닉네임입니다.");
        }
        user.setProfile(request);
    }
}
