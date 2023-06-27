package tc.oc.occ.matchshare.listeners;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.UUID;
import java.util.regex.Pattern;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import tc.oc.occ.dispense.events.players.PGMPlayerSportsmanshipEvent;
import tc.oc.occ.matchshare.MatchShare;
import tc.oc.pgm.api.match.event.MatchPhaseChangeEvent;

public class SportsmanshipListener extends ShareListener {

  private final Pattern pattern = Pattern.compile("gg|good game", Pattern.CASE_INSENSITIVE);

  private Cache<UUID, String> goodSports;

  public SportsmanshipListener(MatchShare plugin) {
    super(plugin);
    this.goodSports = CacheBuilder.newBuilder().build();
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerChat(AsyncPlayerChatEvent event) {
    Player player = event.getPlayer();

    if (player == null || !player.isOnline()) return;
    if (goodSports.getIfPresent(player.getUniqueId()) != null) return;
    if (!getMatch().isFinished()) return;

    if (getMatch().isFinished() && isGG(event.getMessage())) {
      goodSports.put(player.getUniqueId(), event.getMessage());

      callSyncEvent(new PGMPlayerSportsmanshipEvent(player));
    }
  }

  @EventHandler
  public void onMatchPhaseChange(MatchPhaseChangeEvent event) {
    this.goodSports.invalidateAll();
  }

  private boolean isGG(String message) {
    return pattern.matcher(message).find();
  }
}
