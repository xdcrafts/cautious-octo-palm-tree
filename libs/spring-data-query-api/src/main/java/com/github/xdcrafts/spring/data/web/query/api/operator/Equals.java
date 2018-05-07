package com.github.xdcrafts.spring.data.web.query.api.operator;

import javax.validation.constraints.NotEmpty;
import java.util.Collections;
import java.util.List;

/**
 * Equals operator.
 *
 * @author Vadim Dubs
 */
public class Equals implements Operator {

    @NotEmpty
    private List<String> path;
    private Object equals;

    public Equals() {
    }

    public Equals(List<String> path, Object equals) {
        this.path = path;
        this.equals = equals;
    }

    public Equals(String path, Object equals) {
        this(Collections.singletonList(path), equals);
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public Object getEquals() {
        return equals;
    }

    public void setEquals(Object equals) {
        this.equals = equals;
    }

    @Override
    public String toString() {
        return "Equals{" +
                "path=" + path +
                ", equals=" + equals +
                '}';
    }
}
