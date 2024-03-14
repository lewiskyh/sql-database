package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DatabaseTests {
    private Database database;


    private DBTable table;

    private DBFileIO fileIO;

    @BeforeEach
    public void setUp() {

    }
    //Test getDatabaseName
    @Test
    public void testGetDatabaseName(){
        assertEquals("lewis", database.getDatabaseName());
    }

    //Test setDatabaseName
    @Test
    public void testSetDatabaseName(){
        database.setDatabaseName("newName");
        assertEquals("newName", database.getDatabaseName());
    }

    //Test addDBTable - by adding 2 tables
    @Test
    public void testAddDBTable() throws IOException {

         //Create a table class
         DBTable table1 = new DBTable("table1");
         this.fileIO.setDBTable(table1);
         this.database.addDBTable(table1);
         DBTable table2 = new DBTable("table2");
         this.fileIO.setDBTable(table2);
         this.database.addDBTable(table2);
         //Assert table1 and 2 are included in the mapsOfTables
        assertEquals(table1, this.database.getDBTable("table1"));
        assertEquals(table2, this.database.getDBTable("table2"));

    }

    //Test setupDatabase
    /*public void testSetupDatabase() throws IOException {
        //Write a table to the lewis database
        DBTable newTable = new DBTable("newTable");
        //Write attributes
        newTable.addAttribute("id");
        newTable.addAttribute("name");
        //Add table to lewis database
        //Check if all .tab files in lewis folder can be loaded to lewis database
        this.database.setupDatabase("lewis");
        this.database.addDBTable(newTable);
        //Assert that the two tables have the correct number of attributes
        assertEquals(4, this.database.getDBTable("people").getNumberOfAttributes());
        assertEquals(4, this.database.getDBTable("sheds").getNumberOfAttributes());
        assertEquals(2, this.database.getDBTable("newTable").getNumberOfAttributes());

    }*/



}
