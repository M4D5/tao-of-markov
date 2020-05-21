package com.madskahler.markov;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MarkovChainRepository {
    private final Random random;
    private final Map<MarkovChainKey, Set<MarkovChainValue>> map = new HashMap<>();

    public void addStartSequence(String word1, String word2) {
        MarkovChainKey key = new MarkovChainKey(true, null, word1);
        MarkovChainValue value = new MarkovChainValue(false, word2);
        createOrUpdateEntry(key, value);
    }

    public void addSequence(String word1, String word2, String word3) {
        MarkovChainKey key = new MarkovChainKey(false, word1, word2);
        MarkovChainValue value = new MarkovChainValue(false, word3);
        createOrUpdateEntry(key, value);
    }

    public void addEndSequence(String word1, String word2) {
        MarkovChainKey key = new MarkovChainKey(false, word1, word2);
        MarkovChainValue value = new MarkovChainValue(true, null);
        createOrUpdateEntry(key, value);
    }

    public Sequence getRandomStartSequence() {
        List<Map.Entry<MarkovChainKey, Set<MarkovChainValue>>> startSeqs = map.entrySet()
                .stream()
                .filter(kv -> kv.getKey().beginning)
                .collect(Collectors.toList());

        Map.Entry<MarkovChainKey, Set<MarkovChainValue>> sequences = startSeqs.get(random.nextInt(startSeqs.size()));
        return new Sequence(sequences.getKey(), getRandomElement(sequences.getValue()));
    }

    public MarkovChainValue getRandomSequence(MarkovChainKey key) {
        Set<MarkovChainValue> values = map.get(key);

        if (values == null) {
            throw new IllegalStateException(String.format("No sequence could be found matching the words '%s', '%s'", key.getWord1(), key.getWord2()));
        }

        return getRandomElement(values);
    }

    private MarkovChainValue getRandomElement(Set<MarkovChainValue> values) {
        int n = random.nextInt(values.size());

        Iterator<MarkovChainValue> iterator = values.iterator();

        for (int i = 0; i < n; i++) {
            iterator.next();
        }

        return iterator.next();
    }

    private void createOrUpdateEntry(MarkovChainKey key, MarkovChainValue value) {
        if (!map.containsKey(key)) {
            Set<MarkovChainValue> set = new LinkedHashSet<>();
            set.add(value);
            map.put(key, set);
        } else {
            map.get(key).add(value);
        }
    }

    public Map<MarkovChainKey, Set<MarkovChainValue>> getMap() {
        Map<MarkovChainKey, Set<MarkovChainValue>> map = new HashMap<>(this.map);
        map.replaceAll((k, v) -> new HashSet<>(v));
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
        private final String word1;
        private final String word2;

        public MarkovChainKey(boolean beginning, String word1, String word2) {
            if (beginning && word1 != null) {
                throw new IllegalStateException("A beginning sequence may not have a first word");
            }

            this.beginning = beginning;
            this.word1 = word1;
            this.word2 = word2;
        }

        @Override
        public String toString() {
            return (word1 == null ? "<start>" : word1) + " " + word2;
        }
    }

    @EqualsAndHashCode
    @Getter
    public static class MarkovChainValue {
        private final boolean end;
        private final String word;

        public MarkovChainValue(boolean end, String word) {
            if (end && word != null) {
                throw new IllegalStateException("An end value may not have a word");
            }

            this.end = end;
            this.word = word;
        }

        @Override
        public String toString() {
            return word == null ? "<end>" : word;
        }
    }
}
