package edu.uob;
import edu.uob.OXOMoveException.*;

public class OXOController {
    OXOModel gameModel;

    public OXOController(OXOModel model) {
        gameModel = model;
    }

    public void handleIncomingCommand(String command) throws OXOMoveException {

        //Check if end game
        if(this.gameModel.getWinner() != null){
            return;
        }

        //Check if it is the first ever command
        if(this.gameModel.getCurrentPlayerNumber() != 0 && this.gameModel.getCurrentPlayerNumber() != 1){
            this.gameModel.setCurrentPlayerNumber(0);
        }

        //Interpret the command entered by player
        char firstLetter = Character.toLowerCase(command.charAt(0));
        char secondLetter = command.charAt(1);
        int row = firstLetter - 'a';
        int col = secondLetter - '1';

       //Calling methods to check for exceptions
        checkIdentifierLength(command);
        checkIdentifierCharacter(firstLetter);
        checkCellRange(row,col);

        //Update the game state
        int currentPlayerNumber = this.gameModel.getCurrentPlayerNumber();
        int numberOfPlayer = this.gameModel.getNumberOfPlayers();
        OXOPlayer currentPlayer = this.gameModel.getPlayerByNumber(currentPlayerNumber);

        //Set cellowner if the cell is empty now
        if(gameModel.getCellOwner(row,col)==null){
            gameModel.setCellOwner(row, col, currentPlayer);
        }
        //Cell is not empty Exception - cellAlreadyTaken
        else{
            throw new OXOMoveException.CellAlreadyTakenException(row,col);
        }

        //Check for win or draw
        if(this.checkWinConditions(row, col)){
            this.gameModel.setWinner(currentPlayer);
        }
        else if(this.checkGameDrawn()){
            this.gameModel.setGameDrawn(true);
        }

        //Finished the turn, update the current player number
        int nextPlayer = Math.abs(this.gameModel.getCurrentPlayerNumber()-1);
        this.gameModel.setCurrentPlayerNumber(nextPlayer);

    }

    //3 Methods for exception checking
    public void checkIdentifierLength(String command) throws OXOMoveException {
        if (command.length() != 2) {
            throw new OXOMoveException.InvalidIdentifierLengthException(command.length());
        }
    }
    public void checkIdentifierCharacter(char firstLetter) throws OXOMoveException {
        if (firstLetter < 'a' || firstLetter > 'i') {
            throw new OXOMoveException.InvalidIdentifierCharacterException(OXOMoveException.RowOrColumn.ROW, firstLetter);
        }
    }
    public void checkCellRange(int row, int col) throws OXOMoveException {
        if (row < 0 || row >= gameModel.getNumberOfRows()) {
            throw new OXOMoveException.OutsideCellRangeException(OXOMoveException.RowOrColumn.ROW, row);
        } else if (col < 0 || col >= gameModel.getNumberOfColumns()) {
            throw new OXOMoveException.OutsideCellRangeException(OXOMoveException.RowOrColumn.COLUMN, col);
        }
    }


    //Check if game drawn

    public Boolean checkGameDrawn (){
        for(int row =0; row< gameModel.getNumberOfRows(); row++){
            for(int col=0; col<gameModel.getNumberOfColumns(); col++){
                if(gameModel.getCellOwner(row,col)==null){
                    return false;
                }
            }
        }
        return true;
    }

    public Boolean checkWinConditions(int row, int col){
        return this.horizontalWinner(row, col) || this.verticalWinner(row,col) || this.diagonalWinner(row,col);
    }


    public Boolean horizontalWinner(int row, int col){
        //Get winning threshold
        int winThreshold = this.gameModel.getWinThreshold();
        int countOfSame = 0;
        OXOPlayer currentPlayer = this.gameModel.getCellOwner(row,col);

        int currentCol = col;

        while(currentCol >= 0 && this.gameModel.getCellOwner(row,currentCol) == currentPlayer){
            countOfSame++;
            currentCol--;
        }

        currentCol = col + 1;

        while(currentCol < this.gameModel.getNumberOfColumns() && this.gameModel.getCellOwner(row, currentCol) == currentPlayer){
            countOfSame++;
            currentCol++;
        }

        if(countOfSame >= winThreshold){
            return true;
        }
        return false;

    }

