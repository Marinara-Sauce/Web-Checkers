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
 * Unit test for {@link PostResignRoute} component
 *
 * @author <a href="mailto:cfr8511@rit.edu">Charlie Reilly</a>
 */

@Tag("UI-tier")
public class PostResignRouteTest {

    // Component under Test
    private PostResignRoute CuT;

    // Friendly
    Player player = new Player("player");
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

        CuT = new PostResignRoute(gameMaster);
    }

    /**
     * Test if game is not running
     */
    @Test
    public void game_not_running(){
        when(game.gameRunning()).thenReturn(false);

        String result = CuT.handle(request, response).toString();
        Message message = new Message("The game has already ended.", Message.Type.ERROR);

        assertEquals(gson.toJson(message), result);
    }

    /**
     * Test if red resigned
     */
    @Test
    public void red_resign(){
        when(game.gameRunning()).thenReturn(true);
        when(session.attribute(eq(PostSigninRoute.PLAYER_KEY))).thenReturn(player);
        when(game.getRedPlayer()).thenReturn(player);

        String result = CuT.handle(request, response).toString();
        Message message = new Message("Resignation succeeded", Message.Type.INFO);

        assertEquals(gson.toJson(message), result);
    }

    /**
     * Test if white resigned
     */
    @Test
    public void white_resign(){
        when(game.gameRunning()).thenReturn(true);
        when(session.attribute(eq(PostSigninRoute.PLAYER_KEY))).thenReturn(player);
        when(game.getWhitePlayer()).thenReturn(player);

        String result = CuT.handle(request, response).toString();
        Message message = new Message("Resignation succeeded", Message.Type.INFO);

        assertEquals(gson.toJson(message), result);
    }

    /**
     * Test if unknown player tried to resign
     */
    @Test
    public void unknown_player_resign(){
        when(game.gameRunning()).thenReturn(true);
        when(session.attribute(eq(PostSigninRoute.PLAYER_KEY))).thenReturn(new Player("Bob"));
        when(game.getWhitePlayer()).thenReturn(player);
        when(game.getRedPlayer()).thenReturn(player);

        String result = CuT.handle(request, response).toString();
        Message message = new Message("You are not a player.", Message.Type.ERROR);

        assertEquals(gson.toJson(message), result);
    }
}
