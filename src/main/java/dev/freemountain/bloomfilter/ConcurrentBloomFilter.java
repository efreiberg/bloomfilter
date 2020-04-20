package dev.freemountain.bloomfilter;

import java.util.List;
import java.util.concurrent.atomic.AtomicLongArray;

/**
 * Implements a SimpleBloomFilter using an underlying AtomicLongArray for memory visibility and CAS operations and the
 * default hashing strategy. Somewhat inspired by the Guava bloom filter implementation.
 * <p>
 * This class is intended to be thread-safe.
 */
public class ConcurrentBloomFilter implements SimpleBloomFilter {

    private static final int BITS_IN_LONG = 64;
    private final AtomicLongArray longArray;
    private final int MAX_CAPACITY;

    public ConcurrentBloomFilter(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException();
        }
        MAX_CAPACITY = (int) Math.ceil(capacity / (double) BITS_IN_LONG);
        longArray = new AtomicLongArray(MAX_CAPACITY);
    }

    @Override
    public void set(String value) {
        if (value == null) {
            throw new NullPointerException();
        }
        final List<Integer> hashValues = BloomFilterHasher.getHashValues(value);
        hashValues.stream().forEach(hashValue -> {
            boolean success;
            // Find array entry
            int address = (int) (hashValue / (double) BITS_IN_LONG) % MAX_CAPACITY;
            // Find bit
            int offset = hashValue % BITS_IN_LONG;
            long mask = 1 << offset;
            do {
                long oldValue = longArray.get(address);
                // Set bit
                long newValue = oldValue | mask;
                success = longArray.compareAndSet(address, oldValue, newValue);
            }
            while (!success);

        });
    }

    @Override
    public boolean likelyContains(String value) {
        if (value == null) {
            throw new NullPointerException();
        }
        final List<Integer> hashValues = BloomFilterHasher.getHashValues(value);
        return hashValues.stream().allMatch(hashValue -> {
            // Find array entry
            int address = (int) (hashValue / (double) BITS_IN_LONG) % MAX_CAPACITY;
            // Find bit
            int offset = hashValue % BITS_IN_LONG;
            // Isolate bit
            long mask = 1 << offset;
            return (longArray.get(address) & mask) != 0;
        });
    }
}
