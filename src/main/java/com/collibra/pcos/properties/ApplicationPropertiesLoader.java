package com.collibra.pcos.properties;

import com.collibra.pcos.utils.LoggerUtils;
import org.slf4j.Logger;

import java.io.InputStream;
import java.util.Properties;

public class ApplicationPropertiesLoader {

    private static final Logger LOGGER = LoggerUtils.getLogger();
    private static final String DEFAULT_PROPS_FILENAME = "application.properties";

    private ApplicationPropertiesLoader() {
    }

    public static void loadProperties() {
        loadProperties(DEFAULT_PROPS_FILENAME);
    }

    public static void loadProperties(String fileName) {
        Properties propertiesContainer = loadPropertiesFromFile(fileName);
        for (ApplicationProperties appProp : ApplicationProperties.values()) {
            String propKey = appProp.getKey();
            String propValue = propertiesContainer.getProperty(propKey);
            if (propValue != null) {
                appProp.setValue(propValue);
                LOGGER.info("Load property {}={}", propKey, propValue);
            } else {
                LOGGER.error("{} is not set", propKey);
            }
        }
    }

    private static Properties loadPropertiesFromFile(String fileName) {
        final Properties propertiesContainer = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(fileName)) {
            propertiesContainer.load(inputStream);
        } catch (Exception e) {
            LOGGER.error("can't load properties from {}", fileName);
        }
        return propertiesContainer;
    }

}
