package com.pfplaybackend.api.party.domain.value;

import java.io.Serializable;
import java.util.Objects;

public class LinkDomain implements Serializable {

    private final String value;

    private LinkDomain(String value) {
        this.value = value;
    }

    public static LinkDomain of(String value) {
        return new LinkDomain(value == null ? "" : value);
    }

    public String getValue() {
        return value;
    }

    public boolean isEmpty() {
        return value == null || value.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkDomain that = (LinkDomain) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
