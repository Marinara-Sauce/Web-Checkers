package com.webcheckers.application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.webcheckers.model.Player;

/**
 * This class handles all of the players active in the game.
 * 
 * The main feature of this class is the players list. This list stores a copy
 * of every single player in the game. The players then get replicated to the
 * individual.
 * 
 * This can be used to get a list of players, register a new one, and check
 * for duplicate names.
 * 
 * @author <a href="mailto:djb1808@rit.edu">Dan Bliss (djb1808)</a>
 */
public class PlayerLobby {
    private static final Logger LOG = Logger.getLogger(PlayerLobby.class.getName());

    //Stores a list of all active player, maps the name to the player
    private List<Player> players = new ArrayList<>();

    private List<Player> offline_players = new ArrayList<>();

    /**
     * Potential status codes that can be returned when a player is created
     * 
     * These codes are summoned in the following circumstances
     * 
     * SUCCESS - Player was successfully created and added
     * INVALID_NAME - Player's name was empty or contained illegal characters
     * PLAYER_ALREADY_EXISTS - The player's name was taken
     */
    public enum STATUS_CODE {
        INVALID_NAME,
        PLAYER_ALREADY_EXISTS,
        SUCCESS
    }

    /**
     * Takes a player name, makes a new player, and adds it to the playerArray
     * if the name is valid
     * 
     * @param name the name of the player
     * @return a status code cooresponding to an error (if applicable)
     */
    public STATUS_CODE addPlayer(String name)
    {
        //Create the new player, potentially add it later
        Player newPlayer = new Player(name);

        if (name.isEmpty() || name.length() > 30)
            return STATUS_CODE.INVALID_NAME;

        if (containsSpecialCharacter(name))
            return STATUS_CODE.INVALID_NAME;

        if (players.contains(newPlayer))
            return STATUS_CODE.PLAYER_ALREADY_EXISTS;

        if(offline_players.contains(newPlayer)) {
            LOG.config("Returning Player: " +name);
            for(int i = 0; i < offline_players.size(); i++) {
                if(offline_players.get(i).getName().equals(name))
                    players.add(offline_players.remove(i));
            }
        }
        else {
            LOG.config("Created Player: " + name);
            players.add(newPlayer);
        }

        return STATUS_CODE.SUCCESS;
    }

    /**
     * Fetches the most recent player that's been created
     * 
     * @return the most recent player class
     */
    public Player getMostRecentPlayer()
    {
        return players.get(players.size() - 1);
    }

    /**
     * Gets a count of all the players logged in
     * 
     * @return int of all players logged in
     */
    public int getPlayersLoggedIn()
    {
        return players.size();
    }

    /**
     * Gets a list of players that have "joinable" set to true
     * 
     * @return list of all joinable players
     */
    public List<Player> getJoinablePlayers()
    {
        List<Player> joinablePlayers = new ArrayList<>();

        for (Player p : players) {
            if (p.isPlayable())
                joinablePlayers.add(p);
        }

        return joinablePlayers;
    }

    /**
     * Gets the list of currently online players,
     * while omitting a specific player
     * (usually the player running the function)
     *
     * @param omit
     *  the player name to omit (usually the player running the function)
     *
     * @return a collection of all the player - the omitted player
     */
    public Collection<Player> playerList(String omit)
    {
        Collection<Player> allPlayers = new ArrayList<>();

        for (Player player : players) {
            String name = player.toString();

            if (!name.equals(omit))
                allPlayers.add(player);
        }

        return allPlayers;
    }

    /**
     * Gets a player based on a user name
     * 
     * @param name the username to look for
     * @return the player
     */
    public Player getPlayerFromName(String name)
    {
        for (Player player : players) {
            if (player.getName().equals(name))
                return player;
        }

        return null;
    }

    public boolean containsSpecialCharacter(String s)
    {
        Pattern p = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(s);

        return m.find();
    }

    public void removePlayer(String name) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equals(name))
            {
                offline_players.add(players.remove(i));
            }
        }
    }

    public boolean isPlayerOffline(Player player) {
        return offline_players.contains(player);
    }
}
