package tc.oc.occ.matchshare.tracker;

import java.time.Duration;
import java.time.Instant;

public class TeamTimeRecord {

  private Duration timePlayed;

  private Instant startTime;

  public TeamTimeRecord() {
    this.timePlayed = Duration.ZERO;
    this.startTime = null;
  }

  public void end() {
    if (this.startTime != null) {
      this.timePlayed = timePlayed.plus(Duration.between(startTime, Instant.now()));
      this.startTime = null;
    }
  }

  public void start() {
    this.startTime = Instant.now();
  }

  public Duration getTimePlayed() {
    return timePlayed;
  }
}
