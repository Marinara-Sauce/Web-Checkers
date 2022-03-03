package com.webcheckers.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webcheckers.application.GameMaster;
import com.webcheckers.model.Game;
import com.webcheckers.model.Player;
import com.webcheckers.model.Game.GAME_END_REASONS;
import com.webcheckers.util.Message;
import spark.*;

import java.util.logging.Logger;

/**
 *  POST Route for HTTP Request checking for resignation
 *
 * @author <a href="mailto:cfr8511@rit.edu">Charlie Reilly</a>
 */
public class PostResignRoute implements Route {
    private final Logger LOG = Logger.getLogger(PostResignRoute.class.getName());

    private GameMaster gameMaster;

    public PostResignRoute(GameMaster gameMaster){
        this.gameMaster = gameMaster;
    }

    /**
     * Handles the POST request. The POST ends the game and redirects both players
     * to the Home page.
     * @param request the HTTP request
     * @param response the HTTP response
     * @return null
     */
    @Override
    public Object handle(Request request, Response response){
        LOG.config("PostResignRoute was invoked");

        Session httpSession = request.session();

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        Game game = gameMaster.getCurrentGame(request);

        //Check if game exists before continuing resignation
        if(game.gameRunning())
        {
            Player resigningPlayer = httpSession.attribute(PostSigninRoute.PLAYER_KEY);

            //The current game is over, check who resigned and end the game
            if (resigningPlayer.equals(game.getRedPlayer()))
            {
                game.endGame(GAME_END_REASONS.RED_RESIGNED);
                resigningPlayer.changeRating(false);
                game.increaseEloUpdated(1);
            }
            else if (resigningPlayer.equals(game.getWhitePlayer()))
            {
                game.endGame(GAME_END_REASONS.WHITE_RESIGNED);
                resigningPlayer.changeRating(false);
                game.increaseEloUpdated(1);
            }
            else {
                Message failure = new Message("You are not a player.", Message.Type.ERROR);
                return gson.toJson(failure);
            }

            Message success = new Message("Resignation succeeded", Message.Type.INFO);
            return gson.toJson(success);
        }

        Message failure = new Message("The game has already ended.", Message.Type.ERROR);
        return gson.toJson(failure);
    }
}
