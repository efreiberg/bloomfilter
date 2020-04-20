package dev.freemountain.bloomfilter;

public interface SimpleBloomFilter {

    void set(String value);

    boolean likelyContains(String value);
}
