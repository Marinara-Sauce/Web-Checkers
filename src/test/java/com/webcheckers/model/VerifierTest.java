package com.webcheckers.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import com.webcheckers.model.Game.GAME_END_REASONS;
import com.webcheckers.model.Game.MOVE_STATUS_CODES;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Model-tier")
public class VerifierTest {
    private Board board;
    private Game game;
    Verifier CuT;
    String playerName1 = "player1";
    String playerName2 = "player2";
    Player redPlayer;
    Player whitePlayer;

    public void print(boolean b){
        System.out.println(b);
    }
    public void print(String b){
        System.out.println(b);
    }
    public void print(MOVE_STATUS_CODES b){
        System.out.println(b);
    }
    public void print(Board board){
        currentBoard(game.getBoard());
    }
    public void print(int b){
        System.out.println(b);
    }
    private void print(Piece currenPosition) {
        System.out.println(currenPosition);
    }
    public void currentBoard(Board board){
        for(int i = 0 ; i < board.getRows().length ; i++) {
            for(int j = 0 ; j < board.getRows()[i].getSpaces().length ; j++) {
                Space space = board.getRows()[i].getSpaces()[j];
                System.out.print(space.getPiece());
                System.out.println(i +","+ j);
                System.out.println();
            }
        }
    }

    public void pieceInfo(Piece piece){
        System.out.println(piece.getColor());
        System.out.println(piece.getColID());
        System.out.println(piece.getRowID());
        System.out.println(piece.getPosition());
    }

    public Piece currenPosition(int row,int col){
        return game.getBoard().getRows()[row].getSpaces()[col].getPiece();
    }

    @BeforeEach
    public void init(){
        redPlayer = new Player(playerName1);
        whitePlayer = new Player(playerName2);
        CuT = new Verifier();
        game = mock(Game.class);
        board = mock(Board.class);
        game = new Game(1);
        game.startGame(redPlayer, whitePlayer);
    }

    /**
     * Tests that the game can accuratly detect when there's no avaliable hop
     */
    @Test
    public void testNoHopAvaliable(){
        //Im debating whether or not we need a mocked game class
        //Here since the test runs on the first move, then we should
        //be able to simply modify the board directly rather than mock
        //things

        //Spawn a new game with the default board and start it
        Game game = new Game(1);
        game.startGame(redPlayer, whitePlayer);

        //Assert that both pieces have no hop avaliable
        assertFalse(game.getVerifier().hopAvailable(Piece.RED_COLOR, game.getBoard()));
        assertFalse(game.getVerifier().hopAvailable(Piece.WHITE_COLOR, game.getBoard()));

        //Make red make a move, I created a method that will do this without verifying the move
        Move move = new Move(new Position(2, 3), new Position(3, 4));
        game.makeUnverifiedMove(move);

        //Assert that there are still no hops avaliable
        assertFalse(game.getVerifier().hopAvailable(Piece.RED_COLOR, game.getBoard()));
        assertFalse(game.getVerifier().hopAvailable(Piece.WHITE_COLOR, game.getBoard()));
    }

    @Test
    public void testJumpforRegulars(){
        //testing jump when there is a free space on opponent's side

        Position start = new Position(2,1);
        Position end = new Position(3,2);
        Move move = new Move(start, end);
        //move second row first col to third row second col
        game.makeUnverifiedMove(move);
        
        //move fifth row zero col to fourth row first col
        Move move1 = new Move(new Position(5,0),new Position(4,1));
        game.makeUnverifiedMove(move1);
        
        //now there is place for red to make a regular jump
        assertTrue(game.getVerifier().hopAvailable(Piece.RED_COLOR, game.getBoard()));
        //White should be false because there is no where it can jump
        assertFalse(game.getVerifier().hopAvailable(Piece.WHITE_COLOR, game.getBoard()));
    }

    @Test
    public void testJumpforInvalidOwner(){
        //when the game starts, white is trying to touch red's piece
        Move move = new Move(new Position(2,1),new Position(3,2));
        MOVE_STATUS_CODES actual = game.getVerifier().verifyMove(false, move, game);
        assertEquals(MOVE_STATUS_CODES.INVALID_OWNER, actual);
    }

