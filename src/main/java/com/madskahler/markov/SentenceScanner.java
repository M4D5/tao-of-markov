package com.madskahler.markov;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SentenceScanner {
    private static final Pattern END_WORD_PATTERN = Pattern.compile(".*[.!?]$");

    private static String processWord(String s) {
        return remove(s.trim(), "(", ")", "\"", "[", "]");
    }

    private static String remove(String s, String... chars) {
        String result = s;

        for (String c : chars) {
            result = result.replace(c, "");
        }

        return result;
    }

    public static Stream<List<String>> getStream(Scanner scanner) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(new SentenceIterator(scanner), Spliterator.IMMUTABLE), false);
    }

    @RequiredArgsConstructor
    private static class SentenceIterator implements Iterator<List<String>> {
        private final Scanner scanner;

        private boolean initialCheck = false;
        private List<String> nextSentenceBuffer;

        private void updateNextValue() {
            List<String> sentence = new ArrayList<>();

            while (scanner.hasNext()) {
                String word = processWord(scanner.next());
                sentence.add(word);

                if (END_WORD_PATTERN.matcher(word).matches()) {
                    nextSentenceBuffer = sentence;
                    return;
                }
            }

            nextSentenceBuffer = null;
        }

        @Override
        public boolean hasNext() {
            if (!initialCheck) {
                updateNextValue();
                initialCheck = true;
            }

            return nextSentenceBuffer != null;
        }

        @Override
        public List<String> next() {
            if (nextSentenceBuffer == null) {
                throw new NoSuchElementException();
            }

            List<String> sentence = nextSentenceBuffer;
            updateNextValue();
            return sentence;
        }
    }
}
