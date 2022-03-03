package com.webcheckers.util;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Util-Tier")
public class MessageTest {

    // Component under Test
    Message CuT = new Message("Test", Message.Type.INFO);
    Message CuTERR = new Message("Test", Message.Type.ERROR);

    @Test
    public void test_message() {
        String str = CuT.getText();
        Message.Type type = CuT.getType();
        boolean successful = CuT.isSuccessful();
        boolean notsuccessful = CuTERR.isSuccessful();

        assertEquals("Test", str);
        assertEquals(Message.Type.INFO, type);
        assertTrue(successful);
        assertFalse(notsuccessful);
    }
}
