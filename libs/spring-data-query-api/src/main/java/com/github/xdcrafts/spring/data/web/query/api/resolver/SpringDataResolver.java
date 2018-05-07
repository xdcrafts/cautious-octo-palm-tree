package com.github.xdcrafts.spring.data.web.query.api.resolver;

import java.util.Collection;

/**
 * Resolves spring data entities and repositories by string key.
 */
public interface SpringDataResolver {

    /**
     * Check if resolver knows about a key.
     */
    boolean supports(String key);

    /**
     * Returns a list of supported types.
     */
    Collection<KeyEntityTypeRepo> knownTypes();

    /**
     * Resolves entity type and related repo by key, throws exception if key is unknown.
     */
    KeyEntityTypeRepo resolve(String key);
}
