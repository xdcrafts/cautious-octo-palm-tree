package com.github.xdcrafts.octopalm.api.account.validation;

import com.github.xdcrafts.octopalm.api.account.Account;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class BeforeCreateAccountValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return Account.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        // todo
    }
}
