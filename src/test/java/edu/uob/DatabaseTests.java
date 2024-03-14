package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DatabaseTests {
    private Database database;
    private DBTable table;



    @BeforeEach
    public void setUp() {
        this.database = new Database("lewis");
        this.table = new DBTable(this.database.getDatabaseFolderPath());

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

    @Test
    //Test addDBTable - by adding 2 tables
    public void testAddDBTable() throws IOException {

         //Create a table class
         DBTable table1 = new DBTable(this.database.getDatabaseFolderPath(), "table1");
         this.database.addDBTable(table1);
         DBTable table2 = new DBTable(this.database.getDatabaseFolderPath(), "table2");
         this.database.addDBTable(table2);
         //Assert table1 and 2 are included in the mapsOfTables
        assertEquals(table1, this.database.getDBTable("table1"));
        assertEquals(table2, this.database.getDBTable("table2"));
    }

    @Test
    //Test setupDatabase
    public void testSetupDatabase() throws IOException {
        //Write a table to the lewis database
        DBTable newTable = new DBTable(this.database.getDatabaseFolderPath(), "newTable");
        //Write attributes
        newTable.addAttribute("id");
        newTable.addAttribute("name");
        //add entries to the table
        Map<String,String> entry1 = new HashMap<>();
        entry1.put("id", "1");
        entry1.put("name", "Lewis");
        Map<String,String> entry2 = new HashMap<>();
        entry2.put("id", "2");
        entry2.put("name", "Jaime");
        newTable.addEntry(entry1);
        newTable.addEntry(entry2);
        //Write a table file
        newTable.writeTable();
        //Add table to lewis database
        //Check if all .tab files in lewis folder can be loaded to lewis database
        this.database.setupDatabase();
        //Assert that the newTable is included in the mapsOfTables
        assertEquals("newTable",this.database.getDBTable("newTable").getTableName());
        //Assert Existing tables (people and sheds) are included in the mapsOfTables
        assertEquals("people",this.database.getDBTable("people").getTableName());
        assertEquals("sheds",this.database.getDBTable("sheds").getTableName());

    }



}
