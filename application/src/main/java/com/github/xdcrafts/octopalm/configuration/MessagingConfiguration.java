package com.github.xdcrafts.octopalm.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.messaging.MessageChannel;

@Configuration
public class MessagingConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessagingConfiguration.class);

    @Bean
    public MessageChannel auditChannel() {
        return MessageChannels
            .publishSubscribe()
            .get();
    }

    @Bean
    public IntegrationFlow auditFlow() {
        return IntegrationFlows
            .from("auditChannel")
            .handle(m -> LOGGER.info("{}", m))
            .get();
    }
}
