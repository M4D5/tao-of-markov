package com.madskahler.markov;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.madskahler.markov.MarkovChainRepository.*;

@RequiredArgsConstructor
public class MarkovChainGenerator {
    private final MarkovChainRepository repository;

    public List<MarkovChainKey> generateChain() {
        Sequence startSequence = repository.getRandomStartSequence();
        MarkovChainKey currentKey = getNextKey(startSequence.getKey(), startSequence.getValue());
        MarkovChainValue currentValue;

        List<MarkovChainKey> keys = new ArrayList<>();

        do {
            keys.add(currentKey);
            currentValue = repository.getRandomSequence(currentKey);
            currentKey = getNextKey(currentKey, currentValue);
        } while (!currentValue.isEnd());

        return keys;
    }

    private MarkovChainKey getNextKey(MarkovChainKey key, MarkovChainValue value) {
        return new MarkovChainKey(false, key.getWord2(), value.getWord());
    }

    public String generateString() {
        List<String> words = new ArrayList<>();
        List<MarkovChainKey> keys = generateChain();

        for (int i = 0; i < keys.size(); i++) {
            MarkovChainKey key = keys.get(i);
            words.add(key.getWord1());

            if (i == keys.size() - 1) {
                words.add(key.getWord2());
            }
        }

        return String.join(" ", words);
    }
}
