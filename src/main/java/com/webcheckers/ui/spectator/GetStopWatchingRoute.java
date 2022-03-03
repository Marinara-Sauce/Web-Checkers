package com.webcheckers.ui.spectator;

import com.webcheckers.application.GameMaster;
import com.webcheckers.model.Game;
import com.webcheckers.model.Player;
import com.webcheckers.ui.PostSigninRoute;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

/**
 * GET Route handles a spectator clicking the exit button
 *
 * @author <a href="mailto:cfr8511@rit.edu">Charlie Reilly</a>
 */
public class GetStopWatchingRoute implements Route {
    static final String HOME_URL = "/";

    private GameMaster gameMaster;

    public GetStopWatchingRoute(GameMaster gameMaster){ this.gameMaster = gameMaster; }
    @Override
    public Object handle(Request request, Response response) {
        Session httpSession = request.session();

        int gameID = Integer.parseInt(request.queryParams("gameID"));
        Player localPlayer = httpSession.attribute(PostSigninRoute.PLAYER_KEY);
        Game game = gameMaster.getGameByID(gameID);

        game.subtractSpectator(localPlayer);
        response.redirect(HOME_URL);
        return null;
    }
}
