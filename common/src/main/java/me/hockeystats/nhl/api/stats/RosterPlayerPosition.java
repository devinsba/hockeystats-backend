package me.hockeystats.nhl.api.stats;

import lombok.Data;

@Data
public class RosterPlayerPosition {
  String code;
  String name;
  String type;
  String abbreviation;
}
