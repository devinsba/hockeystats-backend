package me.hockeystats.nhl.season;

import com.jmethods.catatumbo.CreatedTimestamp;
import com.jmethods.catatumbo.Entity;
import com.jmethods.catatumbo.Identifier;
import com.jmethods.catatumbo.UpdatedTimestamp;
import com.jmethods.catatumbo.Version;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Data
public class Season {
    @Identifier
    long id;
    LocalDate regularSeasonStartDate;
    LocalDate regularSeasonEndDate;
    LocalDate seasonEndDate;
    Integer numberOfGames;
    Boolean tiesInUse;
    Boolean olympicsParticipation;
    Boolean conferencesInUse;
    Boolean divisionsInUse;
    Boolean wildCardInUse;

    @CreatedTimestamp
    LocalDateTime createdAt;

    @UpdatedTimestamp
    LocalDateTime updatedAt;

    @Version
    long version;
}
