package model.valueObjects;

import exception.InvalidAmountException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public record Money(BigDecimal value) implements Comparable<Money>{
    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money {
        if (value == null) {
            throw new InvalidAmountException("Valor não pode ser null");
        }

        value = value.setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isZero(){
        return value.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isNegativeOrZero(){
        return value.compareTo(BigDecimal.ZERO) <= 0;
    }

    public boolean isGreaterThan(Money other){
        return value.compareTo(other.value) > 0;
    }

    public boolean isEqual(Money other){
        return value.compareTo(other.value) == 0;
    }

    public Money add(Money other) {
        return new Money(value.add(other.value));
    }

    public Money subtract(Money other) {
        return new Money(value.subtract(other.value));
    }

    @Override
    public String toString() {
        return NumberFormat
                .getCurrencyInstance(Locale.of("pt", "BR"))
                .format(value);
    }

    public Money multiply(BigDecimal interestRate) {
        BigDecimal multiply = value.multiply(interestRate);
        return new Money(multiply);
    }

    @Override
    public int compareTo(Money other) {
        return value.compareTo(other.value);
    }
}