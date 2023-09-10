package com.pfplaybackend.api.user.repository;

import com.pfplaybackend.api.entity.UserPermission;
import com.pfplaybackend.api.enums.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<UserPermission, Integer> {
    UserPermission findAllByAuthority(Authority authority);
}
