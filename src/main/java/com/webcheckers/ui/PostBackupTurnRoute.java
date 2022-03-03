package com.webcheckers.ui;

import com.webcheckers.application.GameMaster;
import com.webcheckers.model.Game;
import com.webcheckers.util.Message;

import com.google.gson.Gson;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * POST Route for HTTP Requests checking if a move is valid
 * 
 * @author <a href="mailto:djb1808@rit.edu">Dan Bliss</a>
 */
public class PostBackupTurnRoute implements Route
{
    private final GameMaster gameMaster;

    public static final String MOVE_ATTR = "move";

    public PostBackupTurnRoute(GameMaster gameMaster)
    {
        this.gameMaster = gameMaster;
    }

    /**
     * Handles the POST request. The POST parses the request's body for
     * the JSON string detailing the move information. That is then
     * parsed into an instance of the Move class for further
     * analysis, then returns whether or not the move was valid or not
     * 
     * @param request the HTTP request
     * @param response the HTTP response
     * 
     * @return Message to display
     */
    @Override
    public Object handle(Request request, Response response){
        Message message;
        Gson gson = new Gson();

        Game game = gameMaster.getCurrentGame(request);
        boolean success = game.undoMove();
        
        if (success)
            message = new Message("Backed up successfully!", Message.Type.INFO);
        else
            message = new Message("Back up failed!", Message.Type.ERROR);

        String json;
        json = gson.toJson(message);

        return json;
    }
}
