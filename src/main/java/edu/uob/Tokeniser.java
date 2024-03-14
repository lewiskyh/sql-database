package edu.uob;

import java.util.ArrayList;



public class Tokeniser {

    private String query;

    private Integer tokenIndex;

    private ArrayList<String> tokens;

    private final String[] specialCharacters = {"(", ")", ",", ";"};

    public Tokeniser(String query) {
        this.query = query;
        //The index 0 is always the command keyword
        this.tokenIndex = 1;
        this.tokens = new ArrayList<>();
    }

    public String getTokenAtCurrentIndex() {
        return tokens.get(this.tokenIndex);
    }
}

    /*public String getTokenAtNextIndex() {
        if (this.tokenIndex < this.tokens.size()) {
            this.tokenIndex++;
            return tokens.get(this.tokenIndex++);
        }
    }*/

