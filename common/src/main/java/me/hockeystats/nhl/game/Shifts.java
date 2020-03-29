package me.hockeystats.nhl.game;

import com.jmethods.catatumbo.EntityManager;
import com.jmethods.catatumbo.EntityQueryRequest;
import me.hockeystats.nhl.BaseRepository;

public class Shifts extends BaseRepository<Shift, Long> {
  public Shifts(EntityManager entityManager) {
    super(entityManager);
  }

  @Override
  protected Class<Shift> getEntityClass() {
    return Shift.class;
  }

  @Override
  protected EntityQueryRequest findByIdQuery(Long id) {
    EntityQueryRequest request =
        entityManager.createEntityQueryRequest("SELECT * FROM Shift WHERE nhlId = @nhlId");
    request.setNamedBinding("nhlId", id);
    return request;
  }
}
