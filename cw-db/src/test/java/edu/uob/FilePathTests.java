package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.nio.file.Paths;

public class FilePathTests {

    private FilePath testPath;
    @BeforeEach

    public void setUp() {
        testPath = new FilePath();
    }

    //Test getting the root path
    @Test

    public void testGetRootPath() {

        assert(testPath.getRootFolderPath().equals(Paths.get("databases").toAbsolutePath().toString()));
    }
    
    //Test setting the database path

    
    

}
