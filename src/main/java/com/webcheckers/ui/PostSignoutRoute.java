package com.webcheckers.ui;

import com.webcheckers.application.GameMaster;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Game;
import com.webcheckers.model.Player;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import java.util.Objects;
import java.util.logging.Logger;

import static com.webcheckers.ui.PostSigninRoute.PLAYER_KEY;
import static com.webcheckers.ui.WebServer.HOME_URL;

/**
 * The UI Controller to GET the Sign Out page.
 *
 * @author Liang Lau
 */
public class PostSignoutRoute implements Route {
    private static final Logger LOG = Logger.getLogger(GetHomeRoute.class.getName());

    static final String TITLE_ATTR = "title";
    static final String TITLE = "Sign Out";

    private final PlayerLobby playerLobby;
    private final GameMaster gameMaster;

    public PostSignoutRoute(final PlayerLobby playerLobby, final GameMaster gameMaster) {
        this.playerLobby = Objects.requireNonNull(playerLobby);
        this.gameMaster = gameMaster;
        //
        LOG.config("GetHomeRoute is initialized.");
    }

    /**
     * Render the WebCheckers Sign-In page.
     *
     * @param request
     *   the HTTP request
     * @param response
     *   the HTTP response
     *
     * @return
     *   the rendered HTML for the Home page
     */
    @Override
    public Object handle(Request request, Response response) {
        LOG.finer("GetSignOutRoute is invoked.");
        final Session httpSession = request.session();

        Player player = httpSession.attribute(PostSigninRoute.PLAYER_KEY);
        String playerName = player.getName();

        //If the player is spectating a game
        Game spectatorGame = gameMaster.getSpectatorGame(player);
        if(spectatorGame != null)
            spectatorGame.subtractSpectator(player);

        playerLobby.removePlayer(playerName);

        //If the player is in a game, end that game
        Game gamePlayersIn = gameMaster.gamePlayersIn(player);
        if (gamePlayersIn != null)
            gameMaster.endGame(gamePlayersIn.getId());

        if (playerName != null) {
            request.session().attribute(PLAYER_KEY, null);
        }
        response.redirect(HOME_URL);
        return null;
    }
}

