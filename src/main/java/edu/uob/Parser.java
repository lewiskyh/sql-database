package edu.uob;

import edu.uob.Commands.*;

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

    private Command command;

    private String[] sqlKeyWords = {"USE", "CREATE", "DROP", "ALTER", "INSERT", "SELECT", "UPDATE", "DELETE", "JOIN", "AND", "OR", "LIKE", "SET", "FROM", "WHERE", "INTO", "VALUES", "TABLE", "DATABASE"};

    private ArrayList<String> boolOpearator; //"AND" | "OR"

    private ArrayList<Condition> conditions; // Array List of Condition instances


    public Parser() {}

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
                break;
            }
            case "SELECT":{
                this.command = new SelectCommand();
                parseSelect();
                break;
            }
            case "UPDATE":{
                this.command = new UpdateCommand();
                parseUpdate();
                break;
            }
            case "DELETE":{
                this.command = new DeleteCommand();
                parseDelete();
                break;
            }
            case "JOIN":{
                this.command = new JoinCommand();
                parseJoin();
                break;
            }
            default: throw new DatabaseException("Invalid Command Type");
        }
    }


    private boolean checkLastToken(String lastToken){ return lastToken.equals(";"); }



    public void parseUse(String token) throws DatabaseException, IOException {
        //Parse the name of the database
        if(checkName(token)){
            //Check if database exists
            if (!Paths.get(rootFolderPath, token.toLowerCase()).toFile().exists()){
                throw new DatabaseException("Database does not exist");
            }
            //Set database name
            this.database = new Database(token.toLowerCase());
            this.database.setupDatabase();
            this.command.setWorkingDatabase(this.database);
            return;
        }
        throw new DatabaseException("Invalid Use Syntax");
    }
    //Check if the name conforms with the [PlaintText] using regex
    public boolean checkName (String token) throws DatabaseException {

        if (!token.matches("^[A-Za-z0-9]+$")){
            return false;
        }

        for(String sqlKeyWord : sqlKeyWords){
            if (token.toUpperCase().equals(sqlKeyWord)){
                throw new DatabaseException("Invalid Syntax: SQL Keyword used as name");
            }
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


    public void parseCreateDatabase() throws DatabaseException {
        //Parse the name of the database
        int currentTokenIndex = 2;
        String token = tokeniser.getTokenByIndex(currentTokenIndex);
        if(!checkName(token) ||!this.tokeniser.getTokenByIndex(currentTokenIndex+1).equals(";")){
            throw new DatabaseException("Invalid CreateDatabase Syntax");
        }
        this.database = new Database(token.toLowerCase());
        this.command.setWorkingDatabase(this.database);
        this.command.setWorkingStructure("DATABASE");
    }

    public void parseCreateTable() throws DatabaseException {
        //Parse the name of the database
        int currentTokenIndex = 2;
        String token = tokeniser.getTokenByIndex(currentTokenIndex);
        if(checkName(token)){
            //Check if the next token is ";" - no attribute list
            if(this.tokeniser.getTokenByIndex(currentTokenIndex+1).equals(";")){
                if (this.database == null) {throw new DatabaseException("No database selected"); }
                this.command.setWorkingDatabase(this.database);
                this.command.setWorkingStructure("TABLE");
                DBTable createTable = new DBTable(this.database.getDatabaseFolderPath(), token.toLowerCase());
                this.command.setCreateTable(createTable);
            }
            //Check if attribute list is present
            else if (this.tokeniser.getTokenByIndex(currentTokenIndex+1).equals("(") && this.tokeniser.getTokenByIndex(tokeniser.getTokenSize()-2).equals(")")){
                //Parse the attribute list
                ArrayList<String> attributeListTokens = new ArrayList<>();
                for (int i = currentTokenIndex+2; i < tokeniser.getTokenSize()-2; i++){
                    attributeListTokens.add(tokeniser.getTokenByIndex(i));
                }
                parseAttributeList(attributeListTokens);
                if(this.database == null){ throw new DatabaseException("No database selected"); }
                this.command.setWorkingDatabase(this.database);
                this.command.setWorkingStructure("TABLE");
                DBTable createTable = new DBTable(this.database.getDatabaseFolderPath(), token.toLowerCase());
                this.command.setCreateTable(createTable);
                this.command.addAttributeList(attributeListTokens);
            }
            else { throw new DatabaseException("Invalid CreateTable Syntax"); }
        }

    }

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
                if(!checkName(tokens.get(i)) || tokens.get(i).equalsIgnoreCase("id")){
                    throw new DatabaseException("Invalid AttributeName");
                }
                previousIsAttribute = true;

                if(i < tokens.size()-1 && !tokens.get(i+1).equals(",") ){
                    throw new DatabaseException("Missing comma between attributes");
                }
            }

        }
    }

    public void parseDrop() throws DatabaseException, IOException {
        int currentTokenIndex = 1;
        String token = tokeniser.getTokenByIndex(currentTokenIndex);
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

    public void parseDropDatabase() throws DatabaseException, IOException {
        int currentTokenIndex = 2;
        String token = tokeniser.getTokenByIndex(currentTokenIndex).toLowerCase();
        if(checkName(token) && this.tokeniser.getTokenByIndex(currentTokenIndex+1).equals(";")){
            this.database = new Database(token.toLowerCase());
            this.database.setupDatabase();
            this.command.setWorkingStructure("DATABASE");
            this.command.setWorkingDatabase(this.database);
        }
        else {
            throw new DatabaseException("Invalid Drop Database Syntax - [DatabaseName] expected OR missing ;");
        }
    }

    public void parseDropTable() throws DatabaseException {
        int currentTokenIndex = 2;
        if(this.database == null){ throw new DatabaseException("No database selected for DROP"); }
        String token = tokeniser.getTokenByIndex(currentTokenIndex).toLowerCase();
        if(checkName(token) && this.tokeniser.getTokenByIndex(currentTokenIndex+1).equals(";")){
            if(this.database.getDatabaseName()==null){ throw new DatabaseException("No database selected"); }
            this.command.setWorkingStructure("TABLE");
            this.command.setWorkingDatabase(this.database);
            this.command.setDropTable(this.database.getDBTable(token));
        }
        else {
            throw new DatabaseException("Invalid Drop Database Syntax - [TableName] expected OR missing ;");
        }
    }

    public void parseAlter() throws DatabaseException{
        int currentTokenIndex = 1;
        if(tokeniser.getTokenSize()!=6){ throw new DatabaseException("Invalid Alter Syntax - at least 6 tokens expected"); }
        String token = tokeniser.getTokenByIndex(currentTokenIndex);
        if(this.database == null ){ throw new DatabaseException("No database selected"); }
        if(!token.toUpperCase().equals("TABLE")){
             throw new DatabaseException("Invalid Alter Syntax - \"TABLE\"expected after ALTER");
        }
        if(!checkName(tokeniser.getTokenByIndex(currentTokenIndex+1))){
             throw new DatabaseException("Invalid Alter Syntax - [TableName] expected after \"TABLE\"");
        }
        if(this.database.getDatabaseName()==null){ throw new DatabaseException("No database selected"); }
        this.command.setWorkingDatabase(this.database);
        String alterTableName = tokeniser.getTokenByIndex(currentTokenIndex+1).toLowerCase();
        //Check if alterTable exists in the database
        if(this.database.getDBTable(alterTableName) == null){
            throw new DatabaseException("Invalid Alter Syntax - [TableName] does not exist in the database");
        }
        //Set alterTable to command
        this.command.setAlterTable(this.database.getDBTable(alterTableName));
        switch (tokeniser.getTokenByIndex(currentTokenIndex+2).toUpperCase()){
            case "ADD":{
                if(!checkName(tokeniser.getTokenByIndex(currentTokenIndex+3))){
                    throw new DatabaseException("Invalid Alter Syntax - [AttributeName] expected after ADD");
                }
                this.command.setAlteration("ADD");
                this.command.setAttributeToAlter(tokeniser.getTokenByIndex(currentTokenIndex+3));
                //How to execute add attrbute? Need to pass target table to command too?
                break;

            }
            case "DROP":{
                if(!checkName(tokeniser.getTokenByIndex(currentTokenIndex+3))){
                    throw new DatabaseException("Invalid Alter Syntax - [AttributeName] expected after DROP");
                }
                this.command.setAlteration("DROP");
                this.command.setAttributeToAlter(tokeniser.getTokenByIndex(currentTokenIndex+3));
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
        //Store the value list
        List<String> valueList = new ArrayList<>();
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
                valueList.add(tokens.get(i));
            }
        }
        //pass the value list to command
        this.command.setValueListStored(valueList);
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
        String workingTableName = tokeniser.getTokenByIndex(currentTokenIndex+1).toLowerCase();
        if(this.database==null){ throw new DatabaseException("No database selected");}
        this.command.setWorkingStructure("TABLE");
        //Check if working table exists in the database
        if(this.database.getDBTable(workingTableName) == null){
            throw new DatabaseException("Invalid Insert Syntax - [TableName] does not exist in the database");
        }
        this.command.setWorkingDatabase(this.database);
        this.command.setInsertTableName(workingTableName);

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
        //ValueList stored in command whe parsing parseValueList



    }

    public void parseSelect () throws DatabaseException {
        int currentTokenIndex = 1;
        if(tokeniser.getTokenSize()<5){
            throw new DatabaseException("Invalid Select Syntax - at least 5 tokens expected");
        }
        parseWildAttributeList();
        int indexAtFrom = 0;
        for (int i = 0; i < tokeniser.getTokenSize(); i++){
            if(tokeniser.getTokenByIndex(i).equalsIgnoreCase("FROM")){
                indexAtFrom = i;
                break;
            }
        }
        if(indexAtFrom == 0){ throw new DatabaseException("Invalid Select Syntax - FROM expected"); }
        if(!checkName(tokeniser.getTokenByIndex(indexAtFrom+1))){
            throw new DatabaseException("Invalid Select Syntax - [TableName] expected after FROM");
        }
        //Check if the table exists in the database
        if(this.database == null ||this.database.getDBTable(tokeniser.getTokenByIndex(indexAtFrom+1).toLowerCase()) == null){
            throw new DatabaseException("Invalid Select Syntax - [TableName] does not exist in the database");
        }
        this.command.setWorkingDatabase(this.database);
        this.command.setSelectTable(this.database.getDBTable(tokeniser.getTokenByIndex(indexAtFrom+1).toLowerCase()));

        if(tokeniser.getTokenSize()<indexAtFrom+2){
            throw new DatabaseException("Invalid Select Syntax - [TABLENAME]; OR [TABLENAME] WHERE <Condition>; expected");
        }

        if(tokeniser.getTokenByIndex(indexAtFrom+2).equalsIgnoreCase("WHERE")){
            parseWhere(tokeniser.getAllTokens(), indexAtFrom+2);
        }
        else if(!tokeniser.getTokenByIndex(indexAtFrom+2).equals(";")){
            throw new DatabaseException("Invalid Select Syntax - ; or WHERE expected after [TableName]");
        }


    }

    public void parseWildAttributeList () throws DatabaseException {
        int currentTokenIndex = 1;
        String token = tokeniser.getTokenByIndex(currentTokenIndex);
        if (token.equals("*")) {
            this.command.setWildCard(true);
            return;
        }
        ArrayList<String> attributeListTokens = new ArrayList<>();

        if(!checkName(token)){ throw new DatabaseException("Invalid WildAttributeList Syntax - * or [AttributeName] expected");}
        this.command.setWildCard(false);

        //Check if next token is "," or "FROM"
        if(tokeniser.getTokenByIndex(currentTokenIndex+1).equals(",")){
            //Add elements to ArrayList until "FROM"
            for (int i = currentTokenIndex; i < tokeniser.getTokenSize(); i++){
                if(tokeniser.getTokenByIndex(i).equalsIgnoreCase("FROM")){ break; }
                attributeListTokens.add(tokeniser.getTokenByIndex(i).toLowerCase());
            }
            //Parse the attribute list
            parseAttributeList(attributeListTokens);
            //Add the parsed attributes to command's attribute list
            this.command.addAttributeList(attributeListTokens);
            return;
        }
        //If next token is FROM, only one attribute
        else if(tokeniser.getTokenByIndex(currentTokenIndex+1).equalsIgnoreCase("FROM")){
            //need to interpret adding attribute to command.
            parseAttributeList(attributeListTokens);
            String singleAttribute = tokeniser.getTokenByIndex(currentTokenIndex).toLowerCase();
            //Add the parsed attributes to command's attribute list
            this.command.addAttribute(singleAttribute);
            return;
        }
        else{
            throw new DatabaseException("Invalid WildAttributeList Syntax - , or FROM expected after attribute name");
        }
    }



    //Parse and interpret the WHERE condition, store information to condition class
    public void parseWhere (ArrayList<String> tokens, int indexAtWhere) throws DatabaseException{
        //Single Where condition
        if(!tokeniser.getTokenByIndex(indexAtWhere+1).equals("(")){
            if(!checkName(tokeniser.getTokenByIndex(indexAtWhere+1))){
                throw new DatabaseException("Invalid WHERE Syntax - [AttributeName] expected after WHERE");
            }
            Condition condition = new Condition();
            condition.setAttributeName(tokeniser.getTokenByIndex(indexAtWhere+1).toLowerCase());
            if(!tokeniser.getTokenByIndex(indexAtWhere+2).toUpperCase().matches("(==|>=|<=|!=|LIKE|<|>|)")){
                throw new DatabaseException("Invalid WHERE Syntax - Comparator expected after [AttributeName]");
            }
            condition.setComparator(tokeniser.getTokenByIndex(indexAtWhere+2));
            //Check if the value is a valid value, silent if successful
            parseValue(tokeniser.getTokenByIndex(indexAtWhere+3));
            if(!tokeniser.getTokenByIndex(indexAtWhere+4).equals(";")){
                throw new DatabaseException("Invalid WHERE Syntax - ; expected after VALUE");
            }
            String baseValue = tokeniser.getTokenByIndex(indexAtWhere+3);
            if(baseValue.startsWith("'") && baseValue.endsWith("'")){
                baseValue = baseValue.substring(1, baseValue.length() - 1);
            }
            condition.setBaseValue(baseValue);
            this.command.addCondition(condition);
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
                    this.command.addBooleanOperators(tokens.get(i).toUpperCase());
                }
            }
            //Check if every AND/OR is enclosed by ) and (
            // WHERE (...) AND (...) OR (...);
            if(!this.boolOpearator.isEmpty()) {
                parseTwoConditions(tokens, boolOperatorIndex);
            }
            else{ throw new DatabaseException("Invalid WHERE Syntax - missing AND / OR"); }
            //Check for last ) before closing ;
            if(!tokens.get(tokens.size()-2).equals(")")){
                throw new DatabaseException("Invalid WHERE Syntax - missing ) before ;");
            }

        }
    }

    //Method to interpret the WHERE condition and set the fields of condition instances
    private void parseTwoConditions(ArrayList<String> tokens, ArrayList<Integer> boolOperatorIndex) throws DatabaseException{
        for (Integer index : boolOperatorIndex) {
            if (!tokens.get(index - 1).equals(")") || !tokens.get(index + 1).equals("(")) {
                throw new DatabaseException("Invalid WHERE Syntax - missing ) or (");
            }
            String baseValue = tokeniser.getTokenByIndex(index - 2);
            if (baseValue.startsWith("'") && baseValue.endsWith("'")) {
                baseValue = baseValue.substring(1, baseValue.length() - 1);
            }
            //Create Condition instance and add to list of conditions
            Condition condition = new Condition();
            condition.setAttributeName(tokeniser.getTokenByIndex(index - 4));
            condition.setComparator(tokeniser.getTokenByIndex(index - 3));
            condition.setBaseValue(baseValue);
            this.command.addCondition(condition);
            //Create Condition instance and add to list of conditions
            condition = new Condition();
            baseValue = tokeniser.getTokenByIndex(index + 4);
            if (baseValue.startsWith("'") && baseValue.endsWith("'")) {
                baseValue = baseValue.substring(1, baseValue.length() - 1);
            }
            condition.setAttributeName(tokeniser.getTokenByIndex(index + 2));
            condition.setComparator(tokeniser.getTokenByIndex(index + 3));
            condition.setBaseValue(baseValue);
            this.command.addCondition(condition);
        }
    }

    //Not yet implement command execution.
    public void parseUpdate() throws DatabaseException {
        int currentTokenIndex = 1;
        String token = tokeniser.getTokenByIndex(currentTokenIndex);
        this.command.setWorkingDatabase(this.database);
        if(this.database == null){
            throw new DatabaseException("No database selected for updating");
        }
        if(!checkName(token)) {
            throw new DatabaseException("Invalid Update Syntax - [TableName] expected after UPDATE");
        }
        if(this.command.getWorkingDatabase().getDBTable(token.toLowerCase()) == null){
            throw new DatabaseException("Invalid Update Syntax - [TableName] does not exist in the database");
        }
        this.command.setUpdateTable(token.toLowerCase());
        if(!tokeniser.getTokenByIndex(currentTokenIndex+1).toUpperCase().equals("SET")){
            throw new DatabaseException("Invalid Update Syntax - SET expected after [TableName]");
        }
        int indexAtSet = currentTokenIndex+1;

        int indexAtWhere = 0;
        for (int i = 0; i < tokeniser.getTokenSize(); i++){
            if(tokeniser.getTokenByIndex(i).equalsIgnoreCase("WHERE")){
                indexAtWhere = i;
                break;
            }
        }
        parseNameValueList(indexAtSet, indexAtWhere);
        parseWhere(tokeniser.getAllTokens(), indexAtWhere);
    }

    public void parseNameValuePair(int nameValuePairIndex) throws DatabaseException {
        String token = tokeniser.getTokenByIndex(nameValuePairIndex);
        if(!checkName(token)){
            throw new DatabaseException("Invalid NameValuePair Syntax - [AttributeName] expected");
        }
        if(token.equalsIgnoreCase("id")){
            throw new DatabaseException("Invalid Update Syntax: id cannot be changed");
        }
        ValueSetter valueSetter = new ValueSetter();
        valueSetter.setAttributeName(token.toLowerCase());
        if(!tokeniser.getTokenByIndex(nameValuePairIndex+1).equals("=")){
            throw new DatabaseException("Invalid NameValuePair Syntax - = expected after [AttributeName]");
        }
        parseValue(tokeniser.getTokenByIndex(nameValuePairIndex+2));
        valueSetter.setValueToSet(tokeniser.getTokenByIndex(nameValuePairIndex+2));
        this.command.addValueSetter(valueSetter);
    }

    public void parseNameValueList(int setIndex, int whereIndex) throws DatabaseException {
        int countOfComma = 0;
        int nameValuePairIndex = setIndex+1;
        //Loop between set and where to count commas
        for (int i = setIndex; i < whereIndex; i++){
            if(tokeniser.getTokenByIndex(i).equals(",")){
                countOfComma++;
            }
        }
        //Should only be one name value pair
        if(countOfComma == 0){
            if(!tokeniser.getTokenByIndex(nameValuePairIndex+3).equalsIgnoreCase("WHERE")){
                throw new DatabaseException("Invalid NameValueList Syntax - missing , between NameValuePairs");
            }
            parseNameValuePair(nameValuePairIndex);
            return;
        }
        while(countOfComma > 0){
            parseNameValuePair(nameValuePairIndex);
            //Move to next name value pair by 4 tokens "AttributeName = Value" so 4 tokens
            nameValuePairIndex += 4;
            countOfComma--;
        }

    }

    public void parseDelete () throws DatabaseException {
        int currentTokenIndex = 1;
        this.command.setWorkingDatabase(this.database);
        String token = tokeniser.getTokenByIndex(currentTokenIndex);
        if(this.database==null){
            throw new DatabaseException("No database selected for deleting");
        }
        if(!token.equalsIgnoreCase("FROM")){
            throw new DatabaseException("Invalid Delete Syntax - FROM expected after DELETE");
        }
        if(!checkName(tokeniser.getTokenByIndex(currentTokenIndex+1))){
            throw new DatabaseException("Invalid Delete Syntax - [TableName] expected after FROM");
        }
        //Check if the table exists in the database
        if(this.database.getDBTable(tokeniser.getTokenByIndex(currentTokenIndex+1)) == null){
            throw new DatabaseException("Invalid Delete Syntax - [TableName] does not exist in the database");
        }
        //Set deletTable name to command
        this.command.setDeleteTableName(tokeniser.getTokenByIndex(currentTokenIndex+1).toLowerCase());
        if(tokeniser.getTokenByIndex(currentTokenIndex+2).equalsIgnoreCase("WHERE")){
            parseWhere(tokeniser.getAllTokens(), currentTokenIndex+2);
        }
    }

    public void parseJoin() throws DatabaseException {
        int currentTokenIndex = 1;
        if (tokeniser.getTokenSize() != 9) {
            throw new DatabaseException("Invalid Join Syntax - 9 tokens expected");
        }
        String firstTable = tokeniser.getTokenByIndex(currentTokenIndex).toLowerCase();
        String secondTable = tokeniser.getTokenByIndex(currentTokenIndex + 2).toLowerCase();
        if (!checkName(firstTable) || !checkName(secondTable)) {
            throw new DatabaseException("Invalid Join Syntax - [TableName] expected after JOIN");
        }
        if (this.database == null) {
            throw new DatabaseException("No database selected");
        }

        String andToklen = tokeniser.getTokenByIndex(currentTokenIndex + 1);
        if (!andToklen.equalsIgnoreCase("AND")) {
            throw new DatabaseException("Invalid Join Syntax - AND expected after 1st [TableName]s");
        }

        if (this.database.getDBTable(firstTable) == null || this.database.getDBTable(secondTable) == null) {
            throw new DatabaseException("Invalid Join Syntax - [TableName] does not exist in the database");
        }
        String onToken = tokeniser.getTokenByIndex(currentTokenIndex + 3);
        if (!onToken.equalsIgnoreCase("ON")) {
            throw new DatabaseException("Invalid Join Syntax - ON expected after 2nd [TableName]");
        }
        //For each attribute name, check if it exists in table1 and table2
        String firstAttribute = tokeniser.getTokenByIndex(currentTokenIndex + 4).toLowerCase();
        String secondAttribute = tokeniser.getTokenByIndex(currentTokenIndex + 6).toLowerCase();

        List<String> firstTableAttributes = this.database.getDBTable(firstTable).getAttributes();
        List<String> secondTableAttributes = this.database.getDBTable(secondTable).getAttributes();

        checkJoinAttributeExist(firstTableAttributes, firstAttribute);
        checkJoinAttributeExist(firstTableAttributes, secondAttribute);
        checkJoinAttributeExist(secondTableAttributes, firstAttribute);
        checkJoinAttributeExist(secondTableAttributes, secondAttribute);

    }


    private void checkJoinAttributeExist (List<String> attributes, String attributeName) throws DatabaseException {
        for(String attribute : attributes){
            if(attribute.equalsIgnoreCase(attributeName)){
                return;
            }
        }
        throw new DatabaseException("Invalid Join Syntax - [AttributeName] does not exist in [TableName]");
    }
}







