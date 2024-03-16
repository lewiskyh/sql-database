package edu.uob;

import edu.uob.Commands.Command;
import edu.uob.Commands.CreateCommand;
import edu.uob.Commands.SelectCommand;
import edu.uob.Commands.UseCommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Parser {

    private Tokeniser tokeniser;

    private Database database;

    private final String rootFolderPath = Paths.get("databases").toString();

    private DBTable table;

    private Command command;


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

                //parseDrop();

            }
            case "ALTER":{
                //parseAlter();

            }
            case "INSERT":{
                //parseInsert();

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
                if (this.database != null) {
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
                if(this.database!=null){
                    this.command.setWorkingDatabase(this.database);
                    this.command.setWorkingStructure("TABLE");
                    this.command.addAttributeList(attributeListTokens);
                }
                else { throw new DatabaseException("Didn't not choose database to create table"); }

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


}
