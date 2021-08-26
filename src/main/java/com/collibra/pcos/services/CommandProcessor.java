package com.collibra.pcos.services;

import com.collibra.pcos.session.ExecResult;
import com.collibra.pcos.session.Session;

/**
 * Execute command in the session's context
 */
public interface CommandProcessor {

    /**
     * Execute any supported user command
     * @return INTERMEDIATE or TERMINATE execution result
     */
    ExecResult process(String command, Session session);

    /**
     * Execute greeting command
     * @return INTERMEDIATE or TERMINATE execution result
     */
    ExecResult execSayHello(Session session);

    /**
     * Execute farewell command
     * @return INTERMEDIATE or TERMINATE execution result
     */
    ExecResult execSayGoodBye(Session session);

}
