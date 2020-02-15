package me.hockeystats.nhl.season;

import com.jmethods.catatumbo.EntityManager;
import reactor.core.publisher.Mono;

public class Seasons {
    private final EntityManager entityManager;

    public Seasons(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Mono<Season> findById(long id) {
        return Mono.fromCallable(() -> entityManager.load(Season.class, id));
    }
}
