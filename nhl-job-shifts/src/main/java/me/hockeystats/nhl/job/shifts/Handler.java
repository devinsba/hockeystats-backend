package me.hockeystats.nhl.job.shifts;

import java.util.function.Function;
import me.hockeystats.PubSubMessage;
import me.hockeystats.nhl.api.shifts.ShiftsApi;
import me.hockeystats.nhl.game.Games;
import me.hockeystats.nhl.game.Shift;
import me.hockeystats.nhl.game.Shifts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import retrofit2.Response;

@Component
class Handler {
  private final ShiftsApi shiftsApi;
  private final Shifts shifts;
  private final Games games;

  @Autowired
  Handler(ShiftsApi shiftsApi, Shifts shifts, Games games) {
    this.shiftsApi = shiftsApi;
    this.shifts = shifts;
    this.games = games;
  }

  Function<ServerRequest, Mono<ServerResponse>> requested() {
    return (request ->
        request
            .bodyToMono(PubSubMessage.class)
            .map(b -> b.getMessage().getData())
            .map(Base64Utils::decodeFromString)
            .map(String::new)
            .map(Long::valueOf)
            .log()
            .map(this::forGame)
            .map(f -> f.apply(request))
            .flatMap(m -> m));
  }

  private Function<ServerRequest, Mono<ServerResponse>> forGame(Long nhlId) {
    return (req) ->
        shiftsApi
            .getGameShifts(nhlId)
            .map(Response::body)
            .flatMap(
                shiftsResponse ->
                    games
                        .findByNhlId(nhlId)
                        .flatMap(
                            game ->
                                Flux.fromIterable(shiftsResponse.getData())
                                    .flatMap(
                                        s ->
                                            shifts
                                                .findByNhlId(s.getId())
                                                .switchIfEmpty(Mono.fromSupplier(Shift::new))
                                                .map(
                                                    shift -> {
                                                      shift.setNhlId(s.getId());
                                                      shift.setPlayerId(s.getPlayerId());
                                                      shift.setTeamId(s.getTeamId());
                                                      return shift;
                                                    }))
                                    .transform(shifts::saveAll)
                                    .hasElements()))
            .flatMap(b -> b ? ServerResponse.ok().build() : ServerResponse.notFound().build());
  }
}
