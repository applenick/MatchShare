package tc.oc.occ.matchshare.listeners;

import org.bukkit.Bukkit;
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
    if (!(event.getCountdown() instanceof RestartCountdown restart)) return;
    int online = Bukkit.getOnlinePlayers().size();

    callNewEvent(new PGMRestartEvent(restart.getRemaining(), online));
  }

  @EventHandler
  public void onRestartCancel(CancelRestartEvent event) {
    callNewEvent(new PGMCancelRestartEvent());
  }
}
