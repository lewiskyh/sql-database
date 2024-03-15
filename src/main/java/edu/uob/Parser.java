package edu.uob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Parser {

    private Tokeniser tokeniser;

    private Database database;

    private final String rootFolderPath = Paths.get("databases").toString();

    private DBTable table;


    public Parser(Tokeniser tokeniser) {
        this.tokeniser = tokeniser;
        this.database = new Database("");
    }

    public Tokeniser getTokeniser () {
        return this.tokeniser;
    }

    public void setTokeniser (Tokeniser tokeniser) {
        this.tokeniser = tokeniser;
    }

    public void setDatabase (Database database) {
        this.database = database;
    }

    public Database getDatabase () {
        return this.database;
    }

    //Check if the sql query starts with correct command type and ends with ";"
    public boolean parseCommand(){
        String firstToken = tokeniser.getTokenByIndex(0);
        String lastToken = tokeniser.getTokenByIndex(tokeniser.getTokenSize()-1);
        if (!checkCommandType(firstToken) || !checkLastToken(lastToken)) {
            return false;
        }
        return true;
    }

    private boolean checkCommandType(String firstToken){
        firstToken = firstToken.toUpperCase();
        return switch (firstToken) {
            case "USE", "CREATE", "DROP", "ALTER", "INSERT", "SELECT", "UPDATE", "DELETE", "JOIN" -> true;
            default -> false;
        };
    }

    private boolean checkLastToken(String lastToken){
        return lastToken.equals(";");
    }

    public void parseUse(String token) throws IOException {
        //Parse the name of the database
        if(parseName(token) && this.tokeniser.getTokenByIndex(2).equals(";")){
            //Set database name
            this.database.setDatabaseName(token);
            this.database.setupDatabase();
            return;
        }
        throw new IOException("Invalid USE Syntax");
    }
    //Check if the name conforms with the [PlaintText] using regex
    public boolean parseName (String token){

        if (!token.matches("^[A-Za-z0-9]+$")){
            System.out.println("Name should conform with PlaintText");
            return false;
        }
        return true;
    }

    public void parseCreate() throws IOException {
        String token = tokeniser.getTokenByIndex(1);
        String endToken= tokeniser.getTokenByIndex(tokeniser.getTokenSize()-1);
        if(!endToken.equals(";")){
            throw new IOException("syntax error: missing ; at the end of command");
        }
        //Check if token at index1 is database or table
        if(token.toUpperCase().equals("DATABASE")){
            //Check if next token conforms with [DatabaseName]
            parseCreateDatabase();
        }

        else if(token.toUpperCase().equals("TABLE")){
            //Create a new table
            this.table = new DBTable(this.database.getDatabaseFolderPath());
            //Set the table name
            this.table.setTable(token);
            //Create the table file
            //this.table.createTable();
            //Add the table to the database
            this.database.addDBTable(this.table);
        }
        else{
            throw new IOException("Invalid Syntax: either DATABASE or TABLE expected");
        }
    }

    public void interpretCreateDatabase() throws IOException {
        //Parse the name of the database
        String token = tokeniser.getTokenByIndex(1);
        if(parseName(token) && this.tokeniser.getTokenByIndex(2).equals(";")){
            this.database.setDatabaseName(token);
            File newDatabase = new File(this.database.getDatabaseFolderPath());
            //Check if same name database folder already exists
            if(newDatabase.exists()) throw new IOException("Database already exists");
            newDatabase.mkdir();
        }
        else {
            throw new IOException("Invalid CreateDatabase Syntax");
        }
    }

    public boolean parseCreateDatabase() throws IOException {
        //Parse the name of the database
        String token = tokeniser.getTokenByIndex(2);
        if(parseName(token) && this.tokeniser.getTokenByIndex(3).equals(";")){
            return true;
        }
        else { return false; }
    }

    public boolean parseCreateTable() throws IOException {
        //Parse the name of the database
        String token = tokeniser.getTokenByIndex(1);
        if(parseName(token) && this.tokeniser.getTokenByIndex(3).equals(";")){
            return true;
        }
        else if(tokeniser.getTokenByIndex(3).equals("(") && tokeniser.getTokenByIndex(tokeniser.getTokenSize()-2).equals(")")){
            //Parse the attribute list
            ArrayList<String> attributeListTokens = new ArrayList<>();
            for (int i = 4; i < tokeniser.getTokenSize()-2; i++){
                attributeListTokens.add(tokeniser.getTokenByIndex(i));
            }
            if(!parseAttributeList(attributeListTokens)){throw new IOException("Invalid AttributeList Syntax");}
            return true;
        }
        else {
            return false;
        }
    }

    public void interpretCreateTable () throws IOException {
        //CREATE TABLE [TableName] | CREATE TABLE [TableName] "(" <AttributeList> ")"
        String token = tokeniser.getTokenByIndex(2);
        //Parse the name of table
        if(parseName(token)){
            //Check if the next token is "(", second last token is ")"
            if(tokeniser.getTokenByIndex(3).equals("(") && tokeniser.getTokenByIndex(tokeniser.getTokenSize()-2).equals(")")){
                //Parse the attribute list
                ArrayList<String> attributeListTokens = new ArrayList<>();
                for (int i = 4; i < tokeniser.getTokenSize()-2; i++){
                    attributeListTokens.add(tokeniser.getTokenByIndex(i));
                }
                if(!parseAttributeList(attributeListTokens)){throw new IOException("Invalid AttributeList Syntax");}

                //Create new table and add attributes
                DBTable newTable = new DBTable(this.database.getDatabaseFolderPath(), token);
                for (String attribute: attributeListTokens){
                    if (!attribute.equals(",")) {
                        newTable.addAttribute(attribute);
                    }
                }
            }
            else if (tokeniser.getTokenByIndex(3).equals(";")){
             //Create new table
                DBTable newTable = new DBTable(this.database.getDatabaseFolderPath(), token);
                newTable.writeTable();
                this.database.addDBTable(newTable);
            }
            else{
                throw new IOException("Invalid CreateTable Syntax");
            }
        }


    }

    //Pass tokens between "(" and ")" to parseAttributeList
    public boolean parseAttributeList(ArrayList<String> tokens) throws IOException {
        //Parse until the next token is ")"
        for (int i = 0; i < tokens.size(); i++){
            if(tokens.get(i).equals(",") && i<tokens.size()-1 ){
                continue;
            }
            if(!parseName(tokens.get(i))){
                return false;
            }
        }
        return true;
    }


}
