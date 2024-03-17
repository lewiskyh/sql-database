package edu.uob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tokeniser {

    private String query;

    private Integer currentTokenIndex;

    private ArrayList<String> tokens;

    private final String[] specialCharacters = {"(", ")", ",", ";"};

    public Tokeniser(String query) {
        this.query = query;
        //The index 0 is always the command keyword
        this.currentTokenIndex = 0;
        this.tokens = new ArrayList<>();
    }

    public String getTokenByIndex(Integer index) {
        return tokens.get(index);
    }

    public String getQuery () {
        return this.query;
    }

    public ArrayList<String> getAllTokens() {
        return tokens;
    }

    public Integer getTokenSize(){return tokens.size();}

    public void incrementTokenIndex() { this.currentTokenIndex++; }

    public void setCurrentTokenIndex(Integer index) { this.currentTokenIndex = index; }

    //code from Simon
    public void preprocessQuery() {

        query = query.trim();
        String[] fragments = query.split("'");
        for (int i = 0; i < fragments.length; i++) {
            if (i % 2 != 0) tokens.add("'" + fragments[i] + "'");
            else {
                String[] nextBatchOfTokens = tokenise(fragments[i]);
                tokens.addAll(Arrays.asList(nextBatchOfTokens));
            }
        }
    }

    private String[] tokenise(String fragment) {
        for (int i = 0; i < specialCharacters.length; i++) {
            fragment = fragment.replace(specialCharacters[i], " " + specialCharacters[i] + " ");
        }
        while (fragment.contains("  ")) fragment = fragment.replaceAll("  ", " ");

        fragment = fragment.trim();

        return fragment.split(" ");
    }

        /**
        String query = this.query.trim();
        // Split the query into fragments on singlespace
        String[] fragments = query.split("\\s+");
        for (int i = 0; i < fragments.length; i++) {
            System.out.println(fragments[i]);
            processFragment(fragments[i]);
        }
    }
    private void processFragment(String fragment) {
        // Check string literals and numeric literals
        if (fragment.startsWith("'") && fragment.endsWith("'")) {
            // Add string literals directly
            tokens.add(fragment);
        } else if (fragment.matches("-?\\d+(\\.\\d+)?")) {
            tokens.add(fragment); //append numeric literals
        } else {
            for (String specialCharacter : specialCharacters) {
                fragment = fragment.replace(specialCharacter, " " + specialCharacter + " ");
                }
            }
        // Split into tokens taking into account special characters
        String [] fragmentTokens = fragment.split("\\s+");
        for (String token : fragmentTokens) {
            if (!token.isEmpty()) {
                tokens.add(token);
            }
        }
    }*/
}
