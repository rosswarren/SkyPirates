package com.fullwall.SkyPirates;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class RangeHandlerTest {
    @Test
    public void testRangeHandler() {
        assertEquals(4D, RangeHandler.range(6D, 4D, -2D));
        assertEquals(-2D, RangeHandler.range(-6D, 4D, -2D));
        assertEquals(2D, RangeHandler.range(2D, 4D, -2D));
    }
}
