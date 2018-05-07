package com.github.xdcrafts.spring.data.web.query.api.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.AsPropertyTypeDeserializer;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.xdcrafts.spring.data.web.query.api.operator.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Jackson deserializer for operator interface.
 *
 * @author Vadim Dubs
 */
public class OperatorDeserializer extends AsPropertyTypeDeserializer {

    private static final Map<Class<? extends Operator>, Predicate<JsonNode>> PREDICATES = new HashMap<>();

    private static final Set<String> OPERATOR_NAMES = new HashSet<>();

    private static void registerOperatorPredicate(Class<? extends Operator> operatorClass, String name) {
        PREDICATES.put(operatorClass, n -> n.get(name) != null);
        OPERATOR_NAMES.add(name);
    }

    static {
        registerOperatorPredicate(Not.class, "not");
        registerOperatorPredicate(And.class, "and");
        registerOperatorPredicate(Or.class, "or");
        registerOperatorPredicate(Equals.class, "equals");
        registerOperatorPredicate(In.class, "in");
        registerOperatorPredicate(Like.class, "like");
        registerOperatorPredicate(Greater.class, "greater");
        registerOperatorPredicate(GreaterOrEquals.class, "greaterOrEquals");
        registerOperatorPredicate(Less.class, "less");
        registerOperatorPredicate(LessOrEquals.class, "lessOrEquals");
    }

    private static final TypeFactory TYPE_FACTORY = TypeFactory.defaultInstance();

    public OperatorDeserializer(
        JavaType bt,
        TypeIdResolver idRes,
        String typePropertyName,
        boolean typeIdVisible,
        Class<?> defaultImpl
    ) {
        super(bt, idRes, typePropertyName, typeIdVisible, TYPE_FACTORY.constructType(defaultImpl));
    }

    public OperatorDeserializer(
        AsPropertyTypeDeserializer src,
        BeanProperty property
    ) {
        super(src, property);
    }

    @Override
    public TypeDeserializer forProperty(BeanProperty prop) {
        return (prop == _property) ? this : new OperatorDeserializer(this, prop);
    }

    @Override
    public Object deserializeTypedFromObject(JsonParser jp, DeserializationContext ctxt) throws IOException {
        final JsonNode node = jp.readValueAsTree();
        final Class<?> subType = findSubType(node);

        if (subType == null) {
            throw new IllegalArgumentException("Can not recognize operator. Expected one of: " + OPERATOR_NAMES);
        }

        JavaType type = TYPE_FACTORY.constructType(subType);
        final JsonParser jsonParser = new TreeTraversingParser(node, jp.getCodec());
        if (jsonParser.getCurrentToken() == null) {
            jsonParser.nextToken();
        }
        if (_baseType != null && _baseType.getClass() == type.getClass()) {
            type = TYPE_FACTORY.moreSpecificType(_baseType, type);
        }
        final JsonDeserializer<Object> deserializer = ctxt.findContextualValueDeserializer(type, _property);
        final Object deserialized = deserializer.deserialize(jsonParser, ctxt);
        final Set<ConstraintViolation<Object>> violations =
            Validation.buildDefaultValidatorFactory().getValidator().validate(deserialized);
        if (violations != null && !violations.isEmpty()) {
            final String message = violations
                .stream()
                .map(v -> v.getPropertyPath() + "=" + v.getInvalidValue() + " - " + v.getMessage())
                .collect(Collectors.joining(", "));
            throw new ConstraintViolationException("{" + message + "}", violations);
        }
        return deserialized;
    }

    private Class<? extends Operator> findSubType(JsonNode node) {
        for (Map.Entry<Class<? extends Operator>, Predicate<JsonNode>> entry : PREDICATES.entrySet()) {
            if (entry.getValue().test(node)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
