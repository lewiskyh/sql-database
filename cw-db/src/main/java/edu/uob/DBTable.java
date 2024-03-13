package edu.uob;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class DBTable {
    private String tableName;

    private List<String> attributes;

    private List<Map<String,String>> entries;

    private Integer numberOfEntries;

    public DBTable(){
        this.tableName = "";
        this.numberOfEntries = 0;
        this.attributes = new ArrayList<>();
        this.entries = new ArrayList<>();
    }

    public Integer getNumberOfEntries() { return this.numberOfEntries; }

    public Integer getNumberOfAttributes() { return this.attributes.size(); }

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

    public List <String> getEntryByKey (String primaryKey){
        for(Map<String, String> entry : entries){
            if(entry.get("id").equals(primaryKey)){
                List<String> result = new ArrayList<>();
                for(String attribute : this.attributes){
                    result.add(entry.get(attribute));
                }
                return result;
            }
        }
        return null;
    }

    public List<Map<String, String>> getAllEntries() { return new ArrayList<>(this.entries); }

    public void addEntry(Map<String, String> entry) {
        this.entries.add(new HashMap<>(entry));
        this.numberOfEntries++;
        //print the entry
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


}
