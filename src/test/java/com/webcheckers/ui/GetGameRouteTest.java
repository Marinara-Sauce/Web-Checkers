package com.webcheckers.ui;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webcheckers.application.GameMaster;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Board;
import com.webcheckers.model.Game;
import com.webcheckers.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.*;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit test for {@link GetGameRoute} component
 *
 * @author <a href="mailto:cfr8511@rit.edu">Charlie Reilly</a>
 */

@Tag("UI-tier")
public class GetGameRouteTest {

    // Component under Test
    private GetGameRoute CuT;

    // friendly
    TemplateEngineTester testHelper = new TemplateEngineTester();
    Player redPlayer;
    Player whitePlayer;
    Player spectator = new Player("spectator");
    final Map<String, Object> modeOptions = new HashMap<>(2);
    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();


    // attributes holding mock objects
    private Request request;
    private Response response;
    private TemplateEngine templateEngine;
    private Session session;
    private GameMaster gameMaster;
    private Game game;
    private Board board;
    private PlayerLobby playerLobby;

    /**
     * Set up mock and friendly objects before each test
     */
    @BeforeEach
    public void setup(){
        request = mock(Request.class);
        response = mock(Response.class);
        session = mock(Session.class);
        when(request.session()).thenReturn(session);

        templateEngine = mock(TemplateEngine.class);
        when(templateEngine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

        gameMaster = mock(GameMaster.class);
        game = mock(Game.class);
        playerLobby = mock(PlayerLobby.class);

        redPlayer = new Player("red");
        whitePlayer = new Player("white");

        when(game.getRedPlayer()).thenReturn(redPlayer);
        when(game.getWhitePlayer()).thenReturn(whitePlayer);
        when(game.getTurnAsString()).thenReturn("RED");

        when(request.queryParams(eq(GetGameRoute.GAME_ID_ATTR))).thenReturn("1");
        when(gameMaster.getCurrentGame(eq(request))).thenReturn(game);

        board = mock(Board.class);
        CuT = new GetGameRoute(templateEngine, gameMaster, playerLobby);
    }

    /**
     * Test constructor
     */
    @Test
    public void ctor_withArgs(){ assertNotNull(CuT); }

    /**
     * Test game view creation when you're the white player
     */
    @Test
    public void game_view_normal(){
        when(session.attribute(eq(PostSigninRoute.PLAYER_KEY))).thenReturn(whitePlayer);
        when(session.attribute(eq("view"))).thenReturn("PLAY");
        when(game.getBoard()).thenReturn(board);
        when(game.gameRunning()).thenReturn(true);
        when(game.playerInGame(eq(whitePlayer))).thenReturn(true);

        CuT.handle(request, response);

        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();

        testHelper.assertViewModelAttribute(GetGameRoute.TITTLE_ATTR, "Game");
        testHelper.assertViewModelAttribute(GetGameRoute.CURR_USER_ATTR, whitePlayer);
        testHelper.assertViewModelAttribute(GetGameRoute.RED_PLAYR_ATTR, redPlayer);
        testHelper.assertViewModelAttribute(GetGameRoute.WHTE_PLAYR_ATTR, whitePlayer);
        testHelper.assertViewModelAttribute(GetGameRoute.VIEW_MDE_ATTR, "PLAY");
        testHelper.assertViewModelAttribute(GetGameRoute.ACTVE_COL_ATTR, "RED");
        testHelper.assertViewModelAttribute(GetGameRoute.BOARD_ATTR, board);

        testHelper.assertViewName(GetGameRoute.VIEW_NAME);
    }

    /**
     * Test game view creation when you're the red player
     */
    @Test
    public void game_view_flipped(){
        when(session.attribute(eq(PostSigninRoute.PLAYER_KEY))).thenReturn(redPlayer);
        when(game.getBoard()).thenReturn(board);
        when(board.flipBoard()).thenReturn(board);
        when(game.gameRunning()).thenReturn(true);
        when(game.playerInGame(eq(redPlayer))).thenReturn(true);

        CuT.handle(request, response);

        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();

        testHelper.assertViewModelAttribute(GetGameRoute.CURR_USER_ATTR, redPlayer);
        testHelper.assertViewModelAttribute(GetGameRoute.BOARD_ATTR, board);

        testHelper.assertViewName(GetGameRoute.VIEW_NAME);
    }

    /**
     * Test when white resigns
     */
    @Test
    public void white_resign(){
        when(session.attribute(eq(PostSigninRoute.PLAYER_KEY))).thenReturn(whitePlayer);
        when(session.attribute(eq("view"))).thenReturn("PLAY");
        when(game.gameRunning()).thenReturn(false);
        when(game.getGameOverReason()).thenReturn(Game.GAME_END_REASONS.WHITE_RESIGNED);
        when(game.getWhitePlayer()).thenReturn(whitePlayer);
        when(game.getRedPlayer()).thenReturn(redPlayer);
        when(game.getBoard()).thenReturn(board);
        when(game.getBoard().flipBoard()).thenReturn(board);
        when(game.playerInGame(eq(whitePlayer))).thenReturn(true);

        when(game.getEloUpdated()).thenReturn(0);
        when(game.getWinner()).thenReturn(redPlayer);

        CuT.handle(request, response);

        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();

        modeOptions.put("isGameOver", true);
        modeOptions.put("gameOverMessage", whitePlayer.toString() + " has resigned");

        testHelper.assertViewModelAttribute(GetGameRoute.MODE_JSON_ATTR, gson.toJson(modeOptions));
        testHelper.assertViewName(GetGameRoute.VIEW_NAME);

        verify(game).increaseEloUpdated(1);
        assertEquals("990", whitePlayer.getRating());

        when(session.attribute(eq(PostSigninRoute.PLAYER_KEY))).thenReturn(redPlayer);
        when(game.playerInGame(eq(redPlayer))).thenReturn(true);
        when(game.getEloUpdated()).thenReturn(1);

        CuT.handle(request,response);

        assertEquals("1010", redPlayer.getRating());
    }

    /**
     * Test when red resigns
     */
    @Test
    public void red_resign(){
        when(session.attribute(eq(PostSigninRoute.PLAYER_KEY))).thenReturn(redPlayer);
        when(session.attribute(eq("view"))).thenReturn("PLAY");
        when(game.gameRunning()).thenReturn(false);
        when(game.getGameOverReason()).thenReturn(Game.GAME_END_REASONS.RED_RESIGNED);
        when(game.getRedPlayer()).thenReturn(redPlayer);
        when(game.getBoard()).thenReturn(board);
        when(board.flipBoard()).thenReturn(board);
        when(game.playerInGame(eq(redPlayer))).thenReturn(true);

        when(game.getEloUpdated()).thenReturn(0);
        when(game.getWinner()).thenReturn(whitePlayer);

        CuT.handle(request, response);

        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();

        modeOptions.put("isGameOver", true);
        modeOptions.put("gameOverMessage", redPlayer.toString() + " has resigned");

        testHelper.assertViewModelAttribute(GetGameRoute.MODE_JSON_ATTR, gson.toJson(modeOptions));
        testHelper.assertViewName(GetGameRoute.VIEW_NAME);

        verify(game).increaseEloUpdated(1);
        assertEquals("990", redPlayer.getRating());
    }

    /**
     * Test when red has no pieces left
     */
    @Test
    public void red_no_piece(){
        when(session.attribute(eq(PostSigninRoute.PLAYER_KEY))).thenReturn(whitePlayer);
        when(session.attribute(eq("view"))).thenReturn("PLAY");
        when(game.gameRunning()).thenReturn(false);
        when(game.getGameOverReason()).thenReturn(Game.GAME_END_REASONS.RED_NO_PIECES);
        when(game.getRedPlayer()).thenReturn(redPlayer);
        when(game.getBoard()).thenReturn(board);
        when(board.flipBoard()).thenReturn(board);
        when(game.playerInGame(eq(whitePlayer))).thenReturn(true);

        when(game.getEloUpdated()).thenReturn(0);
        when(game.getWinner()).thenReturn(whitePlayer);

        CuT.handle(request, response);

        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();

        modeOptions.put("isGameOver", true);
        modeOptions.put("gameOverMessage", redPlayer.toString() + " has no more pieces");

        testHelper.assertViewModelAttribute(GetGameRoute.MODE_JSON_ATTR, gson.toJson(modeOptions));
        testHelper.assertViewName(GetGameRoute.VIEW_NAME);

        verify(game).increaseEloUpdated(1);
        assertEquals("1010", whitePlayer.getRating());
    }

    /**
     * Test when white has no pieces left
     */
    @Test
    public void white_no_piece(){
        when(session.attribute(eq(PostSigninRoute.PLAYER_KEY))).thenReturn(whitePlayer);
        when(session.attribute(eq("view"))).thenReturn("PLAY");
        when(game.gameRunning()).thenReturn(false);
        when(game.getGameOverReason()).thenReturn(Game.GAME_END_REASONS.WHITE_NO_PIECES);
        when(game.getWhitePlayer()).thenReturn(whitePlayer);
        when(game.getBoard()).thenReturn(board);
        when(board.flipBoard()).thenReturn(board);
        when(game.playerInGame(eq(whitePlayer))).thenReturn(true);

        when(game.getEloUpdated()).thenReturn(0);
        when(game.getWinner()).thenReturn(redPlayer);

        CuT.handle(request, response);

        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();

        modeOptions.put("isGameOver", true);
        modeOptions.put("gameOverMessage", whitePlayer.toString() + " has no more pieces");

        testHelper.assertViewModelAttribute(GetGameRoute.MODE_JSON_ATTR, gson.toJson(modeOptions));
        testHelper.assertViewName(GetGameRoute.VIEW_NAME);

        verify(game).increaseEloUpdated(1);
        assertEquals("990", whitePlayer.getRating());
    }

    /**
     * Test if the game ends for unknown reason
     */
    @Test
    public void game_end_unknown_reason(){
        when(session.attribute(eq(PostSigninRoute.PLAYER_KEY))).thenReturn(whitePlayer);
        when(session.attribute(eq("view"))).thenReturn("PLAY");
        when(game.gameRunning()).thenReturn(false);
        when(game.getWhitePlayer()).thenReturn(whitePlayer);
        when(game.getBoard()).thenReturn(board);
        when(board.flipBoard()).thenReturn(board);
        when(game.playerInGame(eq(whitePlayer))).thenReturn(true);

        when(game.getEloUpdated()).thenReturn(0);

        CuT.handle(request, response);

        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();

        modeOptions.put("isGameOver", true);
        modeOptions.put("gameOverMessage", "Game Over for Unknown Reason");

        testHelper.assertViewModelAttribute(GetGameRoute.MODE_JSON_ATTR, gson.toJson(modeOptions));
        testHelper.assertViewName(GetGameRoute.VIEW_NAME);
    }

    /**
     * Test if no game id in url
     */
    @Test
    public void no_game_id(){
        when(request.queryParams(eq(GetGameRoute.GAME_ID_ATTR))).thenReturn(null);

        CuT.handle(request, response);

        verify(session).attribute("error", GetGameRoute.NO_GAME_ID);
        verify(response).redirect("/");
    }

    /**
     * Test if invalid game id in url
     */
    @Test
    public void invalid_game_id(){
        when(gameMaster.getCurrentGame(eq(request))).thenReturn(null);

        CuT.handle(request, response);

        verify(session).attribute("error", GetGameRoute.INVALID_GAME_ID);
        verify(response).redirect("/");
    }

    @Test
    public void white_signout(){
        when(session.attribute(eq(PostSigninRoute.PLAYER_KEY))).thenReturn(redPlayer);
        when(session.attribute(eq("view"))).thenReturn("PLAY");
        when(game.gameRunning()).thenReturn(false);
        when(game.getWhitePlayer()).thenReturn(whitePlayer);
        when(game.getRedPlayer()).thenReturn(redPlayer);
        when(game.getBoard()).thenReturn(board);
        when(board.flipBoard()).thenReturn(board);
        when(game.playerInGame(eq(redPlayer))).thenReturn(true);

        when(game.getEloUpdated()).thenReturn(0);
        when(playerLobby.isPlayerOffline(whitePlayer)).thenReturn(true);

        CuT.handle(request, response);

        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();

        modeOptions.put("isGameOver", true);
        modeOptions.put("gameOverMessage", "Game Over for Unknown Reason");

        testHelper.assertViewModelAttribute(GetGameRoute.MODE_JSON_ATTR, gson.toJson(modeOptions));
        testHelper.assertViewName(GetGameRoute.VIEW_NAME);

        verify(game).setWinner(redPlayer);
        verify(game).increaseEloUpdated(2);

        assertEquals("990", whitePlayer.getRating());
        assertEquals("1010", redPlayer.getRating());
    }

    @Test
    public void red_signout(){
        when(session.attribute(eq(PostSigninRoute.PLAYER_KEY))).thenReturn(whitePlayer);
        when(session.attribute(eq("view"))).thenReturn("PLAY");
        when(game.gameRunning()).thenReturn(false);
        when(game.getWhitePlayer()).thenReturn(whitePlayer);
        when(game.getRedPlayer()).thenReturn(redPlayer);
        when(game.getBoard()).thenReturn(board);
        when(board.flipBoard()).thenReturn(board);
        when(game.playerInGame(eq(whitePlayer))).thenReturn(true);

        when(game.getEloUpdated()).thenReturn(0);
        when(playerLobby.isPlayerOffline(redPlayer)).thenReturn(true);

        CuT.handle(request, response);

        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();

        modeOptions.put("isGameOver", true);
        modeOptions.put("gameOverMessage", "Game Over for Unknown Reason");

        testHelper.assertViewModelAttribute(GetGameRoute.MODE_JSON_ATTR, gson.toJson(modeOptions));
        testHelper.assertViewName(GetGameRoute.VIEW_NAME);

        verify(game).setWinner(whitePlayer);
        verify(game).increaseEloUpdated(2);

        assertEquals("990", redPlayer.getRating());
        assertEquals("1010", whitePlayer.getRating());
    }

    @Test
    public void spectator_view_end_game(){
        when(session.attribute(eq(PostSigninRoute.PLAYER_KEY))).thenReturn(spectator);
        when(session.attribute(eq("view"))).thenReturn("PLAY");
        when(game.gameRunning()).thenReturn(false);
        when(game.getWhitePlayer()).thenReturn(whitePlayer);
        when(game.getRedPlayer()).thenReturn(redPlayer);
        when(game.getBoard()).thenReturn(board);
        when(board.flipBoard()).thenReturn(board);
        when(game.playerInGame(eq(spectator))).thenReturn(false);
        when(game.spectatorInGame(spectator)).thenReturn(true);

        when(game.getEloUpdated()).thenReturn(2);
        when(playerLobby.isPlayerOffline(redPlayer)).thenReturn(true);

        CuT.handle(request, response);

        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();

        modeOptions.put("isGameOver", true);
        modeOptions.put("gameOverMessage", "Game Over for Unknown Reason");

        testHelper.assertViewModelAttribute(GetGameRoute.MODE_JSON_ATTR, gson.toJson(modeOptions));
        testHelper.assertViewName(GetGameRoute.VIEW_NAME);

        assertEquals("1000", redPlayer.getRating());
        assertEquals("1000", whitePlayer.getRating());
    }

    /**
     * This should never happened, required for code coverage
     */
    @Test
    public void spec_view_elo(){
        when(session.attribute(eq(PostSigninRoute.PLAYER_KEY))).thenReturn(spectator);
        when(session.attribute(eq("view"))).thenReturn("PLAY");
        when(game.gameRunning()).thenReturn(false);
        when(game.getWhitePlayer()).thenReturn(whitePlayer);
        when(game.getRedPlayer()).thenReturn(redPlayer);
        when(game.getBoard()).thenReturn(board);
        when(board.flipBoard()).thenReturn(board);
        when(game.playerInGame(eq(spectator))).thenReturn(false);
        when(game.spectatorInGame(spectator)).thenReturn(true);

        when(game.getEloUpdated()).thenReturn(0);
        when(playerLobby.isPlayerOffline(redPlayer)).thenReturn(true);

        CuT.handle(request, response);

        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();

        modeOptions.put("isGameOver", true);
        modeOptions.put("gameOverMessage", "Game Over for Unknown Reason");

        testHelper.assertViewModelAttribute(GetGameRoute.MODE_JSON_ATTR, gson.toJson(modeOptions));
        testHelper.assertViewName(GetGameRoute.VIEW_NAME);

        assertEquals("1000", redPlayer.getRating());
        assertEquals("1000", whitePlayer.getRating());
    }

    @Test
    public void player_not_in_game_and_spec(){
        when(session.attribute(eq(PostSigninRoute.PLAYER_KEY))).thenReturn(whitePlayer);

        when(game.playerInGame(eq(whitePlayer))).thenReturn(false);
        when(game.spectatorInGame(eq(whitePlayer))).thenReturn(false);


        CuT.handle(request, response);

        verify(response).redirect("/");
    }

    @Test
    public void player_in_game_not_a_player(){
        when(session.attribute(eq(PostSigninRoute.PLAYER_KEY))).thenReturn(spectator);
        when(session.attribute(eq("view"))).thenReturn("PLAY");
        when(game.getGameOverReason()).thenReturn(Game.GAME_END_REASONS.RED_NO_PIECES);
        when(game.gameRunning()).thenReturn(false);
        when(game.getWhitePlayer()).thenReturn(whitePlayer);
        when(game.getRedPlayer()).thenReturn(redPlayer);
        when(game.getBoard()).thenReturn(board);
        when(board.flipBoard()).thenReturn(board);
        when(game.playerInGame(eq(spectator))).thenReturn(true);
        when(game.spectatorInGame(spectator)).thenReturn(false);

        when(game.getEloUpdated()).thenReturn(0);

        CuT.handle(request, response);

        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();

        modeOptions.put("isGameOver", true);
        modeOptions.put("gameOverMessage", redPlayer + " has no more pieces");

        testHelper.assertViewModelAttribute(GetGameRoute.MODE_JSON_ATTR, gson.toJson(modeOptions));
        testHelper.assertViewName(GetGameRoute.VIEW_NAME);

        assertEquals("1000", redPlayer.getRating());
        assertEquals("1000", whitePlayer.getRating());
    }
}
