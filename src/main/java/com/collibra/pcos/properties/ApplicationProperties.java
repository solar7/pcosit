package com.collibra.pcos.properties;

import java.util.Objects;

public enum ApplicationProperties {

    SERVER_PORT("server.port"),
    SERVER_TIMEOUT("server.session.timeout"),
    SERVER_SIMULTANEOUS_CONNECTIONS("server.simultaneous_connections"),

    MESSAGE_INTRODUCE_MYSELF("message.response.introduce_myself"),
    MESSAGE_HELLO("message.response.hello"),
    MESSAGE_GOOD_BYE("message.response.good_bye"),
    MESSAGE_COMMAND_NOT_FOUND("message.response.command_not_found"),

    MESSAGE_NODE_ADDED("message.response.node_added"),
    MESSAGE_NODE_REMOVED("message.response.node_removed"),

    MESSAGE_EDGE_ADDED("message.response.edge_added"),
    MESSAGE_EDGE_REMOVED("message.response.edge_removed"),

    MESSAGE_NODE_ALREADY_EXISTS("message.response.node_already_exists"),
    MESSAGE_NODE_NOT_FOUND("message.response.node_not_found");

    private final String key;
    private String value;

    ApplicationProperties(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        checkInit();
        return value;
    }

    public int getIntValue() {
        checkInit();
        return Integer.parseInt(value);
    }

    public void setValue(String value) {
        this.value = value;
    }

    private void checkInit() {
        Objects.requireNonNull(value, "ApplicationProperties was not load");
    }
}
