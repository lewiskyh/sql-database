package edu.uob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTests {

    private Parser parser;

    private Integer tokenIndex;

    private final String rootFolderPath = Paths.get("databases").toString();

    private DBTable table;

    @BeforeEach

    public void setUp() {
        this.parser = new Parser(new Tokeniser("SELECT * FROM table12;"));
    }

    //Test parseCommand
    @Test

    public void testParseCommand() {
        this.parser.getTokeniser().preprocessQuery();
        //Should be silent as the query starts with correct command type and ends with ";"
        assertTrue(parser.parseCommand());
        assertEquals("*", this.parser.getTokeniser().getTokenByIndex(1));
        assertEquals("FROM", this.parser.getTokeniser().getTokenByIndex(2));
        assertEquals(";", this.parser.getTokeniser().getTokenByIndex(4));

    }

    //Test parseUse to see if lewis database can be set up and inspect the existence and linkage of tables
    @Test

    public void testParseUse() throws IOException {
        this.parser = new Parser(new Tokeniser("use lewis;"));
        this.parser.getTokeniser().preprocessQuery();
        //lewis database object should be created and the database is setup
        //files in the lewis database should be linked to lewis' database object
        this.parser.parseUse(this.parser.getTokeniser().getTokenByIndex(1));
        assertEquals("lewis", this.parser.getDatabase().getDatabaseName());
        //assert lewis's tablemap contains people and shed table
        assertEquals("people", this.parser.getDatabase().getDBTable("people").getTableName());
    }
    //Test parseNames with invalid name
    @Test

    public void testParseName(){
        String token = "lewis!";
        assertFalse(parser.parseName(token));
    }

    //Test parseAttribute list with valid attribute list
    @Test

    public void testParseAttributeList() throws IOException {
        ArrayList<String> attributes = new ArrayList<>();
        //Create a list of attributes
        attributes.add("name123");
        attributes.add(",");
        attributes.add("ag122e");
        attributes.add(",");
        attributes.add("money");
        assertTrue(this.parser.parseAttributeList(attributes));
    }

    //Test parseAttribute list with invalid attribute list
    @Test
    public void testParseAttributeListInvalid() throws IOException {
        ArrayList<String> attributes = new ArrayList<>();
        //Create a list of attributes
        attributes.add("name123");
        attributes.add(",");
        attributes.add("ag122e");
        attributes.add(",");
        assertFalse(this.parser.parseAttributeList(attributes));
    }

    //Test parseDatabase with a valid token
    @Test
    public void testParseDatabase() throws IOException {
        this.parser = new Parser(new Tokeniser("create database newDatabase;"));
        this.parser.getTokeniser().preprocessQuery();
        assertTrue(this.parser.parseCreateDatabase());
    }

    //Test parseDatabase with an invalid token
    @Test
    public void testParseDatabaseInvalid() throws IOException {
        this.parser = new Parser(new Tokeniser("create newDatabase;"));
        this.parser.getTokeniser().preprocessQuery();
        assertFalse(this.parser.parseCreateDatabase());
    }









}
