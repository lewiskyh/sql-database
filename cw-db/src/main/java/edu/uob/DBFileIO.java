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
import java.util.List;

public class DBFileIO {

    private DBTable dbTable;

    private String tableFilePath;

    private DBFilePath filePath;

    public DBFileIO (DBFilePath filePath, DBTable table){

        this.dbTable = table;
        this.filePath = filePath;
        this.tableFilePath = filePath.getDatabaseFolderPath() + File.separator + table.getTableName() + ".tab";
    }


    public DBTable getDBTable() { return this.dbTable; }
    public void setDBTable(DBTable table) {
        this.dbTable = table;
        this.tableFilePath = this.filePath.getDatabaseFolderPath() + File.separator + table.getTableName() + ".tab";

    }

    public String getTableFilePath() { return this.tableFilePath; }



    public void readFromTable () throws IOException {
        if(this.tableFilePath == null || this.tableFilePath.isEmpty()){
            throw new IOException("File path not set");
        }
        File readFile = new File(this.tableFilePath);
        if (!readFile.exists()) { throw new IOException("Table File does not exist at " + this.tableFilePath); }

        ArrayList<String> lines = new ArrayList<>();
        BufferedReader bufferedReader= new BufferedReader(new FileReader(readFile));
        try{
            String line = bufferedReader.readLine();
            while (line != null && !line.trim().isEmpty()) {
                lines.add(line);
                line = bufferedReader.readLine();
            }

            bufferedReader.close();

        } catch (IOException ioe){ System.out.println("Error reading from file: " + ioe.getMessage()); }

        readFirstLine(lines.get(0));
        if(lines.size() > 1) {
            //Only pass the second row onwards to readEntries
            ArrayList<String> entries = new ArrayList<>(lines.subList(1, lines.size()));
            readEntries(entries);

        }
    }

    public void readFirstLine (String firstLine){
        String[] attributes = firstLine.split("\\t");
        for (String attribute : attributes){
            dbTable.addAttribute(attribute);
        }
    }


    public void readEntries (ArrayList<String> entryLines) {

        //Extract each entry
        for (String line : entryLines) {
            String[] entry = line.split("\\t");

            Map<String, String> entryMap = new HashMap<>();

            for (int i = 0; i < dbTable.getAttributes().size(); i++) {
                String entryValue = i < entry.length ? entry[i] : "";
                entryMap.put(dbTable.getAttributes().get(i), entryValue);
            }
            dbTable.addEntry(entryMap);
        }
    }

    public void writeToTable() throws IOException{
        File writeFile = new File(this.tableFilePath);

        writeFile.createNewFile();
        //Check if writeFile exists after creating it
        if (!writeFile.exists()){
            throw new IOException("Table File does not exist at " + this.tableFilePath);
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(writeFile));
        writeFirstLine(bufferedWriter);
        writeEntries(bufferedWriter);
        bufferedWriter.close();
    }

    public void writeFirstLine (BufferedWriter bufferedWriter) throws IOException{
        for (String attribute : this.dbTable.getAttributes()){
            bufferedWriter.write(attribute + "\t");
        }
        //Move to next line
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    public void writeEntries (BufferedWriter bufferedWriter) throws IOException{
        List<Map<String, String>> allEntries = this.dbTable.getAllEntries();
        List<String> attributes = this.dbTable.getAttributes();

        //Write each entry
        for (Map<String, String> entry : allEntries){
            //For each entry, iterate through the attributes and write the data
            for (int i =0; i< attributes.size(); i++){
                String data = entry.get(attributes.get(i));
                if(data == null){ data = ""; }
                bufferedWriter.write(data + "\t");
            }
            //Move to next line
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();
    }





}
