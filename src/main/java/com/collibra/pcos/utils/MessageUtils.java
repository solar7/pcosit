package com.collibra.pcos.utils;

import com.collibra.pcos.utils.annotations.MessageFormatter;

import java.text.MessageFormat;

public class MessageUtils {

    public static MessageFormatter getMessageFormatter() {
        return MessageFormat::format;
    }
}
