package me.hockeystats.nhl.game;

import com.jmethods.catatumbo.EntityManager;
import com.jmethods.catatumbo.EntityQueryRequest;
import com.jmethods.catatumbo.QueryResponse;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class Games {
  private final EntityManager entityManager;

  public Games(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public Mono<Game> findById(long id) {
    return Mono.fromCallable(
        () -> {
          EntityQueryRequest request =
              entityManager.createEntityQueryRequest("SELECT * FROM Game WHERE gameId = @gameId");
          request.setNamedBinding("gameId", id);
          QueryResponse<Game> response =
              entityManager.executeEntityQueryRequest(Game.class, request);
          List<Game> games = response.getResults();
          if (games.size() > 1) {
            throw new IllegalStateException("Multiple entries for the same season");
          } else if (games.size() == 0) {
            return null;
          }
          return games.get(0);
        });
  }

  public Flux<Game> saveAll(Flux<Game> games) {
    return Mono.fromCallable(
            () -> {
              List<Game> list =
                  games
                      .toStream()
                      .peek(
                          g -> {
                            if (g.getCreatedAt() == null) {
                              g.setCreatedAt(ZonedDateTime.now());
                            }
                          })
                      .collect(Collectors.toList());
              return entityManager.upsert(list);
            })
        .subscribeOn(Schedulers.elastic())
        .flatMapIterable(l -> l);
  }
}
