package com.webcheckers.application;

import java.util.ArrayList;
import java.util.List;

import com.webcheckers.model.Game;
import com.webcheckers.model.Player;

import spark.Request;

public class GameMaster 
{
    //List of all active games
    private List<Game> gamesList = new ArrayList<>();

    //When a game is over, it moves into the archive, used for replays
    private List<Game> gameArchive = new ArrayList<>();

    /**
     * Spawn and start a new game
     * @param redPlayer the red player
     * @param whitePlayer the white player
     * @return the id
     */
    public int startGame(Player redPlayer, Player whitePlayer)
    {
        int id = gamesList.size() + gameArchive.size() + 1;

        Game newGame = new Game(id);

        gamesList.add(newGame);
        newGame.startGame(redPlayer, whitePlayer);

        return id;
    }

    /**
     * Ends a particular game. Can be called anywhere as long
     * as they have the game ID.
     * 
     * If the game is not already over, it will end the game
     * it.
     * 
     * Once this is done it moves the game from the gameList to
     * the archive.
     * 
     * @param id the id of the game to end
     */
    public void endGame(int id)
    {
        Game game = getGameByID(id);
        
        //If the game is still running, end it
        if (game.getGameOverReason() == null)
            game.endGame(Game.GAME_END_REASONS.ENDED_BY_SERVER);
        
        gameArchive.add(game);
        gamesList.remove(game);
    }

    /**
     * Fetches a particular game instance based on an ID
     * 
     * @param id the id to search for
     * @return the game with that id, null if no game found
     */
    public Game getGameByID(int id)
    {
        for (Game game : gamesList) {
            if (game.getId() == id)
                return game;
        }

        //IF this fails, check the archived games
        //When a game ends, it gets archived, so good spot to check

        return getArchivedGameByID(id);
    }

    /**
     * Same as getGameByID() but searches archived game
     * 
     * @param id the id of the game
     * @return the game, null if it's not found
     */
    public Game getArchivedGameByID(int id)
    {
        for (Game game : gameArchive) {
            if (game.getId() == id)
                return game;
        }

        return null;
    }

    /**
     * Identifies if a certain player is in a game
     * 
     * @param player the player to test for
     * @return the game the player is in, null if the player isn't in a game
     */
    public Game gamePlayersIn(Player player)
    {
        for (Game game : gamesList) {
            if (game.playerInGame(player) && game.gameRunning())
                return game;
        }

        return null;
    }

    /**
     * Gets the current game from an HTTP request
     * The attribute stores the ID of the game
     * 
     * @param request the request
     * @return the game
     */
    public Game getCurrentGame(Request request)
    {
        return getGameByID(Integer.parseInt(request.queryParams("gameID")));
    }

    public List<Game> getGames()
    {
        return gamesList;
    }

    public List<Game> getArchivedGames()
    {
        return gameArchive;
    }

    public Game getSpectatorGame(Player player) {
        for (Game game : gamesList) {
            if (game.spectatorInGame(player) && player.isSpectating())
                return game;
        }

        return null;
    }
}