    @Test
    public void testJumpRegularForDirections(){
        //when a regular piece is moving sideway
        Move move = new Move(new Position(2,1),new Position(2,2));
        MOVE_STATUS_CODES actual = game.getVerifier().verifyMove(true, move, game);
        assertEquals(MOVE_STATUS_CODES.INVALID_DIRECTION_SIDEWAYS, actual);
    }
    @Test
    public void testJumpRegularForUp(){
        //when moving the wrong direction, side up
        MOVE_STATUS_CODES actual1 = game.getVerifier().verifyMove(true, new Move(new Position(2,1),new Position(1,1)), game);
        assertEquals(MOVE_STATUS_CODES.INVALID_DIRECTION_UP, actual1);

        MOVE_STATUS_CODES actual = game.getVerifier().verifyMove(true, new Move(new Position(2,1),new Position(3,1)), game);
        assertEquals(MOVE_STATUS_CODES.INVALID_DIRECTION_UP, actual);
    }

    @Test
    public void testJumpRegularForSpaceOccupied(){
        //when a regular piece is moving to a spot with the same type of piece
        Move move = new Move(new Position(2,1),new Position(1,0));
        MOVE_STATUS_CODES actual = game.getVerifier().verifyMove(true, move, game);
        assertEquals(MOVE_STATUS_CODES.SPACE_OCCUPIED, actual);
    }

    @Test
    public void testJumpRegularForSuccessJump(){
        //when a regular piece is moving diagonally
        Move move = new Move(new Position(2,1),new Position(3,2));
        MOVE_STATUS_CODES actual = game.getVerifier().verifyMove(true, move, game);
        assertEquals(MOVE_STATUS_CODES.SUCCESS, actual);
    }

    @Test
    public void testJumpRegularForHoppingItsOwn(){
        // when a piece is trying to capture its own piece, too many spaces error will pop up
        Move move = new Move(new Position(2,1), new Position(3,2));
        game.makeUnverifiedMove(move);
        move = new Move(new Position(2,3), new Position(4,1));
        MOVE_STATUS_CODES actual = game.getVerifier().verifyMove(true, move, game);
        assertEquals(MOVE_STATUS_CODES.TOO_MANY_SPACES_MOVED, actual);
        assertFalse(game.getVerifier().hopAvailable(Piece.RED_COLOR, game.getBoard()));
    }

    @Test
    public void testForMakingMultipleTurns(){
        // when a regular piece attempt to take multiple turns
        Move move = new Move(new Position(2,1),new Position(3,2));
        game.makeMove(move);
        Move move1 = new Move(new Position(1,0), new Position(2,1));
        MOVE_STATUS_CODES actual = game.getVerifier().verifyMove(true, move1, game);
        assertEquals(MOVE_STATUS_CODES.INVALID_DIRECTION_UP, actual);

    }

    @Test
    public void testRegularMultipleJumps(){
        Move move = new Move(new Position(2,3), new Position(3,2));
        MOVE_STATUS_CODES actual = game.makeMove(move);
        move = new Move(new Position(5,0),new Position(4,1));
        game.makeUnverifiedMove(move);
        move = new Move(new Position(1,4), new Position(2,3));
        game.makeUnverifiedMove(move);
        move = new Move(new Position(2,3), new Position(3,4));
        game.makeUnverifiedMove(move);
        move = new Move(new Position(0,5), new Position(1,4));
        game.makeUnverifiedMove(move);
        //testing no jumps after attempted several jumps without verified move code
        move = new Move(new Position(4,1), new Position(2,3));
        actual = game.getVerifier().verifyMove(false,move,game);
        assertEquals(MOVE_STATUS_CODES.NO_JUMP, actual);
        // check if there is a hop before making amove
        assertTrue(game.getVerifier().hopAvailable(Piece.WHITE_COLOR, game.getBoard()));
        //resets standard move
        game.getVerifier().setMadeStandardMove(false);
        game.makeUnverifiedMove(move);
        //checks for the second jump
        move = new Move(new Position(2,3), new Position(0,5));
        actual = game.getVerifier().verifyMove(false, move, game);
        assertEquals(MOVE_STATUS_CODES.SUCCESS, actual);        
    }

