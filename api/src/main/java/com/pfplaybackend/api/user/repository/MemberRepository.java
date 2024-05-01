package com.pfplaybackend.api.user.repository;

import com.pfplaybackend.api.user.model.entity.Member;
import com.pfplaybackend.api.user.repository.custom.MemberRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID>, MemberRepositoryCustom {
    Optional<Member> findByEmail(String email);
}
