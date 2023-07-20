package tc.oc.occ.matchshare.listeners;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import tc.oc.occ.dispense.events.match.PGMMatchParticipationEvent;
import tc.oc.occ.dispense.events.match.PGMMatchWinnerEvent;
import tc.oc.occ.dispense.events.players.PGMPlayerSportsmanshipEvent;
import tc.oc.occ.matchshare.MatchShare;
import tc.oc.pgm.api.match.MatchPhase;
import tc.oc.pgm.api.match.event.MatchPhaseChangeEvent;

public class SportsmanshipListener extends ShareListener {

  private final Pattern pattern =
      Pattern.compile("\"^(.*\\W)?(g\\S?g+|good game)(\\W.*)?$\"", Pattern.CASE_INSENSITIVE);

  private Cache<UUID, String> goodSports;

  private List<Player> winners;
  private List<Player> participants;

  public SportsmanshipListener(MatchShare plugin) {
    super(plugin);
    this.goodSports = CacheBuilder.newBuilder().build();
    this.winners = Lists.newArrayList();
    this.participants = Lists.newArrayList();
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerChat(AsyncPlayerChatEvent event) {
    Player player = event.getPlayer();

    if (player == null || !player.isOnline()) return;
    if (goodSports.getIfPresent(player.getUniqueId()) != null) return;
    if (!getMatch().isFinished()) return;

    if (getMatch().isFinished() && isGG(event.getMessage())) {
      goodSports.put(player.getUniqueId(), event.getMessage());

      callSyncEvent(
          new PGMPlayerSportsmanshipEvent(player, isWinner(player), isParticipant(player)));
    }
  }

  @EventHandler
  public void onMatchWinnerEvent(PGMMatchWinnerEvent event) {
    this.winners = event.getPlayers();
  }

  @EventHandler
  public void onMatchParticipantEvent(PGMMatchParticipationEvent event) {
    this.participants = event.getPlayers();
  }

  @EventHandler
  public void onMatchPhaseChange(MatchPhaseChangeEvent event) {
    MatchPhase newPhase = event.getNewPhase();
    if (newPhase != null && newPhase != MatchPhase.FINISHED) {
      this.goodSports.invalidateAll();
      this.winners = Lists.newArrayList();
      this.participants = Lists.newArrayList();
    }
  }

  private boolean isWinner(Player player) {
    return winners.contains(player);
  }

  private boolean isParticipant(Player player) {
    return participants.contains(player);
  }

  private boolean isGG(String message) {
    return pattern.matcher(message).find();
  }
}
