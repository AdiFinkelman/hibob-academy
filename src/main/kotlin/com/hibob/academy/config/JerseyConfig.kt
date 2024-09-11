package com.hibob.academy.config

import org.glassfish.jersey.server.ResourceConfig
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration

@Configuration
class JerseyInitializer : ResourceConfig() {

    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        logger.info("initializing jersey config")
        packages("com.hibob")
    }
}