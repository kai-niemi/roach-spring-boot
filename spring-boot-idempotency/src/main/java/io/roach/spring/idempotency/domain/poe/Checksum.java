package io.roach.spring.idempotency.domain.poe;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Checksum {
    public static Checksum sha256() {
        return new Checksum();
    }

    private final String algorithm;

    public Checksum() {
        this("SHA-256");
    }

    public Checksum(String algorithm) {
        try {
            MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        this.algorithm = algorithm;
    }

    public String encodeToHex(String input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            byte[] data = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));
            return new String(hex(data));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    private static char[] hex(byte[] bytes) {
        int byteCount = bytes.length;
        char[] result = new char[2 * byteCount];
        int j = 0;
        for (byte b : bytes) {
            result[j++] = HEX_CHARS[(240 & b) >>> 4];
            result[j++] = HEX_CHARS[15 & b];
        }
        return result;
    }
}
