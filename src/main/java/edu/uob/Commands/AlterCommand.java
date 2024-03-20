package edu.uob.Commands;

import edu.uob.DatabaseException;

import java.io.IOException;
import java.util.List;

public class AlterCommand extends Command{

    public AlterCommand() {
        super();
    }

    public void executeCommand() throws IOException, DatabaseException {
        if(attributeToAlter.equals("id")){
            throw new DatabaseException("id attribute cannot be altered");
        }
        List<String> attributes = alterTable.getAttributes();
        Boolean attributeExists = false;
        if(alteration.equals("ADD")){
            for (String attribute : attributes) {
                if(attribute.equalsIgnoreCase(attributeToAlter.toLowerCase())){
                    throw new DatabaseException("Attribute already exists");
                }
            }
            alterTable.addAttribute(attributeToAlter);
            alterTable.writeTable();
        }
        else if(alteration.equals("DROP")){
            for (String attribute : attributes) {
                if(attribute.equalsIgnoreCase(attributeToAlter.toLowerCase())){
                    attributeExists = true;
                }
            }
            if (!attributeExists) { throw new DatabaseException("Attribute does not exist"); }
            alterTable.deleteAttribute(attributeToAlter);
            alterTable.writeTable();
        }
    }

}
