package edu.uob.Commands;

import edu.uob.Condition;
import edu.uob.DBTable;
import edu.uob.Database;
import edu.uob.DatabaseException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Command {

    protected final String rootFolderPath;
    protected Database workingDatabase;
    protected String databaseName;
    protected String databaseFolderPath;
    protected String workingStructure;

    protected List<String> valueListStored;

    protected DBTable createTable;

    protected DBTable dropTable;

    protected DBTable alterTable;

    protected DBTable selectTable;

    protected DBTable displayTable;

    protected String insertTableName;

    protected String alteration;

    protected String attributeToAlter;

    protected boolean wildCard;

    protected List<String> attributeNameList = new ArrayList<>();

    protected List<String> tableNameList = new ArrayList<>();

    protected List<Condition> conditionList = new ArrayList<>();

    public Command(){
        this.rootFolderPath = Paths.get("databases").toString();
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void addCondition(Condition condition){
        this.conditionList.add(condition);
    }

    public List<Condition> getConditionList (){ return this.conditionList; }

    public void setAttributeToAlter(String attributeToAlter){
        this.attributeToAlter = attributeToAlter;
    }

    public void setSelectTable(DBTable selectTable){ this.selectTable = selectTable; }

    public void setAlterTable(DBTable alterTable){
        this.alterTable = alterTable;
    }

    public void setAlteration(String alteration){
        this.alteration = alteration;
    }

    public void setInsertTableName(String insertTable){
        this.insertTableName = insertTable;
    }

    public void setCreateTable(DBTable createTable){
        this.createTable = createTable;
    }

    public void setDropTable(DBTable dropTable){
        this.dropTable = dropTable;
    }

    public boolean getWildCard(){
        return this.wildCard;
    }

    public List<String> getAttributeNameList() {
        return attributeNameList;
    }

    public void setValueListStored(List<String> valueListStored){
        //Access the max ID of the working table and pass it to the first element of the value list
        valueListStored.add(0, Integer.toString(this.workingDatabase.getDBTable(this.insertTableName).getMaxID()));
        this.valueListStored = valueListStored;
    }

    public DBTable getDisplayTable(){
        return this.displayTable;
    }

    public DBTable getInsertTable (){
        return this.workingDatabase.getDBTable(this.insertTableName);
    }
    //Combine entry list with attribute list to create a map
    public Map<String, String> makeInsertValueMap(){
        Map<String, String> insertValues = new HashMap<>();
        for(int i = 0; i < this.attributeNameList.size(); i++){
            insertValues.put(this.attributeNameList.get(i), this.valueListStored.get(i));
        }
        return insertValues;
    }

    public String getDatabaseFolderPath() {
        return databaseFolderPath;
    }

    public Database getWorkingDatabase(){
        return workingDatabase;
    }

    public void setWorkingStructure(String workingStructure){
        this.workingStructure = workingStructure;
    }

    public String getWorkingStructure(){
        return this.workingStructure;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public void setWorkingDatabase(Database workingDatabase) {
        this.workingDatabase = workingDatabase;
    }

    public void setDatabaseFolderPath(Database workingDatabase) {
        this.databaseFolderPath = this.rootFolderPath + File.separator + workingDatabase.getDatabaseName();
    }

    public void addAttributeList(ArrayList<String> attributeList){
        for(String attribute : attributeList){
            if (!attribute.equals(",")){
                this.attributeNameList.add(attribute);
            }
        }
    }

    public void addAttribute(String attributeName){
        this.attributeNameList.add(attributeName);
    }

    public void addTableName(String tableName){ this.tableNameList.add(tableName); }

    public List<String> getTableNameList(){ return this.tableNameList; }

    public List<String> getAttributeList(){ return this.attributeNameList; }

    public void setWildCard(boolean wildCard){
        this.wildCard = wildCard;
    }

    public void executeCommand() throws DatabaseException, IOException {
        //To be overridden by sub-classes
    }

    public String displayTableToString(){
        StringBuilder displayInfo = new StringBuilder();
        //Start in newline
        displayInfo.append("\n");
        if(getConditionList().isEmpty()){
            if(getWildCard()){
                for(String attribute : getDisplayTable().getAttributes()){
                    displayInfo.append(attribute).append("\t");
                }
                displayInfo.append("\n");
                for(Map<String, String> entry : getDisplayTable().getAllEntries()){
                    for(String attribute : getDisplayTable().getAttributes()){
                        String row = entry.getOrDefault(attribute, "");
                        displayInfo.append(row).append("\t");
                    }
                    displayInfo.append("\n");
                }
            }
            else{
                for (String attribute : getAttributeNameList()) {
                    displayInfo.append(attribute).append("\t");
                }
                displayInfo.append("\n");
                for (Map<String, String> entry : getDisplayTable().getAllEntries()) {
                    for (String attribute : getAttributeNameList()) {
                        String row = entry.getOrDefault(attribute, "");
                        displayInfo.append(row).append("\t");
                    }
                    displayInfo.append("\n");
                }
            }
        }
        return displayInfo.toString();
    }
}
