package com.webcheckers.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import com.webcheckers.application.PlayerLobby.STATUS_CODE;
import com.webcheckers.model.Player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit test for the {@link PlayerLobby} component 
 *
 * @author <a href="mailto:djb1808@rit.edu"Dan Bliss (djb1808)</a>
 */
@Tag("Application-tier")
public class PlayerLobbyTest
{
    private static final String VALID_PLAYER_NAME = "validPlayer1";
    private static final String VALID_PLAYER_NAME_2 = "validPlayer2";
    private static final String NOT_A_PLAYER = "notAPlayer";

    private static final String INVALID_PLAYER_NAME_SPECIAL_CHAR = "!invalid_player_2";
    private static final String INVALID_PLAYER_EMPTY = "";
    private static final String INVALID_PLAYER_NAME_LEN = "abcdefghijklmnopqrstuvwxyz123456789";

    //A valid player object
    Player validPlayer;
    Player validPlayer2;

    //The component-under-test
    private PlayerLobby CuT;

    @BeforeEach
    public void testSetup()
    {
        validPlayer = new Player(VALID_PLAYER_NAME);
        validPlayer2 = new Player(VALID_PLAYER_NAME_2);

        CuT = new PlayerLobby();
    }

    @Test
    public void test_create_lobby()
    {
        new PlayerLobby();
    }

    /**
     * Test that we can create a player assuming the name is valid
     * Checks to make sure it returns a success code
     *
     * Testing to make sure the player is stored is done below
     */
    @Test
    public void test_create_valid_player()
    {
        STATUS_CODE status = CuT.addPlayer(VALID_PLAYER_NAME);
        STATUS_CODE status2 = CuT.addPlayer(VALID_PLAYER_NAME_2);

        assertTrue(status == STATUS_CODE.SUCCESS);
        assertTrue(status2 == STATUS_CODE.SUCCESS);

        //Test that we can recall an offline player
        CuT.removePlayer(VALID_PLAYER_NAME);
        CuT.removePlayer(VALID_PLAYER_NAME_2);

        assertTrue(CuT.isPlayerOffline(new Player(VALID_PLAYER_NAME)));
        assertTrue(CuT.isPlayerOffline(new Player(VALID_PLAYER_NAME_2)));

        status2 = CuT.addPlayer(VALID_PLAYER_NAME_2);
        status = CuT.addPlayer(VALID_PLAYER_NAME);

        assertTrue(status == STATUS_CODE.SUCCESS);
        assertTrue(status2 == STATUS_CODE.SUCCESS);
    }

    /**
     * Check that we can recall the most recent player, also
     * confirming they are stored in the player array
     */
    @Test
    public void test_recall_recent_player()
    {
        CuT.addPlayer(VALID_PLAYER_NAME);
        CuT.addPlayer(VALID_PLAYER_NAME_2);

        assertTrue(CuT.getMostRecentPlayer().equals(validPlayer2));
    }

    /**
     * Check that we can spot invalid names
     */
    @Test
    public void test_invalid_players()
    {
        //Test invalid names: 30+ chars, special chars, empty
        assertTrue(CuT.addPlayer(INVALID_PLAYER_NAME_LEN) == STATUS_CODE.INVALID_NAME);
        assertTrue(CuT.addPlayer(INVALID_PLAYER_NAME_SPECIAL_CHAR) == STATUS_CODE.INVALID_NAME);
        assertTrue(CuT.addPlayer(INVALID_PLAYER_EMPTY) == STATUS_CODE.INVALID_NAME);

        //Test invalid names due to duplicates
        CuT.addPlayer(VALID_PLAYER_NAME);
        assertTrue(CuT.addPlayer(VALID_PLAYER_NAME) == STATUS_CODE.PLAYER_ALREADY_EXISTS);
    }

    /**
     * Check that we can get the current number of players
     */
    @Test
    public void test_get_players()
    {
        CuT.addPlayer(VALID_PLAYER_NAME);
        CuT.addPlayer(VALID_PLAYER_NAME_2);

        assertEquals(2, CuT.getPlayersLoggedIn());
    }

    /**
     * Check that we can fetch a player by name
     */
    @Test
    public void test_get_player_by_name()
    {
        CuT.addPlayer(VALID_PLAYER_NAME);
        CuT.addPlayer(VALID_PLAYER_NAME_2);

        assertTrue(CuT.getPlayerFromName(VALID_PLAYER_NAME).equals(validPlayer));
        assertNull(CuT.getPlayerFromName(INVALID_PLAYER_NAME_SPECIAL_CHAR));
    }

    /**
     * Check that we can remove players
     */
    @Test
    public void test_removing_players()
    {
        CuT.addPlayer(VALID_PLAYER_NAME);
        CuT.addPlayer(VALID_PLAYER_NAME_2);

        CuT.removePlayer(VALID_PLAYER_NAME);
        assertEquals(1, CuT.getPlayersLoggedIn());
    }

    /**
     * Check that we can't remove a player that isn't in playerLobby
     */
    @Test
    public void test_removing_non_players()
    {
        CuT.addPlayer(VALID_PLAYER_NAME);
        CuT.addPlayer(VALID_PLAYER_NAME_2);

        final int PLAYERS_LOGGED_IN = CuT.getPlayersLoggedIn();

        CuT.removePlayer(NOT_A_PLAYER);
        assertEquals(PLAYERS_LOGGED_IN, CuT.getPlayersLoggedIn());
    }

    /**
     * Get a list of joinable player
     */
    @Test
    public void test_joinable_players_list()
    {
        CuT.addPlayer(VALID_PLAYER_NAME);
        CuT.addPlayer(VALID_PLAYER_NAME_2);

        //Set false for player 2 (validPlayer2)
        CuT.getPlayerFromName(VALID_PLAYER_NAME_2).setPlayable(false);

        //Check we only have one player that's playable
        assertEquals(1, CuT.getJoinablePlayers().size());
        //Check that that player is joinable
        assertTrue(CuT.getJoinablePlayers().get(0).isPlayable());
    }

    /**
     * Test that we can get a list of all the players
     */
    @Test
    public void test_player_list()
    {
        CuT.addPlayer(VALID_PLAYER_NAME);
        CuT.addPlayer(VALID_PLAYER_NAME_2);

        Collection<Player> expectedPlayer = new ArrayList<>();
        expectedPlayer.add(validPlayer);
        expectedPlayer.add(validPlayer2);

        //Test that we can get a list of all players as strings
        assertTrue(expectedPlayer.equals(CuT.playerList(null)));

        //Test that we can omit certain players
        expectedPlayer.remove(validPlayer2);
        assertTrue(expectedPlayer.equals(CuT.playerList(VALID_PLAYER_NAME_2)));
    }
}