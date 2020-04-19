package dev.freemountain.bloomfilter;

import java.util.BitSet;
import java.util.List;

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
        final List<Integer> hashValues = computeHashValues(value);
        hashValues.stream().forEach(hashValue -> filter.set(hashValue % MAX_CAPACITY));
    }

    public boolean likelyContains(String value) {
        if (value == null) {
            throw new NullPointerException();
        }
        final List<Integer> hashValues = computeHashValues(value);
        return hashValues.stream().allMatch(hashValue -> filter.get(hashValue % MAX_CAPACITY));
    }

    private List<Integer> computeHashValues(String value) {
        return BloomFilterHasher.getHashValues(value);
    }
}
