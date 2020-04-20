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
    private static final int LONG_OFFSET_BITS = 6;
    private static final int LONG_OFFSET_MASK = BITS_IN_LONG - 1;
    private final int MAX_NUM_BITS;
    private final AtomicLongArray longArray;

    public ConcurrentBloomFilter(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException();
        }
        int numLongs = (int) Math.ceil(capacity / (double) BITS_IN_LONG);
        MAX_NUM_BITS = numLongs * BITS_IN_LONG;
        longArray = new AtomicLongArray(numLongs);
    }

    @Override
    public void set(String value) {
        if (value == null) {
            throw new NullPointerException();
        }
        final List<Integer> hashValues = BloomFilterHasher.getHashValues(value);
        hashValues.stream().forEach(hashValue -> {
            boolean success;
            int hashMod = hashValue % MAX_NUM_BITS;
            // Find array entry
            int address = hashMod >>> LONG_OFFSET_BITS;
            // Find bit
            int offset = hashMod & LONG_OFFSET_MASK;
            // Isolate bit
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
            int hashMod = hashValue % MAX_NUM_BITS;
            // Find array entry
            int address = hashMod >>> LONG_OFFSET_BITS;
            // Find bit
            int offset = hashMod & LONG_OFFSET_MASK;
            // Isolate bit
            long mask = 1 << offset;
            return (longArray.get(address) & mask) != 0;
        });
    }
}
