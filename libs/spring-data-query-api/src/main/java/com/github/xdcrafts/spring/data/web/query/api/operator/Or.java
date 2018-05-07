package com.github.xdcrafts.spring.data.web.query.api.operator;

import javax.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.List;

/**
 * Or operator.
 *
 * @author Vadim Dubs
 */
public class Or implements Operator {

    @NotEmpty
    private List<Operator> or;

    public Or() {
    }

    public Or(List<Operator> or) {
        this.or = or;
    }

    public Or(Operator first, Operator second) {
        this(Arrays.asList(first, second));
    }

    public List<Operator> getOr() {
        return or;
    }

    public void setOr(List<Operator> or) {
        this.or = or;
    }

    @Override
    public String toString() {
        return "Or{" + or + '}';
    }
}
