package edu.uob.Commands;

import edu.uob.DatabaseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InsertCommand extends Command{

        public InsertCommand() {
            super();
        }

        public void executeCommand() throws DatabaseException, IOException {
            this.workingDatabase.setupDatabase();
            //Get attribute list from the table
            List<String> attributesFromTable = workingDatabase.getDBTable(insertTableName).getAttributes();
            for(String attributeName : attributesFromTable){
                attributeNameList.add(attributeName);
            }
            //ID is auto-filled by system
            if(valueListStored.size() != attributeNameList.size()){
                throw new DatabaseException("Number of attributes and values mismatch");
            }
            //Pass insert value list to the table
            workingDatabase.getDBTable(insertTableName).addEntry(makeInsertValueMap());
            workingDatabase.getDBTable(insertTableName).writeTable();

        }
}
