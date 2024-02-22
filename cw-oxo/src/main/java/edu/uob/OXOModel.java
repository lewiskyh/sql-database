package edu.uob;


import java.util.ArrayList;
import java.util.List;

public class OXOModel {

    private List<List<OXOPlayer>> cells;
    private OXOPlayer[] players;
    private int currentPlayerNumber;
    private OXOPlayer winner;
    private boolean gameDrawn;
    private int winThreshold;

    //2D arrayList
    public OXOModel(int numberOfRows, int numberOfColumns, int winThresh) {
        winThreshold = winThresh;
        cells = new ArrayList<>();
        for (int j = 0; j < numberOfRows; j++) {
            List<OXOPlayer> row = new ArrayList<>();

            for (int i = 0; i < numberOfColumns; i++) {
                row.add(null);
            }
            cells.add(row);
        }

        players = new OXOPlayer[2];
    }

    public int getNumberOfPlayers() {
        return players.length;
    }

    public void addPlayer(OXOPlayer player) {
        for (int i = 0; i < players.length; i++) {
            if (players[i] == null) {
                players[i] = player;
                return;
            }
        }
    }

    public OXOPlayer getPlayerByNumber(int number) {
        return players[number];
    }

    public OXOPlayer getWinner() {
        return winner;
    }

    public void setWinner(OXOPlayer player) {
        winner = player;
    }

    public int getCurrentPlayerNumber() {
        return currentPlayerNumber;
    }

    public void setCurrentPlayerNumber(int playerNumber) {
        currentPlayerNumber = playerNumber;
    }

    public int getNumberOfRows() {
        return cells.size();
    }

    public int getNumberOfColumns() {
        return cells.get(0).size();
    }

    public OXOPlayer getCellOwner(int rowNumber, int colNumber) {
        return cells.get(rowNumber).get(colNumber);
    }

    public void setCellOwner(int rowNumber, int colNumber, OXOPlayer player) {
        cells.get(rowNumber).set(colNumber,player);
    }

    public void addColumn(){
        if(cells.get(0).size()<9) {
            for (int j = 0; j < cells.size(); j++) {
                cells.get(j).add(null);
            }
        }
    }

    public void addRow(){
        if(cells.size()<9) {
            List<OXOPlayer> row = new ArrayList<>();
            for (int i = 0; i < cells.get(0).size(); i++) {
                row.add(null);
            }
            cells.add(row);
        }
    }

    public void removeColumn(){
        if(cells.get(0).size()>3) {
            int lastColumnIndx = cells.get(0).size() - 1;
            for (int j = 0; j < cells.size(); j++) {
                if (cells.get(j).get(lastColumnIndx) == null) {
                    cells.get(j).remove(lastColumnIndx);
                }
            }
        }
    }


    public void removeRow(){
        if(cells.size()>3) {
            int lastRowIndx = cells.size() - 1;
            Boolean emptyRow = true;
            for (int i = 0; i < cells.get(lastRowIndx).size(); i++) {
                if (cells.get(lastRowIndx).get(i) != null) {
                    emptyRow = false;
                }
            }
            if (emptyRow) {
                cells.remove(lastRowIndx);
            }
        }
    }

    public void setWinThreshold(int winThresh) {
        winThreshold = winThresh;
    }

    public int getWinThreshold() {
        return winThreshold;
    }

    public void setGameDrawn(boolean isDrawn) {
        gameDrawn = isDrawn;
    }

    public boolean isGameDrawn() {
        return gameDrawn;
    }

}
