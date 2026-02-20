package com.pfplaybackend.api.common.util;

import io.hypersistence.tsid.TSID;

public final class TsidGenerator {

    private static final TSID.Factory FACTORY = TSID.Factory.builder()
            .withNodeBits(10)
            .build();

    private TsidGenerator() {}

    public static long nextId() {
        return FACTORY.generate().toLong();
    }
}
