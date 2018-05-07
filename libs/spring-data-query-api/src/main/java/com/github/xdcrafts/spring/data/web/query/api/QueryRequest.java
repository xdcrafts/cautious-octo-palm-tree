package com.github.xdcrafts.spring.data.web.query.api;

import com.github.xdcrafts.spring.data.web.query.api.operator.Operator;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

/**
 * QueryRequest class.
 *
 * @author Vadim Dubs
 */
public class QueryRequest {

    @Min(0)
    private Integer pageNumber = 0;
    @Min(1)
    private Integer pageSize = 100;
    private String sortOrder = "desc";
    private List<String> sortBy = Collections.singletonList("id");
    private List<List<String>> select;
    @NotNull
    private boolean distinct = true;
    private Operator query;

    public QueryRequest() {
    }

    public QueryRequest(
        List<List<String>> select,
        Operator query,
        boolean distinct,
        Integer pageNumber,
        Integer pageSize,
        String sortOrder,
        List<String> sortBy
    ) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.sortOrder = sortOrder;
        this.sortBy = sortBy;
        this.select = select;
        this.distinct = distinct;
        this.query = query;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSort(String sort) {
        this.sortOrder = sort;
    }

    public List<String> getSortBy() {
        return sortBy;
    }

    public void setSortBy(List<String> sortBy) {
        this.sortBy = sortBy;
    }

    public List<List<String>> getSelect() {
        return select;
    }

    public void setSelect(List<List<String>> filters) {
        this.select = filters;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public Operator getQuery() {
        return query;
    }

    public void setQuery(Operator operator) {
        this.query = operator;
    }

    @Override
    public String toString() {
        return "QueryRequest{" +
                "pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                ", sortOrder='" + sortOrder + '\'' +
                ", sortBy=" + sortBy +
                ", select=" + select +
                ", query=" + query +
                '}';
    }
}