    public Boolean verticalWinner(int row, int col){
        //Get winning threshold
        int winThreshold = this.gameModel.getWinThreshold();
        int countOfSame = 0;
        OXOPlayer currentPlayer = this.gameModel.getCellOwner(row,col);

        int currentRow = row;

        while(currentRow >=0 && this.gameModel.getCellOwner(currentRow,col)==currentPlayer){
            countOfSame++;
            currentRow--;
        }
        currentRow = row+1;
        while(currentRow < this.gameModel.getNumberOfRows() && this.gameModel.getCellOwner(currentRow,col)==currentPlayer){
            countOfSame++;
            currentRow++;
        }
        if(countOfSame >= winThreshold){
            return true;
        }
        return false;

    }

    public Boolean checkTopLeftToBottomRight (int row, int col, OXOPlayer currentPlayer, int winThreshold)
    {
        int countOfSame = 1;
        int currentRow = row - 1;
        int currentCol = col - 1;
        while (currentRow >= 0 && currentCol >= 0 && this.gameModel.getCellOwner(currentRow, currentCol) == currentPlayer) {
            countOfSame++;
            currentRow--;
            currentCol--;
        }
        currentRow = row + 1;
        currentCol = col + 1;

        while (currentRow < this.gameModel.getNumberOfRows() && currentCol < this.gameModel.getNumberOfColumns()
                && this.gameModel.getCellOwner(currentRow, currentCol) == currentPlayer) {
            countOfSame++;
            currentRow++;
            currentCol++;
        }

        return countOfSame>=winThreshold;

    }
    public Boolean checkTopRightToBottomLeft (int row, int col, OXOPlayer currentPlayer, int winThreshold)
    {
        int countOfSame = 1;
        // Check diagonal from top-right to bottom-left
        int currentRow = row - 1;
        int currentCol = col + 1;

        while (currentRow >= 0 && currentCol < this.gameModel.getNumberOfColumns()
                && this.gameModel.getCellOwner(currentRow, currentCol) == currentPlayer) {
            countOfSame++;
            currentRow--;
            currentCol++;
        }

        currentRow = row + 1;
        currentCol = col - 1;

        while (currentRow < this.gameModel.getNumberOfRows() && currentCol >= 0
                && this.gameModel.getCellOwner(currentRow, currentCol) == currentPlayer) {
            countOfSame++;
            currentRow++;
            currentCol--;
        }

        return countOfSame >= winThreshold;

    }

    public Boolean diagonalWinner(int row, int col) {
        // Get winning threshold
        int winThreshold = this.gameModel.getWinThreshold();
        int countOfSame = 1; // Start with 1 as we already have the current cell
        OXOPlayer currentPlayer = this.gameModel.getCellOwner(row, col);

        return this.checkTopLeftToBottomRight (row, col, currentPlayer, winThreshold) || this.checkTopRightToBottomLeft(row, col, currentPlayer, winThreshold);
    }





    public void addRow() {
        gameModel.addRow();
    }
    public void removeRow() {
        gameModel.removeRow();
    }
    public void addColumn() {
        gameModel.addColumn();
    }
    public void removeColumn() {
        gameModel.removeColumn();
    }
    public void increaseWinThreshold() {

        if(this.gameModel.getWinner() == null) {

            int row = this.gameModel.getNumberOfRows();
            int col = this.gameModel.getNumberOfColumns();
            //Max threshold is equal to row or col which ever is smaller
            int maxThreshold = Math.min(row, col);

            int currentWinThreshold = this.gameModel.getWinThreshold();
            if (currentWinThreshold < maxThreshold) {
                //Increase the winthreshold by 1
                this.gameModel.setWinThreshold(currentWinThreshold + 1);
            }
        }
    }

    public void decreaseWinThreshold() {

        if(this.gameModel.getCurrentPlayerNumber()!= 0 && this.gameModel.getCurrentPlayerNumber()!= 1){
            int currentWinThreshold = this.gameModel.getWinThreshold();
            if (currentWinThreshold > 3) {
                this.gameModel.setWinThreshold(currentWinThreshold - 1);
            }
        }

    }
    public void reset() {
        //Clear the board
        for(int j=0; j<this.gameModel.getNumberOfRows();j++){
            for(int i=0; i<this.gameModel.getNumberOfColumns();i++){
                this.gameModel.setCellOwner(j,i,null);
            }
        }
        //Reset the current player number to 0
        this.gameModel.setCurrentPlayerNumber(0);

        //Reset the winner
        this.gameModel.setWinner(null);

        //Reset the gamedrawn
        this.gameModel.setGameDrawn(false);
    }
}
