package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

public class FilePathTests {

    private DBFilePath testPath;
    @BeforeEach

    public void setUp() {
        testPath = new DBFilePath();
    }

    //Test getting the root path
    @Test

    public void testGetRootPath() {

        //Print the root path
        System.out.println(testPath.getRootFolderPath());

        assert(testPath.getRootFolderPath().equals(Paths.get("databases").toAbsolutePath().toString()));
    }
    
    //Test getting the database path
    @Test

    public void testGetDatabasePath(){
        testPath.setDatabaseFolderPath("testDatabase");
        //Print the database path
        System.out.println(testPath.getDatabaseFolderPath());
        assert(testPath.getDatabaseFolderPath().equals(Paths.get("databases").toAbsolutePath().toString() + File.separator + "testDatabase"));
    }

    //Test getting the table file path
    @Test

    public void testGetTableFilePath(){
        testPath.setDatabaseFolderPath("testDatabase");
        testPath.setTableFilePath("testPeople");
        assert(testPath.getTableFilePath().equals(Paths.get("databases").toAbsolutePath().toString() + File.separator+ "testDatabase" + File.separator + "testPeople.tab"));
        //Print the table file path
        System.out.println(testPath.getTableFilePath());
    }

    // Test setting the database path
    @Test
    public void testSetDatabasePath(){
        testPath.setDatabaseFolderPath("testDatabase2");
        assert(testPath.getDatabaseFolderPath().equals(Paths.get("databases").toAbsolutePath().toString() + "testDatabase2"));
    }

    // Test setting the table file path
    @Test
    public void testSetTableFilePath(){
        testPath.setDatabaseFolderPath("testDatabase2");
        testPath.setTableFilePath("testPeople2");
        assert(testPath.getTableFilePath().equals(Paths.get("databases").toAbsolutePath().toString() + "testDatabase2" + File.separator + "testPeople2.tab"));
    }


    
    

}
