package com.webcheckers.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webcheckers.application.GameMaster;
import com.webcheckers.model.Game;
import com.webcheckers.model.Player;
import com.webcheckers.util.Message;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

public class PostCheckTurnRoute implements Route
{

    private final GameMaster gameMaster;

    public PostCheckTurnRoute(GameMaster gameMaster)
    {
        this.gameMaster = gameMaster;
    }

    @Override
    public Object handle(Request request, Response response){

        Game game = gameMaster.getCurrentGame(request);

        Session session = request.session();
        String turn = "false";
        Player local = session.attribute(PostSigninRoute.PLAYER_KEY);

        if(!game.gameRunning())
            turn = "true";

        else if (game.playersTurn(local))
            turn = "true";
        
        Message message = new Message(turn, Message.Type.INFO);

        Gson gson = new GsonBuilder().create();

        return gson.toJson(message);
    }
    
}
