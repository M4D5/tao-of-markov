package com.madskahler.markov;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.madskahler.markov.MarkovChainRepository.MarkovChainKey;
import static com.madskahler.markov.MarkovChainRepository.MarkovChainValue;

public class CorpusProcessorTests {

    @Test
    @SneakyThrows(IOException.class)
    public void testSentenceReader() {
        MarkovChainRepository repository = new MarkovChainRepository(new Random());
        CorpusProcessor processor = new CorpusProcessor(repository);

        String str = "This is one sentence. Is this another? I hope so! Thanks, my friend.";

        try (InputStream is = new ByteArrayInputStream(str.getBytes())) {
            Assert.assertEquals(
                    Lists.newArrayList(Lists.newArrayList("This", "is", "one", "sentence."),
                            Lists.newArrayList("Is", "this", "another?"),
                            Lists.newArrayList("I", "hope", "so!"),
                            Lists.newArrayList("Thanks,", "my", "friend.")),
                    processor.readSentences(is));
        }
    }

    @Test
    @SneakyThrows(IOException.class)
    public void testProcess() {
        MarkovChainRepository repository = new MarkovChainRepository(new Random());
        CorpusProcessor processor = new CorpusProcessor(repository);

        String str = "This is one sentence. Is this another? I hope so!";

        try (InputStream is = new ByteArrayInputStream(str.getBytes())) {
            processor.process(is);
        }

        Map<MarkovChainKey, Set<MarkovChainValue>> expected = new HashMap<>();
        expected.put(sk("This"), Sets.newHashSet(v("is")));
        expected.put(k("This", "is"), Sets.newHashSet(v("one")));
        expected.put(k("is", "one"), Sets.newHashSet(v("sentence.")));
        expected.put(k("one", "sentence."), Sets.newHashSet(ev()));
        expected.put(sk("Is"), Sets.newHashSet(v("this")));
        expected.put(k("Is", "this"), Sets.newHashSet(v("another?")));
        expected.put(k("this", "another?"), Sets.newHashSet(ev()));
        expected.put(sk("I"), Sets.newHashSet(v("hope")));
        expected.put(k("I", "hope"), Sets.newHashSet(v("so!")));
        expected.put(k("hope", "so!"), Sets.newHashSet(ev()));

        Assert.assertEquals(
                expected,
                repository.getMap());
    }

    private MarkovChainKey sk(String w) {
        return new MarkovChainKey(true, null, w);
    }

    private MarkovChainKey k(String w1, String w2) {
        return new MarkovChainKey(false, w1, w2);
    }

    private MarkovChainValue v(String w1) {
        return new MarkovChainValue(false, w1);
    }

    private MarkovChainValue ev() {
        return new MarkovChainValue(true, null);
    }
}
