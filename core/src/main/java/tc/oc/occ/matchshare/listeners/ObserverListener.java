package tc.oc.occ.matchshare.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import tc.oc.occ.dispense.events.observers.PGMObserverInteractEvent;
import tc.oc.occ.dispense.events.observers.PGMObserverKitEvent;
import tc.oc.occ.matchshare.MatchShare;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.api.player.event.ObserverInteractEvent;
import tc.oc.pgm.spawns.events.ObserverKitApplyEvent;

public class ObserverListener extends ShareListener {

  public ObserverListener(MatchShare plugin) {
    super(plugin);
  }

  @EventHandler
  public void onInteract(ObserverInteractEvent event) {
    if (event.getClickedItem() == null) return;
    Player observer = event.getPlayer().getBukkit();
    ItemStack itemHeld = event.getClickedItem();
    MatchPlayer targetMatchPlayer = event.getClickedPlayer();
    Player target = targetMatchPlayer != null ? targetMatchPlayer.getBukkit() : null;
    boolean isTargetObserver = targetMatchPlayer != null && targetMatchPlayer.isObserving();
    callNewEvent(new PGMObserverInteractEvent(observer, itemHeld, target, isTargetObserver));
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onKit(ObserverKitApplyEvent event) {
    if (event.getPlayer() == null) return;
    callNewEvent(new PGMObserverKitEvent(event.getPlayer().getBukkit()));
  }
}
