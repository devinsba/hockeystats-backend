package me.hockeystats.nhl.api.stats;

import lombok.Data;

@Data
public class RosterPlayer {
  RosterPlayerInfo person;
  String jerseyNumber;
  RosterPlayerPosition position;
}
