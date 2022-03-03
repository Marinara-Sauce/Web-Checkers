package com.webcheckers.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.TemplateEngine;
import static org.mockito.Mockito.*;

/**
 * Unit test for {@link WebServer} component
 *
 * @author <a href="mailto:cfr8511@rit.edu">Charlie Reilly</a>
 */
@Tag("UI-tier")
public class WebServerTest {

    // Component under Test
    WebServer CuT;

    // Friendly
    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();

    // Mock Objects
    TemplateEngine templateEngine;


    @BeforeEach
    public void setup() {
        templateEngine = mock(TemplateEngine.class);

        CuT = new WebServer(templateEngine, gson);
    }

    /**
     * Test if CuT can initialize
     */
    @Test
    public void init(){
        CuT.initialize();
    }
}
