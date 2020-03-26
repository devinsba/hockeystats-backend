package me.hockeystats.nhl.game;

import com.jmethods.catatumbo.EntityManager;
import com.jmethods.catatumbo.EntityQueryRequest;
import me.hockeystats.BaseRepository;

public class Games extends BaseRepository<Game, Long> {
  public Games(EntityManager entityManager) {
    super(entityManager);
  }

  @Override
  protected Class<Game> getEntityClass() {
    return Game.class;
  }

  @Override
  protected EntityQueryRequest findByIdQuery(Long id) {
    EntityQueryRequest request =
        entityManager.createEntityQueryRequest("SELECT * FROM Game WHERE gameId = @gameId");
    request.setNamedBinding("gameId", id);
    return request;
  }
}
