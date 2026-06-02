package model.valueobject;

import exception.InvalidAmountException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Money implements Comparable<Money>{
    public static final Money ZERO = Money.of("0");

    private final BigDecimal value;

    private Money(BigDecimal value) {
        validateNonNull(value);

        this.value = value.setScale(2, RoundingMode.HALF_EVEN);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (!(o instanceof Money money)) return false;

        return value.compareTo(money.value) == 0;
    }

    @Override
    public int hashCode() {
        return value.stripTrailingZeros().hashCode();
    }

    public boolean isZero(){
        return value.compareTo(BigDecimal.ZERO) == 0;
    }

    public static Money of(String amount) {

        if(amount == null){
            throw new InvalidAmountException(
                    "Valor não pode ser null"
            );
        }

        try {
            return new Money(
                    new BigDecimal(amount)
            );

        } catch (NumberFormatException e) {
            throw new InvalidAmountException(
                    "Valor inválido"
            );
        }
    }

    public static Money of(BigDecimal amount) {

        if(amount == null){
            throw new InvalidAmountException(
                    "Valor não pode ser null"
            );
        }

        return new Money(amount);
    }

    public boolean isNegativeOrZero(){
        return value.compareTo(BigDecimal.ZERO) <= 0;
    }

    public boolean isGreaterThan(Money other){
        validateNonNull(other);
        return value.compareTo(other.value) > 0;
    }

    public boolean isEqual(Money other){
        validateNonNull(other);
        return value.compareTo(other.value) == 0;
    }

    public Money add(Money other) {
        validateNonNull(other);
        return new Money(value.add(other.value));
    }

    public Money subtract(Money other) {
        validateNonNull(other);
        return new Money(value.subtract(other.value));
    }

    public Money negate() {
        return new Money(value.negate());
    }

    public Money multiplyByRate(BigDecimal rate) {
        validateNonNull(rate);
        BigDecimal multiply = value.multiply(rate);
        return new Money(multiply);
    }

    @Override
    public int compareTo(Money other) {
        validateNonNull(other);
        return value.compareTo(other.value);
    }

    private <T> void validateNonNull(T money){
        if(money == null){
            throw new InvalidAmountException("Valor não pode ser null");
        }
    }
}