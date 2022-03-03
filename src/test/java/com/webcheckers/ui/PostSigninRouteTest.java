package com.webcheckers.ui;

import static org.junit.jupiter.api.Assertions.*;

import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit test for {@link PostSigninRoute} component
 *
 * @author <a href="mailto:cfr8511@rit.edu">Charlie Reilly</a>
 */

@Tag("UI-tier")
public class PostSigninRouteTest {

    private final String VALID_NAME = "Bob";
    private final String INVALID_NAME = "";

    // Component under Test
    private PostSigninRoute CuT;

    // friendly
    TemplateEngineTester testHelper = new TemplateEngineTester();
    Player valid = new Player(VALID_NAME);

    // attributes holding mock objects
    private Request request;
    private Response response;
    private Session session;
    private TemplateEngine templateEngine;
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
        playerLobby = mock(PlayerLobby.class);

        when(templateEngine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());
        CuT = new PostSigninRoute(playerLobby, templateEngine);
    }

    /**
     * Test if redirect occurs if valid name input
     */
    @Test
    public void valid_name(){
        when(request.queryParams(PostSigninRoute.NAME_PARAM)).thenReturn(VALID_NAME);

        when(playerLobby.addPlayer(eq(VALID_NAME))).thenReturn(PlayerLobby.STATUS_CODE.SUCCESS);
        when(playerLobby.getMostRecentPlayer()).thenReturn(valid);

        CuT.handle(request, response);

        verify(response).redirect(PostSigninRoute.HOME_URL);
    }

    /**
     * Test if error message created if invalid name input
     */
    @Test
    public void invalid_name(){
        when(request.queryParams(PostSigninRoute.NAME_PARAM)).thenReturn(INVALID_NAME);

        when(playerLobby.addPlayer(eq(INVALID_NAME))).thenReturn(PlayerLobby.STATUS_CODE.INVALID_NAME);

        CuT.handle(request, response);

        testHelper.assertViewName(PostSigninRoute.SIGN_IN_VIEW);
    }

    /**
     * Test if error message created if same name input
     */
    @Test
    public void name_already_exists(){
        playerLobby.addPlayer(VALID_NAME);
        when(request.queryParams(PostSigninRoute.NAME_PARAM)).thenReturn(VALID_NAME);

        when(playerLobby.addPlayer(eq(VALID_NAME))).thenReturn(PlayerLobby.STATUS_CODE.PLAYER_ALREADY_EXISTS);

        CuT.handle(request, response);

        testHelper.assertViewName(PostSigninRoute.SIGN_IN_VIEW);
    }

    /**
     * Test if status code doesn't generate then nothing happens
     */
    @Test
    public void no_status_code(){
        when(request.queryParams(PostSigninRoute.NAME_PARAM)).thenReturn(VALID_NAME);
        when(playerLobby.addPlayer(eq(VALID_NAME))).thenReturn(null);

        assertNull(CuT.handle(request, response));
    }



}
