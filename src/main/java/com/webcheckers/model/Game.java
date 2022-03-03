package com.webcheckers.model;

import java.util.ArrayList;

/**
 * This game class runs an instance of a checkers game. It stores and
 * contains functions for playing a game of checkers
 * 
 * @author <a href="mailto:djb1808@rit.edu">Dan Bliss</a>
 * @author <a href="mailto:jjg6989@rit.edu">Josh Gottschall</a>
 */
public class Game {

    private int gameID;

    // -- Attribute Variables -- //
    private Player redPlayer;
    private Player whitePlayer;

    private boolean gameActive;

    // -- Game Variables -- //

    private Board gameBoard;
    private Verifier verifier;

    private ArrayList<Player> spectators;

    private int whoTurn = 0;
    private int count = 0; // used for counting spectator refreshes
    private int numSpectators = 0;
    private int eloUpdated = 0;
    private Player winner = new Player("!/?(){}"); // not null to avoid null pointer issue in gameData,
                                                         // invalid name to prevent logic failure

    //This variable is set when the game is over, and used to display
    //reason for game ending
    private GAME_END_REASONS gameOverReason;

    // --- Turn based variables (flips with each turn) --- //

    private Board undoBoard; //Board to replace gameBoard with if the undo button is pressed
    private boolean madeMove;
    private boolean newBoardState; // represents if a new board state exists
    private MOVE_STATUS_CODES recentMoveCode;

    /**
     * These move codes indicate the output of a move. If the move is valid, it will return
     * SUCCESS. Otherwise, these values can be used to indicate what the problem is with the move
     */
    public enum MOVE_STATUS_CODES
    {
        SUCCESS,
        JUMP_AVALIABLE,
        INVALID_POSITION,
        INVALID_OWNER,
        INVALID_DIRECTION_SIDEWAYS,
        INVALID_DIRECTION_UP,
        INVALID_DIRECTION_DOWN,
        SPACE_OCCUPIED,
        TOO_MANY_SPACES_MOVED,
        NO_JUMP,
        GAME_OVER
    }

    /**
     * Indicates the reasons that a game can end, from resigning, to having
     * no remaining pieces, to terminating by the server
     */
    public enum GAME_END_REASONS
    {
        RED_NO_PIECES,
        WHITE_NO_PIECES,
        RED_RESIGNED,
        WHITE_RESIGNED,
        ENDED_BY_SERVER
    }

    /**
     * GameMaster() - Initializes the game
     * */
    public Game(int gameID) {
        this.gameID = gameID;

        gameBoard = new Board();
        verifier = new Verifier();

        gameActive = false;
        spectators = new ArrayList<>();
    }

    /**
     * startGame() - Displays the board and loops turns and updates
     * */
    public void startGame(Player redPlayer, Player whitePlayer) {

        this.redPlayer = redPlayer;
        this.whitePlayer = whitePlayer;

        //Create a new board
        gameBoard = new Board();
        whoTurn = 0;
        
        gameActive = true;

        prepareTurn();
    }

    /**
     * Prepares the turn by capturing a board for a backup board
     */
    public void prepareTurn()
    {
        undoBoard = gameBoard.duplicateBoard();
        madeMove = false;
        recentMoveCode = null;

        verifier = new Verifier();

        if (!gameBoard.redHasPieces())
            endGame(GAME_END_REASONS.RED_NO_PIECES);
        else if (!gameBoard.whiteHasPieces())
            endGame(GAME_END_REASONS.WHITE_NO_PIECES);
    }

    /**
     * Makes a move, sends it to the board, and returns a code
     * depending on if the move was successful or not
     * 
     * @param move a class that shows the move positions
     * @return a status code to whether or not the move was valid
     */
    public MOVE_STATUS_CODES makeMove(Move move)
    {
        //If the game is running, we let the player do whatever
        //It won't make changes to the gameboard, so when the
        //next turn is sent we are dandy
        if (!gameRunning())
            return MOVE_STATUS_CODES.SUCCESS;

        //Set moveCode to SUCCESS if so, otherwise set to appliciable error
        MOVE_STATUS_CODES moveCode = verifier.verifyMove(whoTurn == 0, move, this);
        printBothBoards();

        if (moveCode == MOVE_STATUS_CODES.SUCCESS)
        {
            madeMove = true;
            getBoard().makeMove(move); //Forwards the moving to the board
            recentMoveCode = moveCode;
        }

        return moveCode;
    }

