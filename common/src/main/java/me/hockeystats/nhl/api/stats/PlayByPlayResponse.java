package me.hockeystats.nhl.api.stats;

import lombok.Data;

@Data
public class PlayByPlayResponse {
  long gamePk;
  PlayByPlayLiveData liveData;
}
