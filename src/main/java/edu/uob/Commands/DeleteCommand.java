package edu.uob.Commands;

import edu.uob.Condition;
import edu.uob.DatabaseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeleteCommand extends Command{

        public DeleteCommand() {
            super();
        }

        public void executeCommand() throws DatabaseException, IOException {
            if (conditionList.isEmpty()){
                throw new DatabaseException("Invalid Update Syntax: No WHERE condition is given");
            }
            //Follow similar logic to update command to find out target ID
            ArrayList<String> targetID = new ArrayList<>();
            List<Map<String, String>> allEntries = workingDatabase.getDBTable(deleteTableName).getAllEntries();

            if (!conditionList.isEmpty()) {
                if (conditionList.size() == 1) {
                    for (Condition condition : conditionList) {
                        checkIfAttributeExists(condition, workingDatabase.getDBTable(deleteTableName));
                        for (Map<String, String> entry : allEntries) {
                            String valueToCompare = entry.get(condition.getAttributeName());
                            if (condition.compareData(valueToCompare)) {
                                targetID.add(entry.get("id"));
                            }
                        }
                    }
                }
                else {
                    String logicalOperator = getBoolOperators().get(0);
                    for (Map<String, String> entry : allEntries) {
                        boolean matchesConditions = !logicalOperator.equals("OR");

                        for (Condition condition : conditionList) {
                            checkIfAttributeExists(condition, workingDatabase.getDBTable(deleteTableName));
                            String valueToCompare = entry.get(condition.getAttributeName());
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
                    workingDatabase.getDBTable(deleteTableName).deleteEntry(id);
                }
                workingDatabase.getDBTable(deleteTableName).writeTable();
            }
            else {
                throw new DatabaseException("Invalid Delete Syntax: No WHERE condition is given");
            }

        }

}
