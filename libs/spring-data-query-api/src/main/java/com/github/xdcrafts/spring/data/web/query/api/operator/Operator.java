package com.github.xdcrafts.spring.data.web.query.api.operator;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import com.github.xdcrafts.spring.data.web.query.api.jackson.PropertyTypeResolver;

/**
 * Operator interface.
 *
 * @author Vadim Dubs
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
@JsonTypeResolver(PropertyTypeResolver.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Not.class),
    @JsonSubTypes.Type(value = Equals.class),
    @JsonSubTypes.Type(value = In.class),
    @JsonSubTypes.Type(value = Like.class),
    @JsonSubTypes.Type(value = Greater.class),
    @JsonSubTypes.Type(value = GreaterOrEquals.class),
    @JsonSubTypes.Type(value = Less.class),
    @JsonSubTypes.Type(value = LessOrEquals.class),
    @JsonSubTypes.Type(value = And.class),
    @JsonSubTypes.Type(value = Or.class)
})
public interface Operator {
}
