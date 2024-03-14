package edu.uob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DBFileIOTests{

    private DBFileIO fileIO;

    private DBTable table;

    private DBFilePath filePath;


    @BeforeEach

    public void setUp() {

        table = new DBTable();
        filePath = new DBFilePath();
        fileIO = new DBFileIO(filePath, table);
    }

    //Test getDBTable
    @Test
    public void testGetDBTabl(){
        assertEquals(this.table, fileIO.getDBTable());
    }

    //Test setDBTable
    @Test
    public void testSetDBTable(){
        DBTable testingTable = new DBTable();
        fileIO.setDBTable(testingTable);
        assertEquals(testingTable, fileIO.getDBTable());
    }

    //Test readEntries
    @Test
    public void testReadEntries() throws IOException {
        //Create an array list of strings
        ArrayList <String> lines = new ArrayList<>();
        lines.add("A\tB\tC");
        lines.add("4\t5\t6");
        lines.add("7\t8\t9");
        lines.add("10\t11\t12");
        assertEquals(4, lines.size());
        fileIO.readEntries(new ArrayList<>(lines.subList(1, lines.size())));
        assertEquals(3, fileIO.getDBTable().getNumberOfRows());
    }

    // Test readFirstLineFromTable
    @Test
    public void testReadFirstLineFromTable() {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("A\tB\tC");
        lines.add("1\t2\t3");
        lines.add("4\t5\t6");
        assertEquals(3, lines.size());
        fileIO.readFirstLine(lines.get(0));
        //Assert first attribute is A, last attribute is C, second attribute is B
        assertEquals("A", fileIO.getDBTable().getAttributes().get(0));
        assertEquals("B", fileIO.getDBTable().getAttributes().get(1));
        assertEquals("C", fileIO.getDBTable().getAttributes().get(2));
    }

    // Test readFromTable
    @Test
    public void testReadFromTable() throws IOException {
        DBTable peopleTable = new DBTable();
        peopleTable.setTableName("people");
        this.filePath.setDatabaseFolderPath("lewis");
        this.fileIO = new DBFileIO(this.filePath, peopleTable);

        //Read from people.tab
        this.fileIO.readFromTable();
        assertEquals(3, this.fileIO.getDBTable().getNumberOfRows());
    }
    // Test readFromTable2
    @Test
    public void testReadFromTable2() throws IOException {
        DBTable shedTable = new DBTable();
        shedTable.setTableName("sheds");
        this.filePath.setDatabaseFolderPath("lewis");
        this.fileIO = new DBFileIO(this.filePath, shedTable);

        //Read from sheds.tab
        this.fileIO.readFromTable();
        assertEquals(3, this.fileIO.getDBTable().getNumberOfRows());
    }

    //Test writeToTable
    @Test
    public void testWriteToTable() throws IOException {
        DBTable testTable = new DBTable("testTable");
        this.filePath.setDatabaseFolderPath("lewis");
        this.fileIO = new DBFileIO(this.filePath, testTable);
        testTable.addAttribute("id"); testTable.addAttribute("value"); testTable.addAttribute("value2");
        testTable.addRow(Map.of("id", "1", "value", "100", "value2", "20"));
        testTable.addRow(Map.of("id", "2", "value", "30", "value2", "40"));
        testTable.addRow(Map.of("id", "3", "value", "50", "value2", "60"));
        this.fileIO.writeToTable();
        assertEquals(3, this.fileIO.getDBTable().getNumberOfRows());
        assertEquals("1", this.fileIO.getDBTable().getRowByKey("1").get(0));
        assertEquals("60", this.fileIO.getDBTable().getRowByKey("3").get(2));
        //Delete row 1 to see if writeToTable can update the table
        testTable.deleteRow("1");
        this.fileIO.writeToTable();
        assertEquals(2, this.fileIO.getDBTable().getNumberOfRows());
        assertEquals("30", this.fileIO.getDBTable().getRowByKey("2").get(1));
        assertEquals("50", this.fileIO.getDBTable().getRowByKey("3").get(1));
    }





}
