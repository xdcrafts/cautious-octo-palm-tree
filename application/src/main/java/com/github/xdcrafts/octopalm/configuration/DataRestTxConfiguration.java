package com.github.xdcrafts.octopalm.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath:/data-rest-tx.xml")
public class DataRestTxConfiguration {
}
