package tc.oc.occ.matchshare.listeners;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import tc.oc.pgm.api.integration.Integration;
import tc.oc.pgm.api.match.event.MatchFinishEvent;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.events.PlayerJoinPartyEvent;
import tc.oc.pgm.events.PlayerLeavePartyEvent;
import tc.oc.pgm.ffa.Tribute;

public class TimeTrackerListener extends ShareListener {

  private final MatchTimeTracker tracker;

  public TimeTrackerListener(MatchShare plugin) {
    super(plugin);
    this.tracker = plugin.getTimeTracker();
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

  @EventHandler(priority = EventPriority.HIGH)
  public void onMatchFinish(MatchFinishEvent event) {
    final Collection<Competitor> winners = event.getWinners();
    final String winnerName = formatWinnerNames(winners);

    final Duration matchLength = event.getMatch().getDuration();
    if (matchLength.isZero() || matchLength.isNegative()) return;

    final long matchSeconds = matchLength.getSeconds();
    final long winThresholdSeconds = Math.round(matchSeconds * 0.50);
    final long participationThresholdSeconds = Math.round(matchSeconds * 0.35);

    final Set<UUID> winnerIds = getWinnerIds(winners);

    final List<Player> winningPlayers = Lists.newArrayList();
    final List<Player> participatingPlayers = Lists.newArrayList();
    final Map<Player, Duration> playerTimes = Maps.newHashMap();

    tracker
        .getTimeLogs()
        .forEach(
            (uuid, data) -> {
              data.endAll(); // End tracking for match

              // Online check
              final Player player = Bukkit.getPlayer(uuid);
              if (player == null) return;

              final Duration teamTime = data.getTimePlayed(data.getPrimaryTeam());
              playerTimes.put(player, teamTime);

              final boolean qualifiedWinner =
                  winnerIds.contains(uuid)
                      && winners.contains(data.getPrimaryTeam())
                      && teamTime.getSeconds() >= winThresholdSeconds;

              if (qualifiedWinner) {
                winningPlayers.add(player);
              }

              if (data.getTotalTime().getSeconds() >= participationThresholdSeconds) {
                participatingPlayers.add(player);
              }
            });

    // call events for winners & participants
    callNewEvent(new PGMMatchWinnerEvent(winningPlayers));
    callNewEvent(new PGMMatchParticipationEvent(participatingPlayers));
    callNewEvent(new PGMMatchEndEvent(winningPlayers, playerTimes, winnerName, matchLength));

    // Reset all tracker data as match is over and events have been completed
    tracker.reset();
  }

  private Set<UUID> getWinnerIds(Collection<Competitor> winners) {
    Set<UUID> winnerIds = Sets.newHashSet();
    for (Competitor winner : winners) {
      winnerIds.addAll(
          winner.getPlayers().stream().map(MatchPlayer::getId).collect(Collectors.toList()));
    }
    return winnerIds;
  }

  private static String formatWinnerNames(Collection<Competitor> winners) {
    if (winners == null || winners.isEmpty()) return "Unknown";

    final List<String> names =
        winners.stream()
            .map(TimeTrackerListener::getDisplayName)
            .filter(s -> s != null && !s.isEmpty())
            .collect(Collectors.toList());

    if (names.isEmpty()) return "Unknown";
    if (names.size() == 1) return names.get(0);
    if (names.size() == 2) return names.get(0) + " and " + names.get(1);
    return joinNames(names);
  }

  private static String getDisplayName(Competitor competitor) {
    if (competitor instanceof Tribute) {
      return getTributePlayerName((Tribute) competitor);
    }
    return competitor != null ? competitor.getNameLegacy() : null;
  }

  private static String getTributePlayerName(Tribute tribute) {
    if (tribute == null) return null;
    return tribute.getPlayers().stream()
        .findFirst()
        .map(
            mp -> {
              String nick = Integration.getNick(mp.getBukkit());
              return (nick != null && !nick.isEmpty()) ? nick : mp.getNameLegacy();
            })
        .orElse(null);
  }

  private static String joinNames(List<String> names) {
    int size = names.size();
    if (size == 0) return "";
    if (size == 1) return names.get(0);
    if (size == 2) return names.get(0) + " and " + names.get(1);
    String last = names.get(size - 1);
    return String.join(", ", names.subList(0, size - 1)) + ", and " + last;
  }
}
