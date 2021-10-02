package tc.oc.occ.matchshare.listeners;

import org.bukkit.event.EventHandler;
import tc.oc.occ.dispense.events.map.PGMMapLoadEvent;
import tc.oc.occ.dispense.events.match.MatchStatus;
import tc.oc.occ.dispense.events.match.PGMMatchStatusUpdateEvent;
import tc.oc.occ.matchshare.MatchShare;
import tc.oc.pgm.api.map.MapInfo;
import tc.oc.pgm.api.match.MatchPhase;
import tc.oc.pgm.api.match.event.MatchLoadEvent;
import tc.oc.pgm.api.match.event.MatchPhaseChangeEvent;

public class MapListener extends ShareListener {

  public MapListener(MatchShare plugin) {
    super(plugin);
  }

  @EventHandler
  public void onMapLoad(MatchLoadEvent event) {
    MapInfo map = event.getMatch().getMap();
    String name = map.getName();
    String desc = map.getDescription();
    int maxPlayers = map.getMaxPlayers().stream().reduce(0, Integer::sum);
    callNewEvent(new PGMMapLoadEvent(name, desc, maxPlayers));
  }

  @EventHandler
  public void onMatchStatusChange(MatchPhaseChangeEvent event) {
    callNewEvent(new PGMMatchStatusUpdateEvent(convertPhase(event.getNewPhase())));
  }

  private MatchStatus convertPhase(MatchPhase phase) {
    switch (phase) {
      case FINISHED:
        return MatchStatus.FINISHED;
      case RUNNING:
        return MatchStatus.RUNNING;
      case STARTING:
        return MatchStatus.STARTING;
      default:
        return MatchStatus.IDLE;
    }
  }
}
