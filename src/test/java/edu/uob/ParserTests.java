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
        this.parser.parseCreateTable();
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
        this.parser.parseCommand();
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            this.parser.parseCreateTable();
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
        this.parser.parseCreateTable();
        assertEquals("TABLE", this.parser.getCommand().getWorkingStructure());
        assertEquals(newDatabase, this.parser.getCommand().getWorkingDatabase());
        assertEquals(List.of("name"), this.parser.getCommand().getAttributeList());
    }

}








