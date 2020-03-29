package me.hockeystats.nhl.api.stats;

import java.util.List;
import lombok.Data;

@Data
public class PlayByPlayPlays {
  List<PlayByPlayPlay> allPlays;
}
