package com.github.xdcrafts.spring.data.web.query.api.operator;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

/**
 * Like operator.
 *
 * @author Vadim Dubs
 */
public class Like implements Operator {

    @NotEmpty
    private List<String> path;
    @NotNull
    private String like;

    public Like() {
    }

    public Like(List<String> path, String like) {
        this.path = path;
        this.like = like;
    }

    public Like(String path, String like) {
        this(Collections.singletonList(path), like);
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    @Override
    public String toString() {
        return "Like{" +
                "path=" + path +
                ", like=" + like +
                '}';
    }
}
