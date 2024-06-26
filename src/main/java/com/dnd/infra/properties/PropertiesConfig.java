package com.dnd.infra.properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.dnd.infra.storage.StorageProperties;

@EnableConfigurationProperties({
    StorageProperties.class,
})
@Configuration
public class PropertiesConfig {}
