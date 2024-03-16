package edu.uob;

public class DatabaseException extends Throwable {

    String errorMessage;

    public DatabaseException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage () { return errorMessage;}

}
