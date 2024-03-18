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
            if( AlterTable.getAttributes().contains(attributeToAlter) ){
                throw new DatabaseException("Attribute already exists");
            }
            AlterTable.addAttribute(attributeToAlter);
            AlterTable.writeTable();
        }
        else if(alteration.equals("DROP")){
            if( !AlterTable.getAttributes().contains(attributeToAlter) ){
                throw new DatabaseException("Attribute does not exist");
            }
            AlterTable.deleteAttribute(attributeToAlter);
            AlterTable.writeTable();
        }
    }

}
