package com.madskahler.markov;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class MarkovChainGeneratorTests {
    @Test
    @SneakyThrows(IOException.class)
    public void testChainGeneration() {
        MarkovChainRepository repository = new MarkovChainRepository(new Random());
        MarkovChainGenerator chainGenerator = new MarkovChainGenerator(repository);
        CorpusProcessor processor = new CorpusProcessor(repository);

        String sentence = "This is a sentence.";

        try (InputStream is = new ByteArrayInputStream(sentence.getBytes())) {
            processor.process(is);
        }

        Assert.assertEquals(Lists.newArrayList(k("This", "is"), k("is", "a"), k("a", "sentence.")), chainGenerator.generateChain());
    }

    @Test
    @SneakyThrows(IOException.class)
    public void testStringGeneration() {
        MarkovChainRepository repository = new MarkovChainRepository(new Random());
        MarkovChainGenerator chainGenerator = new MarkovChainGenerator(repository);
        CorpusProcessor processor = new CorpusProcessor(repository);

        String sentence = "This is a sentence.";

        try (InputStream is = new ByteArrayInputStream(sentence.getBytes())) {
            processor.process(is);
        }

        Assert.assertEquals("This is a sentence.", chainGenerator.generateString());
    }

    private MarkovChainRepository.MarkovChainKey sk(String w) {
        return new MarkovChainRepository.MarkovChainKey(true, null, w);
    }

    private MarkovChainRepository.MarkovChainKey k(String w1, String w2) {
        return new MarkovChainRepository.MarkovChainKey(false, w1, w2);
    }
}