package com.collibra.pcos.session;

public class Session {

    private static final String DEFAULT_CLIENT_NAME = "ANONYMOUS";

    private final String sessionId;
    private final long sessionOpenedTimeMs;
    private boolean goodByeFlag;
    private long lastAccessTimeMs;

    private String clientName = DEFAULT_CLIENT_NAME;

    public Session(String sessionId) {
        this.lastAccessTimeMs = System.currentTimeMillis();
        this.sessionOpenedTimeMs = System.currentTimeMillis();
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public long getIdleTimeInMs() {
        long current = System.currentTimeMillis();
        return current - lastAccessTimeMs;
    }

    public long getSessionTimeInMs() {
        long current = System.currentTimeMillis();
        return current - sessionOpenedTimeMs;
    }

    public void registerClientActivity() {
        lastAccessTimeMs = System.currentTimeMillis();
    }

    public void setGoodByeSaidFlag() {
        goodByeFlag = true;
    }

    public boolean wasGoodByeSaid() {
        return goodByeFlag;
    }

}
