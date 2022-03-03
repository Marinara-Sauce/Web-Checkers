package com.webcheckers.ui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webcheckers.application.GameMaster;
import com.webcheckers.model.Game;
import com.webcheckers.util.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.*;

/**
 * Unit test for {@link PostSubmitTurnRoute} component
 *
 * author <a href="mailto:cfr8511@rit.edu">Charlie Reilly</a>
 */

@Tag("UI-tier")
public class PostSubmitTurnRouteTest {

    // Component under Test
    private PostSubmitTurnRoute CuT;

    // Friendly
    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();

    // Mock Objects
    private Request request;
    private Response response;
    private Game game;
    private GameMaster gameMaster;
    private Session session;

    /**
     * Set up mock objects
     */
    @BeforeEach
    public void setup(){
        request = mock(Request.class);
        response = mock(Response.class);
        session = mock(Session.class);
        when(request.session()).thenReturn(session);

        //Mocks used for getting session information
        gameMaster = mock(GameMaster.class);
        game = mock(Game.class);

        CuT = new PostSubmitTurnRoute(gameMaster);
    }

    /**
     * Test submitting a move
     */
    @Test
    public void submit_move(){
        when(gameMaster.getCurrentGame(eq(request))).thenReturn(game);

        String result = CuT.handle(request, response).toString();
        verify(game).submitTurn();
        Message message = new Message(PostSubmitTurnRoute.MOVE_SUCCESS, Message.Type.INFO);

        assertEquals(gson.toJson(message), result);
    }
}
