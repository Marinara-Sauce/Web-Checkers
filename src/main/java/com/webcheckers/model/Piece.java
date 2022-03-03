package com.webcheckers.model;

/**
 * This class handles individual pieces for the board
 * The class is typically stored in the Space class
 * 
 * This piece can represent a king, empty, or standard piece,
 * and be of a red or white color
 * 
 * @author <a href="mailto:djb1808@rit.edu">Dan Bliss</a>
 * @author <a href="mailto:jjg6989@rit.edu">Josh Gottschall</a>
 */
public class Piece {

    public static final String RED_COLOR = "RED";
    public static final String WHITE_COLOR = "WHITE";
    public static final String EMPTY = "EMPTY";

    public static final String KING_TYPE = "KING";
    public static final String STANDARD_TYPE = "SINGLE";

    private String location;

    private String type;
    private String color;

    private int rowID = -1;
    private int colID;

    /**
     * CheckersPiece() - Constructor for the CheckersPiece
     *
     * @param player - The color of the player
     * @param location - The location of the piece
     * */
    public Piece(int rowID, int colID, String type, String color) {
        this.type = type;
        this.color = color;
        this.rowID = rowID;
        this.colID = colID;
    }

    /**
     * getLocation() - Returns the location of the piece
     *
     * @return - The location of the piece
     * */
    public String getLocation() {
        return location;
    }

    public Position getPosition()
    {
        return new Position(rowID, colID);
    }

    public Piece(Piece piece) {
        this.rowID = piece.getRowID();
        this.colID = piece.getColID();
        this.color = piece.getColor();
        this.type = piece.getType();
    }

    /**
     * getIsKing() - Returns the status of whether the piece is a king
     *
     * @return - Whether the piece is a king
     * */
    /*
    public boolean getIsKing() {
        return type.equals(KING_TYPE);
    }
     */

    public int getColID() {
        return colID;
    }

    public int getRowID() {
        return rowID;
    }

    public void setColID(int colID) {
        this.colID = colID;
    }

    public void setRowID(int rowID) {
        this.rowID = rowID;
    }

    /**
     * setIsKing() - Sets if the piece is kinged or not
     *
     * @param isKing - Boolean representing if the piece is kinged
     * */
    /*
    public void setIsKing(boolean isKing) {
        this.isKing = isKing;
    }
     */

    /**
     * setLocation() - Sets the location of the piece
     *
     * @param location - The location the piece is being set to
     * */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * getPlayer() - Returns the player color
     *
     * @return - The player color
     * */
    /*
    public String getPlayer() {
        return player;
    }
    */

    public String getType()
    {
        return type;
    }

    public String getColor()
    {
        return color;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return getColor().toLowerCase() + " " + getType().toLowerCase();
    }
}