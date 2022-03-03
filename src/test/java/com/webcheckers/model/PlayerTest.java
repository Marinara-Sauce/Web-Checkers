package com.webcheckers.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Model-Tier")
public class PlayerTest {
    private static final String NAME = "Bob";
    private static final String DIFF_NAME = "Jim";

    @Test
    public void ctor_withArg(){
        Player CuT = new Player(NAME);
        assertEquals(CuT.getName(), NAME);
        assertTrue(CuT.isPlayable());
    }

    @Test
    public void set_playability(){
        Player player = new Player(NAME);
        assertTrue(player.isPlayable());

        player.setPlayable(false);
        assertFalse(player.isPlayable());
    }

    @Test
    public void get_name(){
        Player player = new Player(NAME);
        assertEquals(player.getName(), NAME);
    }

    @Test
    public void equals_null(){
        Player player1 = null;
        Player player2 = new Player(NAME);

        boolean CuT = player2.equals(player1);
        assertFalse(CuT);
    }

    @Test
    public void equals_diff_object(){
        Player player1 = new Player(NAME);

        boolean CuT = player1.equals(NAME);
        assertFalse(CuT);
    }

    @Test
    public void equals_same_name(){
        Player player1 = new Player(NAME);
        Player player2 = new Player(NAME);

        boolean CuT = player1.equals(player2);
        assertTrue(CuT);
    }

    @Test
    public void equals_diff_name(){
        Player player1 = new Player(NAME);
        Player player2 = new Player(DIFF_NAME);

        boolean CuT = player1.equals(player2);
        assertFalse(CuT);
    }

    @Test
    public void hash_code_test(){
        Player player = new Player(NAME);
        int CuT = player.hashCode();
        int actual = player.getName().hashCode();

        assertEquals(CuT, actual);

    }
}
