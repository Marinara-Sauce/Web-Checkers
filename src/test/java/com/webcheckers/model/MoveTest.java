package com.webcheckers.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Model-tier")
public class MoveTest 
{
    private Move moveTest;

    //--Friendly--//
    private Position start;
    private Position end;

    private Game game;

    @BeforeEach
    public void beforeTest()
    {
        start = new Position(0, 0);
        end = new Position(1, 1);

        game = mock(Game.class);
        when(game.getBoard()).thenReturn(new Board());

        moveTest = new Move(start, end);
    }

    @Test
    public void testGetStartPos()
    {
        assertEquals(start, moveTest.getStart());
    }

    @Test
    public void testGetEndPos()
    {
        assertEquals(end, moveTest.getEnd());
    }

    @Test
    public void testToString()
    {
        assertEquals("Start: {" + start.toString() + "} - End: {" + end.toString() + "}", moveTest.toString());
    }
}
