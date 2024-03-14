package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Map;
import java.util.List;

public class DBTableTests {

    private DBTable table;

    private Database database;

    @BeforeEach
    public void setUp() {
        database = new Database("lewis");

        table = new DBTable(database.getDatabaseFolderPath());
    }

    // Test the table name is set correctly
    @Test
    public void testSetTableName() {
        table.setTable("testTable");
        assert(table.getTableName().equals("testTable"));
    }

    // Test getting table name
    @Test
    public void testGetTableName() {
        table.setTable("testTable");
        assert(table.getTableName().equals("testTable"));
    }

    // Test adding an attribute
    @Test
    public void testAddAttribute() {
        table.addAttribute("testAttribute");
        assert(table.getAttributes().contains("testAttribute"));
    }

    //Test deleting an attribute
    @Test
    public void testDeleteAttribute() {
        table.addAttribute("testAttribute");
        table.deleteAttribute("testAttribute");
        assert(!table.getAttributes().contains("testAttribute"));
    }

    //Test getting a list of attribute
    @Test
    public void testGetAttributes(){
        table.addAttribute("testAttribute");
        table.addAttribute("testAttribute2");
        table.addAttribute("testAttribute3");
        assert(table.getAttributes().equals(List.of("testAttribute", "testAttribute2", "testAttribute3")));
        assertEquals(3, table.getNumberOfAttributes());
    }


    //Test getting an entry
    @Test
    public void testGetEntry(){
        table.addAttribute("id");
        table.addAttribute("testAttribute2");
        table.addAttribute("testAttribute3");
        table.addEntry((Map.of("id", "1", "testAttribute2", "2", "testAttribute3", "3")));
        assert(table.getEntryByKey("1").equals(List.of("1", "2", "3")));
    }

    //Test adding 2 entries and getting the number of entries
    @Test
    public void testAddEntry(){
        table.addAttribute("id");
        table.addAttribute("testAttribute2");
        table.addAttribute("testAttribute3");
        table.addEntry((Map.of("id", "1", "testAttribute2", "2", "testAttribute3", "3")));
        table.addEntry((Map.of("id", "2", "testAttribute2", "10", "testAttribute3", "11")));
        assert(table.getEntryByKey("2").equals(List.of("2", "10", "11")));
        assert(table.getNumberOfEntries() == 2);
    }

    //Test deleting 2 enties and getting the new number of entries
    @Test
    public void testDeleteEntry(){
        table.addAttribute("id");
        table.addAttribute("testAttribute2");
        table.addAttribute("testAttribute3");
        table.addEntry((Map.of("id", "1", "testAttribute2", "2", "testAttribute3", "3")));
        table.addEntry((Map.of("id", "2", "testAttribute2", "10", "testAttribute3", "11")));
        table.addEntry((Map.of("id", "3", "testAttribute2", "20", "testAttribute3", "21")));
        assert(table.getNumberOfEntries() == 3);
        table.deleteEntry("1");
        table.deleteEntry("3");
        assert(table.getEntryByKey("1") == null);
        assert(table.getEntryByKey("3") == null);
        assert(table.getEntryByKey("2").equals(List.of("2", "10", "11")));
        assert(table.getNumberOfEntries() == 1);
    }

    //Test getting all entries
    @Test
    public void testGetAllEntries(){
        table.addAttribute("id");
        table.addAttribute("testAttribute2");
        table.addAttribute("testAttribute3");
        table.addEntry((Map.of("id", "1", "testAttribute2", "2", "testAttribute3", "3")));
        table.addEntry((Map.of("id", "2", "testAttribute2", "10", "testAttribute3", "11")));
        table.addEntry((Map.of("id", "3", "testAttribute2", "20", "testAttribute3", "21")));
        assert(table.getAllEntries().equals(List.of(Map.of("id", "1", "testAttribute2", "2", "testAttribute3", "3"), Map.of("id", "2", "testAttribute2", "10", "testAttribute3", "11"), Map.of("id", "3", "testAttribute2", "20", "testAttribute3", "21"))));
    }

    //Test readFromTable method on people.tab

    @Test
    public void testReadFromTable(){
        table.setTable("people");
        try {
            table.readFromTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(3, table.getNumberOfEntries());
        assertEquals(4, table.getNumberOfAttributes());
        //Assert getEnrtyByKey get the expected result
        assert(table.getEntryByKey("1").equals(List.of("1", "Bob", "21", "bob@bob.net")));
        //Assert element of attributes
        assert(table.getAttributes().equals(List.of("id", "Name", "Age", "Email")));

    }

    //Test writetable method

    @Test
    public void testWriteTable() throws IOException {
        //In lewis database
        table.setTable("testTable");
        table.addAttribute("id");
        table.addAttribute("Age");
        table.addAttribute("Sex");
        table.addEntry((Map.of("id", "1", "Age", "21", "Sex", "M")));
        table.addEntry((Map.of("id", "2", "Age", "22", "Sex", "F")));
        table.writeTable();
        assertEquals(2, table.getNumberOfEntries());
        assertEquals(3, table.getNumberOfAttributes());
        //Assert getEnrtyByKey get the expected result
        assert(table.getEntryByKey("1").equals(List.of("1", "21", "M")));
        //Assert element of attributes
        assert(table.getAttributes().equals(List.of("id","Age","Sex")));
        //Delete row1
        table.deleteEntry("1");
        table.writeTable();
        assertEquals(1, table.getNumberOfEntries());
        assertEquals(3, table.getNumberOfAttributes());
        assert(table.getEntryByKey("1") == null);
        assert(table.getEntryByKey("2").equals(List.of("2", "22", "F")));
    }



}
