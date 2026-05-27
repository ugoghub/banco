package model.valueObjects;

import exception.InvalidAmountException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public record Money(BigDecimal value) implements Comparable<Money>{
    public static final Money ZERO = Money.of("0");

    public Money {
        if (value == null) {
            throw new InvalidAmountException("Valor não pode ser null");
        }

        value = value.setScale(2, RoundingMode.HALF_EVEN);
    }

    public boolean isZero(){
        return value.compareTo(BigDecimal.ZERO) == 0;
    }

    public static Money of(String amount){
        try {
            return new Money(new BigDecimal(amount));
        } catch (NumberFormatException e) {
            throw new InvalidAmountException("Valor inválido");
        }
    }

    public boolean isNegativeOrZero(){
        return value.compareTo(BigDecimal.ZERO) <= 0;
    }

    public boolean isGreaterThan(Money other){
        Objects.requireNonNull(other);
        return value.compareTo(other.value) > 0;
    }

    public boolean isEqual(Money other){
        Objects.requireNonNull(other);
        return value.compareTo(other.value) == 0;
    }

    public Money add(Money other) {
        Objects.requireNonNull(other);
        return new Money(value.add(other.value));
    }

    public Money subtract(Money other) {
        Objects.requireNonNull(other);
        return new Money(value.subtract(other.value));
    }

    public Money negate() {
        return new Money(value.negate());
    }

    @Override
    public String toString() {
        return NumberFormat
                .getCurrencyInstance(Locale.of("pt", "BR"))
                .format(value);
    }

    public Money multiplyByRate(BigDecimal rate) {
        Objects.requireNonNull(rate);
        BigDecimal multiply = value.multiply(rate);
        return new Money(multiply);
    }

    @Override
    public int compareTo(Money other) {
        Objects.requireNonNull(other);
        return value.compareTo(other.value);
    }
}