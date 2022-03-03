package com.webcheckers.ui;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webcheckers.application.GameMaster;
import com.webcheckers.model.Game;
import com.webcheckers.model.Player;
import com.webcheckers.util.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;

/**
 * Unit test for {@link PostCheckTurnRoute} component
 *
 * author <a href="mailto:cfr8511@rit.edu">Charlie Reilly</a>
 */

@Tag("UI-tier")
public class PostCheckTurnRouteTest {

    // Component under Test
    private PostCheckTurnRoute CuT;

    // Friendly
    Player local = new Player("local");
    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();

    // Mock Objects
    Request request;
    Response response;
    Session session;
    GameMaster gameMaster;
    Game game;

    /**
     * Set up mock objects before each test
     */
    @BeforeEach
    public void setup(){
        request = mock(Request.class);
        response = mock(Response.class);
        session = mock(Session.class);
        when(request.session()).thenReturn(session);

        gameMaster = mock(GameMaster.class);
        game = mock(Game.class);

        when(gameMaster.getCurrentGame(eq(request))).thenReturn(game);

        CuT = new PostCheckTurnRoute(gameMaster);
    }

    /**
     * Test if game ended
     */
    @Test
    public void game_end(){
        when(game.gameRunning()).thenReturn(false);

        String result = CuT.handle(request, response).toString();

        Message message = new Message("true", Message.Type.INFO);

        assertEquals(gson.toJson(message), result);
    }

    /**
     * Test if it's the local player's turn
     */
    @Test
    public void local_turn(){
        when(session.attribute(eq(PostSigninRoute.PLAYER_KEY))).thenReturn(local);
        when(game.gameRunning()).thenReturn(true);
        when(game.playersTurn(eq(local))).thenReturn(true);

        String result = CuT.handle(request, response).toString();
        Message message = new Message("true", Message.Type.INFO);

        assertEquals(gson.toJson(message), result);
    }

    /**
     * Check if the game is still running & it's not the local's turn
     */
    @Test
    public void no_change(){
        when(session.attribute(eq(PostSigninRoute.PLAYER_KEY))).thenReturn(local);
        when(game.gameRunning()).thenReturn(true);
        when(game.playersTurn(eq(local))).thenReturn(false);

        String result = CuT.handle(request, response).toString();
        Message message = new Message("false", Message.Type.INFO);

        assertEquals(gson.toJson(message), result);
    }
}
