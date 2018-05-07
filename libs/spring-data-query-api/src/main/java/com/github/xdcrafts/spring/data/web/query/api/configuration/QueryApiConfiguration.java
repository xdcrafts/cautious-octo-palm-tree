package com.github.xdcrafts.spring.data.web.query.api.configuration;

import com.github.xdcrafts.spring.data.web.query.api.QueryApiEndpoint;
import com.github.xdcrafts.spring.data.web.query.api.resolver.DefaultSpringDataResolver;
import com.github.xdcrafts.spring.data.web.query.api.resolver.SpringDataResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration that will initialize all required beans.
 *
 * @author Vadim Dubs
 */
@Configuration
public class QueryApiConfiguration {

    @Bean
    public SpringDataResolver springDataResolver() {
        return new DefaultSpringDataResolver();
    }

    @Bean
    public QueryApiEndpoint queryApiEndpoint() {
        return new QueryApiEndpoint();
    }
}
