package com.pfplaybackend.api.guest.repository;

import com.pfplaybackend.api.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}
