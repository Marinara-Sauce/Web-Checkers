package com.webcheckers.ui.spectator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webcheckers.application.GameMaster;
import com.webcheckers.model.Game;
import com.webcheckers.util.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;

/**
 * Unit test for {@link PostSpectatorCheckTurnRoute} component
 *
 * @author <a href="mailto:cfr8511@rit.edu">Charlie Reilly</a>
 */

@Tag("UI-tier")
public class PostSpectatorCheckTurnRouteTest {

    // Component under Test
    private PostSpectatorCheckTurnRoute CuT;

    // Friendly
    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();

    // Mock Objects
    private Request request;
    private Response response;
    private GameMaster gameMaster;
    private Game game;

    /**
     * Setup mock objects before each test
     */
    @BeforeEach
    public void setup(){
        request = mock(Request.class);
        response = mock(Response.class);

        gameMaster = mock(GameMaster.class);
        game = mock(Game.class);
        when(gameMaster.getCurrentGame(eq(request))).thenReturn(game);

        CuT = new PostSpectatorCheckTurnRoute(gameMaster);
    }

    /**
     * Test if the game has ended
     */
    @Test
    public void game_not_running(){
        when(game.gameRunning()).thenReturn(false);

        String result = CuT.handle(request, response).toString();

        Message message = new Message("true", Message.Type.INFO);
        assertEquals(gson.toJson(message), result);
    }

    /**
     * Test if the board has updated with a new move
     */
    @Test
    public void new_state(){
        when(game.gameRunning()).thenReturn(true);
        when(game.getBoardState()).thenReturn(true);

        String result = CuT.handle(request, response).toString();
        verify(game).boardUpdated();

        Message message = new Message("true", Message.Type.INFO);
        assertEquals(gson.toJson(message), result);
    }

    /**
     * Test if the game is still running and there is no new board update
     */
    @Test
    public void no_turn(){
        when(game.gameRunning()).thenReturn(true);
        when(game.getBoardState()).thenReturn(false);

        String result = CuT.handle(request, response).toString();

        Message message = new Message("false", Message.Type.INFO);
        assertEquals(gson.toJson(message), result);
    }

}