    @Test
    public void testKingPieceJumpInFourDirections(){
        Move move1 = new Move(new Position(0,5) , new Position(4,1));
        Move move2 = new Move(new Position(0,3), new Position(4,3));
        Move move3 = new Move(new Position(2,3), new Position(4,5));
        Move move4 = new Move(new Position(2,5), new Position(4,7));
        game.makeUnverifiedMove(move1);
        game.makeUnverifiedMove(move2);
        game.makeUnverifiedMove(move3);
        game.makeUnverifiedMove(move4);
        print(board);
        game.makeUnverifiedMove(new Move(new Position(5,0) , new Position(1,4)));
        game.makeUnverifiedMove(new Move(new Position(1, 4), new Position(0,3)));
        // move back, now this white piece is kinged
        game.makeUnverifiedMove(new Move(new Position(0, 3), new Position(1,4)));
        //king can jump all four direction
        move1 = new Move(new Position(1,4), new Position(0,5));
        assertEquals(MOVE_STATUS_CODES.JUMP_AVALIABLE, game.getVerifier().verifyMove(false, move1, game));
        move2 = new Move(new Position(1,4), new Position(0,3));
        assertEquals(MOVE_STATUS_CODES.JUMP_AVALIABLE, game.getVerifier().verifyMove(false, move2, game));
        move3 = new Move(new Position(1,4),new Position(2,3));
        assertEquals(MOVE_STATUS_CODES.JUMP_AVALIABLE, game.getVerifier().verifyMove(false, move3, game));
        move4 = new Move(new Position(1,4), new Position(2,5));
        assertEquals(MOVE_STATUS_CODES.JUMP_AVALIABLE, game.getVerifier().verifyMove(false, move4, game));
        //put one of the piece back and try to let king jump
        game.makeUnverifiedMove(new Move(new Position(4,1), new Position(2,3)));

        Move move5 = new Move(new Position(1,4), new Position(3,2));
        print(board);
        print(currenPosition(1,4));
        print(currenPosition(2,3));
        assertEquals(true,game.getVerifier().hopAvaliableKing(currenPosition(1, 4), game.getBoard()));
        assertEquals(MOVE_STATUS_CODES.SUCCESS, game.getVerifier().verifyMove(false, move5, game));
    }

    /**
     * Test that the verifier can detect if a king jump is avaliable
     */
    @Test
    public void testKingJumpAvaliabe()
    {
        Board board = game.getBoard();

        //First, replace every single piece with a king
        for (Row r : board.getRows())
            for (Space s : r.getSpaces())
                if (s.getPiece() != null)
                    s.getPiece().setType(Piece.KING_TYPE);
                
        //Assert that there are no hops avaliable
        assertFalse(game.getVerifier().hopAvailable(Piece.RED_COLOR, board));
        assertFalse(game.getVerifier().hopAvailable(Piece.WHITE_COLOR, board));

        //Move two kings so we have a hop avaliable
        game.makeUnverifiedMove(new Move(new Position(2, 5), new Position(3, 4)));
        game.submitTurn();

        game.makeUnverifiedMove(new Move(new Position(5, 2), new Position(4, 3)));

        assertTrue(game.getVerifier().hopAvailable(Piece.RED_COLOR, board));
        assertTrue(game.getVerifier().hopAvailable(Piece.WHITE_COLOR, board));

        //Start a new game, we'll test with this one
        game.endGame(GAME_END_REASONS.ENDED_BY_SERVER);
        game = new Game(2);
        game.startGame(redPlayer, whitePlayer);
        board = game.getBoard();

        //Clear the board
        for (Row r : board.getRows())
            for (Space s : r.getSpaces())
                if (s.getPiece() != null)
                    s.removePiece();

        Piece testPiece = new Piece(0, 0, Piece.RED_COLOR, Piece.KING_TYPE);
        
        //Add a king to the bottom left
        board.getSpace(0, 0).setPiece(testPiece);
        assertFalse(game.getVerifier().hopAvaliableKing(testPiece, board));

        //Add a king right above it on 1, 1
        board.getSpace(1, 1).setPiece(new Piece(1, 1, Piece.WHITE_COLOR, Piece.KING_TYPE));
        assertFalse(game.getVerifier().hopAvaliableKing(testPiece, board));
    }

}