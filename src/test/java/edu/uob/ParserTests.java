package edu.uob;
import edu.uob.Commands.Command;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTests {

    private Parser parser;

    private Integer tokenIndex;

    private final String rootFolderPath = Paths.get("databases").toString();

    private DBTable table;

    @BeforeEach

    public void setUp() {
        this.parser = new Parser(new Tokeniser("USE lewis;"));
    }

    //Test parseCommand
    @Test

    public void testParseCommand() throws DatabaseException, IOException {
        this.parser.getTokeniser().preprocessQuery();
        //Should be silent as the query starts with correct command type and ends with ";"
        this.parser.parseCommand();
        assertEquals("lewis", this.parser.getTokeniser().getTokenByIndex(1));
    }

    //Test parseUse to see if lewis database can be set up and inspect the existence and linkage of tables
    @Test

    public void testParseUse() throws IOException, DatabaseException {
        this.parser = new Parser(new Tokeniser("use lewis;"));
        this.parser.getTokeniser().preprocessQuery();
        //lewis database object should be created and the database is setup
        //files in the lewis database should be linked to lewis' database object
        this.parser.parseCommand();
        assertEquals("lewis", this.parser.getDatabase().getDatabaseName());
        //assert lewis's tablemap contains people and shed table
        assertEquals("people", this.parser.getDatabase().getDBTable("people").getTableName());

    }

    //Test checkName with invalid name
    @Test

    public void testCheckName() throws DatabaseException {
        String token = "lewis!";
        assertFalse(this.parser.checkName(token));

    }

    //Test parseAttribute list with valid attribute list
    @Test

    public void testParseAttributeList() throws DatabaseException {
        ArrayList<String> attributes = new ArrayList<>();
        //Create a list of attributes
        attributes.add("name123");
        attributes.add(",");
        attributes.add("ag122e");
        attributes.add(",");
        attributes.add("money");
        try {
            parser.parseAttributeList(attributes);
        } catch (DatabaseException e) {
            fail("Incorrect exception:" + e.getMessage());
        }
    }

    //Test parseAttribute list with invalid attribute list
    @Test
    public void testParseAttributeListInvalid() throws DatabaseException {
        ArrayList<String> attributes = new ArrayList<>();
        //Create a list of attributes
        attributes.add("name123");
        attributes.add(",");
        attributes.add("ag122e");
        attributes.add(",");
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            parser.parseAttributeList(attributes);
        });
        assertEquals("No attribute after comma", exception.getMessage());
    }

    //Test parseDatabase with a valid token
    @Test
    public void testParseCreateDatabase() throws DatabaseException, IOException {
        this.parser = new Parser(new Tokeniser("create database newDatabase;"));
        this.parser.getTokeniser().preprocessQuery();
        this.parser.parseCommand();
        this.parser.parseCreateDatabase();
        //Assert command object has the correct working structure and database name
        assertEquals("DATABASE", this.parser.getCommand().getWorkingStructure());
        //Assert command has the correct database name
        assertEquals("newDatabase", this.parser.getCommand().getDatabaseName());
    }

    //Test parseDatabase with an invalid token
    @Test
    public void testParseCreateDatabaseInvalid() {
        this.parser = new Parser(new Tokeniser("create database newDatabase!;"));
        this.parser.getTokeniser().preprocessQuery();

        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            this.parser.parseCreateDatabase();
        });
        assertEquals("Invalid CreateDatabase Syntax", exception.getMessage());
    }

    //Test parseCreateTable with valid syntax, no attribute
    @Test
    public void testParseCreateTableValid() throws IOException, DatabaseException {
        this.parser = new Parser(new Tokeniser("create table newTable;"));
        Database newDatabase = new Database("newDatabase");
        //Database is set when parsing Create command, which is not run in this test, here is set manually
        this.parser.setDatabase(newDatabase);
        this.parser.getTokeniser().preprocessQuery();
        this.parser.parseCommand();
        this.parser.parseCreateTable();
        //Assert command object has the correct working structure and database name
        assertEquals("TABLE", this.parser.getCommand().getWorkingStructure());
        //The working database in parser should be same as the working database in command object
        assertEquals(newDatabase, this.parser.getCommand().getWorkingDatabase());
    }

    //Test parseCreateTable with valid syntax, with attribute
    @Test
    public void testParseCreateTableWithAttribute() throws IOException, DatabaseException{
        this.parser = new Parser(new Tokeniser("create table newTable (name, age, money);"));
        Database newDatabase = new Database("newDatabase");
        //Database is set when parsing Create command, which is not run in this test, here is set manually
        this.parser.setDatabase(newDatabase);
        this.parser.getTokeniser().preprocessQuery();
        this.parser.parseCommand();
        //Assert command object has the correct working structure
        assertEquals("TABLE", this.parser.getCommand().getWorkingStructure());
        assertEquals(newDatabase, this.parser.getCommand().getWorkingDatabase());
        //Assert the attribute list is added to the command object
        assertEquals(List.of("name", "age", "money"), this.parser.getCommand().getAttributeList());
    }

    //Test parseCreateTable with invalid syntax with attribute
    @Test
    public void testParseCreateTableInvalid() throws IOException, DatabaseException {
        this.parser = new Parser(new Tokeniser("create table newTable (name age money);"));
        Database newDatabase = new Database("newDatabase");
        //Database is set when parsing Create command, which is not run in this test, here is set manually
        this.parser.setDatabase(newDatabase);
        this.parser.getTokeniser().preprocessQuery();

        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            this.parser.parseCommand();
        });
        assertEquals("Missing comma between attributes", exception.getMessage());
    }

    //Test parseCreateTable with valid syntax with one attribute
    @Test
    public void testParseCreateTableValid2() throws IOException, DatabaseException {
        this.parser = new Parser(new Tokeniser("create table newTable (name);"));
        Database newDatabase = new Database("newDatabase");
        //Database is set when parsing Create command, which is not run in this test, here is set manually
        this.parser.setDatabase(newDatabase);
        this.parser.getTokeniser().preprocessQuery();
        this.parser.parseCommand();
        assertEquals("TABLE", this.parser.getCommand().getWorkingStructure());
        assertEquals(newDatabase, this.parser.getCommand().getWorkingDatabase());
        assertEquals(List.of("name"), this.parser.getCommand().getAttributeList());
    }

    //Test parseDrop - database with valid syntax
    @Test
    public void testParseDropValid() throws DatabaseException, IOException {
        this.parser = new Parser(new Tokeniser("Drop database newDatabase;"));
        Database newDatabase = new Database("newDatabase");
        this.parser.setDatabase(newDatabase);
        this.parser.getTokeniser().preprocessQuery();
        this.parser.parseCommand();
        //Assert command's working structure and database name
        assertEquals("DATABASE", this.parser.getCommand().getWorkingStructure());
        assertEquals("newDatabase", this.parser.getCommand().getDatabaseName());
        assertEquals(newDatabase, this.parser.getCommand().getWorkingDatabase());
    }

    //Tset parseDrop - table with valid syntax
    @Test
    public void testParseDropTableValid() throws DatabaseException, IOException {
        this.parser = new Parser(new Tokeniser("Drop table table;"));
        Database newDatabase = new Database("newDatabase");
        this.parser.setDatabase(newDatabase);
        this.parser.getTokeniser().preprocessQuery();
        this.parser.parseCommand();
        //Assert command's working structure and database name
        assertEquals("TABLE", this.parser.getCommand().getWorkingStructure());
        assertEquals(newDatabase, this.parser.getCommand().getWorkingDatabase());
        //Assert "table" is in command's table name list
        assertEquals(List.of("table"), this.parser.getCommand().getTableNameList());
    }

    //Test parseDrop - table with invalid syntax
    @Test
    public void testParseDropTableInvalid() throws DatabaseException, IOException {
        this.parser = new Parser(new Tokeniser("Drop tab1e table;"));
        Database newDatabase = new Database("newDatabase");
        this.parser.setDatabase(newDatabase);
        this.parser.getTokeniser().preprocessQuery();
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            this.parser.parseCommand();
        });
        assertEquals("Invalid Drop Syntax - DATABASE or TABLE after DROP expected", exception.getMessage());
    }

    //Test parseAlter - ADD with valid syntax
    @Test
    public void testParseAlterTableValid() throws DatabaseException, IOException {
        this.parser = new Parser(new Tokeniser("Alter table table1 add name;"));
        Database newDatabase = new Database("newDatabase");
        this.parser.setDatabase(newDatabase);
        this.parser.getTokeniser().preprocessQuery();
        this.parser.parseCommand();
        try {
            this.parser.parseCommand();
        } catch (DatabaseException e) {
            fail("Incorrect exception:" + e.getMessage());
        }

    }

    //Test parseAlter = DROP with invalid syntax
    @Test
    public void testParseAlterTableInvalid() throws DatabaseException, IOException {
        this.parser = new Parser(new Tokeniser("Alter table table1 drop;"));
        Database newDatabase = new Database("newDatabase");
        this.parser.setDatabase(newDatabase);
        this.parser.getTokeniser().preprocessQuery();
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            this.parser.parseCommand();
        });
        assertEquals("Invalid Alter Syntax - at least 6 tokens expected", exception.getMessage());
    }

    //Test parseAlter = Drop without "TABLE" keyword
    @Test
    public void testParseAlterTableInvalid2() throws DatabaseException, IOException {
        this.parser = new Parser(new Tokeniser("Alter kill table1 drop name;"));
        Database newDatabase = new Database("newDatabase");
        this.parser.setDatabase(newDatabase);
        this.parser.getTokeniser().preprocessQuery();
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            this.parser.parseCommand();
        });
        assertEquals("Invalid Alter Syntax - \"TABLE\"expected after ALTER", exception.getMessage());
    }

    //Test parseValue
    @Test
    public void testparseValue() throws DatabaseException{
        String value = "123";
        try {
            this.parser.parseValue(value);
        } catch (DatabaseException e) {
            fail("Incorrect exception:" + e.getMessage());
        }
        value = "123.0";
        try {
            this.parser.parseValue(value);
        } catch (DatabaseException e) {
            fail("Incorrect exception:" + e.getMessage());
        }
        value = "-123.0";
        try {
            this.parser.parseValue(value);
        } catch (DatabaseException e) {
            fail("Incorrect exception:" + e.getMessage());
        }
        value = "+123.012";
        try {
            this.parser.parseValue(value);
        } catch (DatabaseException e) {
            fail("Incorrect exception:" + e.getMessage());
        }
        value = "TRUE";
        try {
            this.parser.parseValue(value);
        } catch (DatabaseException e) {
            fail("Incorrect exception:" + e.getMessage());
        }
        value = "'AGE'";
        try {
            this.parser.parseValue(value);
        } catch (DatabaseException e) {
            fail("Incorrect exception:" + e.getMessage());
        }
        //Invalid value: without enclosing' '
        value = "SimonTheBest!";
        String finalValue = value;
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            this.parser.parseValue(finalValue);
        });
    }

    //Test parseValueList with valid syntax
    @Test
    public void testParseValueList() throws DatabaseException {
        ArrayList<String> values = new ArrayList<>();
        values.add("'Simon'");
        values.add(",");
        values.add("123.099");
        values.add(",");
        values.add("-123.011");
        values.add(",");
        values.add("+123.011112");
        values.add(",");
        values.add("TRUE");
        values.add(",");
        values.add("'AGE'");
        try {
            this.parser.parseValueList(values);
        } catch (DatabaseException e) {
            fail("Incorrect exception:" + e.getMessage());
        }
    }
    //Test parseValueList with invalid syntax
    @Test
    public void testParseValueListInvalid() throws DatabaseException {
        ArrayList<String> values = new ArrayList<>();
        values.add("'Simon'");
        values.add(",");
        values.add("123.099");
        values.add(",");
        values.add("-123.011");
        values.add(",");
        values.add("+123.011112");
        values.add(",");
        values.add("TRUE");
        values.add(",");;
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            this.parser.parseValueList(values);
        });
    }

    //Test parseValueList with invalid syntax
    @Test
    public void testParseValueListInvalid2() throws DatabaseException {
        ArrayList<String> values = new ArrayList<>();
        values.add("'Simon'");
        values.add("123.099");
        values.add(",");
        values.add("-123.011");
        values.add(",");
        values.add("+123.011112");
        values.add(",");
        values.add("TRUE");
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            this.parser.parseValueList(values);
        });
    }

    //Test parseInsert with valid syntax
    @Test
    public void testParseInsertValid() throws DatabaseException, IOException {
        this.parser = new Parser(new Tokeniser("insert into table values ('Lewis', +123.099, -123.011, TRUE, 'AGE');"));
        Database newDatabase = new Database("newDatabase");
        this.parser.setDatabase(newDatabase);
        this.parser.getTokeniser().preprocessQuery();

        //Assert no exception is thrown
        try {
            this.parser.parseCommand();
        } catch (DatabaseException e) {
            fail("Incorrect exception:" + e.getMessage());
        }
    }
    //Test parseInsert with invalid syntax - missing "INTO"
    @Test
    public void testParseInsertInvalid() throws DatabaseException, IOException {
        this.parser = new Parser(new Tokeniser("insert table values ('Lewis', +123.099, -123.011, TRUE, 'AGE');"));
        Database newDatabase = new Database("newDatabase");
        this.parser.setDatabase(newDatabase);
        this.parser.getTokeniser().preprocessQuery();
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            this.parser.parseCommand();
        });
    }

    //Test parseInsert with invalid syntax - used { instead of (
    @Test
    public void testParseInsertInvalid2() throws DatabaseException, IOException {
        this.parser = new Parser(new Tokeniser("insert into table values {'Lewis', +123.099, -123.011, TRUE, 'AGE'};"));
        Database newDatabase = new Database("newDatabase");
        this.parser.setDatabase(newDatabase);
        this.parser.getTokeniser().preprocessQuery();
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            this.parser.parseCommand();
        });
    }

    //Test parseWhere with valid syntax
    @Test
    public void testParseWhereValid() throws DatabaseException {
        this.parser = new Parser(new Tokeniser("where name != 'Lewis';"));
        this.parser.getTokeniser().preprocessQuery();
        try {
            this.parser.parseWhere(this.parser.getTokeniser().getAllTokens(),0);
        } catch (DatabaseException e) {
            fail("Incorrect exception:" + e.getMessage());
        }
        //Assert conditions are stored
        assertEquals("name", this.parser.getAllConditions().get(0).getAttributeName());
        assertEquals("!=", this.parser.getAllConditions().get(0).getComparator());
        assertEquals("'Lewis'", this.parser.getAllConditions().get(0).getBaseValue());
    }

    //Test parseWhere with two valid conditions
    @Test
    public void testParseWhereValid2() throws DatabaseException {
        this.parser = new Parser(new Tokeniser("where ( name != 'lewis' ) and ( age > 20 );"));
        this.parser.getTokeniser().preprocessQuery();
        try {
            this.parser.parseWhere(this.parser.getTokeniser().getAllTokens(),0);
        } catch (DatabaseException e) {
            fail("Incorrect exception:" + e.getMessage());
        }
        //Assert conditions and booloperator are stored
        assertEquals("name", this.parser.getAllConditions().get(0).getAttributeName());
        assertEquals("!=", this.parser.getAllConditions().get(0).getComparator());
        assertEquals("'lewis'", this.parser.getAllConditions().get(0).getBaseValue());
        assertEquals("age", this.parser.getAllConditions().get(1).getAttributeName());
        assertEquals(">", this.parser.getAllConditions().get(1).getComparator());
        assertEquals("20", this.parser.getAllConditions().get(1).getBaseValue());
        assertEquals("AND", this.parser.getAllBoolOperators().get(0));

    }
    //Test parseWhere with invalid syntax
    @Test
    public void testParseWhereInvalid() throws DatabaseException {
        this.parser = new Parser(new Tokeniser("where ( name != 'Lewis' and ( age > 20 );"));
        this.parser.getTokeniser().preprocessQuery();
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            this.parser.parseWhere(this.parser.getTokeniser().getAllTokens(),1);
        });
    }
    //Test parseSelect with valid syntax
    @Test
    public void testParseSelectValid() throws DatabaseException {
        this.parser = new Parser(new Tokeniser("select name, age from table where name != 'Lewis';"));
        this.parser.getTokeniser().preprocessQuery();
        try {
            this.parser.parseCommand();
        } catch (DatabaseException e) {
            fail("Incorrect exception:" + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //Assert conditions are stored
        assertEquals("name", this.parser.getAllConditions().get(0).getAttributeName());
        assertEquals("!=", this.parser.getAllConditions().get(0).getComparator());
        assertEquals("'Lewis'", this.parser.getAllConditions().get(0).getBaseValue());
        //Not yet implement attributes addition......
    }

    //Test parseSelect with invalid syntax
    @Test
    public void testParseSelectInvalid() throws DatabaseException {
        this.parser = new Parser(new Tokeniser("select name age table where name != 'Lewis';"));
        this.parser.getTokeniser().preprocessQuery();
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            this.parser.parseCommand();
        });
    }

    //Test parseNameValueList with valid syntax
    @Test
    public void testParseNameValueListValid() throws DatabaseException {
        this.parser = new Parser(new Tokeniser("set name = 'Lewis' where age = 20;"));
        this.parser.getTokeniser().preprocessQuery();
        try {
            this.parser.parseNameValueList(0,4);
        } catch (DatabaseException e) {
            fail("Incorrect exception:" + e.getMessage());
        }
    }

    //Test parseNameValueList with 2 pairs
    @Test
    public void testParseNameValueListValid2() throws DatabaseException {
        this.parser = new Parser(new Tokeniser("set name = 'Lewis' , money = 100 where age = 20;"));
        this.parser.getTokeniser().preprocessQuery();
        try {
            this.parser.parseNameValueList(0,4);
        } catch (DatabaseException e) {
            fail("Incorrect exception:" + e.getMessage());
        }
    }

    //Test parseNameValueList with invalid syntax (without ,)
    @Test
    public void testParseNameValueListInvalid() throws DatabaseException {
        this.parser = new Parser(new Tokeniser("set name = 'Lewis' money = 100 where age = 20"));
        this.parser.getTokeniser().preprocessQuery();
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            this.parser.parseNameValueList(0,4);
        });
    }

    //Test parseUpdate with full command
    @Test
    public void testParseUpdateValid() throws DatabaseException {
        this.parser = new Parser(new Tokeniser("update tablename set name = 'Lewis' , money = 100 where age == 20;"));
        this.parser.getTokeniser().preprocessQuery();
        Database newDatabase = new Database("newDatabase");
        this.parser.setDatabase(newDatabase);
        try {
            this.parser.parseCommand();
        } catch (DatabaseException e) {
            fail("Incorrect exception:" + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //Assert conditions are stored
        assertEquals("age", this.parser.getAllConditions().get(0).getAttributeName());
        assertEquals("==", this.parser.getAllConditions().get(0).getComparator());
        assertEquals("20", this.parser.getAllConditions().get(0).getBaseValue());
    }

    //Test parseDelete with valid syntax
    @Test
    public void testParseDeleteValid() throws DatabaseException {
        this.parser = new Parser(new Tokeniser("delete from tablename where age <= 20;"));
        this.parser.getTokeniser().preprocessQuery();
        Database newDatabase = new Database("newDatabase");
        DBTable table = new DBTable(newDatabase.getDatabaseFolderPath(), "tablename");
        newDatabase.addDBTable(table);
        //Ensure tablename exists

        this.parser.setDatabase(newDatabase);
        try {
            this.parser.parseCommand();
        } catch (DatabaseException e) {
            fail("Incorrect exception:" + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //Assert conditions are stored
        assertEquals("age", this.parser.getAllConditions().get(0).getAttributeName());
        assertEquals("<=", this.parser.getAllConditions().get(0).getComparator());
        assertEquals("20", this.parser.getAllConditions().get(0).getBaseValue());
    }

    //Test parseDelete with invalid syntax - without FROM
    @Test
    public void testParseDeleteInvalid() throws DatabaseException {
        this.parser = new Parser(new Tokeniser("delete tablename where age <= 20;"));
        this.parser.getTokeniser().preprocessQuery();
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            this.parser.parseCommand();
        });
    }

    //Test parseDelete without existing table, correct syntax
    @Test
    public void testParseDeleteInvalid2() throws DatabaseException {
        this.parser = new Parser(new Tokeniser("delete from tablename where age <= 20;"));
        this.parser.getTokeniser().preprocessQuery();
        Database newDatabase = new Database("newDatabase");
        this.parser.setDatabase(newDatabase);
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            this.parser.parseCommand();
        });
    }




}








