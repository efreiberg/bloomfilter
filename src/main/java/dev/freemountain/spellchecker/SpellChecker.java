package dev.freemountain.spellchecker;

import dev.freemountain.bloomfilter.SimpleBloomFilter;

/**
 * A bloom filter based spell checker.  It is bring-your-own SimpleBloomFilter and bring-your-own dictionary words.
 */
public class SpellChecker {

    private final SimpleBloomFilter filter;

    public SpellChecker(SimpleBloomFilter bloomFilter, String[] dictionaryWords) {
        if (bloomFilter == null || dictionaryWords == null) {
            throw new NullPointerException();
        }
        filter = bloomFilter;
        for (String word : dictionaryWords) {
            filter.set(word);
        }
    }

    public boolean isCorrectSpelling(String word) {
        if (word == null) {
            throw new NullPointerException();
        }
        return filter.likelyContains(word);
    }

    public void addNewTerm(String word) {
        if (word == null) {
            throw new NullPointerException();
        }
        filter.set(word);
    }
}
