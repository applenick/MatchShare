package tc.oc.occ.matchshare.listeners;

import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import tc.oc.occ.dispense.events.players.PGMPlayerStatsEvent;
import tc.oc.occ.dispense.events.players.data.PGMPlayerStats;
import tc.oc.occ.matchshare.MatchShare;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.match.event.MatchStatsEvent;
import tc.oc.pgm.stats.PlayerStats;
import tc.oc.pgm.stats.StatsMatchModule;

public class StatsListener extends ShareListener {

  public StatsListener(MatchShare plugin) {
    super(plugin);
  }

  @EventHandler
  public void onMatchStats(MatchStatsEvent event) {
    Match match = event.getMatch();
    if (match == null) return;
    StatsMatchModule stats = match.getModule(StatsMatchModule.class);
    if (stats == null) return;

    Map<UUID, PlayerStats> playerStats = stats.getStats();
    if (playerStats.isEmpty()) return;
    playerStats.forEach(
        (id, stat) -> {
          Player player = Bukkit.getPlayer(id);
          if (player != null) {
            PGMPlayerStats wrappedStats =
                new PGMPlayerStats(
                    id,
                    stat.getKills(),
                    stat.getDeaths(),
                    stat.getMaxKillstreak(),
                    stat.getLongestBowKill(),
                    stat.getBowDamage(),
                    stat.getShotsTaken(),
                    stat.getShotsHit(),
                    stat.getDamageDone(),
                    stat.getDamageTaken());
            callNewEvent(new PGMPlayerStatsEvent(player, wrappedStats));
          }
        });
  }
}
