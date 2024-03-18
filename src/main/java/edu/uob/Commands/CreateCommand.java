package edu.uob.Commands;

import edu.uob.DatabaseException;

import java.io.File;
import java.io.IOException;

public class CreateCommand extends Command{

    public CreateCommand() {
        super();
    }

    public void executeCommand() throws DatabaseException, IOException {
        if(workingStructure.equals("DATABASE")){
            //Check if same name database already exists
            File newDatabase = new File(getWorkingDatabase().getDatabaseFolderPath());
            if(newDatabase.exists()){ throw new DatabaseException("Database already exists");}
            newDatabase.mkdir();
        }
        else if(workingStructure.equals("TABLE")){
            //Check if same name table already exists
            File newTable = new File(createTable.getTableFilePath());
            if(newTable.exists()){ throw new DatabaseException("Table already exists");}
            //Determine if Create comes with attribute list.
            if(!attributeNameList.isEmpty()){
                //Pass command's attribute list to table
                for(String attribute: attributeNameList){
                    createTable.addAttribute(attribute);
                }
            }
            createTable.writeTable();
        }
    }
}
