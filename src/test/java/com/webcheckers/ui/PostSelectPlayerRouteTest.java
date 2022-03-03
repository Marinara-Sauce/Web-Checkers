package com.webcheckers.ui;

import static org.junit.jupiter.api.Assertions.*;

import com.webcheckers.application.GameMaster;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Game;
import com.webcheckers.model.Player;

import org.junit.jupiter.api.BeforeEach;
import spark.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("UI-tier")
public class PostSelectPlayerRouteTest {

    private static final String LOCAL_NAME = "Bob";
    private static final String OPPONENT_NAME = "Jim";

    // Component under test
    private PostSelectPlayerRoute CuT;

    // attributes holding mock objects
    private Request request;
    private Response response;
    private Session session;
    private PlayerLobby playerLobby;
    private GameMaster gameMaster;
    private Game game;

    @BeforeEach
    public void setup(){
        request = mock(Request.class);
        response = mock(Response.class);
        session = mock(Session.class);
        when(request.session()).thenReturn(session);

        playerLobby = mock(PlayerLobby.class);
        when(playerLobby.getPlayerFromName(eq(null))).thenReturn(null);

        gameMaster = mock(GameMaster.class);
        game = mock(Game.class);

        CuT = new PostSelectPlayerRoute(playerLobby, gameMaster);
    }

    @Test
    public void null_opponent() {
        //Initial set up for a null opponent & valid local player
        when(session.attribute(eq(PostSigninRoute.PLAYER_KEY))).thenReturn(new Player(LOCAL_NAME));
        when(request.queryParams(eq(PostSelectPlayerRoute.OPPONENT_NAME_ATTR))).thenReturn(null);

        //Session holds attribute "error" and value OPP_DOES_NOT_EXIST when called
        when(session.attribute(eq("error"))).thenReturn(PostSelectPlayerRoute.OPP_DOES_NOT_EXIST);

        CuT.handle(request, response);

        verify(response).redirect(PostSelectPlayerRoute.HOME_URL);
        assertEquals(session.attribute("error"), PostSelectPlayerRoute.OPP_DOES_NOT_EXIST);
    }

    @Test
    public void opponent_not_playable() {
        //Initial set up for a not playable opponent and valid local player
        Player opponent = new Player(OPPONENT_NAME);
        Player local = new Player(LOCAL_NAME);
        opponent.setPlayable(false);

        when(session.attribute(eq(PostSigninRoute.PLAYER_KEY))).thenReturn(local);
        when(request.queryParams(eq(PostSelectPlayerRoute.OPPONENT_NAME_ATTR))).thenReturn(OPPONENT_NAME);
        when(playerLobby.getPlayerFromName(eq(OPPONENT_NAME))).thenReturn(opponent);

        when(gameMaster.gamePlayersIn(eq(opponent))).thenReturn(game);
        when(game.getId()).thenReturn(1);

        CuT.handle(request, response);

        verify(response).redirect(PostSelectPlayerRoute.GAME_URL + "?gameID=" + 1);
        verify(session).attribute("view", "SPECTATOR");
    }

    @Test
    public void null_local() {
        //Initial set up for a null local player & valid opponent
        when(session.attribute(eq(PostSigninRoute.PLAYER_KEY))).thenReturn(null);
        when(request.queryParams(eq(PostSelectPlayerRoute.OPPONENT_NAME_ATTR))).thenReturn(OPPONENT_NAME);
        when(playerLobby.getPlayerFromName(eq(OPPONENT_NAME))).thenReturn(new Player(OPPONENT_NAME));

        //Session holds attribute "error" and value LOC_NOT_LOGGED when called
        when(session.attribute(eq("error"))).thenReturn(PostSelectPlayerRoute.LOC_NOT_LOGGED);

        //Handles given set up
        CuT.handle(request, response);

        verify(response).redirect(PostSelectPlayerRoute.HOME_URL);
        assertEquals(session.attribute("error"), PostSelectPlayerRoute.LOC_NOT_LOGGED);
    }

    @Test
    public void local_not_playable() {
        //Initial set up for a valid opponent and not playable local player
        Player local = new Player(LOCAL_NAME);
        local.setPlayable(false);

        when(session.attribute(eq(PostSigninRoute.PLAYER_KEY))).thenReturn(local);
        when(request.queryParams(eq(PostSelectPlayerRoute.OPPONENT_NAME_ATTR))).thenReturn(OPPONENT_NAME);
        when(playerLobby.getPlayerFromName(eq(OPPONENT_NAME))).thenReturn(new Player(OPPONENT_NAME));

        //Session holds attribute "error" and value LOC_NOT_PLAYABLE when called
        when(session.attribute(eq("error"))).thenReturn(PostSelectPlayerRoute.LOC_NOT_PLAYABLE);

        //Handles given set up
        CuT.handle(request, response);

        verify(response).redirect(PostSelectPlayerRoute.HOME_URL);
        assertEquals(session.attribute("error"),PostSelectPlayerRoute.LOC_NOT_PLAYABLE);
    }

    @Test
    public void opponent_playable() {
        Player localPlayer = new Player(LOCAL_NAME);
        Player opponentPlayer = new Player(OPPONENT_NAME);

        //Initial set up for a valid local and opponent player
        when(session.attribute(eq(PostSigninRoute.PLAYER_KEY))).thenReturn(localPlayer);
        when(request.queryParams(eq(PostSelectPlayerRoute.OPPONENT_NAME_ATTR))).thenReturn(OPPONENT_NAME);
        when(playerLobby.getPlayerFromName(eq(OPPONENT_NAME))).thenReturn(opponentPlayer);

        //Setup a new game
        when(gameMaster.startGame(eq(localPlayer), eq(opponentPlayer))).thenReturn(1);

        //Handles given set up
        CuT.handle(request, response);

        verify(response).redirect(PostSelectPlayerRoute.GAME_URL + "?gameID=1");
        assertNull(session.attribute("error"));
    }

    @Test
    public void opp_spec(){
        Player opponent = new Player(OPPONENT_NAME);
        opponent.setSpectating(true);

        //Initial set up for a spectating opponent & valid local player
        when(session.attribute(eq(PostSigninRoute.PLAYER_KEY))).thenReturn(new Player(LOCAL_NAME));
        when(request.queryParams(eq(PostSelectPlayerRoute.OPPONENT_NAME_ATTR))).thenReturn(OPPONENT_NAME);
        when(playerLobby.getPlayerFromName(eq(OPPONENT_NAME))).thenReturn(opponent);

        //Session holds attribute "error" and value OPP_SPECTATING when called
        when(session.attribute(eq("error"))).thenReturn(PostSelectPlayerRoute.OPP_SPECTATING);

        CuT.handle(request, response);

        verify(response).redirect(PostSelectPlayerRoute.HOME_URL);
        assertEquals(session.attribute("error"), PostSelectPlayerRoute.OPP_SPECTATING);
    }
}
