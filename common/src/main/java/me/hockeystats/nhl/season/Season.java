package me.hockeystats.nhl.season;

import com.jmethods.catatumbo.Entity;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.hockeystats.BaseEntity;

@Entity
@Data
@EqualsAndHashCode
public class Season extends BaseEntity {
  long seasonId;
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
