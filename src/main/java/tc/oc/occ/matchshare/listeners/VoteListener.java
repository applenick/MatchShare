package tc.oc.occ.matchshare.listeners;

import org.bukkit.event.EventHandler;
import tc.oc.occ.dispense.events.players.PGMPlayerVoteEvent;
import tc.oc.occ.matchshare.MatchShare;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.rotation.vote.events.MatchPlayerVoteEvent;

public class VoteListener extends ShareListener {

  public VoteListener(MatchShare plugin) {
    super(plugin);
  }

  @EventHandler
  public void onPlayerVote(MatchPlayerVoteEvent event) {
    MatchPlayer player = event.getPlayer();
    if (player != null && player.getBukkit() != null) {
      callNewEvent(
          new PGMPlayerVoteEvent(player.getBukkit(), event.getMap().getName(), event.isAdd()));
    }
  }
}
