package edu.uob;

import java.io.IOException;
import java.util.HashMap;
import java.io.File;

public class Database {

    private String databaseName;

    private final DBFilePath rootFolderPath;

    private HashMap<String, DBTable> mapsOfTables;

    public Database(String databaseName, DBFilePath rootFolderPath) {
        this.databaseName = databaseName;
        this.rootFolderPath = rootFolderPath;
        this.mapsOfTables = new HashMap<>();
    }

    public String getDatabaseName() {
        return this.databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public DBTable getDBTable(String tableName) {
        return this.mapsOfTables.get(tableName);
    }

    public void addDBTable(DBTable table) throws IOException {
        DBFileIO newTableFile = new DBFileIO(this.rootFolderPath, table);
        newTableFile.writeToTable();
        this.mapsOfTables.put(table.getTableName(), table);

    }

    public void setupDatabase(String databaseName) throws IOException {
        this.rootFolderPath.setDatabaseFolderPath(databaseName);
        File dbDirectory = new File(this.rootFolderPath.getDatabaseFolderPath());

        if (!dbDirectory.exists()) {
            throw new IOException("Cannot create DB folder");
        }

        File[] listOfTableFiles = dbDirectory.listFiles();

        if (listOfTableFiles != null) {
            for (File tablefile : listOfTableFiles) {
                DBTable table = new DBTable();
                table.setTableName(tablefile.getName().replace(".tab", ""));
                DBFileIO fileIO = new DBFileIO(this.rootFolderPath, table);
                fileIO.readFromTable();
                mapsOfTables.put(table.getTableName(), table);
            }
        }
    }

}
