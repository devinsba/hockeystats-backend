package me.hockeystats.nhl.api.stats;

import java.time.LocalDate;
import lombok.Data;

@Data
public class StatsSeason {
  String seasonId;
  LocalDate regularSeasonStartDate;
  LocalDate regularSeasonEndDate;
  LocalDate seasonEndDate;
  Integer numberOfGames;
  Boolean tiesInUse;
  Boolean olympicsParticipation;
  Boolean conferencesInUse;
  Boolean divisionsInUse;
  Boolean wildCardInUse;
}
