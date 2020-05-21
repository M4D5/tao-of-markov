package com.madskahler.markov;

import java.util.HashMap;
import java.util.Map;

public class WordRepository {
    private int currentId = 0;
    private final Map<Integer, String> map = new HashMap<>();
    private final Map<String, Integer> existingWords = new HashMap<>();

    public int getOrAdd(String word) {
        if (existingWords.containsKey(word)) {
            return existingWords.get(word);
        }

        int id = currentId++;
        existingWords.put(word, id);
        map.put(id, word);
        return id;
    }

    public String getById(int id) {
        return map.get(id);
    }
}
