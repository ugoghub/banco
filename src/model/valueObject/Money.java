package model.valueObject;

import exception.InvalidAmountException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

public record Money(BigDecimal value) {

    public Money {
        if (value == null) {
            throw new InvalidAmountException("Valor não pode ser null");
        }

        value = value.setScale(2, RoundingMode.HALF_UP);
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    public boolean isZero(){
        return value.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isPositive() {
        return value.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegative() {
        return value.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isGreaterThanOrEqual(Money other) {
        return value.compareTo(other.value) >= 0;
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
                .getCurrencyInstance(new Locale("pt", "BR"))
                .format(value);
    }

    public Money multiply(BigDecimal interestRate) {
        BigDecimal multiply = value.multiply(interestRate);
        return new Money(multiply);
    }

    public int compareTo(Money other) {
        return value.compareTo(other.value);
    }
}