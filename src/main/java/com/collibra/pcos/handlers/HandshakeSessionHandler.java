package com.collibra.pcos.handlers;

import com.collibra.pcos.session.ExecResult;
import com.collibra.pcos.session.Session;
import com.collibra.pcos.utils.ApplicationContext;
import com.collibra.pcos.utils.annotations.Command;
import com.collibra.pcos.utils.annotations.Handler;
import com.collibra.pcos.utils.annotations.MessageFormatter;

import static com.collibra.pcos.properties.ApplicationProperties.MESSAGE_COMMAND_NOT_FOUND;
import static com.collibra.pcos.properties.ApplicationProperties.MESSAGE_GOOD_BYE;
import static com.collibra.pcos.properties.ApplicationProperties.MESSAGE_HELLO;
import static com.collibra.pcos.properties.ApplicationProperties.MESSAGE_INTRODUCE_MYSELF;

@Handler
public class HandshakeSessionHandler {

    private final MessageFormatter formatter = ApplicationContext.getBean("messageFormatter");

    @Command(hello = true)
    public ExecResult openConnection(Session session) {
        String out = formatter.format(MESSAGE_INTRODUCE_MYSELF.getValue(), session.getSessionId());
        return ExecResult.intermediate(out);
    }

    @Command(pattern = "HI, I'M ([a-zA-Z0-9_-]+)")
    public ExecResult handshake(Session session, String clientName) {
        session.setClientName(clientName);
        String out = formatter.format(MESSAGE_HELLO.getValue(), clientName);
        return ExecResult.intermediate(out);
    }

    @Command(error = true)
    public ExecResult unknownCommand(Session session) {
        String out = formatter.format(MESSAGE_COMMAND_NOT_FOUND.getValue());
        return ExecResult.intermediate(out);
    }

    @Command(pattern = "BYE MATE!", goodbye = true)
    public ExecResult closeSession(Session session) {
        String out = formatter.format(MESSAGE_GOOD_BYE.getValue(), session.getClientName(), session.getSessionTimeInMs());
        session.setGoodByeSaidFlag();
        return ExecResult.terminate(out);
    }

}
