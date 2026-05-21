package test.valueObjects;

import model.valueObjects.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
