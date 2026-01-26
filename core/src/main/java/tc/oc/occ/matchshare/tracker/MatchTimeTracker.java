package tc.oc.occ.matchshare.tracker;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;

public class MatchTimeTracker {

  private final Map<UUID, PlayerMatchTracker> matchData;

  public MatchTimeTracker() {
    this.matchData = Maps.newHashMap();
  }

  public Map<UUID, PlayerMatchTracker> getTimeLogs() {
    return matchData;
  }

  public void reset() {
    this.matchData.clear();
  }

  public PlayerMatchTracker getPlayerData(UUID playerId) {
    PlayerMatchTracker pmt = null;

    if (matchData.containsKey(playerId)) {
      pmt = matchData.get(playerId);
    }

    if (pmt == null) {
      pmt = new PlayerMatchTracker();
      matchData.put(playerId, pmt);
    }

    return pmt;
  }
}
