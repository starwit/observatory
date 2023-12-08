package de.starwit.persistence.sae.repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SaeTester {

    @Autowired
    private SaeRepository saeRepository;

    Logger log = LoggerFactory.getLogger(SaeTester.class);

    List<UUID> uuids = getRandomUUIDs();

    static Random random = new Random();

    static int uuidListSize = 200;

    static int currentuuidListSize = 20;

    @Scheduled(initialDelay = 10, fixedRate = 10)
    public void test() {
        Timestamp captureTimestamp = new Timestamp(System.currentTimeMillis());

        // uses only some uuids for current timeslot
        Set<Integer> uuidIndexSet = new HashSet<>();
        for (int i = 0; i < currentuuidListSize; i++) {
            uuidIndexSet.add(getRandom(0, uuidListSize));
        }
        int randomCount = getRandom(0, currentuuidListSize);

        for (Integer uuidIndex : uuidIndexSet) {
            if (randomCount > 0) {
                saeRepository.insertDetectionData(uuids.get(uuidIndex), captureTimestamp);
                log.info("Added Entry to database with uuid-Index: " + uuids.get(uuidIndex));
                randomCount--;
            }
        }
    }

    private int getRandom(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    private List<UUID> getRandomUUIDs() {
        List<UUID> uuids = new ArrayList<>();
        for (int i = 0; i < uuidListSize; i++) {
            uuids.add(UUID.randomUUID());
        }
        return uuids;
    }
}
