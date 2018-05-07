package com.github.xdcrafts.spring.data.web.query.api.inspector;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Transforms a Java class to map with data describing the structure of the class.
 *
 * @author Vadim Dubs
 */
@SuppressWarnings("unchecked")
public class Inspector {

    private static Map<String, Field> getAllFields(Class type) {
        final Map<String, Field> fields = new LinkedHashMap<>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            for (Field field: c.getDeclaredFields()) {
                if (fields.containsKey(field.getName())) {
                    continue;
                }
                fields.put(field.getName(), field);
            }
        }
        return fields;
    }

    private static boolean annotatedWithOneOf(Field field, List<Class<? extends Annotation>> annotations) {
        return annotations.stream()
            .reduce(false, (acc, ann) -> acc || null != field.getAnnotation(ann), Boolean::logicalOr);
    }

    private static boolean annotatedWithOneOf(Class type, List<Class<? extends Annotation>> annotations) {
        return annotations.stream()
            .reduce(false, (acc, ann) -> acc || null != type.getAnnotation(ann), Boolean::logicalOr);
    }

    /**
     * Transforms Java class to map describing it's structure.
     */
    private static Map<String, Object> inspect(
        Class type,
        List<Class<? extends Annotation>> filterAnnotated,
        List<Class<? extends Annotation>> embedAnnotated,
        Predicate<Class> introspectMatched,
        int depth,
        List<Class> path
    ) {
        final Map<String, Object> schema = new LinkedHashMap<>();
        final Map<String, Field> fields = getAllFields(type);

        final List<Class> newPath = new ArrayList<>(path);
        newPath.add(type);

        for (Map.Entry<String, Field> entry : fields.entrySet()) {
            final String fieldName = entry.getKey();
            final Field field = entry.getValue();

            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            if (annotatedWithOneOf(field, filterAnnotated)) {
                continue;
            }

            final Class fieldType;
            final boolean isCollection;

            if (Collection.class.isAssignableFrom(field.getType())) {
                isCollection = true;
                fieldType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            } else if (field.getType().isArray()) {
                isCollection = true;
                fieldType = field.getType().getComponentType();
            } else {
                isCollection = false;
                fieldType = field.getType();
            }

            if ((depth < 0 || path.size() < depth) && !path.contains(fieldType) && introspectMatched.test(fieldType)) {
                final boolean isEmbeddable = annotatedWithOneOf(fieldType, embedAnnotated);
                final Map<String, Object> value =
                    inspect(fieldType, filterAnnotated, embedAnnotated, introspectMatched, depth, newPath);
                if (isEmbeddable) {
                    schema.putAll(value);
                } else {
                    schema.put(fieldName, isCollection ? Collections.singletonList(value) : value);
                }
            } else {
                final Object value;
                if (Map.class.isAssignableFrom(fieldType)) {
                    value = "{}";
                } else if (fieldType.isEnum()) {
                    value = EnumSet.allOf(fieldType);
                } else {
                    value = fieldType.getSimpleName();
                }
                schema.put(fieldName, isCollection ? Collections.singletonList(value) : value);
            }
        }
        return schema;
    }

    /**
     * Transforms Java class to map describing it's structure.
     */
    private static Map<String, Object> inspect(
        Class type,
        List<Class<? extends Annotation>> filterAnnotated,
        List<Class<? extends Annotation>> embedAnnotated,
        Predicate<Class> introspectMatched,
        int depth
    ) {
        return inspect(type, filterAnnotated, embedAnnotated, introspectMatched, depth, Collections.emptyList());
    }

    /**
     * Default filter for Java primitives.
     */
    public static boolean notCoreJavaTypes(Class type) {
        return !type.isPrimitive()
            && !type.isEnum()
            && !type.getPackage().getName().startsWith("java.");
    }

    /**
     * Transforms Java class to map describing it's structure.
     */
    public static Map<String, Object> inspect(Class type, int depth) {
        return inspect(
            type,
            Arrays.asList(Transient.class, JsonIgnore.class),
            Collections.singletonList(Embeddable.class),
            Inspector::notCoreJavaTypes,
            depth
        );
    }
}
