package tc.oc.occ.matchshare.listeners;

import org.bukkit.event.EventHandler;
import tc.oc.occ.dispense.events.restart.PGMCancelRestartEvent;
import tc.oc.occ.dispense.events.restart.PGMRestartEvent;
import tc.oc.occ.matchshare.MatchShare;
import tc.oc.pgm.events.CountdownStartEvent;
import tc.oc.pgm.restart.CancelRestartEvent;
import tc.oc.pgm.restart.RestartCountdown;

public class RestartListener extends ShareListener {

  public RestartListener(MatchShare plugin) {
    super(plugin);
  }

  @EventHandler
  public void onRestart(CountdownStartEvent event) {
    if (!(event.getCountdown() instanceof RestartCountdown)) return;
    callNewEvent(new PGMRestartEvent());
  }

  @EventHandler
  public void onRestartCancel(CancelRestartEvent event) {
    callNewEvent(new PGMCancelRestartEvent());
  }
}
