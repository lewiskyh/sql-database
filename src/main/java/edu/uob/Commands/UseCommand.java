package edu.uob.Commands;

import edu.uob.Database;
import edu.uob.DatabaseException;

import java.io.File;

public class UseCommand extends Command {


    public UseCommand(){
        super();
    }

    //Setting the database in Command parent class
   //Execute Use Database command to update parent's fields

    @Override
    public void executeCommand() throws DatabaseException {
        //Set database name and folder accoridng to working database
        //Workingdatabase is set to command instance in parseUse
        setDatabaseName(workingDatabase.getDatabaseName());
        setDatabaseFolderPath(workingDatabase);
        //database folder exists?
        File databaseFolder = new File(getDatabaseFolderPath());
        if (!databaseFolder.exists()){
           throw new DatabaseException ("Database folder does not exist");
        }
    }

}
