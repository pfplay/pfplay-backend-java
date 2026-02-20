package com.pfplaybackend.api.party.domain.value;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class LinkDomainConverter implements AttributeConverter<LinkDomain, String> {

    @Override
    public String convertToDatabaseColumn(LinkDomain linkDomain) {
        return linkDomain == null ? null : linkDomain.getValue();
    }

    @Override
    public LinkDomain convertToEntityAttribute(String dbData) {
        return dbData == null ? null : LinkDomain.of(dbData);
    }
}
