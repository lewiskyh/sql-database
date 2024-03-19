package edu.uob.Commands;

import edu.uob.DBTable;
import edu.uob.DatabaseException;

import java.util.Map;

public class SelectCommand extends Command{
    public SelectCommand() {
        super();
    }


    //Three cases - no condition, one condtion and more than one conditions
    //For each condition (attribute checking), if the attribute value for the row fulfills the condition, then add the row to the result
    // Then for each row in result, check which attributes to display to terminal
    public void executeCommand() throws DatabaseException {
        //DisplayTable to store the result to be displayed
        //Check if selectTable exists in workingDatabase
        if (!workingDatabase.getTableMaps().containsKey(selectTable.getTableName())) {
            throw new DatabaseException("Table does not exist");
        }
        //Copy selectTable to displayTable using copy constructor
        displayTable = new DBTable(selectTable);
            /**
            if (wildCard) {
                displayTable.printTable();
            }
            //Print based on attributeNameList stored in command (saved when parsing)
            else {
                for (String attribute : attributeNameList) {
                    System.out.print(attribute + "\t");
                }
                System.out.println();
                for (Map<String, String> entry : displayTable.getAllEntries()) {
                    for (String attribute : attributeNameList) {
                        String row = entry.getOrDefault(attribute, "");
                        System.out.print(row + "\t");
                    }
                    System.out.println();
                }
            }*/
    }




}
