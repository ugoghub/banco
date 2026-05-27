package valueObjects;

import exception.InvalidAmountException;
import model.valueObjects.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void shouldAddValuesCorrectly() {

        Money a = new Money(new BigDecimal("10"));
        Money b = new Money(new BigDecimal("5"));

        Money result = a.add(b);

        assertEquals(
                new BigDecimal("15.00"),
                result.value()
        );
    }

    @Test
    void shouldIdentifyZero() {

        assertTrue(Money.ZERO.isZero());
    }

    @Test
    void shouldSubtractValuesCorrectly() {

        Money a = new Money(new BigDecimal("20"));
        Money b = new Money(new BigDecimal("5"));

        Money result = a.subtract(b);

        assertEquals(
                new BigDecimal("15.00"),
                result.value()
        );
    }

    @Test
    void shouldRoundToTwoDecimalPlaces() {

        Money money =
                new Money(
                        new BigDecimal("10.999")
                );

        assertEquals(
                new BigDecimal("11.00"),
                money.value()
        );
    }

    @Test
    void shouldIdentifyNegativeValues() {

        Money money =
                new Money(
                        new BigDecimal("-10")
                );

        assertTrue(
                money.isNegativeOrZero()
        );
    }

    @Test
    void shouldNegateValue() {

        Money money =
                new Money(new BigDecimal("100"));

        Money result = money.negate();

        assertEquals(
                new BigDecimal("-100.00"),
                result.value()
        );
    }

    @Test
    void shouldCompareValuesCorrectly() {

        Money a =
                new Money(new BigDecimal("10"));

        Money b =
                new Money(new BigDecimal("20"));

        assertTrue(b.isGreaterThan(a));
    }

    @Test
    void shouldIdentifyEqualValues() {

        Money a =
                new Money(new BigDecimal("10"));

        Money b =
                new Money(new BigDecimal("10.00"));

        assertTrue(a.isEqual(b));
    }

    @Test
    void shouldMultiplyValuesCorrectly() {

        Money money =
                Money.of("10");

        Money result =
                money.multiplyByRate(
                        new BigDecimal("2.5")
                );

        assertEquals(
                new BigDecimal("25.00"),
                result.value()
        );
    }

    @Test
    void shouldBeImmutableAfterAddition() {

        Money original =
                Money.of("10");

        Money result =
                original.add(
                        Money.of("5")
                );

        assertEquals(
                Money.of("10"),
                original
        );

        assertEquals(
                Money.of("15"),
                result
        );
    }

    @Test
    void negateShouldNotChangeOriginalValue() {

        Money original =
                Money.of("100");

        Money negated =
                original.negate();

        assertEquals(
                Money.of("100"),
                original
        );

        assertEquals(
                Money.of("-100"),
                negated
        );
    }

    @Test
    void shouldSubtractToNegativeValueCorrectly() {

        Money a =
                Money.of("5");

        Money b =
                Money.of("10");

        Money result =
                a.subtract(b);

        assertEquals(
                new BigDecimal("-5.00"),
                result.value()
        );
    }

    @Test
    void shouldThrowExceptionIfValueIsNotANumber() {
        assertThrows(
                InvalidAmountException.class,
                () -> Money.of("A%[]!()")
        );
    }
}
