package edu.uob;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class DBTable {
    private String tableName;

    private List<String> attributeNames;

    private List<Map<String,String>> rows;

    private Integer numberOfRows;

    //Constructor without table name
    public DBTable(){
        this.tableName = "";
        this.numberOfRows = 0;
        this.attributeNames = new ArrayList<>();
        this.rows = new ArrayList<>();
    }

    //Constructor with table name
    public DBTable(String tableName){
        this.tableName = tableName;
        this.numberOfRows = 0;
        this.attributeNames = new ArrayList<>();
        this.rows = new ArrayList<>();
    }

    public Integer getNumberOfRows() { return this.numberOfRows; }

    public Integer getNumberOfAttributes() { return this.attributeNames.size(); }

    public void setTableName(String tableName) { this.tableName = tableName; }

    public String getTableName() { return this.tableName; }

    public void addAttribute(String attributeName) { this.attributeNames.add(attributeName); }

    public void deleteAttribute (String attributeName) {
        this.attributeNames.remove(attributeName);
        for(Map<String, String> row : this.rows){
            row.remove(attributeName);
        }
    }
    public List<String> getAttributes() { return this.attributeNames; }

    public List <String> getRowByKey (String primaryKey){
        for(Map<String, String> row : rows){
            if(row.get("id").equals(primaryKey)){
                List<String> result = new ArrayList<>();
                for(String attribute : this.attributeNames){
                    result.add(row.get(attribute));
                }
                return result;
            }
        }
        return null;
    }

    public List<Map<String, String>> getAllRows() { return new ArrayList<>(this.rows); }

    public void addRow(Map<String, String> row) {
        this.rows.add(new HashMap<>(row));
        this.numberOfRows++;
    }

    public void deleteRow (String primaryKey){
        for(Map<String, String> row : this.rows){
            if(row.get("id").equals(primaryKey)){
                this.rows.remove(row);
                this.numberOfRows--;
                return;
            }
        }
    }


}
