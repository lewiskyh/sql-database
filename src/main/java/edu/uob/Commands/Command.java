package edu.uob.Commands;

import edu.uob.Database;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Command {

    protected final String rootFolderPath;
    protected Database workingDatabase;
    protected String databaseName;
    protected String databaseFolderPath;
    protected String workingStructure;

    protected List<String> attributeList = new ArrayList<>();

    public Command(){
        this.rootFolderPath = Paths.get("databases").toString();
    }

    public String getDatabaseName() {
        return databaseName;
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
                this.attributeList.add(attribute);
            }
        }
    }

    public List<String> getAttributeList(){ return this.attributeList; }


    public void executeCommand(){
        //To be overridden by sub-classes
    }
}
