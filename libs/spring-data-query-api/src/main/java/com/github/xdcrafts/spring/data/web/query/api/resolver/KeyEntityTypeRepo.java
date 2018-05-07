package com.github.xdcrafts.spring.data.web.query.api.resolver;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public final class KeyEntityTypeRepo<T> {
    private final String key;
    private final Class<T> type;
    private final JpaSpecificationExecutor<T> repo;
    public KeyEntityTypeRepo(String key, Class<T> type, JpaSpecificationExecutor<T> repo) {
        this.key = key;
        this.type = type;
        this.repo = repo;
    }
    public String getKey() {
        return key;
    }
    public Class<T> getType() {
        return type;
    }
    public JpaSpecificationExecutor<T> getRepo() {
        return repo;
    }
    @Override
    public String toString() {
        return "KeyEntityTypeRepo{" +
                "key='" + key + '\'' +
                ", type=" + type +
                ", repo=" + repo +
                '}';
    }
}
