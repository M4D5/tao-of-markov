package com.madskahler.markov;

import com.google.common.io.Resources;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Program {
    @SuppressWarnings("UnstableApiUsage")
    public static void main(String[] args) throws IOException {
        WordRepository wordRepository = new WordRepository();
        MarkovChainRepository repository = new MarkovChainRepository(new Random(), wordRepository);
        CorpusProcessor corpusProcessor = new CorpusProcessor(repository);
        MarkovChainGenerator chainGenerator = new MarkovChainGenerator(repository, wordRepository);
        String str = Resources.toString(Resources.getResource("tao.txt"), StandardCharsets.UTF_8);

        corpusProcessor.process(str);
        System.out.println(chainGenerator.generateString());
    }
}
