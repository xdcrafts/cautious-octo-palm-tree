package com.github.xdcrafts.spring.data.web.query.api;

import com.github.xdcrafts.spring.data.web.query.api.operator.*;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Function;

/**
 * General purpose builder of Spring Data Specifications based on simple data-driven configuration.
 * @param <T> filter type
 *
 * @author Vadim Dubs
 */
@SuppressWarnings("unchecked")
public class Query<T> implements Specification<T> {

    /**
     * Custom runtime exception.
     */
    private static final class QueryException extends RuntimeException {
        private QueryException(String message) {
            super(message);
        }
        private QueryException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Context for operator.
     * @param <T> operator type
     */
    private static final class OperatorContext<T extends Operator> {
        final Root root;
        final Class rootType;
        final ConversionService conversionService;
        final CriteriaQuery criteriaQuery;
        final CriteriaBuilder criteriaBuilder;
        final T operator;
        private OperatorContext(
            Root root,
            Class rootType,
            ConversionService conversionService,
            CriteriaQuery criteriaQuery,
            CriteriaBuilder criteriaBuilder,
            T operator
        ) {
            this.root = root;
            this.rootType = rootType;
            this.conversionService = conversionService;
            this.criteriaQuery = criteriaQuery;
            this.criteriaBuilder = criteriaBuilder;
            this.operator = operator;
        }
    }

    /**
     * Tuple for expression and type.
     */
    private static final class ExpressionWithType {
        private final Expression expression;
        private final Class type;
        private ExpressionWithType(Expression expression, Class type) {
            this.expression = expression;
            this.type = type;
        }
    }

    /**
     * Returns the first {@link Field} in the hierarchy for the specified name.
     * @throws NoSuchFieldException if there is no such field in current class or any of its ancestors.
     */
    private static Field getField(Class<?> clazz, String name) throws NoSuchFieldException {
        Field field = null;
        while (clazz != null && field == null) {
            try {
                field = clazz.getDeclaredField(name);
            } catch (Exception e) {
                // Ignore this exception, take another superclass and try again
            }
            clazz = clazz.getSuperclass();
        }

        if (field == null) {
            throw new NoSuchFieldException();
        }

        return field;
    }

    private static ExpressionWithType buildExpression(Root root, List<String> path, Class rootType) {
        Field field;
        Class<?> attributeType = rootType;
        Path expression = null;
        for (String attributeName : path) {
            try {
                field = getField(attributeType, attributeName);
            } catch (NoSuchFieldException e) {
                throw new QueryException(
                    "Unable to locate attribute '" + attributeName + "' for type " + attributeType, e
                );
            }
            if (Collection.class.isAssignableFrom(field.getType())) {
                attributeType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                if (expression == null) {
                    expression = root.join(attributeName);
                } else {
                    if (!(expression instanceof Join)) {
                        throw new QueryException("Unable to build join-path-join specification.");
                    }
                    expression = ((Join) expression).join(attributeName);
                }
            } else {
                attributeType = field.getType();
                expression = expression == null
                    ? root.get(attributeName)
                    : expression.get(attributeName);
            }
        }
        return new ExpressionWithType(expression, attributeType);
    }

    private static final Map<
            Class<? extends Operator>,
            Function<OperatorContext, Predicate>
        > PREDICATE_BUILDERS = new HashMap<>();

    private static Function<OperatorContext, Predicate> getPredicateBuilder(Operator op) {
        if (!PREDICATE_BUILDERS.containsKey(op.getClass())) {
            throw new IllegalArgumentException(
                "Can not build predicate for operator: " + op.getClass().getSimpleName()
            );
        }
        return PREDICATE_BUILDERS.get(op.getClass());
    }

