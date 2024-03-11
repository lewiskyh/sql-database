package edu.uob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;

public class FileIO {
    /**
    private String filePath;

    private String tableName;


    public FileIO (String DBPath, String tableName){
        this.filePath = DBPath + File.separator + tableName + ".tab";
        this.tableName = tableName;
    }

    public void readTable () throws IOException {
        String fullFileName = this.filePath + File.separator + this.tableName + ".tab";
        File fileToOpen = new File(fullFileName);

    }


    public Boolean readDataFromFile(String fileName){
        try{
            String fullFileName = this.storageFolderPath + File.separator + fileName;
            File fileToOpen = new File(fullFileName);

            try (FileReader reader = new FileReader(fileToOpen);
                 BufferedReader buffReader = new BufferedReader(reader)){
                String line = buffReader.readLine();
                while (line != null) {
                    System.out.println(line);
                    line = buffReader.readLine();
                }
                return true;
            }
        } catch(IOException ioe){
            System.out.println("Error reading from file: " + ioe.getMessage());
            return false;
        }
    }

     /**Create table based on the query from user, for reference ONLY
     public void createTable (String dataBaseName, String tableName, List<String> columnName){
     try {
     dataBaseName = dataBaseName.toLowerCase();
     tableName = tableName.toLowerCase();

     //Create a folder for the database
     File dataBaseFolder = new File(this.storageFolderPath + File.separator + dataBaseName);
     if (!dataBaseFolder.exists()) {
     dataBaseFolder.mkdirs();
     }
     //Create a file for the table
     String tablePath = this.storageFolderPath + File.separator + dataBaseName + File.separator + tableName + ".tab";

     //Check if same table name already exists
     File tableFile = new File(tablePath);
     if (tableFile.exists()) {
     throw new IOException("Table already exists");
     }
     try (BufferedWriter buffWriter = new BufferedWriter(new FileWriter(tableFile))) {
     //Write the column names to the table
     for (String name : columnName) {
     buffWriter.write(name + "\t");
     }
     buffWriter.newLine();
     buffWriter.flush();
     }
     }catch(IOException ioe){
     System.out.println("Error creating table: " + ioe.getMessage());
     }
     }*/
}
