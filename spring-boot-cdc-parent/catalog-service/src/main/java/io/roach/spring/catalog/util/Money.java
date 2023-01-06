package io.roach.spring.catalog.util;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Immutable monetary type that couples an amount with a currency.
 * The amount value is represented by {@code java.math.BigDecimal} and the currency
 * by a ISO-4701 {@code java.util.Currency}.
 */
@JsonSerialize(using = Money.MoneySerializer.class)
@JsonDeserialize(using = Money.MoneyDeserializer.class)
public final class Money implements Serializable, Comparable<Money> {
    public static class MoneySerializer extends StdSerializer<Money> {
        public MoneySerializer() {
            this(null);
        }

        public MoneySerializer(Class<Money> t) {
            super(t);
        }

        @Override
        public void serialize(Money value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException {
            jgen.writeString(value.toString());
        }
    }

    public static class MoneyDeserializer extends StdDeserializer<Money> {
        public MoneyDeserializer() {
            this(null);
        }

        public MoneyDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public Money deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException {
            return Money.of(p.readValueAs(String.class));
        }
    }

    public static final Currency SEK = Currency.getInstance("SEK");

    public static final Currency EUR = Currency.getInstance("EUR");

    public static final Currency USD = Currency.getInstance("USD");

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private static final BigDecimal ONE = BigDecimal.ONE;

    private static final RoundingMode roundingMode = RoundingMode.HALF_EVEN;

    // Can't be final due to JPA
    private BigDecimal amount;

    // Can't be final due to JPA
    private Currency currency;

    protected Money() {
    }

    /**
     * Creates a new Money instance.
     *
     * @param amount the decimal amount (required)
     * @param currency the currency (required)
     */
    public Money(BigDecimal amount, Currency currency) {
        if (amount == null) {
            throw new NullPointerException("value is null");
        }
        if (currency == null) {
            throw new NullPointerException("currency is null");
        }
        if (amount.scale() != currency.getDefaultFractionDigits()) {
            throw new IllegalArgumentException("Wrong number of fraction digits for currency : "
                    + amount.scale()
                    + " != " + currency.getDefaultFractionDigits() + " for: " + amount);
        }
        this.amount = amount;
        this.currency = currency;
    }

    public static Money of(String text) {
        String[] elt = text.split(" ");
        Assert.notEmpty(elt, "Bad format: " + text);
        Assert.isTrue(elt.length == 2, "Bad format: " + text);
        return Money.of(elt[0], elt[1]);
    }

    public static Money of(String amount, String currency) {
        return of(amount, Currency.getInstance(currency));
    }

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, Currency.getInstance(currency));
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money of(String amount, Currency currency) {
        return new Money(new BigDecimal(amount), currency);
    }

