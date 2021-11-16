package tc.oc.occ.matchshare.tracker;

import com.google.common.collect.Maps;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class FlagTimeTracker {

  private Map<UUID, Instant> flagHolders;

  public FlagTimeTracker() {
    this.flagHolders = Maps.newHashMap();
  }

  public void startTracking(UUID playerId) {
    this.flagHolders.put(playerId, Instant.now());
  }

  public long stopTracking(UUID playerId) {
    Instant startTime = flagHolders.remove(playerId);
    if (startTime != null) {
      Duration timeHeld = Duration.between(startTime, Instant.now());
      return timeHeld.getSeconds();
    }
    return 0;
  }

  public void reset() {
    this.flagHolders.clear();
  }
}
