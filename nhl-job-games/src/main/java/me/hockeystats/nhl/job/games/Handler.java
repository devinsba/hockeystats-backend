package me.hockeystats.nhl.job.games;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.function.Function;
import me.hockeystats.PubSubMessage;
import me.hockeystats.nhl.api.stats.Schedule;
import me.hockeystats.nhl.api.stats.ScheduleDate;
import me.hockeystats.nhl.api.stats.StatsApi;
import me.hockeystats.nhl.game.Game;
import me.hockeystats.nhl.game.Games;
import me.hockeystats.nhl.season.Seasons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import retrofit2.Response;

@Component
class Handler {
  private final StatsApi statsApi;
  private final Games games;
  private final Seasons seasons;

  @Autowired
  Handler(StatsApi statsApi, Games games, Seasons seasons) {
    this.statsApi = statsApi;
    this.games = games;
    this.seasons = seasons;
  }

  Function<ServerRequest, Mono<ServerResponse>> today() {
    LocalDate today = LocalDate.now();
    return forDate(today);
  }

  Function<ServerRequest, Mono<ServerResponse>> yesterday() {
    LocalDate yesterday = LocalDate.now().minusDays(1);
    return forDate(yesterday);
  }

  Function<ServerRequest, Mono<ServerResponse>> deleteAll() {
    return request ->
        Mono.just(games.deleteAll())
            .flatMap(l -> ServerResponse.ok().bodyValue(String.format("Deleted %d games", l)));
  }

  Function<ServerRequest, Mono<ServerResponse>> requested() {
    return (request ->
        request
            .bodyToMono(PubSubMessage.class)
            .map(b -> b.getMessage().getData())
            .map(Base64Utils::decodeFromString)
            .map(String::new)
            .map(LocalDate::parse)
            .log()
            .map(this::forDate)
            .map(f -> f.apply(request))
            .flatMap(m -> m));
  }

  private Function<ServerRequest, Mono<ServerResponse>> forDate(LocalDate date) {
    return (req) ->
        statsApi
            .getScheduleForDate(date.toString())
            .map(Response::body)
            .flatMapIterable(Schedule::getDates)
            .flatMapIterable(ScheduleDate::getGames)
            .parallel()
            .flatMap(
                g ->
                    games
                        .findByNhlId(g.getGamePk())
                        .defaultIfEmpty(new Game())
                        .flatMap(
                            game ->
                                seasons
                                    .findByNhlId(Long.valueOf(g.getSeason()))
                                    .map(
                                        season -> {
                                          game.setSeason(season);
                                          game.setNhlId(g.getGamePk());
                                          game.setGameType(g.getGameType());
                                          game.setSeasonId(Long.parseLong(g.getSeason()));
                                          game.setStartAt(
                                              ZonedDateTime.ofInstant(
                                                  g.getGameDate(), ZoneId.of("America/New_York")));
                                          game.setVenue(g.getVenue().getName());
                                          game.setGameStatus(g.getStatus().getDetailedState());
                                          game.setAwayTeamId(
                                              g.getTeams().getAway().getTeam().getId());
                                          game.setAwayScore(g.getTeams().getAway().getScore());
                                          game.setHomeTeamId(
                                              g.getTeams().getHome().getTeam().getId());
                                          game.setHomeScore(g.getTeams().getHome().getScore());
                                          return game;
                                        })))
            .sequential()
            .log()
            .transform(games::saveAll)
            .then(ServerResponse.ok().build());
  }
}
