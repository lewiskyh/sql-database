package edu.uob;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class Database {
    private String databaseName;
    private final String rootFolderPath = Paths.get("databases").toAbsolutePath().toString();
    private String databaseFolderPath;
    private HashMap<String, DBTable> mapsOfTables;

    public Database(String databaseName) {
        this.databaseName = databaseName;
        this.databaseFolderPath = this.rootFolderPath + File.separator + this.databaseName;
        this.mapsOfTables = new HashMap<>();
    }

    public String getDatabaseFolderPath(){
        return this.databaseFolderPath;
    }

    private void setDatabaseFolderPath(){
        this.databaseFolderPath = this.rootFolderPath + File.separator + this.databaseName;
    }

    private String getTableFilePath(String tableName){
        return this.getDatabaseFolderPath() + File.separator + tableName + ".tab";
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
        DBTable newTable = new DBTable(table.getTableName());
        this.mapsOfTables.put(table.getTableName(), table);
    }

    public void setupDatabase() throws IOException {
        File dbDirectory = new File(getDatabaseFolderPath());

        if (!dbDirectory.exists()) {
            throw new IOException("Cannot create DB folder");
        }

        File[] listOfTableFiles = dbDirectory.listFiles();

        if (listOfTableFiles != null) {
            for (File tablefile : listOfTableFiles) {
                try {
                    DBTable table = new DBTable(this.databaseFolderPath);
                    table.setTable(tablefile.getName().replace(".tab", ""));
                    table.readFromTable();
                    mapsOfTables.put(table.getTableName(), table);
                } catch (IOException e) {
                    System.out.println("Error reading table files: " + e.getMessage());
                }
            }
        }
    }

}
