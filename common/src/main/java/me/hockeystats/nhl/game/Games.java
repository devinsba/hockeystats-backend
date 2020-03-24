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
    return games
        .groupBy(
            g -> {
              if (g.getCreatedAt() == null) {
                return "insert";
              } else {
                return "update";
              }
            })
        .flatMap(
            g -> {
              if (g.key().equals("insert")) {
                return Flux.from(g.collectList().map(entityManager::insert))
                    .flatMapIterable(i -> i);
              } else {
                return Flux.from(g.collectList().map(entityManager::update))
                    .flatMapIterable(i -> i);
              }
            });
  }
}
