package tc.oc.occ.matchshare.listeners;

import org.bukkit.event.EventHandler;
import tc.oc.occ.dispense.events.players.PGMPlayerJoinFullEvent;
import tc.oc.occ.matchshare.MatchShare;
import tc.oc.pgm.events.PlayerJoinResultEvent;
import tc.oc.pgm.join.JoinResult;
import tc.oc.pgm.join.JoinResultOption;

public class PlayerJoinListener extends ShareListener {

  public PlayerJoinListener(MatchShare plugin) {
    super(plugin);
  }

  @EventHandler
  public void onMatchIsFull(PlayerJoinResultEvent event) {
    if (event.isCancelled()) return;

    JoinResult result = event.getJoinResult();

    if (!result.isSuccess() && result.getOption() == JoinResultOption.FULL) {
      callNewEvent(new PGMPlayerJoinFullEvent(event.getPlayer().getBukkit()));
      event.setCancelled(true);
    }
  }
}
