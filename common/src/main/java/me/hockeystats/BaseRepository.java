package me.hockeystats;

import com.jmethods.catatumbo.EntityManager;
import com.jmethods.catatumbo.EntityQueryRequest;
import com.jmethods.catatumbo.QueryResponse;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class BaseRepository<Entity extends BaseEntity, ID> {
  protected final EntityManager entityManager;

  protected BaseRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public final Mono<Entity> findById(ID id) {
    return Mono.fromCallable(
        () -> {
          EntityQueryRequest request = findByIdQuery(id);
          QueryResponse<Entity> response =
              entityManager.executeEntityQueryRequest(getEntityClass(), request);
          List<Entity> entities = response.getResults();
          if (entities.size() > 1) {
            throw new IllegalStateException("Multiple entries found when only one expected");
          } else if (entities.size() == 0) {
            return null;
          }
          return entities.get(0);
        });
  }

  public final Flux<Entity> saveAll(Flux<Entity> entities) {
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

  protected abstract Class<Entity> getEntityClass();

  protected abstract EntityQueryRequest findByIdQuery(ID id);
}
