package me.hockeystats.nhl.game;

import com.jmethods.catatumbo.DatastoreKey;
import com.jmethods.catatumbo.Entity;
import com.jmethods.catatumbo.ParentKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.hockeystats.nhl.BaseEntity;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, exclude = "parentKey")
public class Shift extends BaseEntity<Long> {
  @ParentKey private DatastoreKey parentKey;
  Long nhlId;

  int duration;
  int endTime;
  int eventNumber;
  int period;
  long playerId;
  int startTime;
  long teamId;
}
