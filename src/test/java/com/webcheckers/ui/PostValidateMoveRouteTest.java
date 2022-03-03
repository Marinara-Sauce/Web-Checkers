package com.webcheckers.ui;

import static org.junit.jupiter.api.Assertions.*;

import com.webcheckers.application.GameMaster;
import com.webcheckers.model.*;
import com.webcheckers.model.Game.MOVE_STATUS_CODES;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;

/**
 * Unit test for {@link PostValidateMoveRoute} component
 *
 * @author <a href="mailto:cfr8511@rit.edu">Charlie Reilly</a>
 * @author <a href="mailto:cfr8511@rit.edu">Dan Bliss</a>
 */

@Tag("UI-tier")
public class PostValidateMoveRouteTest {

    // Component under test
    private PostValidateMoveRoute CuT;

    // Error messages generated
    private final String INVALID_MOVE_ERR = "{\"text\":\"Invalid Move!\",\"type\":\"ERROR\"}";
    private final String VALID_MOVE = "{\"text\":\"Valid move!\",\"type\":\"INFO\"}";
    private final String INVALID_MOVE_HOP_ERR = "{\"text\":\"Invalid Move! Jump is Avaliable\",\"type\":\"ERROR\"}";

    // This is a potential move that can be generated
    private final String MOVE_EX = "{\"start\":{\"row\":2,\"cell\":1},\"end\":{\"row\":3,\"cell\":0}}";

    // Friendly
    private Request request;
    private Response response;
    private Game game;
    private GameMaster gameMaster;

    @BeforeEach
    public void setup()
    {
        request = mock(Request.class);
        response = mock(Response.class);
        game = mock(Game.class);
        gameMaster = mock(GameMaster.class);

        CuT = new PostValidateMoveRoute(gameMaster);

        //Setup a mock URL and Move (these go unused, usually replaced with anotehr mock)
        when(request.queryParams(eq("actionData"))).thenReturn(MOVE_EX);
        when(request.queryParams(eq(GetGameRoute.GAME_ID_ATTR))).thenReturn("1");

        //Setup mock game and mock moves
        when(gameMaster.getGameByID(eq(1))).thenReturn(game);
        //when(gson.fromJson(eq("DATA_EXAMPLE"), eq(Move.class))).thenReturn(move);
    }

    /**
     * Test that we return a valid move message when we get a 
     * valid move
     */
    @Test
    public void test_valid_move()
    {
        when(game.makeMove(any())).thenReturn(MOVE_STATUS_CODES.SUCCESS);

        String json = (String) CuT.handle(request, response);
        assertTrue(json.equals(VALID_MOVE));
    }

    /**
     * Test that we return "invalid move" on a bad move
     */
    @Test
    public void test_invalid_move()
    {
        when(game.makeMove(any())).thenReturn(MOVE_STATUS_CODES.INVALID_DIRECTION_DOWN);

        String json = (String) CuT.handle(request, response);
        assertTrue(json.equals(INVALID_MOVE_ERR));
    }

    /**
     * Test that we return "jump avaliable" if the verifier
     * says there is
     */
    @Test
    public void test_jump_avaliabe()
    {
        when(game.makeMove(any())).thenReturn(MOVE_STATUS_CODES.JUMP_AVALIABLE);

        String json = (String) CuT.handle(request, response);
        assertTrue(json.equals(INVALID_MOVE_HOP_ERR));
    }

}
