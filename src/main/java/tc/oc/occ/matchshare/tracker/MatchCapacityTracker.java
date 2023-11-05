package tc.oc.occ.matchshare.tracker;

import tc.oc.occ.environment.Environment;
import tc.oc.occ.matchshare.MatchShare;
import tc.oc.occ.matchshare.utils.PGMUtils;
import tc.oc.pgm.api.match.Match;

public class MatchCapacityTracker {

  public MatchCapacityTracker(MatchShare plugin) {
    plugin.getServer().getScheduler().runTaskTimer(plugin, this::updateCapacity, 20L, 20L);
  }

  private void updateCapacity() {
    Match match = PGMUtils.getMatch();
    if (match != null) {
      int max = match.getMaxPlayers();
      setMax(max);
    }
  }

  private boolean isEnvEnabled() {
    return Environment.get() != null;
  }

  private void setMax(int max) {
    if (!isEnvEnabled()) return;
    Environment.get().setEnv("max-players", max);
  }
}
