package com.madskahler.markov;

import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

@RequiredArgsConstructor
public class CorpusProcessor {
    private final MarkovChainRepository repository;

    public void process(String s) throws IOException {
        try (InputStream is = new ByteArrayInputStream(s.getBytes())) {
            process(is);
        }
    }

    public void process(InputStream inputStream) {
        try(Scanner s = new Scanner(inputStream)) {
            SentenceScanner.getStream(s).forEach(this::addSentenceToRepository);
        }

        repository.optimize();
    }

    private void addSentenceToRepository(List<String> sentence) {
        if (sentence.size() <= 1) {
            return;
        }

        repository.addStartSequence(sentence.get(0), sentence.get(1));

        for (int i = 0; i < sentence.size() - 2; i++) {
            repository.addSequence(sentence.get(i), sentence.get(i + 1), sentence.get(i + 2));
        }

        repository.addEndSequence(sentence.get(sentence.size() - 2), sentence.get(sentence.size() - 1));
    }
}
