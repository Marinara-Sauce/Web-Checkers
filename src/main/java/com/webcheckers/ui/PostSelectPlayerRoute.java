package com.webcheckers.ui;

import com.webcheckers.application.GameMaster;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Game;
import com.webcheckers.model.Player;

import java.util.logging.Logger;
import spark.*;

import java.util.HashMap;
import java.util.Map;

public class PostSelectPlayerRoute implements Route {

    private final Logger LOG = Logger.getLogger(PostSelectPlayerRoute.class.getName());

    public static final String OPP_NOT_PLAYABLE = "This player is not playable";
    public static final String OPP_DOES_NOT_EXIST = "This player does not exist!";
    public static final String LOC_NOT_LOGGED = "You are not currently logged in!";
    public static final String LOC_NOT_PLAYABLE = "You are not in a playable state!";
    public static final String OPP_SPECTATING = "This player is currently spectating a game.";

    private final PlayerLobby playerLobby;

    // --- Attributes --- //
    public static final String OPPONENT_NAME_ATTR = "opponent";
    //private static final String MESSAGE_ATTR = "message";

    public static final String GAME_URL = "/game";
    public static final String HOME_URL = "/";

    private final GameMaster gameMaster;

    public PostSelectPlayerRoute(PlayerLobby playerLobby, GameMaster gameMaster){
        this.playerLobby = playerLobby;

        this.gameMaster = gameMaster;
    }

    @Override
    public Object handle(Request request, Response response){

        LOG.config("PostSelectPlayerRoute was invoked");

        final Session httpSession = request.session();
        final Map<String, Object> vm = new HashMap<>();

        // default attribute is PLAY, updates to SPECTATE if selected player not playable
        httpSession.attribute("view", "PLAY");

        vm.put(GetHomeRoute.TITLE_ATTR, GetHomeRoute.TITLE);

        final String playingAgainstName = request.queryParams(OPPONENT_NAME_ATTR);

        //Attempt to get the opponents account
        Player opponent = playerLobby.getPlayerFromName(playingAgainstName);
        //We can fetch ourselves via the http attribute
        Player localPlayer = httpSession.attribute(PostSigninRoute.PLAYER_KEY);

        if (opponent == null)
        {
            httpSession.attribute("error", OPP_DOES_NOT_EXIST);
            response.redirect(HOME_URL);
            //halt();

            return null;
        }

        //We have a valid opponent, lets see if they are playable
        if (!opponent.isPlayable())
        {
            Game playersGame = gameMaster.gamePlayersIn(opponent);
            int gameId = playersGame.getId();

            httpSession.attribute("view", "SPECTATOR");
            playersGame.addSpectator(localPlayer);
            response.redirect(GAME_URL + "?gameID=" + gameId);
            //halt();

            return null;
        }

        if (opponent.isSpectating())
        {
            httpSession.attribute("error", OPP_SPECTATING);
            response.redirect(HOME_URL);

            return null;
        }
        //We have a valid playable opponent, lets make sure we're good

        //Check that we exist first
        if (localPlayer == null)
        {
            httpSession.attribute("error", LOC_NOT_LOGGED);
            response.redirect(HOME_URL);
            //halt();

            return null;
        }

        //Check that we are playable
        if (!localPlayer.isPlayable())
        {
            httpSession.attribute("error", LOC_NOT_PLAYABLE);
            response.redirect(HOME_URL);
            //halt();

            return null;
        }

        LOG.config("We have our players: " + localPlayer.getName() + " (Red) vs. " + opponent.getName() + " (White)");

        //We have our two players! Set their playable status to false
        localPlayer.setPlayable(false); opponent.setPlayable(false);

        //Create and start the game, then redirect to the game page
        int gameId = gameMaster.startGame(localPlayer, opponent);

        response.redirect(GAME_URL + "?gameID=" + gameId);

        return null;
    }

    /*
    private ModelAndView error(final Map<String, Object> vm, final String message)
    {
        vm.put(MESSAGE_ATTR, new Message(message, Message.Type.ERROR));
        return new ModelAndView(vm, HOME_VIEW);
    }
    */
}