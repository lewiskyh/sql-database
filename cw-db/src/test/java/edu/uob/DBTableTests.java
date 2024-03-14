package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.List;

public class DBTableTests {

    private DBTable table;

    @BeforeEach
    public void setUp() {
        table = new DBTable();
    }

    // Test the table name is set correctly
    @Test
    public void testSetTableName() {
        table.setTableName("testTable");
        assert(table.getTableName().equals("testTable"));
    }

    // Test getting table name
    @Test
    public void testGetTableName() {
        table.setTableName("testTable");
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

    }

    //Test getting an entry
    @Test
    public void testGetEntry(){
        table.addAttribute("id");
        table.addAttribute("testAttribute2");
        table.addAttribute("testAttribute3");
        table.addRow((Map.of("id", "1", "testAttribute2", "2", "testAttribute3", "3")));
        assert(table.getRowByKey("1").equals(List.of("1", "2", "3")));
    }

    //Test adding 2 entries and getting the number of entries
    @Test
    public void testAddEntry(){
        table.addAttribute("id");
        table.addAttribute("testAttribute2");
        table.addAttribute("testAttribute3");
        table.addRow((Map.of("id", "1", "testAttribute2", "2", "testAttribute3", "3")));
        table.addRow((Map.of("id", "2", "testAttribute2", "10", "testAttribute3", "11")));
        assert(table.getRowByKey("2").equals(List.of("2", "10", "11")));
        assert(table.getNumberOfRows() == 2);
    }

    //Test deleting 2 entries and getting the new number of entries
    @Test
    public void testDeleteEntry(){
        table.addAttribute("id");
        table.addAttribute("testAttribute2");
        table.addAttribute("testAttribute3");
        table.addRow((Map.of("id", "1", "testAttribute2", "2", "testAttribute3", "3")));
        table.addRow((Map.of("id", "2", "testAttribute2", "10", "testAttribute3", "11")));
        table.addRow((Map.of("id", "3", "testAttribute2", "20", "testAttribute3", "21")));
        assert(table.getNumberOfRows() == 3);
        table.deleteRow("1");
        table.deleteRow("3");
        assert(table.getRowByKey("1") == null);
        assert(table.getRowByKey("3") == null);
        assert(table.getRowByKey("2").equals(List.of("2", "10", "11")));
        assert(table.getNumberOfRows() == 1);
    }

}
