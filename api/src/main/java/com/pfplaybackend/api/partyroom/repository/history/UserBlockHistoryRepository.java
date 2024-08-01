package com.pfplaybackend.api.partyroom.repository.history;

import com.pfplaybackend.api.partyroom.domain.entity.data.history.UserBlockHistoryData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBlockHistoryRepository extends JpaRepository<UserBlockHistoryData, Long> {
}
