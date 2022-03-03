package com.webcheckers.model;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Handles each row on the board (these are horizontal)
 * Each row contains 8 spaces, which are then occupied by pieces
 * 
 * @author <a href="mailto:djb1808@rit.edu">Dan Bliss</a>
 * @author <a href="mailto:jjg6989@rit.edu">Josh Gottschall</a>
 */
public class Row 
{
    //private final List<Space> spaces;
    private final int index;
    private Space[] spaces;

    /**
     * Generates a new row with preset spaces
     * 
     * @param spaces spaces for the row
     * @param index the row's index on the board
     */
    public Row(Space[] spaces, int index)
    {
        this.spaces = spaces;
        this.index = index;
    }

    /**
     * Generates a new row, adds 8 spaces
     * 
     * @param index the index of the row
     */
    public Row(int index)
    {
        this.index = index;
        spaces = new Space[Board.NUM_COLS];
    }

    public void populateRow()
    {
        for (int i = 0 ; i < Board.NUM_COLS ; i++)
        {
            //Start with black space
            spaces[i] = new Space(index, i);

            if (index % 2 == 1)
            {
                //Row starts with black
                if (i % 2 == 0)
                {
                    spaces[i].setIsValid(true);

                    if (index <= 2)
                        spaces[i].setPiece(new Piece(index, i, Piece.STANDARD_TYPE, Piece.RED_COLOR));
                    else if (index >= 5)
                        spaces[i].setPiece(new Piece(index, i, Piece.STANDARD_TYPE, Piece.WHITE_COLOR));
                }
            }
            else
            {
                //Row starts with white
                if (i % 2 == 1)
                {
                    spaces[i].setIsValid(true);

                    if (index <= 2)
                        spaces[i].setPiece(new Piece(index, i, Piece.STANDARD_TYPE, Piece.RED_COLOR));
                    else if (index >= 5)
                        spaces[i].setPiece(new Piece(index, i, Piece.STANDARD_TYPE, Piece.WHITE_COLOR));
                }
            }
        }
    }

    public int getIndex()
    {
        return index;
    }

    public Space[] getSpaces()
    {
        return spaces;
    }

    /**
     * Returns an iterator of the row for the game.ftl file to render
     * the board
     * 
     * @return the iterator of the row
     */
    public Iterator<Space> iterator()
    {
        return Arrays.stream(spaces).iterator();
    }

    /**
     * Returns a copy of the row but flipped
     * 
     * @return the flipped row
     */
    public Row flippedRow()
    {
        Space[] flippedSpaces = new Space[Board.NUM_COLS];

        //Duplicate the spaces in reverse order
        for (int i = spaces.length - 1 ; i >= 0 ; i--)
            flippedSpaces[Math.abs(i - (spaces.length - 1))] = spaces[i];

        return new Row(flippedSpaces, index);
    }

    /**
     * Returns a copy of the current row
     * 
     * @return a new copy of the row with copies of all the spaces
     */
    public Row duplicateRow()
    {
        Row dupRow = new Row(index);

        for (int i = 0 ; i < Board.NUM_COLS ; i++)
            dupRow.getSpaces()[i] = spaces[i].duplicateSpace();

        return dupRow;
    }
}
