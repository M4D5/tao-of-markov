package com.madskahler.markov;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public class MarkovChainRepository {
    private final Random random;
    private final WordRepository wordRepository;

    private final Map<MarkovChainKey, List<MarkovChainValue>> map = new HashMap<>();
    private final Set<MarkovChainKey> startKeys = new LinkedHashSet<>();

    public void addStartSequence(String word1, String word2) {
        MarkovChainKey key = new MarkovChainKey(true, -1, wordRepository.getOrAdd(word1));
        MarkovChainValue value = new MarkovChainValue(false, wordRepository.getOrAdd(word2));
        createOrUpdateEntry(key, value);
        startKeys.add(key);
    }

    public void addSequence(String word1, String word2, String word3) {
        MarkovChainKey key = new MarkovChainKey(false, wordRepository.getOrAdd(word1), wordRepository.getOrAdd(word2));
        MarkovChainValue value = new MarkovChainValue(false, wordRepository.getOrAdd(word3));
        createOrUpdateEntry(key, value);
    }

    public void addEndSequence(String word1, String word2) {
        MarkovChainKey key = new MarkovChainKey(false, wordRepository.getOrAdd(word1), wordRepository.getOrAdd(word2));
        createOrUpdateEntry(key, MarkovChainValue.END_VALUE);
    }

    public Sequence getRandomStartSequence() {
        MarkovChainKey key = getRandomElement(startKeys);
        List<MarkovChainValue> values = map.get(key);
        MarkovChainValue value = values.get(random.nextInt(values.size()));
        return new Sequence(key, value);
    }

    /**
     * Removes the duplicate sequences in the repository
     */
    public void optimize() {
        for(List<MarkovChainValue> values : map.values()) {
            Set<MarkovChainValue> valuesSet = new HashSet<>();

            List<Integer> indexes = new ArrayList<>();

            for (int i = 0; i < values.size(); i++) {
                MarkovChainValue value = values.get(i);
                if (valuesSet.contains(value)) {
                    indexes.add(i);
                } else {
                    valuesSet.add(value);
                }
            }

            for (int i = indexes.size() - 1; i >= 0; i--) {
                values.remove((int) indexes.get(i));
            }
        }
    }

    public MarkovChainValue getRandomSequence(MarkovChainKey key) {
        List<MarkovChainValue> values = map.get(key);

        if (values == null) {
            throw new IllegalStateException(String.format("No sequence could be found matching the words '%s', '%s'", key.getWord1(), key.getWord2()));
        }

        return values.get(random.nextInt(values.size()));
    }

    private <T> T getRandomElement(Collection<T> values) {
        int n = random.nextInt(values.size());

        Iterator<T> iterator = values.iterator();

        for (int i = 0; i < n; i++) {
            iterator.next();
        }

        return iterator.next();
    }

    private void createOrUpdateEntry(MarkovChainKey key, MarkovChainValue value) {
        if (!map.containsKey(key)) {
            List<MarkovChainValue> set = new ArrayList<>();
            set.add(value);
            map.put(key, set);
        } else {
            map.get(key).add(value);
        }
    }

    public Map<MarkovChainKey, List<MarkovChainValue>> getMap() {
        Map<MarkovChainKey, List<MarkovChainValue>> map = new HashMap<>(this.map);
        map.replaceAll((k, v) -> new ArrayList<>(v));
        return map;
    }

    @RequiredArgsConstructor
    @Getter
    public static class Sequence {
        private final MarkovChainKey key;
        private final MarkovChainValue value;
    }

    @EqualsAndHashCode
    @Getter
    public static class MarkovChainKey {
        private final boolean beginning;
        private final int word1;
        private final int word2;

        public MarkovChainKey(boolean beginning, int word1, int word2) {
            if (beginning && word1 != -1) {
                throw new IllegalStateException("A beginning sequence may not have a first word");
            }

            this.beginning = beginning;
            this.word1 = word1;
            this.word2 = word2;
        }

        @Override
        public String toString() {
            return (word1 == -1 ? "<start>" : word1) + " " + word2;
        }
    }

    @EqualsAndHashCode
    @Getter
    public static class MarkovChainValue {
        public static final MarkovChainValue END_VALUE = new MarkovChainValue(true, -1);

        private final boolean end;
        private final int word;

        private MarkovChainValue(boolean end, int word) {
            if (end && word != -1) {
                throw new IllegalStateException("An end value may not have a word");
            }

            this.end = end;
            this.word = word;
        }

        public MarkovChainValue(int word) {
            this.end = false;
            this.word = word;
        }

        @Override
        public String toString() {
            return word == -1 ? "<end>" : Integer.toString(word);
        }
    }
}
