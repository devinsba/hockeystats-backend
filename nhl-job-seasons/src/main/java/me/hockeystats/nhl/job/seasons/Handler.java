package me.hockeystats.nhl.job.seasons;

import me.hockeystats.nhl.api.stats.StatsApi;
import me.hockeystats.nhl.api.stats.StatsSeasons;
import me.hockeystats.nhl.season.Season;
import me.hockeystats.nhl.season.Seasons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import retrofit2.Response;

@Component
class Handler {
  private final StatsApi statsApi;
  private final Seasons seasons;

  @Autowired
  Handler(StatsApi statsApi, Seasons seasons) {
    this.statsApi = statsApi;
    this.seasons = seasons;
  }

  Mono<ServerResponse> handle(ServerRequest request) {
    return statsApi
        .listSeasons()
        .map(Response::body)
        .flatMapIterable(StatsSeasons::getSeasons)
        .parallel()
        .flatMap(
            s ->
                seasons
                    .findById(Long.parseLong(s.getSeasonId()))
                    .defaultIfEmpty(new Season())
                    .map(
                        season -> {
                          season.setSeasonId(Long.parseLong(s.getSeasonId()));
                          season.setRegularSeasonStartDate(s.getRegularSeasonStartDate());
                          season.setRegularSeasonEndDate(s.getRegularSeasonEndDate());
                          season.setSeasonEndDate(s.getSeasonEndDate());
                          season.setNumberOfGames(s.getNumberOfGames());
                          season.setTiesInUse(s.getTiesInUse());
                          season.setOlympicsParticipation(s.getOlympicsParticipation());
                          season.setConferencesInUse(s.getConferencesInUse());
                          season.setDivisionsInUse(s.getDivisionsInUse());
                          season.setWildCardInUse(s.getWildCardInUse());
                          return season;
                        }))
        .sequential()
        .transform(seasons::saveAll)
        .then(ServerResponse.ok().build());
  }
}
