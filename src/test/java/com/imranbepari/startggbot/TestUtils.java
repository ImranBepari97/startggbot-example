package com.imranbepari.startggbot;

import com.imranbepari.startggbot.startgg.model.Entrant;

/**
 * Test utility class.
 */
public class TestUtils {
    public static Entrant createEntrant(Integer id, String name) {
        return new Entrant(1, null,null,null,null,name,null,null,null,null,false,false,false);
    }

}
