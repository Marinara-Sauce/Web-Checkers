package com.webcheckers.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import com.webcheckers.application.GameMaster;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Game;
import com.webcheckers.model.Player;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;
import spark.TemplateEngine;

import com.webcheckers.util.Message;

/**
 * The UI Controller to GET the Home page.
 *
 * @author <a href='mailto:bdbvse@rit.edu'>Bryan Basham</a>
 * @author <a href="mailto:djb1808@rit.edu">Dan Bliss (djb1808)</a>
 */
public class GetHomeRoute implements Route {
    private static final Logger LOG = Logger.getLogger(GetHomeRoute.class.getName());

    public static final Message WELCOME_MSG = Message.info("Welcome to the world of online Checkers.");

    // --- Attributes --- //
    public static final String PLAYERS_ONLINE_ATTR = "numPlayers";
    public static final String PLAYERS_LIST_ATTR = "players";
    public static final String TITLE_ATTR = "title";
    public static final String GAME_ID_ATTR = "gameID";

    // --- Entities --- //
    private final TemplateEngine templateEngine;
    private final PlayerLobby playerLobby;
    private final GameMaster gameMaster;

    // --- Strings --- //
    public static final String TITLE = "Welcome!";

    /**
     * Create the Spark Route (UI controller) to handle all {@code GET /} HTTP requests.
     *
     * @param templateEngine the HTML template rendering engine
     */
    public GetHomeRoute(final TemplateEngine templateEngine, PlayerLobby playerLobby, GameMaster gameMaster) {
        this.templateEngine = Objects.requireNonNull(templateEngine, "templateEngine is required");
        this.playerLobby = playerLobby;
        this.gameMaster = gameMaster;
        //

        LOG.config("GetHomeRoute is initialized.");
    }

    /**
     * Render the WebCheckers Home page.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @return the rendered HTML for the Home page
     */
    @Override
    public Object handle(Request request, Response response) {
        LOG.finer("GetHomeRoute is invoked.");
        //
        final Session httpSession = request.session();
        Map<String, Object> vm = new HashMap<>();
        vm.put(TITLE_ATTR, TITLE);

        //If redirected to HOME view with error message, display error message
        final String errorMessage = httpSession.attribute("error");
        if (errorMessage != null) {
            Message message = Message.error(errorMessage);
            vm.put("message", message);
            httpSession.removeAttribute("error");
        }

        //Initialize the user session

        boolean playerLoggedIn = false;

        Player local = httpSession.attribute(PostSigninRoute.PLAYER_KEY);

        //Check for the existence of a player attribute
        if (local != null) {
            httpSession.attribute("currentUser", local);
            final String playerName = local.getName();

            //Display the current user and make the sign out visible
            vm.put("currentUser", local);
            playerLoggedIn = true;

            //Show the list of joinable players
            vm.put(PLAYERS_LIST_ATTR, playerLobby.playerList(playerName));

            Game playersGame = gameMaster.gamePlayersIn(local);

            //Check to see if the player is in a game. If so, send the player to the game screen
            if (playersGame != null) {
                httpSession.attribute("view", "PLAY");
                response.redirect("/game?gameID=" + playersGame.getId());
                return null;
            }
            //Check if player spectating. If so, send the player to spectate screen
            else {
                Game spectatorGame = gameMaster.getSpectatorGame(local);
                if(spectatorGame != null) {
                    response.redirect("/game?gameID=" + spectatorGame.getId());
                    return null;
                }
            }
        }

        //If the players not logged in, we need to display the num of players online
        if (!playerLoggedIn) {
            vm.put(PLAYERS_ONLINE_ATTR, playerLobby.getPlayersLoggedIn());
            vm.put("message", WELCOME_MSG);
        }

        // render the View
        return templateEngine.render(new ModelAndView(vm, "home.ftl"));
    }
}