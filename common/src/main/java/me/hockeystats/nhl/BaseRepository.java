package me.hockeystats.nhl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jmethods.catatumbo.DatastoreKey;
import com.jmethods.catatumbo.EntityManager;
import com.jmethods.catatumbo.EntityQueryRequest;
import com.jmethods.catatumbo.QueryResponse;
import java.util.List;
import java.util.WeakHashMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class BaseRepository<Entity extends BaseEntity<ID>, ID> {
  protected final EntityManager entityManager;

  private final WeakHashMap<DatastoreKey, Integer> retrievedEntityHashcodeMap = new WeakHashMap<>();
  private final Cache<ID, Entity> byNhlIdCache;

  protected BaseRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
    byNhlIdCache = Caffeine.newBuilder().maximumSize(100).build();
  }

  public final Flux<Entity> findAll() {
    return Flux.push(
        sink -> {
          String query =
              "SELECT * FROM "
                  + getEntityClass().getSimpleName()
                  + " ORDER BY __key__ LIMIT @limit";
          EntityQueryRequest request = entityManager.createEntityQueryRequest(query);
          request.setNamedBinding("limit", 25);

          QueryResponse<Entity> response =
              entityManager.executeEntityQueryRequest(getEntityClass(), request);
          for (Entity e : response.getResults()) {
            retrievedEntityHashcodeMap.put(e.getKey(), e.hashCode());
            sink.next(e);
          }

          query = query + " OFFSET @offset";
          request.setQuery(query);

          while (response.getResults().size() > 0) {
            request.setNamedBinding("offset", response.getEndCursor());

            response = entityManager.executeEntityQueryRequest(getEntityClass(), request);
            for (Entity e : response.getResults()) {
              retrievedEntityHashcodeMap.put(e.getKey(), e.hashCode());
              sink.next(e);
            }
          }
          sink.complete();
        });
  }

  public final Mono<Entity> findByNhlId(ID id) {
    return Mono.fromCallable(
        () -> {
          Entity e = byNhlIdCache.getIfPresent(id);
          if (e == null) {
            e = getEntityByNhlId(id);
            if (e != null) {
              byNhlIdCache.put(id, e);
            }
          }
          return e;
        });
  }

  private Entity getEntityByNhlId(ID id) {
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
  }

  public final Flux<Entity> saveAll(Flux<Entity> entities) {
    return entities
        .groupBy(
            s -> {
              if (s.getKey() == null) {
                return "insert";
              } else {
                return "update";
              }
            })
        .flatMap(
            g -> {
              if (g.key().equals("insert")) {
                return Flux.from(
                        g.doOnNext(e -> byNhlIdCache.invalidate(e.getNhlId()))
                            .collectList()
                            .map(entityManager::insert))
                    .flatMapIterable(i -> i);
              } else {
                return g.groupBy(
                        e -> retrievedEntityHashcodeMap.getOrDefault(e.getKey(), 0) == e.hashCode())
                    .flatMap(
                        gg -> {
                          if (gg.key()) {
                            return Flux.from(
                                gg.doOnNext(e -> byNhlIdCache.invalidate(e.getNhlId())));
                          } else {
                            return Flux.from(
                                    gg.doOnNext(e -> byNhlIdCache.invalidate(e.getNhlId()))
                                        .collectList()
                                        .map(entityManager::update))
                                .flatMapIterable(i -> i);
                          }
                        });
              }
            });
  }

  protected abstract Class<Entity> getEntityClass();

  protected abstract EntityQueryRequest findByIdQuery(ID id);
}