    public static Money of(Double amount, Currency currency) {
        return new Money(BigDecimal.valueOf(amount)
                .setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP), currency);
    }

    public static Money kronor(String amount) {
        return new Money(new BigDecimal(amount), SEK);
    }

    public static Money usd(String amount) {
        return new Money(new BigDecimal(amount), USD);
    }

    public static Money euro(String amount) {
        return new Money(new BigDecimal(amount), EUR);
    }

    public static Money zero(String currency) {
        return zero(Currency.getInstance(currency));
    }

    public static Money zero(Currency currency) {
        BigDecimal amount = ZERO.setScale(currency.getDefaultFractionDigits(), RoundingMode.UNNECESSARY);
        return new Money(amount, currency);
    }

    public static Money one(String currency) {
        Currency c = Currency.getInstance(currency);
        BigDecimal amount = ONE.setScale(c.getDefaultFractionDigits(), RoundingMode.UNNECESSARY);
        return new Money(amount, c);
    }


    public Money plus(Money... addends) {
        BigDecimal copy = BigDecimal.ZERO.add(amount);
        for (Money add : addends) {
            assertSameCurrency(add);
            copy = copy.add(add.amount);
        }
        return new Money(copy, currency);
    }

    public Money minus(Money... subtrahends) {
        BigDecimal a = this.amount;
        for (Money subtrahend : subtrahends) {
            assertSameCurrency(subtrahend);
            a = a.subtract(subtrahend.amount);
        }
        return new Money(a, currency);
    }

    public Money multiply(int multiplier) {
        BigDecimal newAmount = amount.multiply(new BigDecimal(Integer.toString(multiplier)));
        newAmount = setScale(newAmount);
        return new Money(newAmount, currency);
    }

    public Money multiply(double multiplier) {
        assertNotNull(multiplier);
        BigDecimal newAmount = amount.multiply(new BigDecimal(Double.toString(multiplier)));
        newAmount = setScale(newAmount);
        return new Money(newAmount, currency);
    }

    public Money multiply(BigDecimal multiplier) {
        assertNotNull(multiplier);
        BigDecimal newAmount = amount.multiply(multiplier);
        newAmount = setScale(newAmount);
        return new Money(newAmount, currency);
    }

    public Money divideAndRound(double divisor) {
        assertNotNull(divisor);
        BigDecimal newAmount = amount.divide(new BigDecimal(Double.toString(divisor)), 16, roundingMode);
        newAmount = setScale(newAmount);
        return new Money(newAmount, currency);
    }

    public Money divide(BigDecimal divisor) {
        assertNotNull(divisor);
        BigDecimal newAmount = amount.divide(divisor, RoundingMode.UNNECESSARY);
        newAmount = setScale(newAmount);
        return new Money(newAmount, currency);
    }

    public Money divide(double divisor) {
        assertNotNull(divisor);
        BigDecimal newAmount = amount
                .divide(new BigDecimal(Double.toString(divisor)), RoundingMode.UNNECESSARY);
        newAmount = setScale(newAmount);
        return new Money(newAmount, currency);
    }

    public Money remainder(int divisor) {
        return new Money(amount.remainder(
                new BigDecimal(Integer.toString(divisor))), currency);
    }

    public Money max(Money other) {
        assertNotNull(other);
        return compareTo(other) > 0 ? this : other;
    }

    public Money min(Money other) {
        assertNotNull(other);
        return compareTo(other) < 0 ? this : other;
    }

    public boolean isGreaterThan(Money other) {
        assertSameCurrency(other);
        return compareTo(other) > 0;
    }

    public boolean isGreaterThanOrEqualTo(Money other) {
        assertSameCurrency(other);
        return compareTo(other) >= 0;
    }

    public boolean isLessThan(Money other) {
        assertSameCurrency(other);
        return compareTo(other) < 0;
    }

    public boolean isLessThanOrEqualTo(Money other) {
        assertSameCurrency(other);
        return compareTo(other) <= 0;
    }

    public boolean isNegative() {
        return amount.compareTo(ZERO) < 0;
    }

    public boolean isPositive() {
        return !isNegative();
    }

    public boolean isZero() {
        return amount.compareTo(ZERO) == 0;
    }

    public boolean isSameCurrency(Money other) {
        return currency.equals(other.getCurrency());
    }

    private BigDecimal setScale(BigDecimal bigDecimal) {
        return bigDecimal.setScale(currency.getDefaultFractionDigits(), roundingMode);
    }

    private void assertSameCurrency(Money other) {
        assertNotNull(other);
        if (!isSameCurrency(other)) {
            throw new CurrencyMismatchException(
                    currency + " doesn't match " + other.currency);
        }
    }

    private void assertNotNull(Object money) {
        if (money == null) {
            throw new NullPointerException("money is null");
        }
    }

    public Money negate() {
        return new Money(amount.negate(), currency);
    }

    public Currency getCurrency() {
        return currency;
    }

    /**
     * Return the underlying monetary amount.
     *
     * @return the monetary amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Compares this money object with another instance. The money objects are
     * compared by their underlying long value.
     * <p/>
     * {@inheritDoc}
     */
    public int compareTo(Money o) {
        return amount.compareTo(o.amount);
    }

    /**
     * Compares two money objects for equality. The money objects are
     * compared by their underlying bigdecimal value and currency ISO code.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Money money = (Money) o;

        if (!amount.equals(money.amount)) {
            return false;
        }
        if (!currency.equals(money.currency)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = amount.hashCode();
        result = 31 * result + currency.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return amount.toPlainString() + " " + currency;
    }
}
