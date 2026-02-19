package com.pfplaybackend.api.party.application.service.initialize;

import com.pfplaybackend.api.party.application.service.PartyroomManagementService;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.management.CreatePartyroomRequest;
import com.pfplaybackend.api.common.domain.value.UserId;
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