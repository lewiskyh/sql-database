package edu.uob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DBFileIO {

    private final String rootFilePath;

    private String databasePath;

    private String filePath;

    private DBTable table;


    public DBFileIO (DBTable table){

        this.table = table;
        this.rootFilePath = Paths.get("databases").toAbsolutePath().toString();
        setDatabasePath(table.getDatabaseName());
        setFilePath(table.getTableName());
    }

    public void setDatabasePath(String databaseName){
        this.databasePath = this.rootFilePath + databaseName;
        this.filePath = "";
    }

    public String getDatabasePath () { return this.databasePath;}

    public void setFilePath (String tableName){
        this.filePath = this.databasePath + File.separator + tableName + ".tab";
    }
    public String getFilePath () { return this.filePath; }
    public DBTable getTable() { return this.table; }
    public void setTable(DBTable table) { this.table = table; }


    public void readFromTable () throws IOException {
        if(this.filePath == null || this.filePath.isEmpty()){
            throw new IOException("File path not set");
        }
        File readFile = new File(this.filePath);
        if (!readFile.exists()) { throw new IOException("Table File does not exist at " + this.filePath); }

        ArrayList<String> lines = new ArrayList<>();

        try(BufferedReader bufferedReader= new BufferedReader(new FileReader(readFile))){
            String line = bufferedReader.readLine();
            while (line != null) {
                lines.add(line);
                line = bufferedReader.readLine();
            }
        } catch (IOException ioe){ System.out.println("Error reading from file: " + ioe.getMessage()); }

        if(!lines.isEmpty()){
            readFirstLineFromTable(lines.get(0));
            readEntries(lines);
        }
    }

    public void readFirstLineFromTable (String firstLine){
        String[] attributes = firstLine.split("\\t");
        for (String attribute : attributes){
            table.addAttribute(attribute);
        }
    }


    public void readEntries (ArrayList<String> lines){
        if (lines.size() < 2) { return; }
        String firstLine = lines.get(0);
        String [] attributes = firstLine.split("\\t");

        //Extract each entry starting from the second row
        for (int j = 1; j < lines.size(); j++){
            String[] entry = lines.get(j).split("\\t");
            Map<String,String> entryMap = new HashMap<>();

            for (int i =0; i< attributes.length ; i++ ){
                String entryValue = i < entry.length ? entry[i] : "";
                entryMap.put(attributes[i], entryValue);
            }
            table.addEntry(entryMap);
        }

    }




}
