package com.webcheckers.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.webcheckers.model.Game.GAME_END_REASONS;
import com.webcheckers.model.Game.MOVE_STATUS_CODES;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit test for the {@link Game} component
 *
 */
@Tag("Model-tier")

public class GameTest
{
    final String PLAYER_1_NAME = "RED_PLAYER";
    final String PLAYER_2_NAME = "WHITE_PLAYER";

    final int GAME_ID = 1;

    // Friendly classes
    private Player redPlayer;
    private Player whitePlayer;

    // CUT
    private Game cut;

    @BeforeEach
    public void setup()
    {
        redPlayer = new Player(PLAYER_1_NAME);
        whitePlayer = new Player(PLAYER_2_NAME);

        cut = new Game(GAME_ID);
    }

    /**
     * Assert that we can fetch the game id
     */
    @Test
    public void testGetGameID()
    {
        assertEquals(GAME_ID, cut.getId());
    }

    /**
     * Test that we can make the game run and stop
     */
    @Test
    public void testGameRunning()
    {
        //Game should not be running when spawned
        assertFalse(cut.gameRunning());

        cut.startGame(redPlayer, whitePlayer);
        assertTrue(cut.gameRunning());

        cut.endGame(GAME_END_REASONS.ENDED_BY_SERVER);
        assertFalse(cut.gameRunning());
    }

    /**
     * Test that we can make a move
     */
    @Test
    public void testMakeMove()
    {
        cut.startGame(redPlayer, whitePlayer);

        //An example of a valid move
        Position moveStart = new Position(2, 5);
        Position moveEnd = new Position(3, 4);
        Move validMove = new Move(moveStart, moveEnd);

        assertEquals(MOVE_STATUS_CODES.SUCCESS, cut.makeMove(validMove));

        //Try to do an illegal move
        moveStart = new Position(3, 4);
        moveEnd = new Position(4, 4);
        validMove = new Move(moveStart, moveEnd);

        assertNotEquals(MOVE_STATUS_CODES.SUCCESS, cut.makeMove(validMove));

        //Test that when the game is over, moves are always successful
        cut.endGame(GAME_END_REASONS.ENDED_BY_SERVER);
        assertEquals(MOVE_STATUS_CODES.SUCCESS, cut.makeMove(null));
    }

    /**
     * Test that we can hop pieces
     */
    @Test
    public void testHopPiece()
    {
        cut.startGame(redPlayer, whitePlayer);
        cut.hopPiece(new Position(0, 1)); //This is a red piece

        //Check that we have successfully removed the piece
        assertNull(cut.getBoard().getPiece(0, 1));
    }

    /**
     * Test red win detection in prepare turn
     */
    @Test
    public void testWinDetection()
    {
        cut.startGame(redPlayer, whitePlayer);

        //Erase the board of all pieces
        Board b = cut.getBoard();
        for (Row r : b.getRows())
            for (Space s : r.getSpaces())
                s.removePiece();
        
        cut.prepareTurn();
        assertEquals(GAME_END_REASONS.RED_NO_PIECES, cut.getGameOverReason());

        //Start a new game, check for white having no pieces
        cut = new Game(2);
        cut.startGame(redPlayer, whitePlayer);

        //Erase the board of all white
        b = cut.getBoard();
        for (Row r : b.getRows())
            for (Space s : r.getSpaces())
                if (s.getPiece() != null)
                    if (s.getPiece().getColor().equals(Piece.WHITE_COLOR))
                        s.removePiece();
        
        cut.prepareTurn();
        assertEquals(GAME_END_REASONS.WHITE_NO_PIECES, cut.getGameOverReason());
    }

    /**
     * Test that we can submit turns
     */
    @Test
    public void testSubmitTurn()
    {
        cut.startGame(redPlayer, whitePlayer);
        
        //Make a valid move (tested and working)
        Position moveStart = new Position(2, 5);
        Position moveEnd = new Position(3, 4);
        Move validMove = new Move(moveStart, moveEnd);

        cut.makeMove(validMove);
        cut.submitTurn();

        assertEquals("WHITE", cut.getTurnAsString());

        //Test that the turn flips
        moveStart = new Position(5, 2);
        moveEnd = new Position(4, 3);
        validMove = new Move(moveStart, moveEnd);

        cut.makeMove(validMove);
        cut.submitTurn();

        assertEquals("RED", cut.getTurnAsString());

        //Test that we cannot make a turn with an invalid move
        cut.submitTurn();
        assertEquals("RED", cut.getTurnAsString());
    }

