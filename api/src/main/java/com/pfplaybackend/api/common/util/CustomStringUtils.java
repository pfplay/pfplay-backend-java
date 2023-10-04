package com.pfplaybackend.api.common.util;

import java.util.Base64;
import java.util.UUID;

public class CustomStringUtils {
    public static String getRandomUuidWithoutHyphen() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String base64Encoder(String str) {
        return new String(Base64.getEncoder().encode(str.getBytes()));
    }
}
