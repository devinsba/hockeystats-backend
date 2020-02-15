package me.hockeystats.nhl.api.stats;

import java.time.Instant;
import lombok.Data;

@Data
public class ScheduleGame {
  Long gamePk;
  String gameType;
  String season;
  Instant gameDate;
  ScheduleGameStatus status;
  ScheduleGameTeams teams;
  ScheduleGameVenue venue;
}
