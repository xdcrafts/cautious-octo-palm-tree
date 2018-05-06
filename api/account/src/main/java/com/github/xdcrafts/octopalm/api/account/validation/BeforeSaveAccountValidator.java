package com.github.xdcrafts.octopalm.api.account.validation;

import com.github.xdcrafts.octopalm.api.account.Account;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class BeforeSaveAccountValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Account.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // todo
    }
}
