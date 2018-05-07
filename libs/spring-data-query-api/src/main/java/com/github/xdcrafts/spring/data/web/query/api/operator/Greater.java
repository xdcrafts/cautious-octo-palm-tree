package com.github.xdcrafts.spring.data.web.query.api.operator;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

/**
 * Greater operator.
 *
 * @author Vadim Dubs
 */
public class Greater implements Operator {

    @NotEmpty
    private List<String> path;
    @NotNull
    private Object greater;

    public Greater() {
    }

    public Greater(List<String> path, Object greater) {
        this.path = path;
        this.greater = greater;
    }

    public Greater(String path, Object greater) {
        this(Collections.singletonList(path), greater);
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public Object getGreater() {
        return greater;
    }

    public void setGreater(Object greater) {
        this.greater = greater;
    }

    @Override
    public String toString() {
        return "Greater{" +
                "path=" + path +
                ", greater=" + greater +
                '}';
    }
}
