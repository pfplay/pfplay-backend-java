package com.pfplaybackend.api.profile.domain.value;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class NicknameConverter implements AttributeConverter<Nickname, String> {

    @Override
    public String convertToDatabaseColumn(Nickname nickname) {
        return nickname == null ? null : nickname.value();
    }

    @Override
    public Nickname convertToEntityAttribute(String dbData) {
        return (dbData == null || dbData.isBlank()) ? null : new Nickname(dbData);
    }
}
