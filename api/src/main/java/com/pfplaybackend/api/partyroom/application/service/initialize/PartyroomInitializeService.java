package com.pfplaybackend.api.partyroom.application.service.initialize;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.config.jwt.dto.UserCredentials;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.application.service.PartyroomManagementService;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.presentation.payload.request.CreatePartyroomRequest;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartyroomInitializeService {

    private final PartyroomManagementService partyroomManagementService;

    // Service for 'Main Stage' Initialization
    public void addPartyroomByAdmin(UserId adminId) {
        CreatePartyroomRequest request = new CreatePartyroomRequest(
                "Main Stage",
                "Welcome to the main stage",
                "main",
                10);
        partyroomManagementService.createMainStage(request, adminId);
    }
}