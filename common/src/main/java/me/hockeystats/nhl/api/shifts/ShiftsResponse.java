package me.hockeystats.nhl.api.shifts;

import java.util.List;
import lombok.Data;

@Data
public class ShiftsResponse {
  List<Shift> data;
}
