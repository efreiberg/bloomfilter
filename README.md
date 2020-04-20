# bloomfilter
Inspired by [this blog post](http://codekata.com/kata/kata05-bloom-filters/), `bloomfilter` provides the implementation for a simple `SpellChecker`.

Also provided are implementations of a `SimpleBloomFilter` which are used to power the `SpellChecker` implementation.  They include: 
  * `BloomFilter` A simple implementation based on `BitSet`.
  * `ConcurrentBloomFilter` A thread-safe implementation based on `AtomicLongArray`.

See [test files](https://github.com/efreiberg/bloomfilter/blob/master/src/test/) for example usage and sample data.

To run tests locally, clone this repo and run `mvn clean verify`.  Intended for on `Java 8` or above.
