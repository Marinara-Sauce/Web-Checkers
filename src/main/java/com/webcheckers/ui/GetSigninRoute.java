package com.webcheckers.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.TemplateEngine;

/**
 * The UI Controller to GET the Sign In page.
 *
 * @author <a href="mailto:djb1808@rit.edu">Dan Bliss (djb1808)</a>
 */
public class GetSigninRoute implements Route {
  private static final Logger LOG = Logger.getLogger(GetHomeRoute.class.getName());

  static final String TITLE_ATTR = "title";
  static final String TITLE = "Sign In";
  static final String SIGNIN_VIEW = "signin.ftl";

  private final TemplateEngine templateEngine;

  /**
   * Create the Spark Route (UI controller) to handle all {@code GET /} HTTP requests.
   *
   * @param templateEngine
   *   the HTML template rendering engine
   */
  public GetSigninRoute(final TemplateEngine templateEngine) {
    this.templateEngine = Objects.requireNonNull(templateEngine, "templateEngine is required");
    //
    LOG.config("GetHomeRoute is initialized.");
  }

  /**
   * Render the WebCheckers Sign-In page.
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
  public Object handle(Request request, Response response) {
    LOG.finer("GetSigninRoute is invoked.");

    Map<String, Object> vm = new HashMap<>();

    vm.put(TITLE_ATTR, TITLE);
    
    //Leaving this here for future reference, this is how you make a message
    //vm.put("message", new Message("DUMMY_MESSAGE", Message.Type.ERROR));

    // render the View
    return templateEngine.render(new ModelAndView(vm , SIGNIN_VIEW));
  }
}
