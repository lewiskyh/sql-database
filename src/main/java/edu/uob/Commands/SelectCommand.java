package edu.uob.Commands;

import edu.uob.Condition;
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

        //If no condition, then display all rows (execute in server class)

        //If there are conditions (attribute value check), check and remove wanted rows in displayTable
        if (!conditionList.isEmpty()) {
            System.out.println("Conditions exist");
            for (Condition condition : conditionList){
                for (Map<String, String> entry : displayTable.getAllEntries()) {
                    String valueToCompare = entry.get(condition.getAttributeName());
                    if (!condition.compareData(valueToCompare)) {
                        System.out.println("Deleting entry");
                        displayTable.deleteEntry(entry.get("id"));
                    }
                }

            }
        }
    }




}
