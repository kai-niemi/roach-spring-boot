package io.roach.spring.catalog.util;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public abstract class RandomUtils {
    private RandomUtils() {
    }

    public static final ThreadLocalRandom random = ThreadLocalRandom.current();

    private static final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

    public static Money randomMoneyBetween(double low, double high, Currency currency) {
        if (high <= low) {
            throw new IllegalArgumentException("high<=low");
        }
        return Money.of(String.format(Locale.US, "%.2f", random.nextDouble(low, high)), currency);
    }

    public static BigDecimal randomBigDecimal(double low, double high) {
        if (high <= low) {
            throw new IllegalArgumentException("high<=low");
        }
        return new BigDecimal(String.format(Locale.US, "%.2f", random.nextDouble(low, high)));
    }

    public static String randomString(int min) {
        byte[] buffer = new byte[min];
        random.nextBytes(buffer);
        return encoder.encodeToString(buffer);
    }

    public static <E> E selectRandom(E[] collection) {
        return selectRandom(Arrays.asList(collection));
    }

    public static <E> E selectRandom(Collection<E> collection) {
        List<E> givenList = new ArrayList<>(collection);
        return givenList.get(new SecureRandom().nextInt(givenList.size()));
    }
}
