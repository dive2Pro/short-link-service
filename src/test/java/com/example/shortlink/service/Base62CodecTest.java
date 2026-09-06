package com.example.shortlink.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class Base62CodecTest {
    @Test
    void encodeAndDecodeAreInverseOperations() {
        long[] values = {0, 1, 61, 62, 63, 125, Long.MAX_VALUE};

        for (long value : values) {
            assertEquals(value, Base62Codec.decode(Base62Codec.encode(value)));
        }
    }

    @Test
    void decodeRejectsInvalidCharacters() {
        assertThrows(IllegalArgumentException.class, () -> Base62Codec.decode("abc!"));
    }

    @Test
    void encodeRejectsNegativeValues() {
        assertThrows(IllegalArgumentException.class, () -> Base62Codec.encode(-1));
    }
}
