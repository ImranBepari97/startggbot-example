package com.imranbepari.startggbot.app;


import com.imranbepari.startggbot.app.BotEventCache;
import com.imranbepari.startggbot.app.EventInfo;
import com.imranbepari.startggbot.startgg.model.Entrant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.imranbepari.startggbot.TestUtils.createEntrant;


@ActiveProfiles("test")
@SpringBootTest
public class BotEventCacheUnitTest {
    @Test
    public void addCachedStanding() {
        BotEventCache cache = new BotEventCache();

        EventInfo info = new EventInfo("t1", "e1");

        List<Entrant> entrantList = new ArrayList<>();
        entrantList.add(createEntrant(1, "p1"));
        entrantList.add(createEntrant(2, "p2"));
        entrantList.add(createEntrant(3, "p3"));

        cache.putCachedStanding(info, entrantList);

        Assertions.assertEquals(cache.getCachedStanding(info).get(), entrantList);
    }

    @Test
    public void getEmptyList() {
        BotEventCache cache = new BotEventCache();

        EventInfo info = new EventInfo("t1", "e1");

        Assertions.assertEquals(cache.getCachedStanding(info), Optional.empty());
    }

}
