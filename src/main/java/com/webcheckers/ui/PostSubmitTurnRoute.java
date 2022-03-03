package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameMaster;
import com.webcheckers.model.Game;
import com.webcheckers.util.Message;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * POST Route for /submitTurn, handles submitting turns
 * and sending them to the GameMaster for processing
 * 
 * @author <a href="mailto:djb1808@rit.edui">Dan Bliss (djb1808)</a>
 */
public class PostSubmitTurnRoute implements Route
{
    public static String MOVE_SUCCESS = "Successfully made a move!";
    public static String MOVE_FAIL_JUMP_AVALIABLE = "There's a jump avaliable!";
    public static String MOVE_FAIL_INVALID_POSITION = "The position is not valid!";
    //There's probably more failures, but here's what we have for now

    private final GameMaster gameMaster;
    
    public PostSubmitTurnRoute(GameMaster gameMaster)
    {
        this.gameMaster = gameMaster;
    }

    @Override
    public Object handle(Request request, Response response){
        //This may not be legal, we'll find out soon enough
        //Move move = (Move) httpSession.attribute(PostValidateMoveRoute.MOVE_ATTR);

        //MOVE_STATUS_CODES moveStatus = gameMaster.makeMove(move);
        Message postMoveMessage = new Message(MOVE_SUCCESS, Message.Type.INFO);

        // Get current game from gameID
        Game curr_game = gameMaster.getCurrentGame(request);
        curr_game.submitTurn();

        /*
        if (moveStatus == MOVE_STATUS_CODES.SUCCESS)
            postMoveMessage = new Message(MOVE_SUCCESS, Message.Type.INFO);

        else if (moveStatus == MOVE_STATUS_CODES.JUMP_AVALIABLE)
            postMoveMessage = new Message(MOVE_FAIL_JUMP_AVALIABLE, Message.Type.ERROR);

        else if (moveStatus == MOVE_STATUS_CODES.INVALID_POSITION)
            postMoveMessage = new Message(MOVE_FAIL_INVALID_POSITION, Message.Type.ERROR);

        else if (moveStatus == MOVE_STATUS_CODES.INVALID_OWNER)
            postMoveMessage = new Message("You do not own this piece!", Message.Type.ERROR);
        
        else if (moveStatus == MOVE_STATUS_CODES.INVALID_DIRECTION_DOWN 
            || moveStatus == MOVE_STATUS_CODES.INVALID_DIRECTION_SIDEWAYS
            || moveStatus == MOVE_STATUS_CODES.INVALID_DIRECTION_UP)

            postMoveMessage = new Message("Invalid Direction!", Message.Type.ERROR);
        
        else if (moveStatus == MOVE_STATUS_CODES.SPACE_OCCUPIED)
            postMoveMessage = new Message("Space occupied", Message.Type.ERROR);

        else if (moveStatus == MOVE_STATUS_CODES.TOO_MANY_SPACES_MOVED)
            postMoveMessage = new Message("Moved too many spaces!", Message.Type.ERROR);

        else
            postMoveMessage = new Message("Move Failed: Unknown Reason", Message.Type.ERROR);
        */

        //Parse message to GSON
        Gson gson = new Gson();
        return gson.toJson(postMoveMessage);
    }



}
