package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MethodTests {

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






}
