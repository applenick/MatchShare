package tc.oc.occ.matchshare.tracker;

import tc.oc.occ.environment.Environment;
import tc.oc.occ.matchshare.MatchShare;
import tc.oc.occ.matchshare.utils.PGMUtils;
import tc.oc.pgm.api.match.Match;

public class MatchCapacityTracker {

  private int lastMax = -1;

  public MatchCapacityTracker(MatchShare plugin) {
    if (!plugin.getServer().getPluginManager().isPluginEnabled("Environment")) return;
    plugin.getServer().getScheduler().runTaskTimer(plugin, this::updateCapacity, 20L, 20L);
  }

  private void updateCapacity() {
    Match match = PGMUtils.getMatch();
    if (match != null) {
      int max = match.getMaxPlayers();
      if (max != lastMax) {
        setMax(max);
      }
    }
  }

  private void setMax(int max) {
    Environment env = Environment.get();
    if (env == null) return;
    env.setEnv("max-players", max);
    lastMax = max;
  }
}
