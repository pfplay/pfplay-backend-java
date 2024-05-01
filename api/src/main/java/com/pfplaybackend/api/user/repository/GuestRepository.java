package com.pfplaybackend.api.user.repository;

import com.pfplaybackend.api.user.model.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GuestRepository extends JpaRepository<Guest, UUID> {
}
