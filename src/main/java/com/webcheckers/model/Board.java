package com.webcheckers.model;

import java.util.Arrays;
import java.util.Iterator;
/**
 * This class represents the game board
 * The class is iterable so the game.ftl file can represent it
 * 
 * @author <a href="mailto:djb1808@rit.edu">Dan Bliss</a>
 * @author <a href="mailto:jjg6989@rit.edu">Josh Gottschall</a>
 */
public class Board {

    public final static int NUM_ROWS = 8;
    public final static int NUM_COLS = 8;

    //Stores all 8 rows of the board (these are horizontal)
    private Row[] rows;

    /**
     * checkersBoard() - Initializes the board
     * */
    public Board() {
        //board = new Piece[8][8];
        System.out.println("Board has been created!");
        rows = new Row[NUM_ROWS];
        initiateBoard();
    }

    public Space getSpace(int row, int col) {
        return rows[row].getSpaces()[col];
    }

    private Space getSpace(Position position)
    {
        Row row = rows[position.getRow()];
        return row.getSpaces()[position.getCell()];
    }

    /**
     * Only needed when flipping the board
     * 
     * @param rows list of rows (already initialized)
     */
    public Board(Row[] rows)
    {
        this.rows = rows;
    } 
 
    /**
     * initiateBoard() - Puts the pieces in their starting positions
     * */
    private void initiateBoard() {
        for (int i = 0 ; i < rows.length ; i++)
        {
            rows[i] = new Row(i);
            rows[i].populateRow();
        }

        //If you want a test board, put the test board function here
    }

    public Piece getPiece(int row, int col) {
        return rows[row].getSpaces()[col].getPiece();
    }

    /**
     * Makes a move, called after move is verified from the GameMaster
     * 
     * @param move the move class
     * @return move successful
     */
    public void makeMove(Move move)
    {
        //Relocate the moving piece on the board
        Space origin = getSpace(move.start);
        Space end = getSpace(move.end);

        Piece movingPiece = origin.getPiece();
        movingPiece.setRowID(move.end.getRow());
        movingPiece.setColID(move.end.getCell());

        end.setPiece(new Piece(movingPiece));
        origin.removePiece();

        //rows[end.getRowIdx()].getSpaces()[end.getCellIdx()].setPiece(new Piece(movingPiece));

        //Search and promote to kings
        Row redHome = rows[0];
        Row whiteHome = rows[7];

        for(Space space : redHome.getSpaces()) {
            if(space.getPiece() != null && space.getPiece().getColor().equals(Piece.WHITE_COLOR)) {
                space.getPiece().setType(Piece.KING_TYPE);
            }
        }
        for(Space space : whiteHome.getSpaces()) {
            if(space.getPiece() != null && space.getPiece().getColor().equals(Piece.RED_COLOR)) {
                space.getPiece().setType(Piece.KING_TYPE);
            }
        }
    }

    /**
     * Returns an iterator of the rows for displaying
     * 
     * @return iterator of the rows list
     */
    public Iterator<Row> iterator()
    {   
        return Arrays.stream(rows).iterator();
    }

    /**
     * Returns a new board, which is simply a 
     * flipped version of the current board
     * 
     * @return the new flipped board
     */
    public Board flipBoard()
    {
        Row[] flippedRows = new Row[NUM_ROWS];

        //Duplicate the rows in reverse order
        for (int i = rows.length - 1 ; i >= 0 ; i--)
            flippedRows[Math.abs(i - (NUM_ROWS - 1))] = rows[i].flippedRow();

        return new Board(flippedRows);
    }

    /**
     * Check if the red player still has pieces on the board
     * 
     * @return true if there are red pieces
     */
    public boolean redHasPieces()
    {
        for (Row r : rows)
        {
            for (Space s : r.getSpaces())
                if (s.getPiece() != null && s.getPiece().getColor().equals(Piece.RED_COLOR))
                    return true;
        }

        return false;
    }

    /**
     * Check if the white player still has pieces on the board
     * 
     * @return true if there are red pieces
     */
    public boolean whiteHasPieces()
    {
        for (Row r : rows)
        {
            for (Space s : r.getSpaces())
                if (s.getPiece() != null && s.getPiece().getColor().equals(Piece.WHITE_COLOR))
                    return true;
        }

        return false;
    }

    public Row[] getRows()
    {
        return rows;
    }

    /**
     * Duplicates the board by creating empty rows and filling
     * them with duplicates of the current row
     * 
     * @return copy of the current board
     */
    public Board duplicateBoard()
    {
        Board dupBoard = new Board();
        
        for (int i = 0 ; i < Board.NUM_ROWS ; i++)
            dupBoard.getRows()[i] = rows[i].duplicateRow();
 
        return dupBoard;
    }

    // These were messing with our JUnit code covereage, so I commented it out
    // I'm going to leave it here for future debugging
    // /**
    //  * Used for debugging: Clears the current board and creates a double hop
    //  * scenario
    //  */
    // @SuppressWarnings("unused")
    // private void createDoubleHopTest()
    // {
    //     for (int i = 0 ; i < rows.length ; i++)
    //     {
    //         for (int j = 0 ; j < rows[i].getSpaces().length ; j++)
    //         {
    //             rows[i].getSpaces()[j].removePiece();
    //         }
    //     }

    //     rows[0].getSpaces()[0].setPiece(new Piece(0, 0, Piece.STANDARD_TYPE, Piece.RED_COLOR));
    //     rows[1].getSpaces()[1].setPiece(new Piece(1, 1, Piece.STANDARD_TYPE, Piece.WHITE_COLOR));
    //     rows[3].getSpaces()[3].setPiece(new Piece(3, 3, Piece.STANDARD_TYPE, Piece.WHITE_COLOR));
    // }

    // /**
    //  * Used for debugging: Clears the current board and creates 4 pieces,
    //  * 2 white and 2 red at the center ready for hopping
    //  */
    // @SuppressWarnings("unused")
    // private void createEndGameTest()
    // {
    //     for (int i = 0 ; i < rows.length ; i++)
    //     {
    //         for (int j = 0 ; j < rows[i].getSpaces().length ; j++)
    //         {
    //             rows[i].getSpaces()[j].removePiece();
    //         }
    //     }

    //     rows[2].getSpaces()[3].setPiece(new Piece(0, 0, Piece.STANDARD_TYPE, Piece.RED_COLOR));
    //     rows[2].getSpaces()[5].setPiece(new Piece(0, 0, Piece.STANDARD_TYPE, Piece.RED_COLOR));
    //     rows[4].getSpaces()[3].setPiece(new Piece(1, 1, Piece.STANDARD_TYPE, Piece.WHITE_COLOR));
    //     rows[4].getSpaces()[5].setPiece(new Piece(3, 3, Piece.STANDARD_TYPE, Piece.WHITE_COLOR));
    // }
}