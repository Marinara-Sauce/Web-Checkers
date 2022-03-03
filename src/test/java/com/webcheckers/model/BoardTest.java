package com.webcheckers.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Model-tier")
public class BoardTest {

    private Board boardTest;

    //Just a simple red standard piece to move around
    private Piece testPiece;

    @BeforeEach
    public void setupTest()
    {
        testPiece = new Piece(0, 0, Piece.STANDARD_TYPE, Piece.RED_COLOR);

        boardTest = new Board();
    }

    /**
     * Test that we can make moves, does not need to verify it's accurate or
     * make a move with a king, because this function in Board only moves the piece
     * 
     * Board assumes the move was already validated
     */
    @Test
    public void testMakeMove()
    {
        Position startPos = new Position(0, 0);
        Position endPos = new Position(1, 1);

        //Set a piece at 0, 0 and move it to 1, 1
        boardTest.getSpace(0, 0).setPiece(testPiece);
        boardTest.makeMove(new Move(startPos, endPos));

        //Test that we have a piece at 1,1 and not at 0,0
        assertNotNull(boardTest.getPiece(endPos.getRow(), endPos.getCell()));
        assertNull(boardTest.getPiece(startPos.getRow(), startPos.getCell()));
    }

    @Test
    public void testFlippedBoard()
    {
        Position standardPos = new Position(0, 0);
        Position flippedPos = new Position(7, 7);

        boardTest.getSpace(standardPos.getRow(), standardPos.getCell()).setPiece(testPiece);

        Board flippedBoard = boardTest.flipBoard();

        //Check that the piece is in the flipped position on the new board
        assertNotNull(flippedBoard.getSpace(flippedPos.getRow(), flippedPos.getCell()));
    }
}
