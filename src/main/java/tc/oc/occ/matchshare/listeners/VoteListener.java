package tc.oc.occ.matchshare.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import tc.oc.occ.dispense.events.players.PGMPlayerVoteEvent;
import tc.oc.occ.matchshare.MatchShare;
import tc.oc.pgm.api.event.PlayerVoteEvent;

public class VoteListener extends ShareListener {

  public VoteListener(MatchShare plugin) {
    super(plugin);
  }

  @EventHandler
  public void onPlayerVote(PlayerVoteEvent event) {
    Player player = Bukkit.getPlayer(event.getPlayerId());
    if (player != null) {
      callNewEvent(new PGMPlayerVoteEvent(player, event.getMap().getName(), event.isAdd()));
    }
  }
}
