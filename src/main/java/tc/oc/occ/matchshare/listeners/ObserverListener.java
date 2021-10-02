package tc.oc.occ.matchshare.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import tc.oc.occ.dispense.events.observers.PGMObserverInteractEvent;
import tc.oc.occ.dispense.events.observers.PGMObserverKitEvent;
import tc.oc.occ.matchshare.MatchShare;
import tc.oc.pgm.api.player.event.ObserverInteractEvent;
import tc.oc.pgm.spawns.events.ObserverKitApplyEvent;

public class ObserverListener extends ShareListener {

  public ObserverListener(MatchShare plugin) {
    super(plugin);
  }

  @EventHandler
  public void onInteract(ObserverInteractEvent event) {
    if (event.getClickedItem() == null) return;
    callNewEvent(
        new PGMObserverInteractEvent(event.getPlayer().getBukkit(), event.getClickedItem()));
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onKit(ObserverKitApplyEvent event) {
    if (event.getPlayer() == null) return;
    callNewEvent(new PGMObserverKitEvent(event.getPlayer().getBukkit()));
  }
}
