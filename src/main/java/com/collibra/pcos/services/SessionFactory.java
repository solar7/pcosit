package com.collibra.pcos.services;

import com.collibra.pcos.session.Session;

public interface SessionFactory {

    Session createNewSession();

}
