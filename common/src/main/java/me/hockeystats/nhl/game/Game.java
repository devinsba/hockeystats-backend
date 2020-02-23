package me.hockeystats.nhl.game;

import com.jmethods.catatumbo.CreatedTimestamp;
import com.jmethods.catatumbo.Entity;
import com.jmethods.catatumbo.Identifier;
import com.jmethods.catatumbo.UpdatedTimestamp;
import com.jmethods.catatumbo.Version;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import lombok.Data;

@Entity
@Data
public class Game {
    @Identifier
    long id;

    long gameId;

    private String gameType;
    private long seasonId;
    private LocalDateTime startAt;
    private String venue;
    private String gameStatus;

    private long awayTeamId;
    private int awayScore;
    private long homeTeamId;
    private int homeScore;

    @CreatedTimestamp
    ZonedDateTime createdAt;

    @UpdatedTimestamp
    ZonedDateTime updatedAt;

    @Version
    long version;
}
