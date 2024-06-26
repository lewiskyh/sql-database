package edu.uob;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class DBTable {
    private String tableName;

    private List<String> attributes;

    private List<Map<String,String>> rows;

    private Integer maxID; // Only change when new entry added

    private String tableFilePath;

    private String databaseFolderPath;

    public DBTable(String databaseFolderPath){
        this.tableName = "";
        this.attributes = new ArrayList<>();
        this.rows = new ArrayList<>();
        this.maxID = 0;
        this.databaseFolderPath = databaseFolderPath;
        this.tableFilePath = databaseFolderPath + File.separator + this.tableName + ".tab";
        this.attributes.add("id");
    }

    //Constructor with table name
    public DBTable(String databaseFolderPath, String tableName){
        this.tableName = tableName;
        this.maxID = 0;
        this.attributes = new ArrayList<>();
        this.rows = new ArrayList<>();
        this.databaseFolderPath = databaseFolderPath;
        this.tableFilePath = databaseFolderPath + File.separator + this.tableName + ".tab";
        this.attributes.add("id");
    }
    //Copy constructor
    public DBTable(DBTable displayTable){
        this.tableName = displayTable.tableName;
        this.attributes = new ArrayList<>(displayTable.attributes);
        this.rows = new ArrayList<>(displayTable.rows);
        this.maxID = displayTable.maxID;
        this.databaseFolderPath = displayTable.databaseFolderPath;
        this.tableFilePath = displayTable.tableFilePath;
    }


    public Integer getMaxID() { return this.maxID; }

    public void setTable(String tableName) {
        this.tableName = tableName;
        this.tableFilePath = this.databaseFolderPath + File.separator + this.tableName + ".tab";
    }

    public String getTableName() { return this.tableName; }

    public void addAttribute(String attributeName) { this.attributes.add(attributeName); }

    public void deleteAttribute (String attributeName) {
        List<String> attributesIgnoreCase = this.getAttributes();
        String targetAttribute = "";
        for (String attribute : attributesIgnoreCase) {
            if(attribute.equalsIgnoreCase(attributeName.toLowerCase())){
                targetAttribute = attribute;
                this.attributes.remove(attribute);
                break;
            }
        }
        for(Map<String, String> row : rows){
            row.remove(targetAttribute);
        }
    }
    public List<String> getAttributes() { return this.attributes; }

    public String getTableFilePath() { return this.tableFilePath; }

    public List<Map<String, String>> getAllEntries() { return new ArrayList<>(this.rows); }

    public void addEntry(Map<String, String> entry) {
        this.rows.add(new HashMap<>(entry));
        this.maxID++;
    }


    public void deleteEntry (String primaryKey){
        for(Map<String, String> row : rows){
            if(row.get("id").equals(primaryKey)){
                rows.remove(row);
                return;
            }
        }
    }

    public void updateEntry(String primaryKey, String attributeName, String newValue){
        for(Map<String, String> row : rows){
            if(row.get("id").equals(primaryKey)){
                if(newValue.startsWith("'") && newValue.endsWith("'")){
                    newValue = newValue.substring(1, newValue.length() - 1);
                }
                row.put(attributeName, newValue);
                return;
            }
        }
    }



    public void readFromTable() throws IOException {
        if(this.tableFilePath == null || this.tableFilePath.isEmpty()){
            throw new IOException("File path not set");
        }
        File readFile = new File(this.tableFilePath);
        if (!readFile.exists()) {
            throw new IOException("Table File does not exist at " + this.tableFilePath);
        }

        try(BufferedReader bufferedReader= new BufferedReader(new FileReader(readFile))){
            String line = bufferedReader.readLine();
            // the attributes from first line
            if(line!=null){
                String[] attributes = line.split("\\t");
                this.attributes.clear();
                for (String attribute : attributes){
                    this.attributes.add(attribute);
                }
            }
            this.rows.clear();
            while((line = bufferedReader.readLine()) != null && !line.trim().isEmpty()){
                processRows(line);
            }
        } catch (IOException ioe) {
            System.out.println("Error reading table: " + ioe.getMessage());
        }
    }

    public void processRows (String line){
        String[] row = line.split("\\t");
        Map<String, String> rowMap = new HashMap<>();
        for (int i = 0; i< this.attributes.size(); i++){
            String data = i < row.length ? row[i] : "";
            rowMap.put(this.attributes.get(i), data);
        }
        addEntry(rowMap);
    }
    public void writeTable() throws IOException {
        File writeFile = new File(this.tableFilePath);

        if (!writeFile.exists()){writeFile.createNewFile();}

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(writeFile))) {
            writeFirstLine(bufferedWriter);
            writeRows(bufferedWriter);
        } catch (IOException ioe) {
            System.out.println("Error writing to file: " + ioe.getMessage());
        }
    }

    private void writeFirstLine (BufferedWriter bufferedWriter) throws IOException {
        for (String attribute : this.getAttributes()) {
            bufferedWriter.write(attribute + "\t");
        }
        //move to next line
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    private void writeRows (BufferedWriter bufferedWriter) throws IOException {
        List<Map<String, String>> allRows = this.getAllEntries();
        List<String> attributes = this.getAttributes();

        for(Map<String, String> row: allRows){
            for (int i = 0; i< attributes.size(); i++){
                String data = row.get(attributes.get(i));
                //if data start and end with single quote, trim them
                if (data == null) { data = ""; }
                else if (data.startsWith("'") && data.endsWith("'")) {
                    data = data.substring(1, data.length() - 1);
                }
                bufferedWriter.write(data + "\t");
            }
            //move to next line
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();
    }


}
