package edu.uob;

import edu.uob.Commands.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private Tokeniser tokeniser;

    private Database database;

    private final String rootFolderPath = Paths.get("databases").toString();

    private DBTable table;

    private Command command;

    private ArrayList<String> boolOpearator; //"AND" | "OR"

    private ArrayList<Condition> conditions; // Array List of Condition instances


    public Parser(Tokeniser tokeniser) {
        this.tokeniser = tokeniser;
        this.database = new Database("");
    }

    public Command getCommand(){ return this.command; }

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

    public ArrayList<Condition> getAllConditions () {
        return this.conditions;
    }

    public ArrayList<String> getAllBoolOperators () {
        return this.boolOpearator;
    }

    //First parsing for every command
    //Ending with ; or command is empty?
    //Then return the command object according to the command type
    public Command parseCommand() throws DatabaseException, IOException {
        String firstToken = tokeniser.getTokenByIndex(0);
        String lastToken = tokeniser.getTokenByIndex(tokeniser.getTokenSize()-1);
        if (!checkLastToken(lastToken) || this.tokeniser.getAllTokens().isEmpty()){
            throw new DatabaseException("Invalid Command Syntax. It should start with Command type and end with ;");
        }
        checkCommandType(firstToken);
        return this.command;
    }

    private void checkCommandType(String firstToken) throws IOException, DatabaseException {
        firstToken = firstToken.toUpperCase();
        switch (firstToken) {
            case "USE":{
                this.command = new UseCommand();
                parseUse(tokeniser.getTokenByIndex(1));
                break;
            }
            case "CREATE":{
                this.command = new CreateCommand();
                parseCreate();
                break;
            }
            case "DROP":{
                this.command = new DropCommand();
                parseDrop();
                break;

            }
            case "ALTER":{
                this.command = new AlterCommand();
                parseAlter();
                break;

            }
            case "INSERT":{
                this.command = new InsertCommand();
                parseInsert();

            }
            case "SELECT":{
                this.command = new SelectCommand();
                break;

            }
            case "UPDATE":{
                //parseUpdate();
            }
            case "DELETE":{
                //parseDelete();

            }
            case "JOIN":{
                //parseJoin();
            }
            default: throw new DatabaseException("Invalid Command Type");
        }
    }


    private boolean checkLastToken(String lastToken){ return lastToken.equals(";"); }



    public void parseUse(String token) throws DatabaseException, IOException {
        //Parse the name of the database
        if(checkName(token)){
            //Set database name
            this.database.setDatabaseName(token);
            this.database.setupDatabase();
            this.command.setWorkingDatabase(this.database);
            return;
        }
        throw new DatabaseException("Invalid Use Syntax");
    }
    //Check if the name conforms with the [PlaintText] using regex
    public boolean checkName (String token){

        if (!token.matches("^[A-Za-z0-9]+$")){
            return false;
        }
        return true;
    }

    public void parseCreate() throws DatabaseException {
        int currentTokenIndex = 1;
        int correctTokenSize = tokeniser.getTokenSize();
        String token = tokeniser.getTokenByIndex(currentTokenIndex);
        if(tokeniser.getTokenSize()<correctTokenSize){
            throw new DatabaseException("Invalid Create Syntax, at least at length of 4");
        }
        //Check if token at index1 is database or table
        if(token.toUpperCase().equals("DATABASE")){
            //Check if next token conforms with [DatabaseName]
            parseCreateDatabase();
        }

        else if(token.toUpperCase().equals("TABLE")){
            //Check if next token conforms with [TableName]
            parseCreateTable();
        }
        else{
            throw new DatabaseException("Invalid Syntax: either DATABASE or TABLE expected");
        }
    }

    /**

    public void interpretCreateDatabase() throws IOException {
        //Parse the name of the database
        String token = tokeniser.getTokenByIndex(2);
        if(checkName(token) && this.tokeniser.getTokenByIndex(3).equals(";")){
            File newDatabase = new File(this.database.getDatabaseFolderPath());
            //Check if same name database folder already exists
            if(newDatabase.exists()) throw new IOException("Database already exists");
            newDatabase.mkdir();
        }
        else {
            throw new IOException("Invalid CreateDatabase Syntax");
        }
    }*/

    public void parseCreateDatabase() throws DatabaseException {
        //Parse the name of the database
        int currentTokenIndex = 2;
        String token = tokeniser.getTokenByIndex(currentTokenIndex);
        if(!checkName(token) ||!this.tokeniser.getTokenByIndex(currentTokenIndex+1).equals(";")){
            throw new DatabaseException("Invalid CreateDatabase Syntax");
        }
        this.command.setWorkingStructure("DATABASE");
        this.command.setDatabaseName(token);
    }

    public void parseCreateTable() throws DatabaseException {
        //Parse the name of the database
        int currentTokenIndex = 2;
        String token = tokeniser.getTokenByIndex(currentTokenIndex);
        if(checkName(token)){
            //Check if the next token is ";" - no attribute list
            if(this.tokeniser.getTokenByIndex(currentTokenIndex+1).equals(";")){
                if (!this.database.getDatabaseName().isEmpty()) {
                    this.command.setWorkingDatabase(this.database);
                    this.command.setWorkingStructure("TABLE");
                }
                else { throw new DatabaseException("Didn't not choose database to create table"); }
            }
            //Check if attribute list is present
            else if (this.tokeniser.getTokenByIndex(currentTokenIndex+1).equals("(") && this.tokeniser.getTokenByIndex(tokeniser.getTokenSize()-2).equals(")")){
                //Parse the attribute list
                ArrayList<String> attributeListTokens = new ArrayList<>();
                for (int i = currentTokenIndex+2; i < tokeniser.getTokenSize()-2; i++){
                    attributeListTokens.add(tokeniser.getTokenByIndex(i));
                }
                parseAttributeList(attributeListTokens);
                if(!this.database.getDatabaseName().isEmpty()){
                    this.command.setWorkingDatabase(this.database);
                    this.command.setWorkingStructure("TABLE");
                    this.command.addAttributeList(attributeListTokens);
                }
                else { throw new DatabaseException("No database selected"); }

            }
            else { throw new DatabaseException("Invalid CreateTable Syntax"); }
        }

    }
    /**

    public void interpretCreateTable () throws DatabaseException, IOException {
        //CREATE TABLE [TableName] | CREATE TABLE [TableName] "(" <AttributeList> ")"
        String token = tokeniser.getTokenByIndex(2);
        //Parse the name of table
        if(checkName(token)){
            //Check if the next token is "(", second last token is ")"
            if(tokeniser.getTokenByIndex(3).equals("(") && tokeniser.getTokenByIndex(tokeniser.getTokenSize()-2).equals(")")){
                //Parse the attribute list
                ArrayList<String> attributeListTokens = new ArrayList<>();
                for (int i = 4; i < tokeniser.getTokenSize()-2; i++){
                    attributeListTokens.add(tokeniser.getTokenByIndex(i));
                }
                parseAttributeList(attributeListTokens);

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
                throw new DatabaseException("Invalid CreateTable Syntax");
            }
        }


    }*/

    //Pass tokens between "(" and ")" to parseAttributeList
    public void parseAttributeList(ArrayList<String> tokens) throws DatabaseException {
        boolean previousIsAttribute = false;
        for (int i = 0; i < tokens.size(); i++){
            if(tokens.get(i).equals(",")){
                if(!previousIsAttribute){
                    throw new DatabaseException("Invalid AttributeList Syntax");
                }
                else if(i == tokens.size()-1){
                    throw new DatabaseException("No attribute after comma");
                }
                previousIsAttribute = false;
            }
            else{
                if(!checkName(tokens.get(i))){
                    throw new DatabaseException("Invalid AttributeName");
                }
                previousIsAttribute = true;

                if(i < tokens.size()-1 && !tokens.get(i+1).equals(",") ){
                    throw new DatabaseException("Missing comma between attributes");
                }
            }

        }
    }

    public void parseDrop() throws DatabaseException {
        int currentTokenIndex = 1;
        String token = tokeniser.getTokenByIndex(currentTokenIndex);
        if(this.database.getDatabaseName().isEmpty()){
            throw new DatabaseException("No database selected");
        }
        switch (token.toUpperCase()){
            case "DATABASE":{
                parseDropDatabase();
                break;
            }
            case "TABLE":{
                parseDropTable();
                break;
            }
            default: throw new DatabaseException("Invalid Drop Syntax - DATABASE or TABLE after DROP expected");
        }
    }

    public void parseDropDatabase() throws DatabaseException {
        int currentTokenIndex = 2;
        String token = tokeniser.getTokenByIndex(currentTokenIndex);
        if(checkName(token) && this.tokeniser.getTokenByIndex(currentTokenIndex+1).equals(";")){
            this.command.setWorkingStructure("DATABASE");
            this.command.setDatabaseName(token);
            this.command.setWorkingDatabase(this.database);
        }
        else {
            throw new DatabaseException("Invalid Drop Database Syntax - [DatabaseName] expected OR missing ;");
        }
    }

    public void parseDropTable() throws DatabaseException {
        int currentTokenIndex = 2;
        String token = tokeniser.getTokenByIndex(currentTokenIndex);
        if(checkName(token) && this.tokeniser.getTokenByIndex(currentTokenIndex+1).equals(";")){
            this.command.setWorkingStructure("TABLE");
            this.command.setWorkingDatabase(this.database);
            //print
            System.out.print (token);
            this.command.addTableName(token);
        }
        else {
            throw new DatabaseException("Invalid Drop Database Syntax - [TableName] expected OR missing ;");
        }
    }

    public void parseAlter() throws DatabaseException{
        int currentTokenIndex = 1;
        if(tokeniser.getTokenSize()!=6){ throw new DatabaseException("Invalid Alter Syntax - at least 6 tokens expected"); }
        String token = tokeniser.getTokenByIndex(currentTokenIndex);
        if(this.database.getDatabaseName().isEmpty()){ throw new DatabaseException("No database selected"); }
        if(!token.toUpperCase().equals("TABLE")){
             throw new DatabaseException("Invalid Alter Syntax - \"TABLE\"expected after ALTER");
        }
        if(!checkName(tokeniser.getTokenByIndex(currentTokenIndex+1))){
             throw new DatabaseException("Invalid Alter Syntax - [TableName] expected after \"TABLE\"");
        }
        switch (tokeniser.getTokenByIndex(currentTokenIndex+2).toUpperCase()){
            case "ADD":{
                if(!checkName(tokeniser.getTokenByIndex(currentTokenIndex+3))){
                    throw new DatabaseException("Invalid Alter Syntax - [AttributeName] expected after ADD");
                }
                this.command.setWorkingStructure("TABLE");
                this.command.setWorkingDatabase(this.database);
                //How to execute add attrbute? Need to pass target table to command too?
                break;

            }
            case "DROP":{
                if(!checkName(tokeniser.getTokenByIndex(currentTokenIndex+3))){
                    throw new DatabaseException("Invalid Alter Syntax - [AttributeName] expected after DROP");
                }
                this.command.setWorkingStructure("TABLE");
                this.command.setWorkingDatabase(this.database);
                //How to execute drop attribute?Need to pass target table to command too?
                break;
            }
            default: throw new DatabaseException("Invalid Alter Syntax - ADD or DROP expected after [TableName]");
        }

    }

    public void parseValue (String token) throws DatabaseException {
        String regex = "(TRUE|FALSE)" + "|([+-]?\\d+\\.\\d+)" + "|([+-]?\\d+)" + "|(NULL)" + "|'([^'\\\\]*(\\\\.[^'\\\\]*)*)'";
        Pattern regexPattern = Pattern.compile(regex);
        Matcher matcher = regexPattern.matcher(token);
        if (!matcher.matches()){ throw new DatabaseException("Invalid Value Syntax"); }
    }


    public void parseValueList(ArrayList<String> tokens) throws DatabaseException {
        boolean previousIsValue = false;
        for (int i = 0; i < tokens.size(); i++){
            if(tokens.get(i).equals(",")){
                if(!previousIsValue){
                    throw new DatabaseException("Invalid ValueList Syntax");
                }
                else if(i == tokens.size()-1){
                    throw new DatabaseException("No value after comma");
                }
                previousIsValue = false;
            }
            else{
                parseValue(tokens.get(i));
                previousIsValue = true;

                if(i < tokens.size()-1 && !tokens.get(i+1).equals(",") ){
                    throw new DatabaseException("Missing comma between values");
                }
            }
        }
    }

    public void parseInsert () throws DatabaseException {
        int currentTokenIndex = 1;
        String token = tokeniser.getTokenByIndex(currentTokenIndex);
        if(tokeniser.getTokenSize()<8){
            throw new DatabaseException("Invalid Insert Syntax - at least 8 tokens expected");
        }
        if(!token.toUpperCase().equals("INTO")){
            throw new DatabaseException("Invalid Insert Syntax - INTO expected after INSERT");
        }
        if(!checkName(tokeniser.getTokenByIndex(currentTokenIndex+1))){
            throw new DatabaseException("Invalid Insert Syntax - [TableName] expected after INTO");
        }
        if(!tokeniser.getTokenByIndex(currentTokenIndex+2).toUpperCase().equals("VALUES")){
            throw new DatabaseException("Invalid Insert Syntax - VALUES expected after [TableName]");
        }
        if(!tokeniser.getTokenByIndex(currentTokenIndex+3).equals("(") || !tokeniser.getTokenByIndex(tokeniser.getTokenSize()-2).equals(")")){
            throw new DatabaseException("Invalid Insert Syntax - ( and ) expected");
        }
        ArrayList<String> valueListTokens = new ArrayList<>();
        for (int i = currentTokenIndex+4; i < tokeniser.getTokenSize()-2; i++){
            valueListTokens.add(tokeniser.getTokenByIndex(i));
        }
        parseValueList(valueListTokens);
    }

    public void parseSelect () throws DatabaseException {
        int currentTokenIndex = 1;
        String token = tokeniser.getTokenByIndex(currentTokenIndex);
        if(tokeniser.getTokenSize()<5){
            throw new DatabaseException("Invalid Select Syntax - at least 5 tokens expected");
        }


    }

    //Method to interpret the WHERE condition and set the fields of condition instances
    private void createTwoConditions(ArrayList<String> tokens, ArrayList<Integer> boolOperatorIndex) throws DatabaseException{
        for (Integer index : boolOperatorIndex) {
            if (!tokens.get(index - 1).equals(")") || !tokens.get(index + 1).equals("(")) {
                throw new DatabaseException("Invalid WHERE Syntax - missing ) or (");
            }
            //Create Condition instance and add to list of conditions
            Condition condition = new Condition();
            condition.setAttributeName(tokeniser.getTokenByIndex(index - 4));
            condition.setComparator(tokeniser.getTokenByIndex(index - 3));
            condition.setBaseValue(tokeniser.getTokenByIndex(index - 2));
            this.conditions.add(condition);
            //Create Condition instance and add to list of conditions
            condition = new Condition();
            condition.setAttributeName(tokeniser.getTokenByIndex(index + 2));
            condition.setComparator(tokeniser.getTokenByIndex(index + 3));
            condition.setBaseValue(tokeniser.getTokenByIndex(index + 4));
            this.conditions.add(condition);
        }
    }




    //Parse and interpret the WHERE condition, store information to condition class
    public void parseWhere (ArrayList<String> tokens, int indexAtWhere) throws DatabaseException{
        //Single Where condition
        if(!tokeniser.getTokenByIndex(indexAtWhere+1).equals("(")){
            if(!checkName(tokeniser.getTokenByIndex(indexAtWhere+1))){
                throw new DatabaseException("Invalid WHERE Syntax - [AttributeName] expected after WHERE");
            }
            this.conditions = new ArrayList<>();
            Condition condition = new Condition();
            condition.setAttributeName(tokeniser.getTokenByIndex(indexAtWhere+1));
            if(!tokeniser.getTokenByIndex(indexAtWhere+2).toUpperCase().matches("(==|>=|<=|!=|LIKE|<|>|)")){
                throw new DatabaseException("Invalid WHERE Syntax - Comparator expected after [AttributeName]");
            }
            condition.setComparator(tokeniser.getTokenByIndex(indexAtWhere+2));
            //Check if the value is a valid value, silent if successful
            parseValue(tokeniser.getTokenByIndex(indexAtWhere+3));
            if(!tokeniser.getTokenByIndex(indexAtWhere+4).equals(";")){
                throw new DatabaseException("Invalid WHERE Syntax - ; expected after VALUE");
            }
            condition.setBaseValue(tokeniser.getTokenByIndex(indexAtWhere+3));
            this.conditions.add(condition);
        }
        //More than one conditions
        else{
            //Store index of AND / OR
            ArrayList<Integer> boolOperatorIndex = new ArrayList<>();
            this.boolOpearator = new ArrayList<>();
            this.conditions = new ArrayList<>();

            for (int i = indexAtWhere; i < tokens.size(); i++){
                if(tokens.get(i).equalsIgnoreCase("AND") || tokens.get(i).equalsIgnoreCase("OR")){
                    boolOperatorIndex.add(i);
                    this.boolOpearator.add(tokens.get(i).toUpperCase()); // Adding AND / OR to the list
                }
            }
            //Check if every AND/OR is enclosed by ) and (
            // WHERE (...) AND (...) OR (...);
            if(!this.boolOpearator.isEmpty()) { createTwoConditions(tokens, boolOperatorIndex); }
            else{ throw new DatabaseException("Invalid WHERE Syntax - missing AND / OR"); }
            //Check for last ) before closing ;
            if(!tokens.get(tokens.size()-2).equals(")")){
                throw new DatabaseException("Invalid WHERE Syntax - missing ) before ;");
            }

        }
    }


    }







