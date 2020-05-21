package com.madskahler.markov;

import com.google.common.base.Stopwatch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Program {
    public static void main(String[] args) throws IOException {
        WordRepository wordRepository = new WordRepository();
        MarkovChainRepository repository = new MarkovChainRepository(new Random(), wordRepository);
        CorpusProcessor corpusProcessor = new CorpusProcessor(repository);
        MarkovChainGenerator chainGenerator = new MarkovChainGenerator(repository, wordRepository);

        Path corpusPath = Paths.get("D:/tao.txt");
        String str = String.join("\n", Files.readAllLines(corpusPath));

        int iterations = 1;

        Stopwatch sw = Stopwatch.createStarted();
        for (int i = 0; i < iterations; i++) {
            corpusProcessor.process(str);
        }
        sw.stop();

        System.out.println(String.format("Processed corpus %d times, average time: %.2fms.", iterations, sw.elapsed(TimeUnit.MICROSECONDS) / (double) (iterations * 1000)));

        iterations = 10000000;

        sw.reset();
        sw.start();
        for (int i = 0; i < iterations; i++) {
            chainGenerator.generateString();
        }
        sw.stop();

        System.out.println(String.format("Generated %d sentences, average time: %.2fms.", iterations, sw.elapsed(TimeUnit.MICROSECONDS) / (double) (iterations * 1000)));

        System.out.println(chainGenerator.generateString());
    }


}
