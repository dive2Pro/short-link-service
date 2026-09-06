package com.example.shortlink.service;

/**
 * Converts non-negative long values to and from URL-friendly Base62 text.
 *
 * The alphabet is part of the data format: changing its order would make all
 * previously generated codes decode to different values.
 */
public final class Base62Codec {
    public static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int RADIX = ALPHABET.length();

    private Base62Codec() {
    }

    public static String encode(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Base62 only accepts non-negative values");
        }
        if (value == 0) {
            return "0";
        }

        StringBuilder encoded = new StringBuilder();
        long remaining = value;
        while (remaining > 0) {
            int digit = (int) (remaining % RADIX);
            encoded.append(ALPHABET.charAt(digit));
            remaining /= RADIX;
        }
        return encoded.reverse().toString();
    }

    public static long decode(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Base62 text must not be blank");
        }

        long value = 0;
        for (int i = 0; i < text.length(); i++) {
            int digit = ALPHABET.indexOf(text.charAt(i));
            if (digit < 0) {
                throw new IllegalArgumentException("Invalid Base62 character: " + text.charAt(i));
            }
            // Check before multiplying so malformed input cannot overflow silently.
            if (value > (Long.MAX_VALUE - digit) / RADIX) {
                throw new IllegalArgumentException("Base62 value exceeds Long.MAX_VALUE");
            }
            value = value * RADIX + digit;
        }
        return value;
    }
}
