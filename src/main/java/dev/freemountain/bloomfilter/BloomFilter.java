package dev.freemountain.bloomfilter;

import java.util.BitSet;
import java.util.List;

/**
 * Implements a SimpleBloomFilter using an underlying BitSet and the default hashing strategy.
 * <p>
 * This is not a thread-safe class.
 */
public class BloomFilter implements SimpleBloomFilter {

    private final BitSet filter;
    private final int MAX_CAPACITY;

    public BloomFilter(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException();
        }
        MAX_CAPACITY = capacity;
        filter = new BitSet(MAX_CAPACITY);
    }

    public void set(String value) {
        if (value == null) {
            throw new NullPointerException();
        }
        final List<Integer> hashValues = BloomFilterHasher.getHashValues(value);
        hashValues.stream().forEach(hashValue -> filter.set(hashValue % MAX_CAPACITY));
    }

    public boolean likelyContains(String value) {
        if (value == null) {
            throw new NullPointerException();
        }
        final List<Integer> hashValues = BloomFilterHasher.getHashValues(value);
        return hashValues.stream().allMatch(hashValue -> filter.get(hashValue % MAX_CAPACITY));
    }
}
