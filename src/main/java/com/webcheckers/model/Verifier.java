package com.webcheckers.model;

import com.webcheckers.model.Game.MOVE_STATUS_CODES;

/**
 * This class contains all the logic to determine if a move is valid
 * or not. This class does not make any changes to the board,
 * and passes off information to the Game class to allow it to
 * do so.
 * 
 * @author <a href="mailto:djb1808@rit.edu">Dan Bliss</a>
 * @author <a href="mailto:jjg6989@rit.edu">Josh Gottschall</a>
 */
public class Verifier {

    //Determines whether to print all information (lengthy)
    private final boolean DEBUG = false;

    //The current move the player is on, used for double hops
    int moveIndex = 0;

    //Made standard move
    boolean madeStandardMove = false;

    /**
     * The meat of this class. This function is called to determine
     * if a specific move is valid. This will then return a move code
     * and tell the Game if any pieces need to be hopped.
     * 
     * This function does not directly move any pieces
     * 
     * @param redTurn if it's the red player's turn
     * @param move the move to make
     * @param game the game
     * @return a move code depending on the legality of the move
     */
    public Game.MOVE_STATUS_CODES verifyMove(boolean redTurn, Move move, Game game)
    {
        System.out.println("Verifying: " + move);

        Board board = game.getBoard();

        Position start = move.getStart();
        Position end = move.getEnd();

        Piece pieceMoving = board.getPiece(start.getRow(), start.getCell());

        if (pieceMoving == null)
            System.out.println("We don't have a piece?");

        System.out.println("Piece is a " + pieceMoving.getType());

        //Verify that we own the piece
        if (!(pieceMoving.getColor().equals(Piece.RED_COLOR) && redTurn
                || pieceMoving.getColor().equals(Piece.WHITE_COLOR) && !redTurn)) {
            moveIndex = 0;
            return MOVE_STATUS_CODES.INVALID_OWNER;
        }

        //Verify that there isn't a piece we're moving onto
        if (board.getSpace(end.getRow(), end.getCell()).getPiece() != null) {
            moveIndex = 0;
            return MOVE_STATUS_CODES.SPACE_OCCUPIED;
        }

        //Verify that we are moving diagonally
        if (start.getCell() == end.getCell()) {
            moveIndex = 0;
            return MOVE_STATUS_CODES.INVALID_DIRECTION_UP;
        }

        //Count the number of diagonal and rows we are going
        int diagonal = Math.abs(end.getCell() - start.getCell());
        int rowsMoving = Math.abs(end.getRow() - start.getRow());

        System.out.println("Moving " + diagonal + " diagonally | Moving " + rowsMoving + " Rows");

        //If we are not a king, rowsMoving must be >= 1
        if (pieceMoving.getColor().equals(Piece.RED_COLOR))
        {
            if (end.getRow() - start.getRow() < 1 && !pieceMoving.getType().equals(Piece.KING_TYPE)) {
                moveIndex = 0;
                return MOVE_STATUS_CODES.INVALID_DIRECTION_SIDEWAYS;
            }
        }
        else
        {
            if (start.getRow() - end.getRow() < 1 && !pieceMoving.getType().equals(Piece.KING_TYPE)) {
                moveIndex = 0;
                return MOVE_STATUS_CODES.INVALID_DIRECTION_SIDEWAYS;
            }
        }


        //Check that the rows and diagonal moves are identical
        if (rowsMoving != diagonal) {
            moveIndex = 0;
            return MOVE_STATUS_CODES.INVALID_POSITION;
        }

        //Standard move, no jumps. If this is the case, return true
        System.out.println(moveIndex);
        if (moveIndex == 0 && rowsMoving == 1)
        {
            System.out.println("Detected standard move! Current move index is: " + moveIndex);

            if (hopAvailable(pieceMoving.getColor(), board))
                return MOVE_STATUS_CODES.JUMP_AVALIABLE;

            moveIndex++;
            madeStandardMove = true;
            return MOVE_STATUS_CODES.SUCCESS;
        }
        else if (moveIndex > 0 && rowsMoving == 1) {
            return MOVE_STATUS_CODES.INVALID_DIRECTION_UP;
        }

        //Jumps are processed here. Jumps can happen on any move, so we don't need
        //to check the move index

        //Verify that we are moving 2 rows for a successful jump
        if (rowsMoving == 2)
        {
            System.out.println(pieceMoving);

            int startRow = pieceMoving.getRowID();
            int startCol = pieceMoving.getColID();

            int endRow = end.getRow();
            int endCol = end.getCell();

            int changeRow = endRow - startRow;
            int changeCol = endCol - startCol;

            System.out.println(startCol);
            System.out.println(startRow);

            System.out.println(changeCol);
            System.out.println(changeRow);

            int midRow = startRow + changeRow/2;
            int midCol = startCol + changeCol/2;

            if(redTurn && changeRow < 0 && !pieceMoving.getType().equals(Piece.KING_TYPE)) {
                return MOVE_STATUS_CODES.INVALID_DIRECTION_DOWN;
            } else if (!redTurn && changeRow > 1 && !pieceMoving.getType().equals(Piece.KING_TYPE)) {
                return MOVE_STATUS_CODES.INVALID_DIRECTION_UP;
            }

            //Get the piece we should be hopping
            Piece hoppingPiece = board.getPiece(midRow, midCol);
            Row row = board.getRows()[midRow];
            System.out.println("Row: " + row);
            Space space = row.getSpaces()[midCol];
            System.out.println("Space: "+ space);
            System.out.println("Piece: " + space.getPiece());

            System.out.println("Piece to hop (" + midCol + "," + midRow + ")");
            System.out.println("Hopping piece: " + hoppingPiece);

            if (hoppingPiece == null) {
                return MOVE_STATUS_CODES.INVALID_DIRECTION_UP;
            }

            System.out.println("Hopped piece position: (" + midCol + "," + midRow + ")");

            //If the piece we are hopping is our own, invalid move
            if (hoppingPiece.getColor().equals(pieceMoving.getColor()))
                return MOVE_STATUS_CODES.TOO_MANY_SPACES_MOVED;
            
            //If we already made a standard move, no jump
            if (madeStandardMove)
                return MOVE_STATUS_CODES.NO_JUMP;

            //We have a valid piece that we are hopping! Hop it!
            game.hopPiece(new Position(midRow, midCol));

            moveIndex++;

            return MOVE_STATUS_CODES.SUCCESS;
        }

        return null;
    }

