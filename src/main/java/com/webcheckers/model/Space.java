package com.webcheckers.model;

/**
 * Handles individual spaces on the chess board
 * These are usually stored in the rows (8 per row)
 * 
 * Each space holds a piece, an "empty space" is simply
 * an empty piece.
 * 
 * @author <a href="mailto:djb1808@rit.edu">Dan Bliss</a>
 * @author <a href="mailto:jjg6989@rit.edu">Josh Gottschall</a>
 */
public class Space 
{
    final private int cellIdx; //Cell index
    final private int rowIdx;
    private boolean isValid;
    private Piece piece;

    /**
     * Generates a new space without a piece
     * 
     * @param cellIdx the index of the space within the row
     */
    public Space(int rowIdx, int cellIdx)
    {
        this.cellIdx = cellIdx;
        this.rowIdx = rowIdx;
        isValid = false;
    }

    /**
     * Generates a new space with isValid in mind
     * 
     * @param cellIdx the index of the space within the row
     * @param rowIdx the index of the row
     * @param isValid is the space valid
     * @param piece the piece to set
     */
    public Space(int rowIdx, int cellIdx, boolean isValid, Piece piece)
    {
        this.rowIdx = rowIdx;
        this.cellIdx = cellIdx;
        this.isValid = isValid;
        this.piece = piece;
    }

    /**
     * Replaces the current piece with a new one
     * 
     * @param piece the piece to replace the current one with
     */
    public void setPiece(Piece piece)
    {
        //Check if we need to invalidate this spot
        if (piece != null)
            setIsValid(false);
        else
            setIsValid(true);

        this.piece = piece;
    }

    public int getRowIdx() {
        return rowIdx;
    }

    /**
     * "Removes" the piece on the board by setting the piece to empty
     */
    public void removePiece()
    {
        piece = null;
        setIsValid(true);
    }

    /**
     * Creates a duplicate of the current space
     * 
     * @return a new copy of the current space
     */
    public Space duplicateSpace()
    {
        if (piece != null)
            return new Space(rowIdx, cellIdx, isValid, new Piece(piece));
        else
            return new Space(rowIdx, cellIdx, isValid, null);
    }

    /**
     * IsValid returns false if the space is a white space,
     * otherwise it returns true
     * 
     * @return if the space is valid
     */
    public boolean isValid()
    {
        return isValid;
    }

    public void setIsValid(boolean isValid)
    {
        this.isValid = isValid;
    }

    public int getCellIdx()
    {
        return cellIdx;
    }

    public Piece getPiece()
    {
        return piece;
    }
}
