package dev.freemountain.bloomfilter;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class SimpleBloomFilterTest {

    private static String[] dictionaryWords;
    private static String[] nonsensicalWords;
    private Function<Integer, SimpleBloomFilter> bloomFilterSupplier;

    public SimpleBloomFilterTest(Function<Integer, SimpleBloomFilter> filterSupplier) {
        bloomFilterSupplier = filterSupplier;
    }

    @Parameterized.Parameters
    public static List<Function<Integer, SimpleBloomFilter>> instancesToTest() {
        return Arrays.asList(
            BloomFilter::new,
            ConcurrentBloomFilter::new
        );
    }


    @BeforeClass
    public static void beforeAll() throws Exception {
        // load test words into memory
        URL url = Resources.getResource("listOfWords.csv");
        String result = Resources.toString(url, Charsets.UTF_8);
        dictionaryWords = result.split(",");

        url = Resources.getResource("randomWords.csv");
        result = Resources.toString(url, Charsets.UTF_8);
        nonsensicalWords = result.split(",");
    }

    public void setup(SimpleBloomFilter bloomFilter) {
        for (String word : dictionaryWords) {
            bloomFilter.set(word);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidConstruction() {
        bloomFilterSupplier.apply(-1);
        fail();
    }

    @Test(expected = NullPointerException.class)
    public void setNullThrows() {
        SimpleBloomFilter bloomFilter = bloomFilterSupplier.apply(100);
        bloomFilter.set(null);
        fail();
    }

    @Test(expected = NullPointerException.class)
    public void getNullThrows() {
        SimpleBloomFilter bloomFilter = bloomFilterSupplier.apply(100);
        bloomFilter.likelyContains(null);
        fail();
    }

    @Test
    public void setAndGet() {
        SimpleBloomFilter bloomFilter = bloomFilterSupplier.apply(100);
        bloomFilter.set("foobarbaz");
        assertTrue(bloomFilter.likelyContains("foobarbaz"));
    }

    @Test
    public void allCollisions() {
        SimpleBloomFilter bloomFilter = bloomFilterSupplier.apply(1);
        setup(bloomFilter);
        assertTrue(bloomFilter.likelyContains("foobarbaz"));
    }

    @Test
    public void containsAll() {
        SimpleBloomFilter bloomFilter = bloomFilterSupplier.apply(1_000_000_000);
        setup(bloomFilter);
        for (String word : dictionaryWords) {
            assertTrue(bloomFilter.likelyContains(word));
        }
    }

    @Test
    public void tolerableCollisionRate() {
        SimpleBloomFilter bloomFilter = bloomFilterSupplier.apply(1_000_000);
        setup(bloomFilter);
        double collisions = 0;
        for (String word : nonsensicalWords) {
            if (bloomFilter.likelyContains(word)) {
                collisions++;
            }
        }
        assertTrue((dictionaryWords.length - collisions) / dictionaryWords.length > 0.95);
    }
}