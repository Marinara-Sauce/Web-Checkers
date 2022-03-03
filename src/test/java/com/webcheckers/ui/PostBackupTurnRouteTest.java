package com.webcheckers.ui;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webcheckers.application.GameMaster;
import com.webcheckers.model.Game;
import com.webcheckers.util.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.*;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Unit test for {@link PostBackupTurnRoute} component
 *
 * @author <a href="mailto:cfr8511@rit.edu">Charlie Reilly</a>
 */

@Tag("UI-tier")
public class PostBackupTurnRouteTest {

    // Component under Test
    private PostBackupTurnRoute CuT;

    // Friendly
    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();

    // Mock Objects
    private Request request;
    private Response response;
    private Game game;
    private GameMaster gameMaster;

    /**
     * Set up mock objects before each test
     */
    @BeforeEach
    public void setup(){
        request = mock(Request.class);
        response = mock(Response.class);
        gameMaster = mock(GameMaster.class);
        game = mock(Game.class);

        CuT = new PostBackupTurnRoute(gameMaster);
    }

    /**
     * Test if undoMove successful
     */
    @Test
    public void success_message(){
        when(gameMaster.getCurrentGame(eq(request))).thenReturn(game);
        when(game.undoMove()).thenReturn(true);

        System.out.println("Game: " + gameMaster.getCurrentGame(request));
        System.out.println("Backup: " + game.undoMove());

        String result = CuT.handle(request, response).toString();
        Message message = new Message("Backed up successfully!", Message.Type.INFO);

        assertEquals(gson.toJson(message), result);
    }

    /**
     * Test if undoMove is not successful
     */
    @Test
    public void not_success_message(){
        when(gameMaster.getCurrentGame(eq(request))).thenReturn(game);
        when(game.undoMove()).thenReturn(false);

        String result = CuT.handle(request, response).toString();
        Message message = new Message("Back up failed!", Message.Type.ERROR);

        assertEquals(gson.toJson(message), result);
    }
}
