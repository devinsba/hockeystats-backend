package me.hockeystats.nhl.season;

import com.jmethods.catatumbo.Entity;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.hockeystats.nhl.BaseEntity;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Season extends BaseEntity<Long> {
  private Long nhlId;
  private LocalDate regularSeasonStartDate;
  private LocalDate regularSeasonEndDate;
  private LocalDate seasonEndDate;
  private Integer numberOfGames;
  private Boolean tiesInUse;
  private Boolean olympicsParticipation;
  private Boolean conferencesInUse;
  private Boolean divisionsInUse;
  private Boolean wildCardInUse;

  private ZonedDateTime lastResultBackfillPerformedAt =
      ZonedDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC);
}
