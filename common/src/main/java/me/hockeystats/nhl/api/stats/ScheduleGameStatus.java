package me.hockeystats.nhl.api.stats;

import lombok.Data;

@Data
public class ScheduleGameStatus {
  String abstractGameState;
  String codedGameState;
  String detailedState;
  String statusCode;
  Boolean startTimeTBD;
}
