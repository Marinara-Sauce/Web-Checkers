package com.webcheckers.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.webcheckers.model.Game;
import com.webcheckers.model.Player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import spark.Request;

/**
 * Unit test for the {@link PlayerLobby} component 
 *
 * @author <a href="mailto:djb1808@rit.edu"Dan Bliss (djb1808)</a>
 */
@Tag("Application-tier")
public class GameMasterTest
{
    //Dummy names
    private final String RED_PLAYER = "RED";
    private final String WHITE_PLAYER = "WHITE";

    private final String RED_PLAYER2 = "RED2";
    private final String WHITE_PLAYER2 = "WHITE2";

    //The component-under-test
    private GameMaster cut;

    // Friendly
    Player spectator = new Player("spectator");
    Player spectator2 = new Player("spectator2");

    @BeforeEach
    public void testSetup()
    {
        cut = new GameMaster();
    }

    @Test
    public void test_create_lobby()
    {
        new GameMaster();
    }

    /**
     * Test that we can receive the games list
     */
    @Test
    public void testGetGames()
    {
        //Simply add 2 games and make sure that the length of the list is 2
        cut.startGame(new Player(RED_PLAYER), new Player(WHITE_PLAYER));
        cut.startGame(new Player(RED_PLAYER), new Player(WHITE_PLAYER));

        assertEquals(2, cut.getGames().size());
    }

    /**
     * Test that we can start a game
     */
    @Test
    public void testStartGame()
    {
        //Games list is empty, so the new game ID should be 1
        int gameId = cut.startGame(new Player(RED_PLAYER), new Player(WHITE_PLAYER));
        assertEquals(1, gameId);

        //Assert that we can spawn a new game and the ID increments by one
        gameId = cut.startGame(new Player(RED_PLAYER), new Player(WHITE_PLAYER));
        assertEquals(2, gameId);

        //Assert that the games are in the gamesList
        assertEquals(2, cut.getGames().size());
    }

    /**
     * Test that we can fetch the archived games list
     */
    @Test
    public void testGetArchivedList()
    {
        //Start 2 games
        spawnTwoGames();

        //End the 2 games
        cut.endGame(1); cut.endGame(2);

        //Make sure that the list has 2 elements
        assertEquals(2, cut.getArchivedGames().size());
    }

    /**
     * Test that we can fetch games by an ID
     */
    @Test
    public void testGetGameByID()
    {
        //Start 2 games
        spawnTwoGames();

        //Get the 1st and 2nd game
        Game game1 = cut.getGameByID(1);
        Game game2 = cut.getGameByID(2);

        //Attempt to get a game that doesn't exist
        Game game3 = cut.getGameByID(3);

        //Make sure the games are identical to what we asked for
        assertEquals(1, game1.getId());
        assertEquals(2, game2.getId());

        assertNull(game3);
    }

    /**
     * Test that we can end games
     */
    @Test
    public void testEndingGames()
    {
        //Start 2 games
        spawnTwoGames();

        //End game 2
        cut.endGame(2);

        //Check that the games in the archived list and current list are the right games
        assertEquals(1, cut.getGames().get(0).getId());
        assertEquals(2, cut.getArchivedGames().get(0).getId());

        //Check that there aren't any extra elements in the arrays
        assertEquals(1, cut.getGames().size());
        assertEquals(1, cut.getArchivedGames().size());

        Game game1 = cut.getGameByID(1);
        game1.endGame(Game.GAME_END_REASONS.RED_NO_PIECES);
        cut.endGame(1);

        assertEquals(1, cut.getArchivedGames().get(1).getId());
    }

    /**
     * Check that we can get an archived game by an ID
     */
    @Test
    public void testGettingArchivedGameByID()
    {
        //Start 2 games
        spawnTwoGames();

        //End both games
        cut.endGame(1); cut.endGame(2);

        //Fetch the 2nd game, make sure the index matches
        Game game = cut.getArchivedGameByID(2);
        assertEquals(2, game.getId());

        //Attempt to get a game that doesn't exist
        assertNull(cut.getArchivedGameByID(3));
    }

    /**
     * Test that we can fetch what game a player is in
     */
    @Test
    public void testPlayerInAGame()
    {
        spawnTwoGames();

        Player playerInGame = new Player(RED_PLAYER);
        Player playerInGame2 = new Player(RED_PLAYER2);
        Player playerNotInGame = new Player("DavidMunson");

        Game game1 = cut.gamePlayersIn(playerInGame); //ID: 1
        Game game2 = cut.gamePlayersIn(playerInGame2); //ID: 2
        Game game3 = cut.gamePlayersIn(playerNotInGame); //NULL

        assertEquals(1, game1.getId());
        assertEquals(2, game2.getId());
        assertNull(game3);
    }

    /**
     * Test we can get the current game based off a request
     */
    @Test
    public void testGetCurrentGame()
    {
        spawnTwoGames();

        //Setup the mock request
        Request request = mock(Request.class);
        when(request.queryParams(eq("gameID"))).thenReturn("1");

        //Test that we can fetch the game
        Game game = cut.getCurrentGame(request);
        assertEquals(1, game.getId());
    }

    @Test
    public void spectator_game() {
        spawnTwoGames();

        Game game1 = cut.getGameByID(1);
        game1.addSpectator(spectator);
        Game game2 = cut.getGameByID(2);
        game2.addSpectator(spectator2);


        Game spectatorGame = cut.getSpectatorGame(spectator);
        assertEquals(1, spectatorGame.getId());
        spectatorGame = cut.getSpectatorGame(spectator2);
        assertEquals(2, spectatorGame.getId());
    }

    @Test
    public void spectator_not_in_game() {
        Game spectatorGame = cut.getSpectatorGame(spectator);

        assertNull(spectatorGame);
    }

    @Test
    public void spec_in_game_not_spec() {
        spawnTwoGames();

        Game game1 = cut.getGameByID(1);
        game1.addSpectator(spectator);
        spectator.setSpectating(false);

        Game spectatorGame = cut.getSpectatorGame(spectator);
        assertNull(spectatorGame);
    }

    /**
     * Spawns two games, with id's 1 and 2 for testing
     */
    public void spawnTwoGames()
    {
        cut.startGame(new Player(RED_PLAYER), new Player(WHITE_PLAYER));
        cut.startGame(new Player(RED_PLAYER2), new Player(WHITE_PLAYER2));
    }
}