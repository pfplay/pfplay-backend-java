package com.pfplaybackend.api.partyroom.repository.history;

import com.pfplaybackend.api.partyroom.domain.entity.data.history.UserPenaltyHistoryData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPenaltyHistoryRepository extends JpaRepository<UserPenaltyHistoryData, Long> {
}