    static {
        PREDICATE_BUILDERS.put(
            Not.class,
            operatorContext -> {
                final OperatorContext<Not> ctx = (OperatorContext<Not>) operatorContext;
                final OperatorContext opCtx = new OperatorContext<>(
                    ctx.root,
                    ctx.rootType,
                    ctx.conversionService,
                    ctx.criteriaQuery,
                    ctx.criteriaBuilder,
                    ctx.operator.getNot()
                );
                final Predicate predicate = getPredicateBuilder(ctx.operator.getNot()).apply(opCtx);
                return ctx.criteriaBuilder.not(predicate);
            }
        );
        PREDICATE_BUILDERS.put(
            And.class,
            operatorContext -> {
                final OperatorContext<And> ctx = (OperatorContext<And>) operatorContext;
                return ctx.criteriaBuilder.and(ctx.operator.getAnd().stream()
                    .map(op -> {
                        final OperatorContext opCtx = new OperatorContext<>(
                            ctx.root, ctx.rootType, ctx.conversionService, ctx.criteriaQuery, ctx.criteriaBuilder, op
                        );
                        return getPredicateBuilder(op).apply(opCtx);
                    }).toArray(Predicate[]::new));
            }
        );
        PREDICATE_BUILDERS.put(
            Or.class,
            operatorContext -> {
                final OperatorContext<Or> ctx = (OperatorContext<Or>) operatorContext;
                return ctx.criteriaBuilder.or(ctx.operator.getOr().stream()
                    .map(op -> {
                        final OperatorContext opCtx = new OperatorContext<>(
                            ctx.root, ctx.rootType, ctx.conversionService, ctx.criteriaQuery, ctx.criteriaBuilder, op
                        );
                        return getPredicateBuilder(op).apply(opCtx);
                    }).toArray(Predicate[]::new));
            }
        );
        PREDICATE_BUILDERS.put(
            Equals.class,
            operatorContext -> {
                final OperatorContext<Equals> ctx = (OperatorContext<Equals>) operatorContext;
                final ExpressionWithType expressionWithType =
                    buildExpression(ctx.root, ctx.operator.getPath(), ctx.rootType);
                if (ctx.operator.getEquals() == null) {
                    return ctx.criteriaBuilder.isNull(expressionWithType.expression);
                } else {
                    return ctx.criteriaBuilder.equal(
                        expressionWithType.expression,
                        ctx.conversionService.convert(ctx.operator.getEquals(), expressionWithType.type)
                    );
                }
            }
        );
        PREDICATE_BUILDERS.put(
            In.class,
            operatorContext -> {
                final OperatorContext<In> ctx = (OperatorContext<In>) operatorContext;
                final ExpressionWithType expressionWithType =
                    buildExpression(ctx.root, ctx.operator.getPath(), ctx.rootType);
                final List values = new ArrayList();
                ctx.operator.getIn()
                    .forEach(v -> values.add(ctx.conversionService.convert(v, expressionWithType.type)));
                return expressionWithType.expression.in(values);
            }
        );
        PREDICATE_BUILDERS.put(
            Like.class,
            operatorContext -> {
                final OperatorContext<Like> ctx = (OperatorContext<Like>) operatorContext;
                final ExpressionWithType expressionWithType =
                    buildExpression(ctx.root, ctx.operator.getPath(), ctx.rootType);
                if (!String.class.isAssignableFrom(expressionWithType.type)) {
                    throw new QueryException("'like' supported only for string values");
                }
                return ctx.criteriaBuilder.like(expressionWithType.expression, ctx.operator.getLike());
            }
        );
        PREDICATE_BUILDERS.put(
            Greater.class,
            operatorContext -> {
                final OperatorContext<Greater> ctx = (OperatorContext<Greater>) operatorContext;
                final ExpressionWithType expressionWithType =
                    buildExpression(ctx.root, ctx.operator.getPath(), ctx.rootType);
                if (!Comparable.class.isAssignableFrom(expressionWithType.type)) {
                    throw new QueryException("'greater' supported only for comparable values");
                }
                final Comparable value = (Comparable) ctx.conversionService
                    .convert(ctx.operator.getGreater(), expressionWithType.type);
                return ctx.criteriaBuilder.greaterThan(expressionWithType.expression, value);
            }
        );
        PREDICATE_BUILDERS.put(
            GreaterOrEquals.class,
            operatorContext -> {
                final OperatorContext<GreaterOrEquals> ctx = (OperatorContext<GreaterOrEquals>) operatorContext;
                final ExpressionWithType expressionWithType =
                    buildExpression(ctx.root, ctx.operator.getPath(), ctx.rootType);
                if (!Comparable.class.isAssignableFrom(expressionWithType.type)) {
                    throw new QueryException("'greaterOrEquals' supported only for comparable values");
                }
                final Comparable value = (Comparable) ctx.conversionService
                    .convert(ctx.operator.getGreaterOrEquals(), expressionWithType.type);
                return ctx.criteriaBuilder.greaterThanOrEqualTo(expressionWithType.expression, value);
            }
        );
        PREDICATE_BUILDERS.put(
            Less.class,
            operatorContext -> {
                final OperatorContext<Less> ctx = (OperatorContext<Less>) operatorContext;
                final ExpressionWithType expressionWithType =
                    buildExpression(ctx.root, ctx.operator.getPath(), ctx.rootType);
                if (!Comparable.class.isAssignableFrom(expressionWithType.type)) {
                    throw new QueryException("'less' supported only for comparable values");
                }
                final Comparable value = (Comparable) ctx.conversionService
                    .convert(ctx.operator.getLess(), expressionWithType.type);
                return ctx.criteriaBuilder.lessThan(expressionWithType.expression, value);
            }
        );
        PREDICATE_BUILDERS.put(
            LessOrEquals.class,
            operatorContext -> {
                final OperatorContext<LessOrEquals> ctx = (OperatorContext<LessOrEquals>) operatorContext;
                final ExpressionWithType expressionWithType =
                    buildExpression(ctx.root, ctx.operator.getPath(), ctx.rootType);
                if (!Comparable.class.isAssignableFrom(expressionWithType.type)) {
                    throw new QueryException("'lessOrEquals' supported only for comparable values");
                }
                final Comparable value = (Comparable) ctx.conversionService
                        .convert(ctx.operator.getLessOrEquals(), expressionWithType.type);
                return ctx.criteriaBuilder.lessThan(expressionWithType.expression, value);
            }
        );
    }

    private final Class<T> entityType;
    private final boolean distinct;
    private final Operator operator;
    private final ConversionService conversionService;

    public Query(
        final ConversionService conversionService,
        final Class<T> entityType,
        final Operator operator,
        final boolean distinct
    ) {
        this.operator = operator;
        this.distinct = distinct;
        this.entityType = entityType;
        this.conversionService = conversionService;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (this.operator == null) {
            return cb.and();
        }
        query.distinct(this.distinct);
        return getPredicateBuilder(this.operator)
            .apply(new OperatorContext(
                root,
                this.entityType,
                this.conversionService,
                query,
                cb,
                this.operator
            ));
    }
}