    /**
     * Determines if there is a hop avaliable for a specific king piece
     * 
     * @param piece the piece to check
     * @param board the current board
     * @return whether the king piece can make a hop
     */
    public boolean hopAvaliableKing(Piece piece, Board board)
    {
        String color = piece.getColor();
        int row = piece.getRowID();
        int col = piece.getColID();

        Piece topLeft = null;
        Piece topRight = null;
        Piece bottomRight = null;
        Piece bottomLeft = null;

        if(row == 0 && col == 0) {
            bottomRight = board.getPiece(row + 1, col + 1);
            if(bottomRight != null && !bottomRight.getColor().equals(color)) {
                return (board.getPiece(row + 2, col + 2) == null);
            }
        }

        if(row == 0 && col == 7) {
            bottomLeft = board.getPiece(row + 1, col - 1);
            if(bottomLeft != null && !bottomLeft.getColor().equals(color)) {
                return (board.getPiece(row + 2, col - 2) == null);
            }
        }

        if(row == 7 && col == 0) {
            topRight = board.getPiece(row - 1, col + 1);
            if(topRight != null && !topRight.getColor().equals(color)) {
                return (board.getPiece(row - 2, col + 2) == null);
            }
        }

        if(row == 7 && col == 7) {
            topLeft = board.getPiece(row - 1, col - 1);
            if(topLeft != null && !topLeft.getColor().equals(color)) {
                return (board.getPiece(row - 2, col - 2) == null);
            }
        }

        if(row != 0 && col != 0) topLeft = board.getPiece(row - 1, col - 1);
        if(row != 0 && col != 7) topRight = board.getPiece(row - 1, col + 1);
        if(row != 7 && col != 0) bottomLeft = board.getPiece(row + 1, col - 1);
        if(row != 7 && col != 7) bottomRight = board.getPiece(row + 1, col + 1);

        if(topLeft != null && !topLeft.getColor().equals(color)) {
            System.out.println(1);
            return (board.getPiece(row - 2, col - 2) == null);
        } else if(topRight != null && !topRight.getColor().equals(color)) {
            System.out.println(2);
            return (board.getPiece(row - 2, col + 2) == null);
        } else if(bottomLeft != null && !bottomLeft.getColor().equals(color)) {
            System.out.println(3);
            return (board.getPiece(row + 2, col - 2) == null);
        } else if(bottomRight != null && !bottomRight.getColor().equals(color)) {
            System.out.println(4);
            return (board.getPiece(row + 2, col + 2) == null);
        }
        return false;
    }

