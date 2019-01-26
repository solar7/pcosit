package com.collibra.pcos.services.impl;

import com.collibra.pcos.services.SessionFactory;
import com.collibra.pcos.services.SessionIdentifierGenerator;
import com.collibra.pcos.session.Session;

import static com.collibra.pcos.utils.ApplicationContext.getBean;

public class SessionFactoryImpl implements SessionFactory {

    private final SessionIdentifierGenerator idGenerator = getBean("idGenerator");

    @Override
    public Session createNewSession() {
        String nextSessionId = idGenerator.newSessionIdentifier();
        Session newSession = new Session(nextSessionId);
        return newSession;
    }

}
