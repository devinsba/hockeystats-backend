package me.hockeystats.nhl.job.backfill;

import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import lombok.SneakyThrows;
import me.hockeystats.nhl.season.Season;
import me.hockeystats.nhl.season.Seasons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
class Handler {
  private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();
  private static final ProjectTopicName DATES_TO_SCAN_TOPIC =
      ProjectTopicName.of(PROJECT_ID, "dates-to-scan");

  private final Seasons seasons;
  private final Publisher datesToScanPublisher;

  @SneakyThrows
  @Autowired
  Handler(Seasons seasons) {
    this.seasons = seasons;
    datesToScanPublisher = Publisher.newBuilder(DATES_TO_SCAN_TOPIC).build();
  }

  Function<ServerRequest, Mono<ServerResponse>> gamesForSeason() {
    return (request ->
        seasons
            .findAll()
            .filter(
                s ->
                    s.getLastResultBackfillPerformedAt()
                        .isBefore(ZonedDateTime.now().minusMinutes(15)))
            .elementAt(0)
            .log()
            .doOnSuccess(
                s -> {
                  s.setLastResultBackfillPerformedAt(
                      ZonedDateTime.now(ZoneId.of("America/New_York")));
                  seasons.saveAll(Flux.just(s)).subscribeOn(Schedulers.elastic()).subscribe();
                })
            .flatMap(this::backfillGamesForSeason)
            .onErrorResume(t -> ServerResponse.ok().bodyValue("Nothing to do\n")));
  }

  Mono<ServerResponse> backfillGamesForSeason(Season season) {
    return Mono.just(season)
        .flatMapIterable(
            s -> {
              List<LocalDate> dates = new ArrayList<>();
              LocalDate current = s.getRegularSeasonStartDate().minusWeeks(5);
              while (!current.isAfter(s.getSeasonEndDate()) && !current.isAfter(LocalDate.now())) {
                dates.add(current);
                current = current.plusDays(1);
              }
              return dates;
            })
        .map(d -> PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(d.toString())).build())
        .log()
        .map(datesToScanPublisher::publish)
        .map(
            f -> {
              try {
                return f.get();
              } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
              }
            })
        .then(ServerResponse.ok().bodyValue(season.getNhlId()));
  }
}
