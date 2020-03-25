package me.hockeystats.nhl.game;

import com.jmethods.catatumbo.EntityManager;
import com.jmethods.catatumbo.EntityQueryRequest;
import com.jmethods.catatumbo.QueryResponse;
import java.util.List;
import me.hockeystats.BaseRepository;
import reactor.core.publisher.Mono;

public class Games extends BaseRepository<Game> {
  public Games(EntityManager entityManager) {
    super(entityManager);
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
}
