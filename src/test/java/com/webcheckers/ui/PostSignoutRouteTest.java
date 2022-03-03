package com.webcheckers.ui;
import com.webcheckers.application.GameMaster;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Game;
import com.webcheckers.model.Player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import spark.*;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;
import org.junit.jupiter.api.Test;

/**
 * Unit test for {@link PostSignoutRoute} component
 *
 * @author <a href="mailto:ll6209@rit.edu">Liang Liu</a>
 * @author <a href="mailto:cfr8511@rit.edu">Charlie Reilly</a>
 */

@Tag("UI-tier")
public class PostSignoutRouteTest {

    // Component under Test
    private PostSignoutRoute CuT;

    // Friendly
    Player Red;

    // Mock Attributes
    private Request request;
    private Session session;
    private Response response;
    private PlayerLobby playerLobby;
    private Game game;
    private GameMaster gameMaster;
    private Player player; //required to test null player name

    /**
     * Setup mock objects before each test
     */
    @BeforeEach
    public void setup()
    {
        request = mock(Request.class);
        response = mock(Response.class);
        session = mock(Session.class);

        when(request.session()).thenReturn(session);

        player = mock(Player.class);
        playerLobby = mock(PlayerLobby.class);
        game = mock(Game.class);
        gameMaster = mock(GameMaster.class);
        
        when(request.session()).thenReturn(session);

        CuT = new PostSignoutRoute(playerLobby, gameMaster);
    }

    /**
     * Test if a valid player can sign out
     */
    @Test
    public void valid_signout()
    {
        Red = new Player("player1");
        when(session.attribute(PostSigninRoute.PLAYER_KEY)).thenReturn( Red );
        when(gameMaster.gamePlayersIn(eq(Red))).thenReturn(null);

        CuT.handle(request, response);

        verify(playerLobby).removePlayer(Red.getName());
        assertNull(playerLobby.getPlayerFromName(Red.getName()));
    }

    /**
     * Test what happens if null name when attempting to sign out
     */
    @Test
    public void null_name_signout(){
        when(session.attribute(PostSigninRoute.PLAYER_KEY)).thenReturn(player);
        when(player.getName()).thenReturn(null);

        CuT.handle(request, response);

        verify(response).redirect("/");
    }

    /**
     * Test if player signs out mid game
     */
    @Test
    public void signout_in_game(){
        Red = new Player("player1");
        when(session.attribute(PostSigninRoute.PLAYER_KEY)).thenReturn( Red );
        when(gameMaster.gamePlayersIn(eq(Red))).thenReturn(game);
        when(game.getId()).thenReturn(1);

        CuT.handle(request, response);

        verify(gameMaster).endGame(1);
        verify(playerLobby).removePlayer(Red.getName());
    }

    /**
     * Test if a spectator signs out while spectating a game
     */
    @Test
    public void spec_signout(){
        Red = new Player("player1");
        when(session.attribute(PostSigninRoute.PLAYER_KEY)).thenReturn(Red);
        when(gameMaster.getSpectatorGame(eq(Red))).thenReturn(game);
        when(gameMaster.gamePlayersIn(eq(Red))).thenReturn(null);

        CuT.handle(request, response);

        verify(game).subtractSpectator(Red);
        verify(playerLobby).removePlayer(Red.getName());
    }
}
