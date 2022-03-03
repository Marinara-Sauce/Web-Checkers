package com.webcheckers.ui;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateEngine;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link GetSigninRoute} component
 *
 * @author <a href="mailto:cfr8511@rit.edu">Charlie Reilly</a>
 */

@Tag("UI-tier")
public class GetSigninRouteTest {

    // Component under Test
    private GetSigninRoute CuT;

    // friendly
    TemplateEngineTester testHelper = new TemplateEngineTester();

    // attributes holding mock objects
    private Request request;
    private Response response;
    private TemplateEngine templateEngine;

    /**
     * Set up mock and friendly objects before each test
     */
    @BeforeEach
    public void setup(){
        request = mock(Request.class);
        response = mock(Response.class);

        templateEngine = mock(TemplateEngine.class);
        when(templateEngine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

        CuT = new GetSigninRoute(templateEngine);
    }

    /**
     * Test constructor
     */
    @Test
    public void ctor_withArg(){ assertNotNull(CuT); }

    /**
     * Test the creation of the sign-in view
     */
    @Test
    public void get_view(){
        CuT.handle(request, response);

        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();

        testHelper.assertViewModelAttribute(GetSigninRoute.TITLE_ATTR, GetSigninRoute.TITLE);
        testHelper.assertViewName(GetSigninRoute.SIGNIN_VIEW);
    }
}
