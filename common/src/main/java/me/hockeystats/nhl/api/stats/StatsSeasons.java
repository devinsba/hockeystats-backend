package me.hockeystats.nhl.api.stats;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class StatsSeasons {
  List<StatsSeason> seasons = new ArrayList<>();
}
