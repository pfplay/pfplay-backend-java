package com.pfplaybackend.api.user.repository;

import com.pfplaybackend.api.entity.UserPermission;
import com.pfplaybackend.api.common.enums.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPermissionRepository extends JpaRepository<UserPermission, Integer> {
    UserPermission findAllByAuthority(Authority authority);
}
