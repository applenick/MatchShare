package tc.oc.occ.matchshare.listeners;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import tc.oc.occ.dispense.events.match.PGMMatchEndEvent;
import tc.oc.occ.dispense.events.match.PGMMatchParticipationEvent;
import tc.oc.occ.dispense.events.match.PGMMatchWinnerEvent;
import tc.oc.occ.matchshare.MatchShare;
import tc.oc.occ.matchshare.tracker.MatchTimeTracker;
import tc.oc.pgm.api.match.event.MatchFinishEvent;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.events.PlayerJoinPartyEvent;
import tc.oc.pgm.events.PlayerLeavePartyEvent;

public class TimeTrackerListener extends ShareListener {

  private final MatchTimeTracker tracker;

  public TimeTrackerListener(MatchShare plugin) {
    super(plugin);
    this.tracker = plugin.getTimeTracker();
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onMatchFinish(MatchFinishEvent event) {
    List<Player> winningPlayers = Lists.newArrayList();
    List<Player> participatingPlayers = Lists.newArrayList();
    Map<Player, Duration> playerTimes = Maps.newHashMap();
    String winnerName = event.getWinner() != null ? event.getWinner().getNameLegacy() : null;

    // Match Info
    List<UUID> winnerIds = getWinnerIds(event.getWinners());
    Duration matchLength = event.getMatch().getDuration();

    // Error check: if match length was for some reason 0, don't perform win logic
    if (matchLength.isZero()) return;

    // Iterate through all tracked players
    tracker
        .getTimeLogs()
        .forEach(
            (uuid, data) -> {

              // End time for player
              data.endAll();

              // Make sure they're online
              Player player = Bukkit.getPlayer(uuid);
              if (player != null) {
                // Check if they should be included as winners
                if (winnerIds.contains(uuid)
                    && event.getWinners().contains(data.getPrimaryTeam())
                    && data.getTimePlayed(data.getPrimaryTeam()).getSeconds()
                        >= (matchLength.getSeconds() * 0.5)) {
                  winningPlayers.add(player);
                }

                // Ensure they have enough time for participating progress
                if (data.getTotalTime().getSeconds() >= matchLength.getSeconds() * 0.35) {
                  participatingPlayers.add(player);
                }

                // Include time played on the player's primary team
                playerTimes.put(player, data.getTimePlayed(data.getPrimaryTeam()));
              }
            });

    // Call Events for winner & participating
    callNewEvent(new PGMMatchWinnerEvent(winningPlayers));
    callNewEvent(new PGMMatchParticipationEvent(participatingPlayers));

    // General end event
    callNewEvent(new PGMMatchEndEvent(winningPlayers, playerTimes, winnerName, matchLength));

    // Reset all tracker data as match is over and events have been completed
    tracker.reset();
  }

  @EventHandler
  public void onPlayerJoinMatch(PlayerJoinPartyEvent event) {
    // End time tracking for old party
    if (event.getOldParty() instanceof Competitor) {
      tracker.getPlayerData(event.getPlayer().getId()).end((Competitor) event.getOldParty());
    }

    // When joining a party that's playing, start time tracking
    if (event.getNewParty() instanceof Competitor) {
      tracker.getPlayerData(event.getPlayer().getId()).start((Competitor) event.getNewParty());
    }
  }

  @EventHandler
  public void onPlayerLeaveMatch(PlayerLeavePartyEvent event) {
    if (event.getMatch().isRunning() && event.getParty() instanceof Competitor) {
      tracker.getPlayerData(event.getPlayer().getId()).end((Competitor) event.getParty());
    }
  }

  private List<UUID> getWinnerIds(Collection<Competitor> winners) {
    List<UUID> winnerIds = Lists.newArrayList();
    for (Competitor winner : winners) {
      winnerIds.addAll(
          winner.getPlayers().stream().map(MatchPlayer::getId).collect(Collectors.toList()));
    }
    return winnerIds;
  }
}
