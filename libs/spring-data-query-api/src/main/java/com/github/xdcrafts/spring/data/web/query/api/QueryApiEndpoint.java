package com.github.xdcrafts.spring.data.web.query.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.xdcrafts.spring.data.web.query.api.inspector.Inspector;
import com.github.xdcrafts.spring.data.web.query.api.operator.Operator;
import com.github.xdcrafts.spring.data.web.query.api.resolver.KeyEntityTypeRepo;
import com.github.xdcrafts.spring.data.web.query.api.resolver.SpringDataResolver;
import com.github.xdcrafts.spring.data.web.query.api.selector.Node;
import com.github.xdcrafts.spring.data.web.query.api.selector.Selector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Query API Endpoint.
 *
 * @author Vadim Dubs
 */
@SuppressWarnings("unchecked")
@RestController
@RequestMapping("${spring.data.web.query.api.basePath}")
public class QueryApiEndpoint {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier("defaultConversionService")
    private ConversionService conversionService;

    @Autowired
    private SpringDataResolver springDataResolver;

    /**
     * Get type mappings.
     */
    @GetMapping(path = "known-types")
    public List<String> supportedTypes() {
        return this.springDataResolver.knownTypes()
            .stream()
            .map(KeyEntityTypeRepo::getKey)
            .collect(Collectors.toList());
    }

    /**
     * Get type mappings.
     */
    @GetMapping(path = "known-types/{type}")
    public Map<String, Object> mappingSchema(
        @PathVariable("type") String entityType,
        @RequestParam(value = "depth", defaultValue = "-1") int depth
    ) {
        return Inspector.inspect(this.springDataResolver.resolve(entityType).getType(), depth);
    }

    /**
     * Query request.
     */
    @PostMapping(path = "{type}/query")
    public Page<?> query(
        @PathVariable("type") String entityType,
        @RequestBody @Valid QueryRequest queryRequest
    ) {
        final KeyEntityTypeRepo keyEntityTypeRepo = this.springDataResolver.resolve(entityType);
        final Specification specification = new Query(
            this.conversionService,
            keyEntityTypeRepo.getType(),
            queryRequest.getQuery(),
            queryRequest.isDistinct()
        );
        final PageRequest pageRequest = PageRequest.of(
            queryRequest.getPageNumber(),
            queryRequest.getPageSize(),
            Sort.Direction.fromString(queryRequest.getSortOrder()),
            queryRequest.getSortBy().toArray(new String[0])
        );
        final Page page = keyEntityTypeRepo.getRepo().findAll(specification, pageRequest);
        final Node selector = Selector.asTree(queryRequest.getSelect());
        if (selector == null || selector.isEmpty()) {
            return page;
        } else {
            return page.map(v -> Selector.select(selector, v));
        }
    }

    /**
     * Query request.
     *
     * @throws IOException in case of query parse errors
     */
    @GetMapping(path = "{type}/query")
    public Page<?> query(
        @PathVariable("type") String entityType,
        @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
        @RequestParam(value = "pageSize", defaultValue = "100") Integer pageSize,
        @RequestParam(value = "sortOrder", defaultValue = "desc") String sortOrder,
        @RequestParam(value = "sortBy", defaultValue = "id") List<String> sortBy,
        @RequestParam(value = "distinct", defaultValue = "true") boolean distinct,
        @RequestParam(value = "select", required = false) List<String> select,
        @RequestParam(value = "query", required = false) String query
    ) throws IOException {
        final QueryRequest queryRequest = new QueryRequest(
            select == null
                ? null
                : select.stream().map(str -> Arrays.asList(str.split("\\."))).collect(Collectors.toList()),
            query == null || query.isEmpty()
                ? null
                : this.objectMapper.readValue(query, Operator.class),
            distinct,
            pageNumber,
            pageSize,
            sortOrder,
            sortBy
        );
        return query(entityType, queryRequest);
    }
}
