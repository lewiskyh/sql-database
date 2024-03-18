package edu.uob.Commands;

import edu.uob.DatabaseException;

import java.io.File;

public class DropCommand extends Command{

        public DropCommand() {
            super();
        }

        public void executeCommand() throws DatabaseException {
            if(workingStructure.equals("TABLE")){
                //check if table exists
                File dropTableFile = new File(dropTable.getTableFilePath());
                if(!dropTableFile.exists()){ throw new DatabaseException("No such table");}
                workingDatabase.deleteDBTable(dropTable.getTableName());
            }
            else if(workingStructure.equals("DATABASE")){
                //check if database exists
                File dropDatabase = new File(getWorkingDatabase().getDatabaseFolderPath());
                if(!dropDatabase.exists()){ throw new DatabaseException("No such database");}
                //Delete tables in the database's tablemap
                for(String tableName: getWorkingDatabase().getTableMaps().keySet()){
                    workingDatabase.deleteDBTable(tableName);
                }
                dropDatabase.delete();

            }

            //delete all tables inside the database folder

        }

}
