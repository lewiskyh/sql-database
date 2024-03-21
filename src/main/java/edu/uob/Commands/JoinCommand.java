package edu.uob.Commands;

import edu.uob.DBTable;
import edu.uob.DatabaseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoinCommand extends Command{

    public JoinCommand() { super(); }

    public void executeCommand() {

        displayTable = new DBTable(workingDatabase.getDatabaseFolderPath());

        DBTable table1 = joinTable1;
        DBTable table2 = joinTable2;

        List<String> combinedAttributes = new ArrayList<>();

        //For each entry in table1, check if the entry value is same as the value in table2 at the join attributes
        for(Map<String, String> row1 : table1.getAllEntries()){
            for(Map<String,String > row2 : table2.getAllEntries()){
                if(row1.get(joinAttribute1).equals(row2.get(joinAttribute2))) {
                    //If the values are same, add the entry to the display table
                    //Do not include "id" attribute in the display table
                    Map<String, String> combinedRow = new HashMap<>();
                    for(String attribute : table1.getAttributes()) {
                        if(!attribute.equals("id")) {
                            combinedRow.put(table1.getTableName() + "." + attribute, row1.get(attribute));
                        }
                    }
                    for(String attribute : table2.getAttributes()) {
                        if(!attribute.equals("id")){
                            combinedRow.put(table2.getTableName() + "." + attribute, row2.get(attribute));
                        }
                    }
                    displayTable.addEntry(combinedRow);
                }
            }
        }
        //Write attributes to the display table, excluding the join attributes
        for(String attribute : table1.getAttributes()) {
            if(!attribute.equals(joinAttribute1) && !attribute.equals("id")) {
                combinedAttributes.add(table1.getTableName() + "." + attribute);
            }
        }
        for(String attribute : table2.getAttributes()) {
            if(!attribute.equals(joinAttribute2) && !attribute.equals("id")){
                combinedAttributes.add(table2.getTableName() + "." + attribute);
            }
        }
        for(String attribute : combinedAttributes) {
            displayTable.addAttribute(attribute);
        }
        //Manually add the id for each entry
        int id = 0;
        for(Map<String, String> entry : displayTable.getAllEntries()){
            entry.put("id", Integer.toString(id));
            id++;
        }


    }
}
