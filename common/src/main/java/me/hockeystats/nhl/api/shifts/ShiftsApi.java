package me.hockeystats.nhl.api.shifts;

import reactor.core.publisher.Mono;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ShiftsApi {
  @GET("/stats/rest/en/shiftcharts?cayenneExp=gameId={gameId}")
  Mono<Response<ShiftsResponse>> getGameShifts(@Path("gameId") long gameId);
}
