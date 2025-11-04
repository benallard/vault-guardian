package de.benallard.occurent.eventstore.spring.data;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.occurrent.condition.Condition;
import org.occurrent.eventstore.api.SortBy;
import org.occurrent.filter.Filter;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * A mapper for all-things query related:
 * - Filter
 * - SortBy
 */
@Component
public class QueryMapper {

    public Specification<CloudEventEntity> mapFilter(Filter aFilter) {
        return switch (aFilter) {
            case Filter.All all -> Specification.unrestricted();
            case Filter.SingleConditionFilter single -> (root, query, builder) -> {
                Expression<?> field = root.get(single.fieldName());
                return mapCondition(field, single.condition(), builder);
            };
            case Filter.CompositionFilter composition -> {
                List<Specification<CloudEventEntity>> specs = composition.filters().stream()
                        .map(this::mapFilter)
                        .toList();
                yield switch (composition.operator()) {
                    case OR -> Specification.anyOf(specs);
                    case AND -> Specification.allOf(specs);
                };
            }
        };
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Predicate mapCondition(Expression field, Condition condition, CriteriaBuilder builder) {
        return switch (condition) {
            case Condition.InOperandCondition<?> inOperand -> {
                var inClause = builder.in(field);
                for (Object op : inOperand.operand()) {
                    inClause.value(op);
                }
                yield inClause;
            }
            case Condition.SingleOperandCondition<?> singleOperand -> switch (singleOperand.operandConditionName()) {
                case EQ -> builder.equal(field, singleOperand.operand());
                case NE -> builder.notEqual(field, singleOperand.operand());
                case GT -> builder.greaterThan(field, (Comparable) singleOperand.operand());
                case GTE -> builder.greaterThanOrEqualTo(field, (Comparable) singleOperand.operand());
                case LT -> builder.lessThan(field, (Comparable) singleOperand.operand());
                case LTE -> builder.lessThanOrEqualTo(field, (Comparable) singleOperand.operand());
            };
            /* Honestly I don't really get that part of the API...
             * As the fieldName is the same for the whole chain */
            case Condition.MultiOperandCondition<?> multiOperand -> {
                Predicate[] conditions = multiOperand.operations().stream()
                        .map(c -> mapCondition(field, c, builder))
                        .toArray(Predicate[]::new);
                yield switch (multiOperand.operationName()) {
                    case AND -> builder.and(conditions);
                    case OR -> builder.or(conditions);
                    case NOT -> builder.not(conditions[0]);
                };
            }
        };
    }

    public Sort mapSortBy(SortBy aSortBy) {
        return switch (aSortBy) {
            case SortBy.Unsorted _ -> Sort.unsorted();
            case SortBy.Natural _ -> Sort.by(Sort.Direction.ASC, "streamPosition");
            case SortBy.SingleFieldImpl singleFieldSort -> {
                Sort.Direction dir = singleFieldSort.direction == SortBy.SortDirection.ASCENDING ?
                        Sort.Direction.ASC : Sort.Direction.DESC;
                yield Sort.by(dir, singleFieldSort.fieldName);
            }
            case SortBy.MultipleSortStepsImpl multipleSort -> {
                var sort = Sort.unsorted();
                for (SortBy sortBy : multipleSort.steps) {
                    sort = sort.and(mapSortBy(sortBy));
                }
                yield sort;
            }
        };
    }

}
