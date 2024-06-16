package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.partyroom.presentation.payload.request.RegisterNoticeRequest;

public interface PartyroomNoticeService {
    void registerNotice(RegisterNoticeRequest request);
}
