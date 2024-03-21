package edu.uob;

import java.io.Serial;

public class DatabaseException extends Throwable {

    @Serial private static final long serialVersionUID = 1;

    String errorMessage;

    public DatabaseException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage () { return errorMessage;}

}