    /**
     * Test that we can undo moves
     */
    @Test
    public void testUndoMove()
    {
        //Test that we cant undo before starting the game
        assertFalse(cut.undoMove());

        cut.startGame(redPlayer, whitePlayer);

        //Make the same move made in the above test
        Position moveStart = new Position(2, 5);
        Position moveEnd = new Position(3, 4);
        Move validMove = new Move(moveStart, moveEnd);

        cut.makeMove(validMove);

        //Back it up, check for the piece to have been moved back
        assertTrue(cut.undoMove());
        assertNotNull(cut.getBoard().getPiece(2, 5));
    }

    /**
     * Test that we can identify if it's a players turn
     */
    @Test
    public void testPlayersTurnCheck()
    {
        cut.startGame(redPlayer, whitePlayer);
        assertTrue(cut.playersTurn(redPlayer));

        //Make a move, flip the turn, check agains
        Position moveStart = new Position(2, 5);
        Position moveEnd = new Position(3, 4);
        Move validMove = new Move(moveStart, moveEnd);

        cut.makeMove(validMove);
        cut.submitTurn();

        assertTrue(cut.playersTurn(whitePlayer));
    }

    /**
     * Test that we can get the players turn as a string
     */
    @Test
    public void testPlayerTurnAsString()
    {
        cut.startGame(redPlayer, whitePlayer);
        assertTrue(cut.getTurnAsString().equals("RED"));

        //Make a move, flip the turn, check agains
        Position moveStart = new Position(2, 5);
        Position moveEnd = new Position(3, 4);
        Move validMove = new Move(moveStart, moveEnd);

        cut.makeMove(validMove);
        cut.submitTurn();

        assertTrue(cut.getTurnAsString().equals("WHITE"));
    }

    /**
     * Test that we can check if it's a specific player's turn as a bool
     */
    @Test
    public void testIsPlayerTurn()
    {
        cut.startGame(redPlayer, whitePlayer);
        assertTrue(cut.isPlayersTurn(redPlayer));
        assertFalse(cut.isPlayersTurn(whitePlayer));

        //Flip the turn then check again
        Position moveStart = new Position(2, 5);
        Position moveEnd = new Position(3, 4);
        Move validMove = new Move(moveStart, moveEnd);

        cut.makeMove(validMove);
        cut.submitTurn();

        assertFalse(cut.isPlayersTurn(redPlayer));
        assertTrue(cut.isPlayersTurn(whitePlayer));
    }

    /**
     * Test that we can detect if a player is in a game
     */
    @Test
    public void testPlayerInGame()
    {
        cut.startGame(redPlayer, whitePlayer);
        assertTrue(cut.playerInGame(redPlayer));
        assertTrue(cut.playerInGame(whitePlayer));
        assertFalse(cut.playerInGame(null));
    }

    /**
     * Test that we can add spectators to the game
     */
    @Test
    public void testAddAndRemoveSpectator()
    {
        Player spectator = new Player("SPECTATOR");
        cut.startGame(redPlayer, whitePlayer);

        cut.addSpectator(spectator);
        assertTrue(cut.spectatorInGame(spectator));

        //Test that we can then Remove them
        cut.subtractSpectator(spectator);
        assertFalse(cut.spectatorInGame(spectator));

        //While we're here, check that we can't cause null point exceptions
        assertFalse(cut.spectatorInGame(null));

        //Also, make sure we can send board updated
        cut.boardUpdated();
    }

    /**
     * Test that we can access the red and white players
     */
    @Test
    public void testAccessRedAndWhite()
    {
        cut.startGame(redPlayer, whitePlayer);
        assertEquals(cut.getRedPlayer(), redPlayer);
        assertEquals(cut.getWhitePlayer(), whitePlayer);
    }

    /**
     * Test that we can fetch the board state
     */
    @Test
    public void testGetBoardState()
    {
        assertFalse(cut.getBoardState());
    }

    /**
     * Test that we can set the winner
     */
    @Test
    public void testGetAndSetWinner()
    {
        cut.setWinner(redPlayer);
        assertEquals(redPlayer, cut.getWinner());
    }

    /**
     * Test that we can update elo
     */
    @Test
    public void testUpdatedElo()
    {
        int elo = cut.getEloUpdated();
        cut.increaseEloUpdated(1);
        assertEquals(elo + 1, cut.getEloUpdated());
    }
}
