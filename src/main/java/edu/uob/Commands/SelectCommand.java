package edu.uob.Commands;

import edu.uob.Condition;
import edu.uob.DBTable;
import edu.uob.DatabaseException;

import java.util.ArrayList;
import java.util.List;
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
        List<String> attributeToCheck = displayTable.getAttributes();
        String attributeToCompare = "";


        //If no condition, then display all rows (execute in server class)

        //If there are conditions (attribute value check), check and remove wanted rows in displayTable
        if (!conditionList.isEmpty()) {
            if (conditionList.size() == 1) {
                for(String attribute : attributeToCheck){
                    if(attribute.equalsIgnoreCase(conditionList.get(0).getAttributeName())){
                        attributeToCompare= attribute;
                        break;
                    }
                }
                for (Condition condition : conditionList) {
                    //Check if the attribute stored in condtion exists in the table
                    checkIfAttributeExists(condition, displayTable);
                    for (Map<String, String> entry : displayTable.getAllEntries()) {
                        String valueToCompare = entry.get(attributeToCompare);
                        System.out.println(valueToCompare);
                        if (!condition.compareData(valueToCompare)) {
                            displayTable.deleteEntry(entry.get("id"));
                        }
                    }
                }
            }
            //More than one conditions to consider AND / OR?
            else {
                List<Map<String, String>> entryToDelete = new ArrayList<>();

                for (Map<String, String> entry : displayTable.getAllEntries()) {
                    boolean delete = false;
                    if (getBoolOperators().get(0).equals("AND")) {
                        delete = false;
                        for (Condition condition : conditionList) {
                            checkIfAttributeExists(condition, displayTable);
                            attributeToCompare = "";
                            for(String attribute : attributeToCheck){
                                if(attribute.equalsIgnoreCase(condition.getAttributeName())){
                                    attributeToCompare= attribute;
                                    break;
                                }
                            }
                            String valueToCompare = entry.get(attributeToCompare);
                            if (!condition.compareData(valueToCompare)) {
                                delete = true;
                                break;
                            }
                        }
                    } else if (getBoolOperators().get(0).equals("OR")) {
                        delete = true;
                        for (Condition condition : conditionList) {
                            checkIfAttributeExists(condition, displayTable);
                            attributeToCompare = "";
                            for(String attribute : attributeToCheck){
                                if(attribute.equalsIgnoreCase(condition.getAttributeName())){
                                    attributeToCompare= attribute;
                                    break;
                                }
                            }
                            String valueToCompare = entry.get(attributeToCompare);
                            if (condition.compareData(valueToCompare)) {
                                delete = false;
                                break;
                            }
                        }
                    }
                    if (delete) {
                        entryToDelete.add(entry);
                    }
                }
                for (Map<String, String> entry : entryToDelete) {
                    displayTable.deleteEntry(entry.get("id"));
                }
            }
        }
    }




}
