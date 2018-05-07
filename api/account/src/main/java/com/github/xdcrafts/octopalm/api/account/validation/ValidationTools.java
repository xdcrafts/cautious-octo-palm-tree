package com.github.xdcrafts.octopalm.api.account.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;

/**
 * Helper functions.
 *
 * @author Vadim Dubs
 */
public class ValidationTools {

    public static Validator validateWithGroups(SmartValidator smartValidator, Class<?>... groups) {
        return new Validator() {
            @Override
            public boolean supports(Class<?> clazz) {
                return smartValidator.supports(clazz);
            }
            @Override
            public void validate(Object target, Errors errors) {
                smartValidator.validate(target, errors, groups);
            }
        };
    }
}
