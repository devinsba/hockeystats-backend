package me.hockeystats.nhl.api.stats;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class ScheduleDate {
  LocalDate date;
  List<ScheduleGame> games;
}
