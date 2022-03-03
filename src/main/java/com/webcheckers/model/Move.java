package com.webcheckers.model;

/**
 * Handles a move and can check if it's valid
 * 
 * @author <a href="mailto:djb1808@rit.edu">Dan Bliss</a>
 */
public class Move 
{
    public final Position start;
    public final Position end;

    public Move(Position start, Position end)
    {
        this.start = start;
        this.end = end;
    }
  
    public Position getStart()
    {
        return start;
    }

    public Position getEnd()
    {
        return end;
    }

    /**
     * Converts a move to a string
     */
    @Override
    public String toString()
    {
        return "Start: {" + start.toString() + "} - End: {" + end.toString() + "}"; 
    }
}