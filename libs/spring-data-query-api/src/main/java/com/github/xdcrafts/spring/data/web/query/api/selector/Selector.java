package com.github.xdcrafts.spring.data.web.query.api.selector;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Selector API class.
 *
 * @author Vadim Dubs
 */
@SuppressWarnings("unchecked")
public final class Selector {

    private Selector() {
    }

    /**
     * Forces initialization of Hibernate lazy proxies.
     */
    private static <T> T initializeAndUnproxy(T entity) {
        if (entity == null) {
            return null;
        }
        if (entity instanceof HibernateProxy) {
            Hibernate.initialize(entity);
            entity = (T) ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
        }
        return entity;
    }

    /**
     * Returns the first {@link Field} in the hierarchy for the specified name.
     * @throws NoSuchFieldException if there is no such field in current class or any of its ancestors.
     */
    private static Object getFieldValue(
        Object object, String name
    ) throws NoSuchFieldException, IllegalAccessException {
        final Object entity = initializeAndUnproxy(object);
        Class clazz = entity.getClass();
        Field field = null;
        while (clazz != null && field == null) {
            try {
                field = clazz.getDeclaredField(name);
            } catch (Exception e) {
                // Ignore this exception, take another superclass and try again
            }
            clazz = clazz.getSuperclass();
        }
        if (field == null) {
            throw new NoSuchFieldException();
        }
        final boolean isAccessible = field.isAccessible();
        field.setAccessible(true);
        final Object value = field.get(entity);
        field.setAccessible(isAccessible);
        return value;
    }

    /**
     * Converts data structure structure to tree.
     */
    public static Node asTree(List<List<String>> select) {
        if (select == null) {
            return null;
        }
        final Node root = new Node("root");
        for (List<String> path: select) {
            if (path.isEmpty()) {
                continue;
            }
            Node currentNode = root;
            int currentPosition = 0;
            while (currentPosition < path.size()) {
                final String name = path.get(currentPosition);
                if (currentNode.hasChild(name)) {
                    currentNode = currentNode.getChild(name);
                } else {
                    final Node child = new Node(name);
                    currentNode.addChild(child);
                    currentNode = child;
                }
                currentPosition++;
            }
        }
        return root;
    }

    /**
     * Returns map of objects, according to selector structure.
     */
    public static Object select(Node selector, Object value) {
        if (value == null) {
            return null;
        }
        if (selector.isEmpty()) {
            return value;
        }
        if (value instanceof Collection) {
            return ((Collection) value).stream().map(v -> select(selector, v)).collect(Collectors.toList());
        }
        final Map<String, Object> result = new LinkedHashMap<>();
        final boolean isMap = value instanceof Map;
        for (Node child: selector.getChildren()) {
            try {
                final String name = child.getName();
                final Object attribute = isMap ? ((Map) value).get(name) : getFieldValue(value, name);
                if (attribute != null) {
                    result.put(name, select(child, attribute));
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return result;
    }
}
