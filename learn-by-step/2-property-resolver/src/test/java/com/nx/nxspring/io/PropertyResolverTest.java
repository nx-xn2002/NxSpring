package com.nx.nxspring.io;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

class PropertyResolverTest {
    @Test
    public void loadTest() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("src/main/resources/app.properties"));
        PropertyResolver propertyResolver = new PropertyResolver(properties);
        String appName = propertyResolver.getProperty("app.name");
        float appVersion = propertyResolver.getProperty("app.version", float.class);
        System.out.println(appName);
        System.out.println(appVersion);
    }
}