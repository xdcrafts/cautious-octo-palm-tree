package com.github.xdcrafts.octopalm.api.account.validation;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.validation.SmartValidator;

/**
 * Configuration for account validation.
 *
 * @author Vadim Dubs
 */
@Configuration
public class AccountValidationConfiguration implements InitializingBean {

    @Autowired
    private SmartValidator validator;

    @Autowired
    private ValidatingRepositoryEventListener validatingRepositoryEventListener;

    @Override
    public void afterPropertiesSet() {
        this.validatingRepositoryEventListener.addValidator("beforeCreate", this.validator);
        this.validatingRepositoryEventListener.addValidator("beforeSave", this.validator);
    }
}
