package com.pfplaybackend.api.user.repository.user;

import com.pfplaybackend.api.user.model.entity.user.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}
