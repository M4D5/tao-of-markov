package com.madskahler.markov;

import lombok.RequiredArgsConstructor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class CorpusProcessor {
    private static final Pattern END_WORD_PATTERN = Pattern.compile(".*[.!?]$");
    private final MarkovChainRepository repository;

    public void process(InputStream is) {
        process(readSentences(is));
    }

    public void process(String s) {
        process(readSentences(s));
    }

    public void process(List<List<String>> sentences) {
        for (List<String> sentence : sentences) {
            if(sentence.size() <= 1) {
                continue;
            }

            repository.addStartSequence(sentence.get(0), sentence.get(1));

            for (int i = 0; i < sentence.size() - 2; i++) {
                repository.addSequence(sentence.get(i), sentence.get(i + 1), sentence.get(i + 2));
            }

            repository.addEndSequence(sentence.get(sentence.size() - 2), sentence.get(sentence.size() - 1));
        }
    }


    public List<List<String>> readSentences(String s) {
        String[] words = s.split("[ \\n]");

        List<List<String>> sentences = new ArrayList<>();
        List<String> currentSentence = new ArrayList<>();

        for(String w : words) {
            if(w.isEmpty()) {
                continue;
            }

            String word = processWord(w);
            currentSentence.add(word);

            if (END_WORD_PATTERN.matcher(word).matches()) {
                sentences.add(currentSentence);
                currentSentence = new ArrayList<>();
            }
        }

        return sentences;
    }

    public List<List<String>> readSentences(InputStream is) {
        List<List<String>> sentences = new ArrayList<>();

        try (Scanner s = new Scanner(is)) {
            List<String> words = new ArrayList<>();

            while (s.hasNext()) {
                String word = processWord(s.next());
                words.add(word);

                if (END_WORD_PATTERN.matcher(word).matches()) {
                    sentences.add(words);
                    words = new ArrayList<>();
                }
            }
        }

        return sentences;
    }

    private String processWord(String s) {
        return remove(s.trim(), "(", ")", "\"", "[", "]");
    }

    private String remove(String s, String ...chars) {
        String result = s;

        for(String c : chars) {
            result = result.replace(c, "");
        }

        return result;
    }
}
