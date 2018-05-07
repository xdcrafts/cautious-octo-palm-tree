package com.github.xdcrafts.spring.data.web.query.api.operator;

import javax.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.List;

/**
 * And operator.
 *
 * @author Vadim Dubs
 */
public class And implements Operator {

    @NotEmpty
    private List<Operator> and;

    public And() {
    }

    public And(List<Operator> and) {
        this.and = and;
    }

    public And(Operator first, Operator second) {
        this(Arrays.asList(first, second));
    }

    public List<Operator> getAnd() {
        return and;
    }

    public void setAnd(List<Operator> and) {
        this.and = and;
    }

    @Override
    public String toString() {
        return "And{" + and + '}';
    }
}
