package com.pfplaybackend.api.user.service;

import com.pfplaybackend.api.config.ObjectMapperConfig;
import com.pfplaybackend.api.config.TokenProvider;
import com.pfplaybackend.api.config.WebClientConfig;
import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.entity.UserPermission;
import com.pfplaybackend.api.enums.Authority;
import com.pfplaybackend.api.user.presentation.dto.UserPermissionDto;
import com.pfplaybackend.api.user.presentation.dto.UserSaveDto;
import com.pfplaybackend.api.user.presentation.request.ProfileUpdateRequest;
import com.pfplaybackend.api.user.presentation.response.UserInfoResponse;
import com.pfplaybackend.api.user.presentation.response.UserLoginSuccessResponse;
import com.pfplaybackend.api.user.repository.PermissionRepository;
import com.pfplaybackend.api.user.repository.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final WebClientConfig web;
    private final TokenProvider tokenProvider;
    private final PermissionRepository permissionRepository;
    private final ObjectMapperConfig om;

    public UserService(UserRepository userRepository,
                       WebClientConfig web,
                       TokenProvider tokenProvider,
                       PermissionRepository permissionRepository,
                       ObjectMapperConfig om) {
        this.userRepository = userRepository;
        this.web = web;
        this.tokenProvider = tokenProvider;
        this.permissionRepository = permissionRepository;
        this.om = om;
    }

    @Transactional
    public User save(String email) {
        // 회원가입
        UserSaveDto userDto = UserSaveDto.builder()
                .email(email)
                .authority(Authority.ROLE_USER)
                .build();

        return userRepository.save(userDto.toEntity());
    }

    public String registeredUserReturnJwt(User user, String email, Long userId) {
        return tokenProvider.createAccessToken(user.getAuthority(), email, userId);
    }

    @Transactional(readOnly = true)
    public User findByUser(String email) {
        return userRepository.findByEmail(email);
    }

    public <T> T request(String uri, Class<T> responseType) {
        return web.webClient()
                .get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    @Transactional
    public void updateProfile(User user, ProfileUpdateRequest request) {
        user.updateProfile(request);
    }

    @Transactional(readOnly = true)
    public UserPermission getUserPermission(Authority authority) {
        return permissionRepository.findAllByAuthority(authority);
    }

    public UserLoginSuccessResponse register(UserInfoResponse token) {

        String email = token.getEmail();
        if (Objects.isNull(email) || email.isEmpty()) {
            throw new NoSuchElementException();
        }

        Optional<User> findUser = Optional.ofNullable(userRepository.findByEmail(email));

        if (findUser.isEmpty()) {
            User user = save(email);
            UserPermissionDto userPermissionDto = om.mapper().convertValue(getUserPermission(Authority.ROLE_USER), UserPermissionDto.class);
            return new UserLoginSuccessResponse(
                    user.getId(),
                    null,
                    true,
                    user.getAuthority(),
                    registeredUserReturnJwt(user, user.getEmail(), user.getId()),
                    userPermissionDto
            );
        }

        UserPermissionDto userPermissionDto = om.mapper().convertValue(getUserPermission(findUser.get().getAuthority()), UserPermissionDto.class);
        return new UserLoginSuccessResponse(
                findUser.get().getId(),
                findUser.get().getNickname(),
                false,
                findUser.get().getAuthority(),
                registeredUserReturnJwt(findUser.get(), email, findUser.get().getId()),
                userPermissionDto
        );

    }
}
