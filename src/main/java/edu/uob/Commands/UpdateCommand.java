package edu.uob.Commands;

import edu.uob.Condition;
import edu.uob.DBTable;
import edu.uob.DatabaseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UpdateCommand extends Command{

        public UpdateCommand() {
            super();
        }

        public void executeCommand() throws DatabaseException, IOException {
            //Update values in the table using valueSetterList
            //Check if attribute exists in table
            //But need to find out the target ID first based on WHERE condition

            //Check if condition list is empty
            if (conditionList.isEmpty()){
                throw new DatabaseException("Invalid Update Syntax: No WHERE condition is given");
            }
            //Find out target ID from the updateTable based on conditionlist
            //Edit the entry in the updateTable and use copy constructor to copy to the sameName table in database
            ArrayList<String> targetID = new ArrayList<>();
            List<Map<String, String>> allEntries = workingDatabase.getDBTable(updateTableName).getAllEntries();
            List<String> attributeToCheck = workingDatabase.getDBTable(updateTableName).getAttributes();
            String attributeToCompare = "";
            for(String attribute : attributeToCheck){
                if(attribute.equalsIgnoreCase(conditionList.get(0).getAttributeName())){
                    attributeToCompare= attribute;
                    break;
                }
            }
            if (!conditionList.isEmpty()) {
                if (conditionList.size() == 1) {
                    // Handling single condition, similar to deletion logic
                    for (Condition condition : conditionList) {
                        checkIfAttributeExists(condition, workingDatabase.getDBTable(updateTableName));
                        for (Map<String, String> entry : allEntries) {
                            String valueToCompare = entry.get(attributeToCompare);
                            if (condition.compareData(valueToCompare)) {
                                targetID.add(entry.get("id"));
                            }
                        }
                    }
                } else {
                    String logicalOperator = getBoolOperators().get(0);
                    for (Map<String, String> entry : allEntries) {
                        boolean matchesConditions = !logicalOperator.equals("OR");

                        for (Condition condition : conditionList) {
                            checkIfAttributeExists(condition, workingDatabase.getDBTable(updateTableName));
                            attributeToCompare = "";
                            for(String attribute : attributeToCheck){
                                if(attribute.equalsIgnoreCase(condition.getAttributeName())){
                                    attributeToCompare= attribute;
                                    break;
                                }
                            }
                            String valueToCompare = entry.get(attributeToCompare);
                            boolean conditionMatch = condition.compareData(valueToCompare);
                            if (logicalOperator.equals("AND") && !conditionMatch) {
                                matchesConditions = false;
                                break;
                            } else if (logicalOperator.equals("OR") && conditionMatch) {
                                matchesConditions = true;
                                break;
                            }
                        }
                        if (matchesConditions) {
                            targetID.add(entry.get("id"));
                        }
                    }
                }
                for (String id : targetID) {
                    for (ValueSetter valueSet : valueSetterList) {
                        System.out.println("value to set " + valueSet.getValueToSet() + " attribute name " + valueSet.getAttributeName());
                        workingDatabase.getDBTable(updateTableName).updateEntry(id, valueSet.getAttributeName(), valueSet.getValueToSet());
                    }
                }
                workingDatabase.getDBTable(updateTableName).writeTable();
            }
            else{ throw new DatabaseException("Invalid Update Syntax: No WHERE condition is given"); }
        }
}
