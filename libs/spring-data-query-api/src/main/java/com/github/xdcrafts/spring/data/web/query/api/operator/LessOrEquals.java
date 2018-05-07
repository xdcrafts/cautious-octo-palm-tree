package com.github.xdcrafts.spring.data.web.query.api.operator;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

/**
 * Less or equals operator.
 *
 * @author Vadim Dubs
 */
public class LessOrEquals implements Operator {

    @NotEmpty
    private List<String> path;
    @NotNull
    private Object lessOrEquals;

    public LessOrEquals() {
    }

    public LessOrEquals(List<String> path, Object lessOrEquals) {
        this.path = path;
        this.lessOrEquals = lessOrEquals;
    }

    public LessOrEquals(String path, Object lessOrEquals) {
        this(Collections.singletonList(path), lessOrEquals);
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public Object getLessOrEquals() {
        return lessOrEquals;
    }

    public void setLessOrEquals(Object lessOrEquals) {
        this.lessOrEquals = lessOrEquals;
    }

    @Override
    public String toString() {
        return "LessOrEquals{" +
                "path=" + path +
                ", lessOrEquals=" + lessOrEquals +
                '}';
    }
}
