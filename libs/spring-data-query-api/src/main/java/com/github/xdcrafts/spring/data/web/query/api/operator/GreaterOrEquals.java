package com.github.xdcrafts.spring.data.web.query.api.operator;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

/**
 * Greater or equals operator.
 *
 * @author Vadim Dubs
 */
public class GreaterOrEquals implements Operator {

    @NotEmpty
    private List<String> path;
    @NotNull
    private Object greaterOrEquals;

    public GreaterOrEquals() {
    }

    public GreaterOrEquals(List<String> path, Object greaterOrEquals) {
        this.path = path;
        this.greaterOrEquals = greaterOrEquals;
    }

    public GreaterOrEquals(String path, Object greaterOrEquals) {
        this(Collections.singletonList(path), greaterOrEquals);
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public Object getGreaterOrEquals() {
        return greaterOrEquals;
    }

    public void setGreaterOrEquals(Object greaterOrEquals) {
        this.greaterOrEquals = greaterOrEquals;
    }

    @Override
    public String toString() {
        return "GreaterOrEquals{" +
                "path=" + path +
                ", greaterOrEquals=" + greaterOrEquals +
                '}';
    }
}
