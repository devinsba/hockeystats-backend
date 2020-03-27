package me.hockeystats;

import com.jmethods.catatumbo.CreatedTimestamp;
import com.jmethods.catatumbo.DatastoreKey;
import com.jmethods.catatumbo.Identifier;
import com.jmethods.catatumbo.Key;
import com.jmethods.catatumbo.MappedSuperClass;
import com.jmethods.catatumbo.UpdatedTimestamp;
import com.jmethods.catatumbo.Version;
import java.time.ZonedDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@MappedSuperClass
@Data
@EqualsAndHashCode
public abstract class BaseEntity {
  @Identifier long id;

  @Key DatastoreKey key;

  @CreatedTimestamp ZonedDateTime createdAt;

  @UpdatedTimestamp ZonedDateTime updatedAt;

  @Version long version;
}
