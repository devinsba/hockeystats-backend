package me.hockeystats.nhl.job.seasons;

import me.hockeystats.nhl.api.stats.StatsApi;
import me.hockeystats.nhl.season.Seasons;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
class Handler {
    private final StatsApi statsApi;
    private final Seasons seasons;

    Handler(StatsApi statsApi, Seasons seasons) {
        this.statsApi = statsApi;
        this.seasons = seasons;
    }

    Mono<ServerResponse> handle(ServerRequest request) {
        return ServerResponse.ok().build();
    }
}
