package com.webcheckers.model;

/**
 * Handles a specific position on the board
 * 
 * @author <a href="mailto:djb1808@rit.edu">Dan Bliss</a>
 */
public class Position 
{
    private final int row; //Horizontal
    private final int cell; //Vertical

    public Position(int row, int cell)
    {
        this.row = row;
        this.cell = cell;
    }

    public int getRow()
    {
        return row;
    }

    public int getCell()
    {
        return cell;
    }

    @Override
    public String toString()
    {
        return "x: " + cell + " | y: " + row;
    }
}
