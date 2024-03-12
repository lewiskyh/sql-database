package edu.uob;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class DBTable {
    private String tableName;

    private String databaseName;

    private List<String> attributes;

    private List<Map<String,String>> entries;

    private Integer numberOfEntries;

    public DBTable(String databaseName){
        this.tableName = "";
        this.numberOfEntries = 0;
        this.attributes = new ArrayList<>();
        this.entries = new ArrayList<>();
        this.databaseName = databaseName;
    }

    public Integer getNumberOfEntries() { return this.numberOfEntries; }

    public void setTableName(String tableName) { this.tableName = tableName; }

    public String getTableName() { return this.tableName; }

    public void addAttribute(String attributeName) { this.attributes.add(attributeName); }

    public void deleteAttribute (String attributeName) {
        this.attributes.remove(attributeName);
        for(Map<String, String> entry : entries){
            entry.remove(attributeName);
        }
    }
    public List<String> getAttributes() { return this.attributes; }

    public List <String> getEntry (String primaryKey){
        for(Map<String, String> entry : entries){
            if(entry.get("id").equals(primaryKey)){
                List<String> result = new ArrayList<>();
                result.addAll(entry.values());
                return result;
            }
        }
        return null;
    }

    public void addEntry(Map<String, String> entry) {
        this.entries.add(new HashMap<>(entry));
        this.numberOfEntries++;
    }

    public void deleteEntry (String primaryKey){
        for(Map<String, String> entry : entries){
            if(entry.get("id").equals(primaryKey)){
                entries.remove(entry);
                this.numberOfEntries--;
                return;
            }
        }
    }

    public String getDatabaseName () { return this.databaseName; }


}
