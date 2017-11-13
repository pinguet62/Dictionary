package fr.pinguet62.jsfring.webapp.jsf.component.filter;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.SimpleExpression;
import fr.pinguet62.jsfring.webapp.jsf.component.filter.operator.Operator;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Stream.of;

/**
 * Class used to store the {@link Operator} and arguments of a filter on {@link SimpleExpression}.
 *
 * @param <Exp> The type of {@link SimpleExpression} on which apply filter.
 * @param <T>   The type of parameter of the {@link Operator}.
 */
public abstract class PathFilter<Exp extends SimpleExpression<T>, T extends Serializable>
        implements Supplier<Predicate>, Serializable {

    private static final long serialVersionUID = 1;

    /**
     * The {@link Operator} of filter.
     */
    private Operator<Exp, T> operator;

    /**
     * The {@link SimpleExpression} on which apply filter.
     */
    private final Exp path;

    /**
     * The arguments of {@link Operator}.
     * <p>
     * The value can be initialized (not {@code null}) but used.
     */
    private T value1, value2;

    /**
     * @param path The {@link #path}.
     */
    public PathFilter(Exp path) {
        this.path = path;
    }

    /**
     * Compare two {@link PathFilter} by comparing:
     * <ul>
     * <li>The {@link #path}</li>
     * <li>The {@link #operator}</li>
     * <li>And the parameters {@link #value1} and {@link #value2}</li>
     * </ul>
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof PathFilter))
            return false;
        PathFilter<Exp, T> other = (PathFilter<Exp, T>) obj;
        return Objects.equals(path, other.path)
                && Objects.equals(operator, other.operator)
                && Objects.equals(value1, other.value1)
                && Objects.equals(value2, other.value2);
    }

    /**
     * Generate the {@link Predicate} used to filter results.
     *
     * @return The {@link Predicate}.<br>
     * If {@link #operator} is {@code null}, then return an empty {@link Predicate}.
     */
    @Override
    public Predicate get() {
        if (operator == null)
            return new BooleanBuilder();
        return operator.apply(path, value1, value2);
    }

    public Operator<Exp, T> getOperator() {
        return operator;
    }

    /**
     * Get the list of {@link Operator}s, applicable on {@link #path}.
     * <p>
     * <b>By default:</b> Initialized with all available {@link Operator}.<br>
     * Override this method to list available {@link Operator}.
     *
     * @return The ordered list of {@link Operator}s.
     */
    public abstract List<Operator<Exp, T>> getOperators();

    public T getValue1() {
        return value1;
    }

    public T getValue2() {
        return value2;
    }

    /**
     * Calculate the hash code from:
     * <ul>
     * <li>The {@link #path}</li>
     * <li>The {@link #operator}</li>
     * <li>And the parameters {@link #value1} and {@link #value2}</li>
     * </ul>
     */
    @Override
    public int hashCode() {
        return Objects.hash(path, operator, value1, value2);
    }

    /**
     * Check that argument values are correct, according to {@link Operator#getNumberOfParameters() number of arguments} of
     * {@link #operator}.
     *
     * @return The result.
     */
    public boolean isValid() {
        if (operator == null)
            return true;
        return values().allMatch(Objects::isNull) || values().anyMatch(v -> !Objects.isNull(v));
    }

    /**
     * TODO Change visibility and argument type of this method. See
     * {@link PathFilterComponent#getConvertedValue(javax.faces.context.FacesContext, Object)}
     */
    void setOperator(Operator<?, ?> operator2) {
        this.operator = (Operator<Exp, T>) operator2;
    }

    /**
     * TODO Change visibility and argument type of this method. See
     * {@link PathFilterComponent#getConvertedValue(javax.faces.context.FacesContext, Object)}
     */
    void setValue1(Object object) {
        this.value1 = (T) object;
    }

    /**
     * TODO Change visibility and argument type of this method. See
     * {@link PathFilterComponent#getConvertedValue(javax.faces.context.FacesContext, Object)}
     */
    void setValue2(Object value2) {
        this.value2 = (T) value2;
    }

    @Override
    public String toString() {
        if (operator == null)
            return "";

        switch (operator.getNumberOfParameters()) {
            case 0:
                return operator.getMessage();
            case 1:
                return String.format("%s(%s)", operator.getMessage(), value1);
            case 2:
                return String.format("%s(%s, %s)", operator.getMessage(), value1, value2);
            default:
                throw new UnsupportedOperationException("Invalid number of parameters for operator " + operator);
        }
    }

    /**
     * {@link Stream} of used argument values.<br>
     * The number of values depends of {@link Operator#getNumberOfParameters() number of arguments}.
     */
    private Stream<T> values() {
        return of(value1, value2).limit(operator.getNumberOfParameters());
    }

}