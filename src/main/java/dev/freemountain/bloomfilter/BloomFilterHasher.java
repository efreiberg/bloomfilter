package dev.freemountain.bloomfilter;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import java.util.ArrayList;
import java.util.List;

class BloomFilterHasher {

    private static final List<HashFunction> hashFunctions = ImmutableList.copyOf(new HashFunction[]{
        Hashing.murmur3_128()
    });

    static List<Integer> getHashValues(String value) {
        if (value == null) {
            throw new NullPointerException();
        }
        List<Integer> hashValues = new ArrayList<>();
        hashFunctions.forEach(hashFunction -> {
            long hash = hashFunction.hashUnencodedChars(value.subSequence(0, value.length())).asLong();
            hashValues.add(Math.abs((int) hash));
            hashValues.add(Math.abs((int) (hash >>> 32)));
        });
        return hashValues;
    }
}
