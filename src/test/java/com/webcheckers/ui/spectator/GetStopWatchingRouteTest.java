package com.webcheckers.ui.spectator;

import static org.mockito.Mockito.*;

import com.webcheckers.application.GameMaster;
import com.webcheckers.model.Game;
import com.webcheckers.model.Player;
import com.webcheckers.ui.PostSigninRoute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.*;

/**
 * Unit test for {@link GetStopWatchingRoute} component
 *
 * author <a href="mailto:cfr8511@rit.edu">Charlie Reilly</a>
 */

@Tag("UI-tier")
public class GetStopWatchingRouteTest {

    // Component under Test
    private GetStopWatchingRoute CuT;

    // Friendly
    Player local = new Player("local");

    // Mock Objects
    private Request request;
    private Response response;
    private GameMaster gameMaster;
    private Session session;
    private Game game;

    /**
     * Setup mock objects before each test
     */
    @BeforeEach
    public void setup(){
        request = mock(Request.class);
        response = mock(Response.class);
        session = mock(Session.class);

        when(request.session()).thenReturn(session);
        gameMaster = mock(GameMaster.class);
        game = mock(Game.class);

        CuT = new GetStopWatchingRoute(gameMaster);
    }

    /**
     * Test if a spectator presses the exit button
     */
    @Test
    public void exit(){
        when(session.attribute(eq(PostSigninRoute.PLAYER_KEY))).thenReturn(local);
        when(request.queryParams(eq("gameID"))).thenReturn("1");
        when(gameMaster.getGameByID(eq(1))).thenReturn(game);

        CuT.handle(request, response);

        verify(game).subtractSpectator(local);
        verify(response).redirect(GetStopWatchingRoute.HOME_URL);
    }
}
