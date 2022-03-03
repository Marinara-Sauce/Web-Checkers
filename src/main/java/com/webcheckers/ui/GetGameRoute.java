package com.webcheckers.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webcheckers.application.GameMaster;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Game;
import com.webcheckers.model.Player;
import com.webcheckers.model.Game.GAME_END_REASONS;

import spark.*;

import java.util.HashMap;
import java.util.Objects;
import java.util.Map;

/**
 * Handles HTTP requests to /game, and handles displaying the game
 *
 * @author <a href="mailto:djb1808@rit.edu">Dan Bliss</a>
 * @author <a href="mailto:cfr8511@rit.edu">Charlie Reilly</a>
 */
public class GetGameRoute implements Route
{

    static final String VIEW_NAME = "game.ftl";
    static final String TITTLE_ATTR = "title";
    static final String CURR_USER_ATTR = "currentUser";
    static final String RED_PLAYR_ATTR = "redPlayer";
    static final String WHTE_PLAYR_ATTR = "whitePlayer";
    static final String VIEW_MDE_ATTR = "viewMode";
    static final String ACTVE_COL_ATTR = "activeColor";
    static final String GAME_ID_ATTR = "gameID";
    static final String BOARD_ATTR = "board";
    static final String MODE_OPT_ATTR = "modeOptions";
    static final String MODE_JSON_ATTR = "modeOptionsAsJSON";
    static final String WINNER_ATTR = "winner";

    static final String GAME_ID_FORMAT = "/game?gameID=1";
    static final String NO_GAME_ID = "You have not entered a game ID.";
    static final String INVALID_GAME_ID = "You cannot enter a gameID url when you are not in/spectating the game with said gameID.";

    private final TemplateEngine templateEngine;
    private final GameMaster gameMaster;
    private final PlayerLobby playerLobby;

    public GetGameRoute(TemplateEngine templateEngine, GameMaster gameMaster, PlayerLobby playerLobby){
        Objects.requireNonNull(templateEngine, "templateEngine must not be null.");
        this.templateEngine = templateEngine;

        this.playerLobby = playerLobby;
        this.gameMaster = gameMaster;
    }

    /**
     * Handles HTTP Get Requests to /game, renders the board and
     * sets needed variables
     *
     * @param request the HTTP request
     * @param response the HTTP response
     *
     * @return model and view of the board
     */
    @Override
    public Object handle(Request request, Response response){
        final Session httpSession = request.session();

        final Map<String, Object> vm = new HashMap<>();
        final Map<String, Object> modeOptions = new HashMap<>(2);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        Player localPlayer = httpSession.attribute(PostSigninRoute.PLAYER_KEY);
        
        //Fetch the current game from either a param or attribute
        Game game;


        //If we don't have a game id, then go back to home
        if (request.queryParams(GAME_ID_ATTR) == null)
        {
            httpSession.attribute("error", NO_GAME_ID);
            response.redirect("/");
            return null;
        }
        else
        {
            game = gameMaster.getCurrentGame(request);
            if (game == null || (!game.playerInGame(localPlayer) && !game.spectatorInGame(localPlayer)))
            {
                //If we enter an invalid param or user inputs a gameID url when not in a game and not spectating
                httpSession.attribute("error", INVALID_GAME_ID);
                response.redirect("/");
                return null;
            }
        }

        String view = httpSession.attribute("view");
        vm.put(TITTLE_ATTR, "Game");

        //If the game ended
        if(!game.gameRunning()){

            modeOptions.put("isGameOver", true);

            String gameOverMessage = gameOverMessage(game);
            modeOptions.put("gameOverMessage", gameOverMessage);

            vm.put(MODE_OPT_ATTR, modeOptions);
            vm.put(MODE_JSON_ATTR, gson.toJson(modeOptions));

            if(game.getEloUpdated() < 2)
                updateElo(localPlayer, game, gameOverMessage);
        }

        //These pertain to the current game's status
        vm.put(RED_PLAYR_ATTR, game.getRedPlayer());
        vm.put(WHTE_PLAYR_ATTR, game.getWhitePlayer());
        vm.put(CURR_USER_ATTR, localPlayer);
        vm.put(ACTVE_COL_ATTR, game.getTurnAsString());
        vm.put(GAME_ID_ATTR, game.getId());
        vm.put(WINNER_ATTR, game.getWinner());

        // PLAY, SPECTATOR, REPLAY
        vm.put(VIEW_MDE_ATTR, view);

        //Render the board, flip if needed
        if (localPlayer == game.getRedPlayer())
            vm.put(BOARD_ATTR, game.getBoard().flipBoard());
        else
            vm.put(BOARD_ATTR, game.getBoard());

        return templateEngine.render(new ModelAndView(vm, VIEW_NAME));
    }

    private String gameOverMessage(Game game)
    {
        if (game.getGameOverReason() == GAME_END_REASONS.RED_RESIGNED) {
            game.setWinner(game.getWhitePlayer());
            return game.getRedPlayer().toString() + " has resigned";
        }

        else if (game.getGameOverReason() == GAME_END_REASONS.WHITE_RESIGNED) {
            game.setWinner(game.getRedPlayer());
            return game.getWhitePlayer().toString() + " has resigned";
        }

        else if (game.getGameOverReason() == GAME_END_REASONS.RED_NO_PIECES) {
            game.setWinner(game.getWhitePlayer());
            return game.getRedPlayer().toString() + " has no more pieces";
        }

        else if (game.getGameOverReason() == GAME_END_REASONS.WHITE_NO_PIECES) {
            game.setWinner(game.getRedPlayer());
            return game.getWhitePlayer().toString() + " has no more pieces";
        }

        else return "Game Over for Unknown Reason";
    }

    private void updateElo(Player local, Game game, String gameOverMessage) {
        Player redPlayer = game.getRedPlayer();
        Player whitePlayer = game.getWhitePlayer();
        Player winner = game.getWinner();

        if(game.playerInGame(local)) {
            if (gameOverMessage.equals("Game Over for Unknown Reason") && playerLobby.isPlayerOffline(redPlayer)) {
                game.setWinner(whitePlayer);
                redPlayer.changeRating(false);
                whitePlayer.changeRating(true);
                game.increaseEloUpdated(2);
            } else if (gameOverMessage.equals("Game Over for Unknown Reason") && playerLobby.isPlayerOffline(whitePlayer)) {
                game.setWinner(redPlayer);
                whitePlayer.changeRating(false);
                redPlayer.changeRating(true);
                game.increaseEloUpdated(2);
            } else if (!gameOverMessage.equals("Game Over for Unknown Reason") && local == redPlayer) {
                local.changeRating(local == winner);
                game.increaseEloUpdated(1);
            } else if (!gameOverMessage.equals("Game Over for Unknown Reason") && local == whitePlayer) {
                local.changeRating(local == winner);
                game.increaseEloUpdated(1);
            }
        }
    }

}