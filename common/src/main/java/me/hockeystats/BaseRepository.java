package me.hockeystats;

import com.jmethods.catatumbo.DatastoreKey;
import com.jmethods.catatumbo.EntityManager;
import com.jmethods.catatumbo.EntityQueryRequest;
import com.jmethods.catatumbo.QueryResponse;
import java.util.List;
import java.util.WeakHashMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class BaseRepository<Entity extends BaseEntity, ID> {
  protected final EntityManager entityManager;
  private final WeakHashMap<DatastoreKey, Integer> retrievedEntityHashcodeMap = new WeakHashMap<>();

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
          Entity e = entities.get(0);
          retrievedEntityHashcodeMap.put(e.getKey(), e.hashCode());
          return e;
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
                return g.groupBy(
                        e -> retrievedEntityHashcodeMap.getOrDefault(e.getKey(), 0) == e.hashCode())
                    .flatMap(
                        gg -> {
                          if (gg.key()) {
                            return Flux.from(gg);
                          } else {
                            return Flux.from(gg.collectList().map(entityManager::update))
                                .flatMapIterable(i -> i);
                          }
                        });
              }
            });
  }

  protected abstract Class<Entity> getEntityClass();

  protected abstract EntityQueryRequest findByIdQuery(ID id);
}
