package com.collibra.pcos.services.impl;

import com.collibra.pcos.services.SessionIdentifierGenerator;

import java.util.UUID;

/**
 *  {@link java.util.UUID} generator is not efficient and could cause performance issues in some cases
 */
public class UuidBasedSessionIdGenerator implements SessionIdentifierGenerator {

    @Override
    public String newSessionIdentifier() {
        return UUID.randomUUID().toString();
    }

}
