package edu.uob.Commands;

import edu.uob.Database;

public class UseCommand extends Command {


    public UseCommand(){
        super();
    }

    //Setting the database in Command parent class
   //Execute Use Database command to update parent's fields

    @Override
    public void executeCommand() {
        //Set database name and folder accoridng to working database
        setDatabaseName(databaseName);
        setDatabaseFolderPath(workingDatabase);
    }

}
