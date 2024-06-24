package com.pfplaybackend.api.partyroom.domain.entity.domainmodel;

import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.enums.PartyroomType;
import com.pfplaybackend.api.partyroom.domain.value.DJ;
import com.pfplaybackend.api.partyroom.domain.value.LinkAddress;
import com.pfplaybackend.api.partyroom.domain.value.Notice;
import com.pfplaybackend.api.partyroom.domain.value.PartymemberId;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@SuperBuilder(toBuilder = true)
public class Partyroom {
    private Long Id;
    private String title;
    private String description;
    private PartymemberId hostId;
    private PartyroomType partyroomType;
    private Notice notice;
    private boolean isActive;
    private List<DJ> djQueue;
    private Map<PartymemberId, Partymember> partymemberMap;
    private LinkAddress linkAddress;
    private int entranceCount;

    @Builder
    public Partyroom(Long id, String title, String description, PartymemberId hostId,
                     boolean isActive, List<DJ> djQueue, Map<PartymemberId, Partymember> partymemberMap,
                     LinkAddress linkAddress, int entranceCount, Notice notice, PartyroomType partyroomType) {
        this.Id = id;
        this.title = title;
        this.description = description;
        this.hostId = hostId;
        this.isActive = isActive;
        this.djQueue = djQueue;
        this.partymemberMap = partymemberMap;
        this.linkAddress = linkAddress;
        this.entranceCount = entranceCount;
        this.notice = notice;
        this.partyroomType = partyroomType;
    }

    public Partyroom() {}

    private static List<DJ> initializeDJQueue() {
        return new ArrayList<DJ>();
    }

    private static Map<PartymemberId, Partymember> initializePartymemberMap(Partymember partymember) {
        return new HashMap<>() {{
            put(partymember.getPartymemberId(), partymember);
        }};
    }

    static public Partyroom create(String title, String description, String suffixUri, Partymember partymember, PartyroomType partyroomType) {
        return Partyroom.builder()
                .title(title)
                .description(description)
                .hostId(partymember.getPartymemberId())
                .isActive(false)
                .linkAddress(new LinkAddress(suffixUri))
                .partymemberMap(initializePartymemberMap(partymember))
                .djQueue(initializeDJQueue())
                .entranceCount(1)
                .notice(new Notice())
                .partyroomType(partyroomType)
                .build();
    }

    public Partyroom enter(Partymember partymember) throws Exception {
        if(this.entranceCount < 200) {
            throw new Exception();
        }else{
            this.entranceCount++;
        }
        return this;
    }

    public Partyroom updateNotice(Notice notice) {
        return this.toBuilder()
                .notice(notice)
                .build();
    }

    public Partyroom lockQueue() {
        return new Partyroom();
    }

    public Partyroom addQueue() {
        return new Partyroom();
    }

    public Partyroom removeQueue() {
        return new Partyroom();
    }

    public PartyroomData toData() {
        return new PartyroomData();
    }
}