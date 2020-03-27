package me.hockeystats.nhl.game;

import com.jmethods.catatumbo.DatastoreKey;
import com.jmethods.catatumbo.Entity;
import com.jmethods.catatumbo.ParentKey;
import java.time.ZonedDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.hockeystats.nhl.BaseEntity;
import me.hockeystats.nhl.season.Season;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, exclude = "parentKey")
public class Game extends BaseEntity<Long> {
  @ParentKey private DatastoreKey parentKey;
  private Long nhlId;
  private String gameType;
  private long seasonId;
  private ZonedDateTime startAt;
  private String venue;
  private String gameStatus;
  private long awayTeamId;
  private int awayScore;
  private long homeTeamId;
  private int homeScore;

  public void setSeason(Season season) {
    parentKey = season.getKey();
  }
}
