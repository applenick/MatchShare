package tc.oc.occ.matchshare.listeners;

import com.google.common.collect.HashMultimap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDespawnInVoidEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;
import tc.oc.occ.dispense.events.objectives.PGMWoolDestroyEvent;
import tc.oc.occ.matchshare.MatchShare;
import tc.oc.pgm.api.match.event.MatchFinishEvent;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.api.player.ParticipantState;
import tc.oc.pgm.goals.Goal;
import tc.oc.pgm.goals.GoalMatchModule;
import tc.oc.pgm.wool.MonumentWool;

public class WoolDestructionListener extends ShareListener {

  private Map<Item, UUID> droppedWools;
  private final HashMultimap<DyeColor, UUID> destroyedWools;

  public WoolDestructionListener(MatchShare plugin) {
    super(plugin);
    this.droppedWools = new WeakHashMap<>();
    this.destroyedWools = HashMultimap.create();
  }

  public void acceptWoolDestroy(ParticipantState player, DyeColor color) {
    if (!this.destroyedWools.containsEntry(color, player.getId())) {
      this.destroyedWools.put(color, player.getId());
      if (player.getPlayer().isPresent()) {
        callNewEvent(new PGMWoolDestroyEvent(player.getPlayer().get().getBukkit(), color));
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void handleCraft(final CraftItemEvent event) {
    if (!(event.getWhoClicked() instanceof Player)) return;

    ParticipantState player = getParticipantState(((Player) event.getWhoClicked()).getUniqueId());
    if (player == null) return;

    for (ItemStack ingredient : event.getInventory().getMatrix()) {
      if (this.isDestroyableWool(ingredient, player.getParty())) {
        acceptWoolDestroy(player, ((Wool) ingredient.getData()).getColor());
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDrop(PlayerDropItemEvent event) {
    ParticipantState player = getParticipantState(event.getPlayer().getUniqueId());
    if (player == null) return;

    Competitor team = player.getParty();
    Item itemDrop = event.getItemDrop();
    ItemStack item = itemDrop.getItemStack();

    if (isDestroyableWool(item, team)) {
      this.droppedWools.put(itemDrop, player.getId());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onVoidDespawn(EntityDespawnInVoidEvent event) {
    Entity entity = event.getEntity();
    if (!(entity instanceof Item)) return;
    ItemStack stack = ((Item) entity).getItemStack();

    UUID playerId = this.droppedWools.remove(entity);
    if (playerId == null) return;

    ParticipantState player = getParticipantState(playerId);
    if (player == null) return;

    if (isDestroyableWool(stack, player.getParty())) {
      acceptWoolDestroy(player, ((Wool) stack.getData()).getColor());
    }
  }

  @EventHandler
  public void onMatchEnd(MatchFinishEvent event) {
    this.droppedWools.clear();
    this.destroyedWools.clear();
  }

  @EventHandler
  public void onWoolMerge(ItemMergeEvent event) {
    if (event.getEntity().getItemStack().getType() == Material.WOOL) {
      if (droppedWools.containsKey(event.getEntity())) {
        event.setCancelled(true);
      }
    }
  }

  private boolean isDestroyableWool(ItemStack stack, Competitor team) {
    if (stack == null || stack.getType() != Material.WOOL) {
      return false;
    }

    DyeColor color = ((Wool) stack.getData()).getColor();
    boolean enemyOwned = false;

    GoalMatchModule gmm = team.getMatch().getModule(GoalMatchModule.class);

    if (gmm != null) {
      for (Goal goal : gmm.getGoals()) {
        if (goal instanceof MonumentWool) {
          MonumentWool wool = (MonumentWool) goal;
          if (wool.isVisible() && !wool.isPlaced() && wool.getDyeColor() == color) {
            if (wool.getOwner() == team) {
              return false;
            } else {
              enemyOwned = true;
            }
          }
        }
      }
    }

    return enemyOwned;
  }
}
