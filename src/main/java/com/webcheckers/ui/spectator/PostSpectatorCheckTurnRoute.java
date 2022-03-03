package com.webcheckers.ui.spectator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webcheckers.application.GameMaster;
import com.webcheckers.model.Game;
import com.webcheckers.util.Message;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Post Route handles spectator game page refresh when move occurs
 *
 * @author <a href="mailto:cfr8511@rit.edu">Charlie Reilly</a>
 */
public class PostSpectatorCheckTurnRoute implements Route
{

    private final GameMaster gameMaster;

    public PostSpectatorCheckTurnRoute(GameMaster gameMaster) {
        this.gameMaster = gameMaster;
    }

    @Override
    public Object handle(Request request, Response response) {

        Game game = gameMaster.getCurrentGame(request);
        String turn = "false";

        if (!game.gameRunning()) { turn = "true"; }

        else if (game.getBoardState()) {
            game.boardUpdated();
            turn = "true";
        }

        Message message = new Message(turn, Message.Type.INFO);
        Gson gson = new GsonBuilder().create();

        return gson.toJson(message);
    }
}