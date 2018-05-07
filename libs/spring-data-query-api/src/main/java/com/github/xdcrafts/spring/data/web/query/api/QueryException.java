package com.github.xdcrafts.spring.data.web.query.api;

/**
 * Query exception.
 *
 * @author Vadim Dubs
 */
public final class QueryException extends RuntimeException {

    public QueryException(Throwable cause) {
        super(cause);
    }

    public QueryException(String message) {
        super(message);
    }
}
