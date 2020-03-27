package me.hockeystats.nhl.game;

import com.jmethods.catatumbo.Entity;
import java.time.ZonedDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.hockeystats.nhl.BaseEntity;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Game extends BaseEntity<Long> {
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
}