    /**
     * Determines if there is a hop available for a specific color.
     * Usually the first function used to determine if there are
     * valid moves available
     * 
     * @param color the color to check
     * @param board the game board
     * @return whether a hop is available
     */
    public boolean hopAvailable(String color, Board board) {
        print("Hop available has been instigated for color: " + color);
        for(int i = 0 ; i < board.getRows().length ; i++) {
            for(int j = 0 ; j < board.getRows()[i].getSpaces().length ; j++) {

                Space space = board.getRows()[i].getSpaces()[j];

                if (space.getPiece() != null && space.getPiece().getColor().equals(color)) {
                    if(space.getPiece().getType().equals(Piece.KING_TYPE)) {
                        if(hopAvaliableKing(space.getPiece(), board)) {
                            return true;
                        }
                    }
                    else
                    if(hopAvailable(space.getPiece(), board))
                        return true;
                }

            }
        }
        return false;
    }

    /**
     * A function used to determine if there is a hop available
     * for any standard piece
     * 
     * @param piece the piece to check
     * @param board the current game board
     * @return whether there is a hop available
     */
    public boolean hopAvailable(Piece piece, Board board) {
        String color = piece.getColor();
        int direction = 0;
        if(color.equals(Piece.WHITE_COLOR)) {
            direction = -1;
        } else if (color.equals(Piece.RED_COLOR)) {
            direction = 1;
        } else {
            return false;
        }

        if(direction < 0) {
            int row = piece.getRowID();
            int col = piece.getColID();

            Piece topLeft = null;
            Piece topRight = null;

            if(row > 1 && col > 1) topLeft = board.getPiece(row - 1, col - 1);
            if(row > 1 && col < 6) topRight = board.getPiece(row - 1, col + 1);

            if(row == 7 && col == 0) {
                topRight = board.getPiece(row - 1, col + 1);
                if(topRight != null && !topRight.getColor().equals(color)) {
                    return (board.getPiece(row - 2, col + 2) == null);
                }
            }

            if(row == 7 && col == 7) {
                topLeft = board.getPiece(row - 1, col - 1);
                if(topLeft != null && !topLeft.getColor().equals(color)) {
                    return (board.getPiece(row - 2, col - 2) == null);
                }
            }

            if(topLeft != null && !topLeft.getColor().equals(color)) {
                return (board.getPiece(row - 2, col - 2) == null);
            } else if(topRight != null && !topRight.getColor().equals(color)) {
                return (board.getPiece(row - 2, col + 2) == null);
            }
            return false;
        } else if (direction > 0) {
            int row = piece.getRowID();
            int col = piece.getColID();

            Piece bottomLeft = null;
            Piece bottomRight = null;

            if(row == 0 && col == 0) {
                bottomRight = board.getPiece(row + 1, col + 1);
                if(bottomRight != null && !bottomRight.getColor().equals(color)) {
                    return (board.getPiece(row + 2, col + 2) == null);
                }
            }

            if(row == 0 && col == 7) {
                bottomLeft = board.getPiece(row + 1, col - 1);
                if(bottomLeft != null && !bottomLeft.getColor().equals(color)) {
                    return (board.getPiece(row + 2, col - 2) == null);
                }
            }

            if(row < 6 && col > 1) bottomLeft = board.getPiece(row + 1, col - 1);
            if(row < 6 && col < 6) bottomRight = board.getPiece(row + 1, col + 1);

            if(bottomLeft != null && !bottomLeft.getColor().equals(color)) {
                return (board.getPiece(row + 2, col - 2) == null);
            } else if(bottomRight != null && !bottomRight.getColor().equals(color)) {
                return (board.getPiece(row + 2, col + 2) == null);
            }
            return false;
        }
        return false;
    }

    /**
     * Debug function, prints the string if debug is on
     * @param s the string to print
     */
    private void print(String s)
    {
        if (DEBUG) System.out.println(s);
    }

    /**
     * Set made standard move
     */
    public void setMadeStandardMove(boolean s)
    {
        madeStandardMove = s;
    }
}