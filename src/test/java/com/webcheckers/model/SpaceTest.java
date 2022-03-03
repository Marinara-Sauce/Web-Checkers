package com.webcheckers.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit test class for Space
 * 
 * @author Dan Bliss
 */
public class SpaceTest 
{
    private Space cut;

    //The majority of these functions are tested in
    //other classes, so we only need these tests

    @BeforeEach
    public void setup()
    {
        cut = new Space(0, 0);
    }

    @Test
    public void testSetPiece()
    {
        Piece piece = new Piece(0, 0, Piece.RED_COLOR, Piece.STANDARD_TYPE);
        cut.setPiece(piece);

        assertFalse(cut.isValid());

        //Remove the piece
        cut.setPiece(null);
        assertTrue(cut.isValid());
    }

    @Test
    public void testGetCellAndRow()
    {
        assertEquals(0, cut.getCellIdx());
        assertEquals(0, cut.getRowIdx());
    }
}
