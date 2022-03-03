package com.webcheckers.ui;

import com.webcheckers.application.GameMaster;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Game;
import com.webcheckers.model.Player;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateEngine;
import spark.Session;

/**
 * Unit test for the {@link GetHomeRoute} component
 *
 * @author <a href="mailto:djb1808@rit.edu">Dan Bliss (djb1808)</a>
 * @author <a href="mailto:cfr8511@rit.edu">Charlie Reilly</a>
 */

@Tag("UI-tier")
public class GetHomeRouteTest
{
    public static final String VALID_PLAYER_NAME_1 = "DaveMunson";

    //Create the component-under-test
    private GetHomeRoute CuT;

    //Friendly
    private TemplateEngineTester testHelper = new TemplateEngineTester();
    private PlayerLobby playerLobby = new PlayerLobby();
    private Player local = new Player(VALID_PLAYER_NAME_1);

    //Mock Attributes
    private Request request;
    private Session session;
    private Response response;

    private TemplateEngine templateEngine;
    private GameMaster gameMaster;
    private Game game;

    /**
     * Set up mock components and the CuT before each test
     */
    @BeforeEach
    public void setup()
    {
        request = mock(Request.class);
        response = mock(Response.class);
        session = mock(Session.class);
        when(request.session()).thenReturn(session);

        templateEngine = mock(TemplateEngine.class);
        when(templateEngine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

        gameMaster = mock(GameMaster.class);
        game = mock(Game.class);

        CuT = new GetHomeRoute(templateEngine, playerLobby, gameMaster);
    }

    /**
     * Test bad logins via special character and long name
     */
    @Test
    public void render_no_logged_player()
    {
        CuT.handle(request, response);

        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();

        //Test for standard stuff
        testHelper.assertViewModelAttribute(GetHomeRoute.TITLE_ATTR, GetHomeRoute.TITLE);

        //Test for the exsistence of the player counter
        testHelper.assertViewModelAttribute(GetHomeRoute.PLAYERS_ONLINE_ATTR, playerLobby.getPlayersLoggedIn());
        //Test for the nonexsistence of a current user
        testHelper.assertViewModelAttributeIsAbsent("currentUser");

        //Add a new player and test that the players logged in reflects this
        playerLobby.addPlayer(VALID_PLAYER_NAME_1);

        CuT.handle(request, response);

        testHelper.assertViewModelAttribute(GetHomeRoute.PLAYERS_ONLINE_ATTR, playerLobby.getPlayersLoggedIn());
    }

    /**
     * Test that the window displays proper information when there's a player
     * logged in
     */

    @Test
    public void render_player_logged_in()
    {
        //Add a player to the session
        when(session.attribute(PostSigninRoute.PLAYER_KEY)).thenReturn(local);

        CuT.handle(request, response);

        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();

        //Test for the title as usual
        testHelper.assertViewModelAttribute(GetHomeRoute.TITLE_ATTR, GetHomeRoute.TITLE);

        //Test that the online players counter is invisible
        testHelper.assertViewModelAttributeIsAbsent(GetHomeRoute.PLAYERS_ONLINE_ATTR);

        //Test that the player name is visible at the top
        testHelper.assertViewModelAttribute("currentUser", local);

        //Check that we have create a list of all active players
        testHelper.assertViewModelAttribute(GetHomeRoute.PLAYERS_LIST_ATTR, playerLobby.playerList(VALID_PLAYER_NAME_1));
    }

    /**
     * Test that the player gets redirected when a player is logged in
     */

    @Test
    public void redirect_on_game_running()
    {
        when(game.gameRunning()).thenReturn(true);
        when(game.playerInGame(local)).thenReturn(true);

        when(session.attribute(PostSigninRoute.PLAYER_KEY)).thenReturn(local);
        when(gameMaster.gamePlayersIn(eq(local))).thenReturn(game);
        when(game.getId()).thenReturn(1);

        System.out.println("Game player is in: " + gameMaster.gamePlayersIn(local).getId());

        CuT.handle(request, response);

        //Test that we do a redirect
        verify(response).redirect("/game?gameID=1");
    }

    /**
     * Test that an error message displays when not null
     */
    @Test
    public void error_message(){
        when(session.attribute(PostSigninRoute.PLAYER_KEY)).thenReturn(local);

        final String success = "success";

        when(session.attribute("error")).thenReturn(success);

        CuT.handle(request, response);

        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();

        /*
        Can't check "message" attribute value as Message does not override its equals method
        So, verify the statement after which it occurs
        */
        verify(session).removeAttribute("error");
    }

    /**
     * Test game redirect condition when gameRunning = true & playerInGame = false
     */
    @Test
    public void redirect_fail_by_1(){
        when(game.gameRunning()).thenReturn(true);
        when(game.playerInGame(local)).thenReturn(false);

        when(session.attribute(PostSigninRoute.PLAYER_KEY)).thenReturn(local);

        CuT.handle(request, response);

        //we stay on home
        testHelper.assertViewName("home.ftl");
    }

    /**
     * Test game redirect condition when gameRunning = false & playerInGame = true
     */
    @Test
    public void redirect_fail_by_2(){
        when(game.gameRunning()).thenReturn(false);
        when(game.playerInGame(local)).thenReturn(true);

        when(session.attribute(PostSigninRoute.PLAYER_KEY)).thenReturn(local);

        CuT.handle(request, response);

        //we stay on home
        testHelper.assertViewName("home.ftl");
    }

    /**
     * Test if a spectator presses the home hyperlink
     */
    @Test
    public void redirect_spec(){
        when(session.attribute(PostSigninRoute.PLAYER_KEY)).thenReturn(local);
        when(game.gameRunning()).thenReturn(true);
        when(game.playerInGame(local)).thenReturn(false);
        when(gameMaster.getSpectatorGame(local)).thenReturn(game);
        when(game.getId()).thenReturn(1);

        CuT.handle(request, response);

        verify(response).redirect("/game?gameID=" + game.getId());
    }

}
