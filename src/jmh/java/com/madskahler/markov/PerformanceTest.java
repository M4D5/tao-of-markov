package com.madskahler.markov;

import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 2)
@Fork(2)
@Measurement(iterations = 5)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class PerformanceTest {
    private CorpusProcessor processor;
    private MarkovChainRepository repository;
    private MarkovChainGenerator generator;

    @Setup
    public void setup() throws IOException {
        repository = new MarkovChainRepository(new Random());
        processor = new CorpusProcessor(repository);

        Path corpusPath = Paths.get("D:/corpus/t9TextCorpus.txt");

        try (InputStream fis = Files.newInputStream(corpusPath)) {
            processor.process(fis);
        }

        generator = new MarkovChainGenerator(repository);
    }

    @Benchmark
    public void testProcessor() {
        generator.generateString();
    }
}
