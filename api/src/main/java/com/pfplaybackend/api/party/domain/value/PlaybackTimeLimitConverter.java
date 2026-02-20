package com.pfplaybackend.api.party.domain.value;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PlaybackTimeLimitConverter implements AttributeConverter<PlaybackTimeLimit, Integer> {

    @Override
    public Integer convertToDatabaseColumn(PlaybackTimeLimit limit) {
        return limit == null ? 0 : limit.getMinutes();
    }

    @Override
    public PlaybackTimeLimit convertToEntityAttribute(Integer dbData) {
        return dbData == null ? PlaybackTimeLimit.unlimited() : PlaybackTimeLimit.ofMinutes(dbData);
    }
}
