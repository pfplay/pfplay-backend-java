package com.pfplaybackend.api.partyroom.application.service;


public interface PartyroomNoticeService {
    void registerNotice(Long partyroomId, String content);
}
