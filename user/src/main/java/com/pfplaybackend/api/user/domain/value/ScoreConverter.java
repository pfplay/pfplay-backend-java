package com.pfplaybackend.api.user.domain.value;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ScoreConverter implements AttributeConverter<Score, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Score score) {
        return score == null ? 0 : score.getValue();
    }

    @Override
    public Score convertToEntityAttribute(Integer dbData) {
        return dbData == null ? Score.zero() : new Score(dbData);
    }
}
