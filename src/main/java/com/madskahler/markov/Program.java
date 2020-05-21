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
        MarkovChainRepository repository = new MarkovChainRepository(new Random());
        CorpusProcessor corpusProcessor = new CorpusProcessor(repository);
        MarkovChainGenerator chainGenerator = new MarkovChainGenerator(repository);

        Path corpusPath = Paths.get("D:/corpus/t9TextCorpus.txt");


        String str = String.join("\n", Files.readAllLines(corpusPath));

        int iterations = 4;

        Stopwatch sw = Stopwatch.createStarted();
        for (int i = 0; i < iterations; i++) {
            corpusProcessor.process(str);
        }
        sw.stop();

        System.out.println(String.format("Processed corpus %d times, average time: %sms.", iterations, sw.elapsed(TimeUnit.MILLISECONDS) / iterations));

//        iterations = 10000;
//
//        sw.reset();
//        sw.start();
//        for (int i = 0; i < iterations; i++) {
//            chainGenerator.generateString();
//        }
//        sw.stop();
//
//        System.out.println(String.format("Generated %d sentences, average time: %sms.", iterations, sw.elapsed(TimeUnit.MILLISECONDS) / iterations));
//
//        System.out.println(chainGenerator.generateString());
    }
}
