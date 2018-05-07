package com.github.xdcrafts.spring.data.web.query.api.operator;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

/**
 * Less operator.
 *
 * @author Vadim Dubs
 */
public class Less implements Operator {

    @NotEmpty
    private List<String> path;
    @NotNull
    private Object less;

    public Less() {
    }

    public Less(List<String> path, Object less) {
        this.path = path;
        this.less = less;
    }

    public Less(String path, Object less) {
        this(Collections.singletonList(path), less);
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public Object getLess() {
        return less;
    }

    public void setLess(Object less) {
        this.less = less;
    }

    @Override
    public String toString() {
        return "Less{" +
                "path=" + path +
                ", less=" + less +
                '}';
    }
}
