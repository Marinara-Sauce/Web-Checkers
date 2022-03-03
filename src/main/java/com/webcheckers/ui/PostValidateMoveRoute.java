package com.webcheckers.ui;

import com.webcheckers.application.GameMaster;
import com.webcheckers.model.Game;
import com.webcheckers.model.Move;
import com.webcheckers.model.Game.MOVE_STATUS_CODES;
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
public class PostValidateMoveRoute implements Route
{
    private final GameMaster gameMaster;

    public static final String MOVE_ATTR = "move";

    public PostValidateMoveRoute(GameMaster gameMaster)
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

        String inJson = request.queryParams("actionData");
        int gameID = Integer.parseInt(request.queryParams(GetGameRoute.GAME_ID_ATTR));

        Game game = gameMaster.getGameByID(gameID);
        Move move = gson.fromJson(inJson, Move.class);

        MOVE_STATUS_CODES moveCode = game.makeMove(move);

        if (moveCode == MOVE_STATUS_CODES.SUCCESS)
            message = new Message("Valid move!", Message.Type.INFO);
        else if (moveCode == MOVE_STATUS_CODES.JUMP_AVALIABLE)
            message = new Message("Invalid Move! Jump is Avaliable", Message.Type.ERROR);
        else
            message = new Message("Invalid Move!", Message.Type.ERROR);

        String json;
        json = gson.toJson(message);

        return json;
    }
}