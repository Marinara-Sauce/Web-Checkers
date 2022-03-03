package com.webcheckers.model;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit test for {@link Piece} component
 *
 * @author <a href="mailto:cfr8511@rit.edu">Charlie Reilly</a>
 */

@Tag("Model-tier")
public class PieceTest {

    // component under test
    private Piece CuT;

    @BeforeEach
    public void setup(){
        CuT = new Piece(0, 0, "test", "red");
    }

    @Test
    public void ctor(){
        assertEquals(0, CuT.getRowID());
        assertEquals(0, CuT.getColID());
        assertEquals("test", CuT.getType());
        assertEquals("red", CuT.getColor());
    }

    @Test
    public void location(){
        CuT.setLocation("0,0");
        String loc = CuT.getLocation();

        assertEquals("0,0", loc);
    }

    @Test
    public void set_ctor(){
        CuT.setRowID(1);
        CuT.setColID(1);
        CuT.setType("test2");
        assertEquals(1, CuT.getRowID());
        assertEquals(1, CuT.getColID());
        assertEquals("test2", CuT.getType());
    }

    @Test
    public void get_pos(){
        Position pos = CuT.getPosition();
        Gson gson = new Gson();

        assertEquals(gson.toJson(pos), gson.toJson(CuT.getPosition()));
    }

    @Test
    public void to_string(){
        assertEquals("red test", CuT.toString());
    }



}
