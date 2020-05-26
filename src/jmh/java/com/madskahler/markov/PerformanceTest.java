package com.madskahler.markov;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 2)
@Fork(2)
@Measurement(iterations = 2)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class PerformanceTest {
    private WordRepository wordRepository;
    private CorpusProcessor processor;
    private MarkovChainRepository repository;
    private MarkovChainGenerator generator;

    private String text;

    @Setup
    public void setup() throws IOException {
        wordRepository = new WordRepository();
        repository = new MarkovChainRepository(new Random(), wordRepository);
        processor = new CorpusProcessor(repository);

        Path corpusPath = Paths.get("D:/tao.txt");
        text = String.join("\n", Files.readAllLines(corpusPath));
        processor.process(text);
        generator = new MarkovChainGenerator(repository, wordRepository);
    }

    @Benchmark
    public void testProcessor() throws IOException {
        processor.process(text);
    }

    @Benchmark
    public void testGenerator(Blackhole blackhole) {
        blackhole.consume(generator.generateString());
    }
}
