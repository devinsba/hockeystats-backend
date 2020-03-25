package me.hockeystats;

import com.jmethods.catatumbo.EntityManager;
import reactor.core.publisher.Flux;

public abstract class BaseRepository<E extends BaseEntity> {
  protected final EntityManager entityManager;

  protected BaseRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public final Flux<E> saveAll(Flux<E> entities) {
    return entities
        .groupBy(
            s -> {
              if (s.getCreatedAt() == null) {
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
