package io.roach.spring.pagination.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

public class RandomData {
    private static final Logger logger = LoggerFactory.getLogger(RandomData.class);

    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    private static final List<String> firstNames = new ArrayList<>();

    private static final List<String> lastNames = new ArrayList<>();

    private static final List<String> cities = new ArrayList<>();

    private static final List<String> countries = new ArrayList<>();

    private static final List<String> currencies = new ArrayList<>();

    private static final List<String> states = new ArrayList<>();

    private static final List<String> stateCodes = new ArrayList<>();

    private static final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

    static {
        firstNames.addAll(readLines("random/firstname_female.txt"));
        firstNames.addAll(readLines("random/firstname_male.txt"));
        lastNames.addAll(readLines(("random/surnames.txt")));
        cities.addAll(readLines(("random/cities.txt")));
        states.addAll(readLines(("random/states.txt")));
        stateCodes.addAll(readLines(("random/state_code.txt")));

        for (Locale locale : Locale.getAvailableLocales()) {
            if (StringUtils.hasLength(locale.getDisplayCountry(Locale.US))) {
                countries.add(locale.getDisplayCountry(Locale.US));
            }
        }

        for (Currency currency : Currency.getAvailableCurrencies()) {
            currencies.add(currency.getCurrencyCode());
        }
    }

    private static List<String> readLines(String path) {
        try (InputStream resource = new ClassPathResource(path).getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(resource))) {
            return reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("", e);
        }
        return Collections.emptyList();
    }

    public static BigDecimal randomMoneyBetween(String low, String high) {
        return randomMoneyBetween(Double.parseDouble(low), Double.parseDouble(high), 2);
    }

    public static BigDecimal randomMoneyBetween(double low, double high, int fractions) {
        if (high <= low) {
            throw new IllegalArgumentException("high<=low");
        }
        return BigDecimal.valueOf(random.nextDouble(low, high))
                .setScale(fractions, RoundingMode.HALF_UP);
    }

    public static <T extends Enum<?>> T selectRandom(Class<T> clazz) {
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    public static <E> E selectRandom(List<E> collection) {
        return collection.get(random.nextInt(collection.size()));
    }

    public static <K> K selectRandom(Set<K> set) {
        Object[] keys = set.toArray();
        return (K) keys[random.nextInt(keys.length)];
    }

    public static <E> E selectRandom(E[] collection) {
        return collection[random.nextInt(collection.length)];
    }

    public static <E> Collection<E> selectRandomUnique(List<E> collection, int count) {
        if (count > collection.size()) {
            throw new IllegalArgumentException("Not enough elements");
        }

        Set<E> uniqueElements = new HashSet<>();
        while (uniqueElements.size() < count) {
            uniqueElements.add(selectRandom(collection));
        }

        return uniqueElements;
    }

    public static <E> Collection<E> selectRandomUnique(E[] array, int count) {
        if (count > array.length) {
            throw new IllegalArgumentException("Not enough elements");
        }

        Set<E> uniqueElements = new HashSet<>();
        while (uniqueElements.size() < count) {
            uniqueElements.add(selectRandom(array));
        }

        return uniqueElements;
    }

    public static <T> T selectRandomWeighted(Collection<T> items, List<Double> weights) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Empty collection");
        }
        if (items.size() != weights.size()) {
            throw new IllegalArgumentException("Collection and weights mismatch");
        }

        double totalWeight = weights.stream().mapToDouble(w -> w).sum();
        double randomWeight = random.nextDouble() * totalWeight;
        double cumulativeWeight = 0;

        int idx = 0;
        for (T item : items) {
            cumulativeWeight += weights.get(idx++);
            if (cumulativeWeight >= randomWeight) {
                return item;
            }
        }

        throw new IllegalStateException("This is not possible");
    }

    public static int randomInt(int start, int end) {
        return random.nextInt(start, end);
    }

    public static double randomDouble(double start, int end) {
        return random.nextDouble(start, end);
    }

    public static String randomFirstName() {
        return selectRandom(firstNames);
    }

    public static String randomLastName() {
        return selectRandom(lastNames);
    }

    public static String randomCity() {
        return StringUtils.capitalize(selectRandom(cities));
    }

    public static String randomPhoneNumber() {
        StringBuilder sb = new StringBuilder()
                .append("(")
                .append(random.nextInt(9) + 1);
        for (int i = 0; i < 2; i++) {
            sb.append(random.nextInt(10));
        }
        sb.append(") ")
                .append(random.nextInt(9) + 1);
        for (int i = 0; i < 2; i++) {
            sb.append(random.nextInt(10));
        }
        sb.append("-");
        for (int i = 0; i < 4; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public static String randomCountry() {
        return selectRandom(countries);
    }

    public static String randomCurrency() {
        return selectRandom(currencies);
    }

    public static String randomState() {
        return selectRandom(states);
    }

    public static String randomStateCode() {
        return selectRandom(stateCodes);
    }

    public static String randomZipCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public static String randomEmail() {
        String sb = randomFirstName().toLowerCase()
                + "."
                + randomLastName().toLowerCase()
                + "@example.com";
        return sb.replace(' ', '.');
    }

    public static String randomWord(int min) {
        byte[] buffer = new byte[min];
        random.nextBytes(buffer);
        return encoder.encodeToString(buffer);
    }
}

