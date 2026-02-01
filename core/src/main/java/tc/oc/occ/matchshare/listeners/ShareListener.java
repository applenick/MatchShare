package tc.oc.occ.matchshare.listeners;

import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import tc.oc.occ.dispense.events.DispenseEvent;
import tc.oc.occ.matchshare.MatchShare;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.player.ParticipantState;

public abstract class ShareListener implements Listener {

  protected final MatchShare plugin;
  protected final Logger logger;

  public ShareListener(MatchShare plugin) {
    this.plugin = plugin;
    this.logger = plugin.getLogger();
  }

  protected void callNewEvent(DispenseEvent event) {
    plugin.getServer().getPluginManager().callEvent(event);
  }

  protected void callSyncEvent(DispenseEvent event) {
    plugin.getServer().getScheduler().runTask(plugin, () -> callNewEvent(event));
  }

  protected Match getMatch() {
    return PGM.get().getMatchManager().getMatches().hasNext()
        ? PGM.get().getMatchManager().getMatches().next()
        : null;
  }

  protected boolean isParticipating(Player player) {
    return getMatch() != null && getMatch().getParticipant(player) != null;
  }

  protected ParticipantState getParticipantState(UUID playerId) {
    return PGM.get().getMatchManager().getParticipantState(playerId);
  }
}
