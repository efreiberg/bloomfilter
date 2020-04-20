package dev.freemountain.spellchecker;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import dev.freemountain.bloomfilter.BloomFilter;
import java.net.URL;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SpellCheckerTest {

    private static String[] dictionaryWords;
    private SpellChecker spellChecker;

    @BeforeClass
    public static void beforeAll() throws Exception {
        // load words into memory
        URL url = Resources.getResource("listOfWords.csv");
        String result = Resources.toString(url, Charsets.UTF_8);
        dictionaryWords = result.split(",");
    }

    @Before
    public void beforeEach() {
        spellChecker = new SpellChecker(new BloomFilter(1_000_000_000), dictionaryWords);
    }

    @Test(expected = NullPointerException.class)
    public void setNullThrows() {
        spellChecker.addNewTerm(null);
        fail();
    }

    @Test(expected = NullPointerException.class)
    public void getNullThrows() {
        spellChecker.isCorrectSpelling(null);
        fail();
    }

    @Test
    public void testMisspelling() {
        assertFalse(spellChecker.isCorrectSpelling("freind"));
    }

    @Test
    public void testCorrectSpelling() {
        assertTrue(spellChecker.isCorrectSpelling("friend"));
    }

    @Test
    public void testDictionaryUpdate() {
        assertFalse(spellChecker.isCorrectSpelling("FooBarBaz!"));
        spellChecker.addNewTerm("FooBarBaz!");
        assertTrue(spellChecker.isCorrectSpelling("FooBarBaz!"));
    }

}