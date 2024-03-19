package edu.uob.Commands;

import edu.uob.DatabaseException;

import java.io.IOException;

public class AlterCommand extends Command{

    public AlterCommand() {
        super();
    }

    public void executeCommand() throws IOException, DatabaseException {
        if(attributeToAlter.equals("id")){
            throw new DatabaseException("id attribute cannot be altered");
        }
        if(alteration.equals("ADD")){
            if(alterTable.getAttributes().contains(attributeToAlter.toLowerCase()) ){
                throw new DatabaseException("Attribute already exists");
            }
            alterTable.addAttribute(attributeToAlter);
            alterTable.writeTable();
        }
        else if(alteration.equals("DROP")){
            if( !alterTable.getAttributes().contains(attributeToAlter.toLowerCase()) ){
                throw new DatabaseException("Attribute does not exist");
            }
            alterTable.deleteAttribute(attributeToAlter);
            alterTable.writeTable();
        }
    }

}
