package com.github.xdcrafts.spring.data.web.query.api.operator;

import javax.validation.constraints.NotNull;

/**
 * Not operator.
 *
 * @author Vadim Dubs
 */
public class Not implements Operator {

    @NotNull
    private Operator not;

    public Not() {
    }

    public Not(Operator not) {
        this.not = not;
    }

    public Operator getNot() {
        return not;
    }

    public void setNot(Operator not) {
        this.not = not;
    }

    @Override
    public String toString() {
        return "Not{" + not + '}';
    }
}
