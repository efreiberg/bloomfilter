package dev.freemountain.bloomfilter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.net.URL;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class BloomFilterTest {

    private static String[] words;

    @BeforeClass
    public static void beforeAll() throws Exception {
        // load words into memory
        URL url = Resources.getResource("listOfWords.csv");
        String result = Resources.toString(url, Charsets.UTF_8);
        words = result.split(",");
    }

    public void setup(BloomFilter bloomFilter) {
        for (String word : words) {
            bloomFilter.set(word);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidConstruction() {
        new BloomFilter(-1);
        fail();
    }

    @Test(expected = NullPointerException.class)
    public void setNullThrows() {
        BloomFilter bloomFilter = new BloomFilter(100);
        bloomFilter.set(null);
        fail();
    }

    @Test(expected = NullPointerException.class)
    public void getNullThrows() {
        BloomFilter bloomFilter = new BloomFilter(100);
        bloomFilter.likelyContains(null);
        fail();
    }

    @Test
    public void containsAll() {
        BloomFilter bloomFilter = new BloomFilter(1_000_000);
        setup(bloomFilter);
        for (String word : words) {
            assertTrue(bloomFilter.likelyContains(word));
        }
    }

    @Test
    public void tolerableCollisionRate() {
        BloomFilter bloomFilter = new BloomFilter(1_000_000);
        setup(bloomFilter);
        double collisions = 0;
        for (String word : words) {
            if (bloomFilter.likelyContains("a" + word + "z")) {
                collisions++;
            }
        }
        assertTrue((words.length - collisions) / words.length > 0.99);
    }

    @Test
    public void allCollisions() {
        BloomFilter bloomFilter = new BloomFilter(1);
        setup(bloomFilter);
        assertTrue(bloomFilter.likelyContains("foobarbaz"));
    }
}