    /**
     * Makes a move, but does not verify it
     * This is for test classes
     * 
     * @param move a class that shows the move positions
     * @return a status code to whether or not the move was valid
     */
    public MOVE_STATUS_CODES makeUnverifiedMove(Move move)
    {

        //Set moveCode to SUCCESS if so, otherwise set to appliciable error
        MOVE_STATUS_CODES moveCode = MOVE_STATUS_CODES.SUCCESS;
        printBothBoards();

        if (true)
        {
            madeMove = true;
            getBoard().makeMove(move); //Forwards the moving to the board
            recentMoveCode = moveCode;
        }

        return moveCode;
    }

    /**
     * Called by the verifier, hops a piece if needed
     */
    public void hopPiece(Position pos)
    {
        System.out.println("Hopping piece at: " + pos.toString());
        Space spaceToHop = gameBoard.getSpace(pos.getRow(), pos.getCell());

        spaceToHop.removePiece();
    }

    /**
     * Submits a turn by flipping the turn and preparing the turn
     * variables again
     * 
     * @precondition recentMoveCode is valid
     */
    public void submitTurn()
    {
        printBothBoards();
        //If the move is successful, make the move and flip the turn
        if (madeMove && recentMoveCode == MOVE_STATUS_CODES.SUCCESS)
        {
            //Flip the turn 
            whoTurn++;
            newBoardState = true;
            if (whoTurn == 2) whoTurn = 0;
        }

        //Start the next turn
        prepareTurn();
    }

    /**
     * Undo's a move by setting the current game board to the backup board
     * stored at the start of the turn
     * 
     * @return whether or not the backup was successful
     */
    public boolean undoMove()
    {
        //In theory the button is disabled, so we don't neeeeed to check it...
        //But to play it safe here are some checks

        if (undoBoard == null)
            return false;
        
        System.out.println("Attempting to undo recent move...");
        gameBoard = undoBoard.duplicateBoard();

        madeMove = false;
        recentMoveCode = null;

        verifier = new Verifier();
        printBothBoards();
        return true;
    }

    /**
     * Check if its the player's turn
     * @param p the player to check
     * @return whether or not it's their turn
     */
    public boolean playersTurn(Player p)
    {
        if (whoTurn == 0)
            return p.equals(redPlayer);
        else
            return !p.equals(redPlayer);
    }

    public void printBothBoards()
    {
        System.out.println("Current Board: " + gameBoard + " | Undo Board: " + undoBoard);
    }

    public Player getRedPlayer()
    {
        return redPlayer;
    }

    public Player getWhitePlayer()
    {
        return whitePlayer;
    }

    public Board getBoard()
    {
        return gameBoard;
    }

    public String getTurnAsString()
    {
        if (whoTurn == 0)
            return "RED";
        else
            return "WHITE";
    }

    public boolean gameRunning()
    {
        return gameActive;
    }

    /**
     * Checks if it's a certain players turn
     * 
     * @param player the player to check
     * @return true if that player has a turn, false if otherwise
     */
    public boolean isPlayersTurn(Player player)
    {
        return (whoTurn == 0 && player == redPlayer) || (whoTurn == 1 && player == whitePlayer);
    }
  
    /**
     * Checks if a certain player is actively playing this game
     * 
     * @param player the player to check
     * @return true if the player is playing, false otherwise
     */
    public boolean playerInGame(Player player)
    {
        if (player == null)
            return false;
            
        return redPlayer.equals(player) || whitePlayer.equals(player);
    }

    public GAME_END_REASONS getGameOverReason()
    {
        return gameOverReason;
    }

    public int getId()
    {
        return gameID;
    }

    public Verifier getVerifier()
    {
        return verifier;
    }

    /**
     * Ends the current game, specifies a reason as to why the game has ended
     * 
     * @param reason the reason for the game to end
     */
    public void endGame(GAME_END_REASONS reason) {
        gameOverReason = reason;
        System.out.println(reason);
        gameActive = false;
        redPlayer.setPlayable(true); whitePlayer.setPlayable(true);
    }

    public boolean getBoardState() { return this.newBoardState; }

    public void boardUpdated() {
        count++;
        if(count == numSpectators) {
            this.newBoardState = false;
            count = 0;
        }
    }

    public void addSpectator(Player player) {
        spectators.add(player);
        player.setSpectating(true);
        numSpectators++;
    }

    public void subtractSpectator(Player player) {
        spectators.remove(player);
        player.setSpectating(false);
        numSpectators--;
    }
  
    public boolean spectatorInGame(Player player) {
        if (player == null)
            return false;

        return spectators.contains(player);
    }

    public void increaseEloUpdated(int i) { this.eloUpdated += i; }
    public int getEloUpdated() { return this.eloUpdated; }

    public void setWinner(Player player) { this.winner = player; }
    public Player getWinner() { return this.winner; }
}