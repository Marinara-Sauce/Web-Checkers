package com.webcheckers.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.webcheckers.application.PlayerLobby;
import com.webcheckers.application.PlayerLobby.STATUS_CODE;
import com.webcheckers.model.Player;
import com.webcheckers.util.Message;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;
import spark.TemplateEngine;

/**
 * The UI Controller for POST after the sign in page (makeaccount page)
 * 
 * @author <a href="mailto:djb1808@rit.edu">Dan Bliss (djb1808)</a>
 */
public class PostSigninRoute implements Route
{
    private static final Logger LOG = Logger.getLogger(GetHomeRoute.class.getName());
    
    //Name of the parameter the name goes in
    public static final String NAME_PARAM = "userName";

    public static String MESSAGE_ATTR = "message";

    public static String SIGN_IN_VIEW = "signin.ftl";
    public static String HOME_URL = "/";

    //Message to display if the name is invalid
    public static String INVALID_NAME_MSG = "Invalid Username! Make sure your "
        + "name isn't empty or contain any special characters!";

    //Message to display if the name is already taken
    public static final String NAME_TAKEN_MSG = "This name is already in use!";

    //Key for the player HTTP attribute
    public static final String PLAYER_KEY = "player";

    // --- Entities --- //
    private final TemplateEngine templateEngine;
    private final PlayerLobby playerLobby;

    /**
     * Create the Spark Route (UI controller) to handle all {@code GET /} HTTP requests.
     *
     * @param templateEngine
     *   the HTML template rendering engine
     */
    public PostSigninRoute(PlayerLobby playerLobby, TemplateEngine templateEngine)
    {
        this.playerLobby = playerLobby;
        this.templateEngine = templateEngine;
    }

    /**
     * Handles POST requests from the sign in page.
     * Sets the HTTP attribute and redirects to home if success
     * Sends back to the sign in page on an error
     *
     * @param request
     *   the HTTP request
     * @param response
     *   the HTTP response
     *
     * @return
     *   the rendered HTML for the Home page
     */
    @Override
    public Object handle(Request request, Response response){
        
        LOG.config("PostSigninRoute is invoked");
        
        final Map<String, Object> vm = new HashMap<>();

        vm.put(GetSigninRoute.TITLE_ATTR, GetSigninRoute.TITLE);

        final String name = request.queryParams(NAME_PARAM); //Attempted username

        ModelAndView mv = null; //Set and returned depending on circumstances

        //Communicate with PlayerLobby to attempt to create that name
        STATUS_CODE status = playerLobby.addPlayer(name);

        if (status == STATUS_CODE.SUCCESS)
        {
            //Setup the new player as an HTTP attribute
            final Player newPlayer = playerLobby.getMostRecentPlayer();
            final Session httpSession = request.session();

            httpSession.attribute(PLAYER_KEY, newPlayer);

            //Once the player is registered, get us back home
            response.redirect(HOME_URL);
            return null;
        }

        if (status == STATUS_CODE.INVALID_NAME)
            mv = error(vm, INVALID_NAME_MSG);
        
        else if (status == STATUS_CODE.PLAYER_ALREADY_EXISTS)
            mv = error(vm, NAME_TAKEN_MSG);

        return templateEngine.render(mv);
    }

    //Functions for the different reactions towards signing in

    private ModelAndView error(final Map<String, Object> vm, final String message)
    {
        vm.put(MESSAGE_ATTR, new Message(message, Message.Type.ERROR));
        return new ModelAndView(vm, SIGN_IN_VIEW);
    }
}
