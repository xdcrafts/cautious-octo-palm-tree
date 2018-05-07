package com.github.xdcrafts.spring.data.web.query.api.operator;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

/**
 * In operator.
 *
 * @author Vadim Dubs
 */
public class In implements Operator {

    @NotEmpty
    private List<String> path;
    @NotNull
    private List<Object> in;

    public In() {
    }

    public In(List<String> path, List<Object> in) {
        this.path = path;
        this.in = in;
    }

    public In(String path, List<Object> in) {
        this(Collections.singletonList(path), in);
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public List<Object> getIn() {
        return in;
    }

    public void setIn(List<Object> in) {
        this.in = in;
    }

    @Override
    public String toString() {
        return "In{" +
                "path=" + path +
                ", in=" + in +
                '}';
    }
}
