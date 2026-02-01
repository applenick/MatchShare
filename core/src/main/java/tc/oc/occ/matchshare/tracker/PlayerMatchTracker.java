package tc.oc.occ.matchshare.tracker;

import com.google.common.collect.Maps;
import java.time.Duration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import tc.oc.pgm.api.party.Competitor;

public class PlayerMatchTracker {

  private final Map<Competitor, TeamTimeRecord> timeLogs;

  public PlayerMatchTracker() {
    this.timeLogs = Maps.newHashMap();
  }

  public void start(Competitor competitor) {
    if (!timeLogs.containsKey(competitor)) {
      timeLogs.put(competitor, new TeamTimeRecord());
    }

    TeamTimeRecord timeLog = timeLogs.get(competitor);
    timeLog.start();
  }

  public void endAll() {
    timeLogs.keySet().forEach(this::end);
  }

  public void end(Competitor competitor) {
    if (timeLogs.containsKey(competitor)) {
      timeLogs.get(competitor).end();
    }
  }

  public Duration getTotalTime() {
    Duration total = Duration.ZERO;
    for (Duration timePlayed :
        timeLogs.values().stream().map(TeamTimeRecord::getTimePlayed).toList()) {
      total = total.plus(timePlayed);
    }
    return total;
  }

  public Duration getTimePlayed(Competitor competitor) {
    TeamTimeRecord time = timeLogs.get(competitor);

    if (time != null) {
      return time.getTimePlayed();
    }

    return Duration.ZERO;
  }

  public Competitor getPrimaryTeam() {
    if (timeLogs.isEmpty()) return null;
    Optional<Duration> maxTime =
        timeLogs.values().stream().map(TeamTimeRecord::getTimePlayed).max(Duration::compareTo);
    for (Entry<Competitor, TeamTimeRecord> e : timeLogs.entrySet()) {
      if (e.getValue().getTimePlayed().equals(maxTime.get())) {
        return e.getKey();
      }
    }
    return null;
  }
}
