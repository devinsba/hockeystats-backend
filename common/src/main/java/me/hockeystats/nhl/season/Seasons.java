package me.hockeystats.nhl.season;

import com.jmethods.catatumbo.EntityManager;
import com.jmethods.catatumbo.EntityQueryRequest;
import com.jmethods.catatumbo.QueryResponse;
import java.util.List;
import java.util.stream.Collectors;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Seasons {
    private final EntityManager entityManager;

    public Seasons(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Mono<Season> findById(long id) {
        return Mono.fromCallable(() -> {
            EntityQueryRequest request = entityManager.createEntityQueryRequest("SELECT * FROM Season WHERE seasonId = @seasonId");
            request.setNamedBinding("seasonId", id);
            QueryResponse<Season> response = entityManager.executeEntityQueryRequest(Season.class, request);
            List<Season> seasons = response.getResults();
            if (seasons.size() > 1) {
                throw new IllegalStateException("Multiple entries for the same season");
            } else if (seasons.size() == 0) {
                return null;
            }
            return seasons.get(0);
        });
    }

    public Mono<Season> save(Season season) {
        return Mono.fromCallable(() -> entityManager.upsert(season));
    }

    public Flux<Season> saveAll(Flux<Season> seasons) {
        return Flux.fromIterable(entityManager.upsert(seasons.toStream().collect(Collectors.toList())));
    }
}
