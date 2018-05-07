package com.github.xdcrafts.spring.data.web.query.api.jackson;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;

import java.util.Collection;

/**
 * Property type resolver for Operator sub-types.
 *
 * @author Vadim Dubs
 */
public class PropertyTypeResolver extends StdTypeResolverBuilder {
    @Override
    public TypeDeserializer buildTypeDeserializer(
        DeserializationConfig config,
        JavaType baseType,
        Collection<NamedType> subtypes
    ) {
        return new OperatorDeserializer(baseType, null, _typeProperty, _typeIdVisible, _defaultImpl);
    }
}
