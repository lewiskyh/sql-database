package edu.uob;

import java.io.File;
import java.nio.file.Paths;

public class DBFilePath {

    private String rootFolderPath;

    private String databaseFolderPath;

    private String tableFilePath;

    public DBFilePath() {
        this.rootFolderPath = Paths.get("databases").toAbsolutePath().toString();
    }

    public void setDatabaseFolderPath (String databaseName){
        this.databaseFolderPath = this.rootFolderPath + databaseName;
        this.tableFilePath = "";
    }

    public void setTableFilePath (String tableName){
        this.tableFilePath = this.databaseFolderPath + File.separator + tableName + ".tab";
    }







}
