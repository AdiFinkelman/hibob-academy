package com.hibob.academy.config

import com.hibob.academy.filters.AuthenticationFilter
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.spring.SpringComponentProvider
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration

@Configuration
class JerseyInitializer : ResourceConfig() {

    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        logger.info("initializing jersey config")
        register(SpringComponentProvider::class.java)
        register(AuthenticationFilter::class.java)
        packages("com.hibob.academy.resource")
    }
}