package com.github.xdcrafts.spring.data.web.query.api.annotation;

import com.github.xdcrafts.spring.data.web.query.api.configuration.QueryApiConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Annotation to enable spring data web query api via annotation configuration.
 *
 * @author Vadim Dubs
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(QueryApiConfiguration.class)
public @interface EnableDataWebQueryApi {

    String[] scanBasePackages() default {};
}
