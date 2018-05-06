package com.github.xdcrafts.octopalm.configuration;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Configuration
public class DataRestValidationConfiguration implements InitializingBean {

    private static final List<String> EVENTS = Arrays.asList(
        "beforeCreate", "afterCreate",
        "beforeSave", "afterSave",
        "beforeLinkSave", "afterLinkSave",
        "beforeDelete", "afterDelete"
    );

    @Autowired
    private ValidatingRepositoryEventListener validatingRepositoryEventListener;

    @Autowired
    private Map<String, Validator> validators;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.validators.forEach((name, validator) -> {
            for (String event: EVENTS) {
                if (name.startsWith(event)) {
                    this.validatingRepositoryEventListener.addValidator(event, validator);
                    break;
                }
            }
        });
    }
}
