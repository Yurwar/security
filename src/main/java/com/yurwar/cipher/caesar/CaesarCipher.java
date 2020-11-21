package com.yurwar.cipher.caesar;

import com.yurwar.SecurityUtils;
import com.yurwar.cipher.BruteforceDecoder;
import com.yurwar.cipher.Encoder;

import java.util.*;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.lang.Math.pow;

public class CaesarCipher implements BruteforceDecoder, Encoder {
    private static final int BYTES_IN_KEY = 1;
    private static final int BITS_IN_BYTE = 8;

    private static final String KEY_FORMAT_EXCEPTION = "INVALID KEY. Key should be in range from 0 to 255";

    @Override
    public String decode(String textToDecode, String key) {
        return encode(textToDecode, key);
    }


    @Override
    public String encode(String textToEncode, String key) {
        int parsedKey = parseKey(key);
        return textToEncode.chars().mapToObj(c -> (char) (c ^ parsedKey))
                .collect(Collector.of(StringBuilder::new, StringBuilder::append, StringBuilder::append, StringBuilder::toString));
    }

    private int parseKey(String key) {
        try {
            return Integer.parseInt(key);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(KEY_FORMAT_EXCEPTION, e);
        }
    }

    @Override
    public String tryDecode(String text) {
        String key = findKey(text);
        return decode(text, key);
    }

    private String findKey(String text) {
        Map<Integer, Double> chiSquaredForKeys = new LinkedHashMap<>();
        for (int key = 0; key < pow(2, BYTES_IN_KEY * BITS_IN_BYTE); key++) {
            String decodedText = decode(text, String.valueOf(key));
            double chiSquared = calculateChiSquared(decodedText);
            chiSquaredForKeys.put(key, chiSquared);
        }
        Comparator<Map.Entry<Integer, Double>> entryComparator = Comparator
                .comparingDouble(Map.Entry::getValue);

        Map.Entry<Integer, Double> minChiSquaredForKey = chiSquaredForKeys
                .entrySet()
                .stream()
                .min(entryComparator)
                .orElseThrow();

        return minChiSquaredForKey.getKey().toString();
    }

    private double calculateChiSquared(String decodedText) {
        Map<Character, Integer> textCharactersOccurrences = new TreeMap<>();

        List<Character> decodedTextCharacters = decodedText
                .chars().mapToObj(c -> (char) c).collect(Collectors.toList());
        List<Character> distinctChars = decodedTextCharacters.stream().distinct().collect(Collectors.toList());

        for (Character character : distinctChars) {
            int charOccurrences = Collections.frequency(decodedTextCharacters, character);
            textCharactersOccurrences.put(character, charOccurrences);
        }

        return Arrays.stream(SecurityUtils.ENGLISH_ALPHABET.split(""))
                .map(letter -> letter.charAt(0))
                .mapToDouble(letter -> {
                    int textLength = decodedText.length();
                    double letterFrequency = SecurityUtils.ENGLISH_LETTERS_FREQUENCY.get(letter);
                    double actualOccurrences = Optional.ofNullable(textCharactersOccurrences.get(letter)).orElse(0);
                    double expectedOccurrences = letterFrequency * textLength;

                    return (pow((actualOccurrences - expectedOccurrences), 2)) / expectedOccurrences;
                }).sum();
    }
}
