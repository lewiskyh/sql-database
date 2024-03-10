package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.List;

/** This class implements the DB server. */
public class DBServer {

    private static final char END_OF_TRANSMISSION = 4;
    private String storageFolderPath;

    public static void main(String args[]) throws IOException {
        DBServer server = new DBServer();
        //server.readDataFromFile("people.tab");
        server.blockingListenOn(8888);
    }

    /**
    * KEEP this signature otherwise we won't be able to mark your submission correctly.
    */
    public DBServer() {
        storageFolderPath = Paths.get("databases").toAbsolutePath().toString();
        try {
            // Create the database storage folder if it doesn't already exist !
            Files.createDirectories(Paths.get(storageFolderPath));
        } catch(IOException ioe) {
            System.out.println("Can't seem to create database storage folder " + storageFolderPath);
        }
    }

    /**Create a method to read in the data from the file and print out to terminal.*/
    public void readDataFromFile(String fileName){
        try{
            String fullFileName = this.storageFolderPath + File.separator + fileName;
            File fileToOpen = new File(fullFileName);

            try (FileReader reader = new FileReader(fileToOpen);
                BufferedReader buffReader = new BufferedReader(reader)){
                String line = buffReader.readLine();
                while (line != null) {
                    System.out.println(line);
                    line = buffReader.readLine();
                }
            }
        } catch(IOException ioe){
                System.out.println("Error reading from file: " + ioe.getMessage());
        }
    }
    /**Create table based on the query from user*/
    public void createTable (String dataBaseName, String tableName, List<String> columnName){
        try{
            dataBaseName = dataBaseName.toLowerCase();
            tableName = tableName.toLowerCase();

            //Create a folder for the database
            File dataBaseFolder = new File(this.storageFolderPath + File.separator + dataBaseName);
            if(!dataBaseFolder.exists()) {
                dataBaseFolder.mkdirs();
            }
            //Create a file for the table
            String tablePath = this.storageFolderPath + File.separator + dataBaseName + File.separator + tableName + ".tab";
            FileWriter fileWriter = new FileWriter(tablePath);
            BufferedWriter buffWriter = new BufferedWriter(fileWriter);
            //Write the column names to the table
            for (String column : columnName){
                buffWriter.write(column + "\t");
            }
            buffWriter.newLine();
            buffWriter.close();
        } catch(IOException ioe){
            System.out.println("Error creating table: " + ioe.getMessage());

        }
    }


    /**
    * KEEP this signature (i.e. {@code edu.uob.DBServer.handleCommand(String)}) otherwise we won't be
    * able to mark your submission correctly.
    *
    * <p>This method handles all incoming DB commands and carries out the required actions.
    */
    public String handleCommand(String command) {
        // TODO implement your server logic here
        return "";
    }

    //  === Methods below handle networking aspects of the project - you will not need to change these ! ===

    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.err.println("Server encountered a non-fatal IO error:");
                    e.printStackTrace();
                    System.err.println("Continuing...");
                }
            }
        }
    }

    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {

            System.out.println("Connection established: " + serverSocket.getInetAddress());
            while (!Thread.interrupted()) {
                String incomingCommand = reader.readLine();
                System.out.println("Received message: " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}
