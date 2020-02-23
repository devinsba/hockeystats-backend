package me.hockeystats.nhl.job.games;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import me.hockeystats.nhl.api.stats.Schedule;
import me.hockeystats.nhl.api.stats.ScheduleDate;
import me.hockeystats.nhl.api.stats.StatsApi;
import me.hockeystats.nhl.game.Game;
import me.hockeystats.nhl.game.Games;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import retrofit2.Response;

@Component
class Handler {
  private final StatsApi statsApi;
  private final Games games;

  @Autowired
  Handler(StatsApi statsApi, Games games) {
    this.statsApi = statsApi;
    this.games = games;
  }

  Mono<ServerResponse> handleToday(ServerRequest request) {
    return statsApi
        .getScheduleForDate(LocalDate.now().toString())
        .map(Response::body)
        .flatMapIterable(Schedule::getDates)
        .flatMapIterable(ScheduleDate::getGames)
        .parallel()
        .flatMap(
            g ->
                games
                    .findById(g.getGamePk())
                    .defaultIfEmpty(new Game())
                    .map(
                        game -> {
                          game.setGameId(g.getGamePk());
                          game.setGameType(g.getGameType());
                          game.setSeasonId(Long.parseLong(g.getSeason()));
                          game.setStartAt(LocalDateTime.ofInstant(g.getGameDate(), ZoneOffset.UTC));
                          game.setVenue(g.getVenue().getName());
                          game.setGameStatus(g.getStatus().getDetailedState());
                          game.setAwayTeamId(g.getTeams().getAway().getTeam().getId());
                          game.setAwayScore(g.getTeams().getAway().getScore());
                          game.setHomeTeamId(g.getTeams().getHome().getTeam().getId());
                          game.setHomeScore(g.getTeams().getHome().getScore());
                          return game;
                        }))
        .sequential()
        .transform(games::saveAll)
        .then(ServerResponse.ok().build());
  }
}
