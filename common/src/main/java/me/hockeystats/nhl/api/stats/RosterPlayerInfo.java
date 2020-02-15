package me.hockeystats.nhl.api.stats;

import lombok.Data;

@Data
public class RosterPlayerInfo {
  long id;
  String fullName;
  String link;
}
