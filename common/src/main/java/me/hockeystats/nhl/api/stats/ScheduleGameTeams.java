package me.hockeystats.nhl.api.stats;

import lombok.Data;

@Data
public class ScheduleGameTeams {
  ScheduleGameTeam away;
  ScheduleGameTeam home;
}
