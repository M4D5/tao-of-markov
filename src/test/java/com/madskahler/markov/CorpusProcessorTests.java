package com.madskahler.markov;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static com.madskahler.markov.MarkovChainRepository.MarkovChainKey;
import static com.madskahler.markov.MarkovChainRepository.MarkovChainValue;

public class CorpusProcessorTests {
    private final WordRepository wordRepository = new WordRepository();

    @Test
    @SneakyThrows(IOException.class)
    public void testSentenceReader() {
        String str = "This is one sentence. Is this another? I hope so! Thanks, my friend.";

        try (InputStream is = new ByteArrayInputStream(str.getBytes()); Scanner s = new Scanner(is)) {
            Assert.assertEquals(
                    Lists.newArrayList(Lists.newArrayList("This", "is", "one", "sentence."),
                            Lists.newArrayList("Is", "this", "another?"),
                            Lists.newArrayList("I", "hope", "so!"),
                            Lists.newArrayList("Thanks,", "my", "friend.")),
                    SentenceScanner.getStream(s).collect(Collectors.toList()));
        }
    }

    @Test
    @SneakyThrows(IOException.class)
    public void testProcess() {
        MarkovChainRepository repository = new MarkovChainRepository(new Random(), wordRepository);
        CorpusProcessor processor = new CorpusProcessor(repository);

        String str = "This is one sentence. Is this another? I hope so!";

        try (InputStream is = new ByteArrayInputStream(str.getBytes())) {
            processor.process(is);
        }

        Map<MarkovChainKey, List<MarkovChainValue>> expected = new HashMap<>();
        expected.put(sk("This"), Lists.newArrayList(v("is")));
        expected.put(k("This", "is"), Lists.newArrayList(v("one")));
        expected.put(k("is", "one"), Lists.newArrayList(v("sentence.")));
        expected.put(k("one", "sentence."), Lists.newArrayList(ev()));
        expected.put(sk("Is"), Lists.newArrayList(v("this")));
        expected.put(k("Is", "this"), Lists.newArrayList(v("another?")));
        expected.put(k("this", "another?"), Lists.newArrayList(ev()));
        expected.put(sk("I"), Lists.newArrayList(v("hope")));
        expected.put(k("I", "hope"), Lists.newArrayList(v("so!")));
        expected.put(k("hope", "so!"), Lists.newArrayList(ev()));

        Assert.assertEquals(
                expected,
                repository.getMap());
    }

    private MarkovChainKey sk(String w) {
        return new MarkovChainKey(true, -1, wordRepository.getOrAdd(w));
    }

    private MarkovChainKey k(String w1, String w2) {
        return new MarkovChainKey(false, wordRepository.getOrAdd(w1), wordRepository.getOrAdd(w2));
    }

    private MarkovChainValue v(String w1) {
        return new MarkovChainValue(wordRepository.getOrAdd(w1));
    }

    private MarkovChainValue ev() {
        return MarkovChainValue.END_VALUE;
    }
}
