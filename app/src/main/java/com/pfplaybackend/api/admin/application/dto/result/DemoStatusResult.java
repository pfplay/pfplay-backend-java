package com.pfplaybackend.api.admin.application.dto.result;

public record DemoStatusResult(Boolean initialized, Long virtualMemberCount, Long generalRoomCount) {
}
