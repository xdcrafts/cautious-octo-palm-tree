package com.github.xdcrafts.spring.data.web.query.api.resolver;

import com.github.xdcrafts.spring.data.web.query.api.annotation.EnableDataWebQueryApi;
import org.atteo.evo.inflector.English;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Character.isAlphabetic;
import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;

/**
 * Default implementation of resolver that scans all packages specified by @EnableDataWebQueryApi annotation.
 *
 * @author Vadim Dubs
 */
@SuppressWarnings("unchecked")
public class DefaultSpringDataResolver implements SpringDataResolver {

    /**
     * Resolves the actual generic type arguments for a base class, as viewed from a subclass or implementation.
     *
     * @param <T> base type
     * @param offspring class or interface subclassing or extending the base type
     * @param base base class
     * @param actualArgs the actual type arguments passed to the offspring class
     * @return actual generic type arguments, must match the type parameters of the offspring class. If omitted, the
     * type parameters will be used instead.
     */
    private static <T> Type[] resolveActualTypeArgs(Class<? extends T> offspring, Class<T> base, Type... actualArgs) {
        if (offspring == null
            || base == null
            || (actualArgs.length != 0 && actualArgs.length != offspring.getTypeParameters().length)) {
            throw new IllegalArgumentException(
                "offspring and base should not be null, number ot actualArgs should match number of offspring types"
            );
        }
        if (actualArgs.length == 0) {
            actualArgs = offspring.getTypeParameters();
        }
        Map<String, Type> typeVariables = new HashMap<>();
        for (int i = 0; i < actualArgs.length; i++) {
            TypeVariable<?> typeVariable = offspring.getTypeParameters()[i];
            typeVariables.put(typeVariable.getName(), actualArgs[i]);
        }
        List<Type> ancestors = new LinkedList<>();
        if (offspring.getGenericSuperclass() != null) {
            ancestors.add(offspring.getGenericSuperclass());
        }
        Collections.addAll(ancestors, offspring.getGenericInterfaces());
        for (Type type : ancestors) {
            if (type instanceof Class<?>) {
                Class<?> ancestorClass = (Class<?>) type;
                if (base.isAssignableFrom(ancestorClass)) {
                    Type[] result = resolveActualTypeArgs((Class<? extends T>) ancestorClass, base);
                    if (result != null) {
                        return result;
                    }
                }
            }
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type rawType = parameterizedType.getRawType();
                if (rawType instanceof Class<?>) {
                    Class<?> rawTypeClass = (Class<?>) rawType;
                    if (base.isAssignableFrom(rawTypeClass)) {
                        List<Type> resolvedTypes = new LinkedList<>();
                        for (Type t : parameterizedType.getActualTypeArguments()) {
                            if (t instanceof TypeVariable<?>) {
                                Type resolvedType = typeVariables.get(((TypeVariable<?>) t).getName());
                                resolvedTypes.add(resolvedType != null ? resolvedType : t);
                            } else {
                                resolvedTypes.add(t);
                            }
                        }

                        Type[] result = resolveActualTypeArgs(
                            (Class<? extends T>) rawTypeClass,
                            base,
                            resolvedTypes.toArray(new Type[] {})
                        );
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
        }
        return offspring.equals(base) ? actualArgs : new Type[]{};
    }

    /**
     * Transforms 'someCamelCasedName' to 'some-camel-cased-name'.
     */
    private static String unCamelCase(String name) {
        final char[] charArray = name.toCharArray();
        final StringBuilder newName = new StringBuilder();
        boolean isPreviousCharInLowerCase = true;
        for (int i = 0; i < charArray.length; i++) {
            final char c = charArray[i];
            if (isAlphabetic(c)) {
                if (isUpperCase(c)) {
                    if (i != 0 && isPreviousCharInLowerCase) {
                        newName.append('-');
                    }
                    newName.append(toLowerCase(c));
                    isPreviousCharInLowerCase = false;
                } else {
                    newName.append(c);
                    isPreviousCharInLowerCase = true;
                }
            } else {
                if (c == '_') {
                    newName.append('-');
                } else {
                    newName.append(c);
                }
                isPreviousCharInLowerCase = false;
            }
        }
        return newName.toString();
    }

    @Autowired
    private ApplicationContext applicationContext;
    private Map<String, KeyEntityTypeRepo> mapping;

    @PostConstruct
    public void init() {
        mapping = new TreeMap<>();
        final Map<Class, JpaSpecificationExecutor> repos = this.applicationContext
            .getBeansOfType(JpaSpecificationExecutor.class)
            .values()
            .stream()
            .map(repo -> new AbstractMap.SimpleEntry<>(
                ((Class<?>) resolveActualTypeArgs(repo.getClass(), JpaSpecificationExecutor.class)[0]),
                repo
            ))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        final Set<Class> entityTypes = this.applicationContext
            .getBeansWithAnnotation(EnableDataWebQueryApi.class)
            .values()
            .stream()
            .map(Object::getClass)
            .distinct()
            .flatMap(t -> {
                final String[] packages = t.getAnnotation(EnableDataWebQueryApi.class).scanBasePackages();
                final List<String> packageList = new ArrayList<>();
                packageList.add(t.getPackage().getName());
                packageList.addAll(Arrays.asList(packages));
                return packageList.stream();
            })
            .filter(Objects::nonNull)
            .map(Reflections::new)
            .flatMap(r -> r.getTypesAnnotatedWith(Entity.class).stream())
            .collect(Collectors.toSet());
        for (Class entity : entityTypes) {
            final String name = unCamelCase(entity.getSimpleName());
            final String pluralName = English.plural(name);
            if (this.mapping.containsKey(pluralName)) {
                throw new IllegalArgumentException(pluralName + " is already registered!");
            }
            if (repos.containsKey(entity)) {
                this.mapping.put(pluralName, new KeyEntityTypeRepo(pluralName, entity, repos.get(entity)));
            }
        }
    }

    @Override
    public boolean supports(String key) {
        return this.mapping.containsKey(key);
    }

    @Override
    public Collection<KeyEntityTypeRepo> knownTypes() {
        return this.mapping.values();
    }

    @Override
    public KeyEntityTypeRepo resolve(String key) {
        if (!supports(key)) {
            throw new IllegalArgumentException("Unknown entity type: '" + key + "'");
        }
        return this.mapping.get(key);
    }
}
