package tc.oc.occ.matchshare.listeners;

import static tc.oc.occ.matchshare.utils.WoolUtils.WOOL;
import static tc.oc.pgm.util.material.ColorUtils.COLOR_UTILS;

import com.google.common.collect.HashMultimap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import tc.oc.occ.dispense.events.objectives.PGMWoolDestroyEvent;
import tc.oc.occ.matchshare.MatchShare;
import tc.oc.pgm.api.match.event.MatchFinishEvent;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.api.player.ParticipantState;
import tc.oc.pgm.goals.Goal;
import tc.oc.pgm.goals.GoalMatchModule;
import tc.oc.pgm.goals.ShowOption;
import tc.oc.pgm.util.event.entity.EntityDespawnInVoidEvent;
import tc.oc.pgm.util.material.MaterialData;
import tc.oc.pgm.wool.MonumentWool;

public class WoolDestructionListener extends ShareListener {

  private final Map<Item, UUID> droppedWools;
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

    ParticipantState player = getParticipantState(event.getWhoClicked().getUniqueId());
    if (player == null) return;

    for (ItemStack ingredient : event.getInventory().getMatrix()) {
      if (this.isDestroyableWool(ingredient, player.getParty())) {
        for (DyeColor color : DyeColor.values()) {
          if (COLOR_UTILS.isColor(MaterialData.item(ingredient), color)) {
            acceptWoolDestroy(player, color);
            break;
          }
        }
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
      for (DyeColor color : DyeColor.values()) {
        if (COLOR_UTILS.isColor(MaterialData.item(stack), color)) {
          acceptWoolDestroy(player, color);
          break;
        }
      }
    }
  }

  @EventHandler
  public void onMatchEnd(MatchFinishEvent event) {
    this.droppedWools.clear();
    this.destroyedWools.clear();
  }

  @EventHandler
  public void onWoolMerge(ItemMergeEvent event) {
    if (WOOL.matches(event.getEntity().getItemStack().getType())) {
      if (droppedWools.containsKey(event.getEntity())) {
        event.setCancelled(true);
      }
    }
  }

  private boolean isDestroyableWool(ItemStack stack, Competitor team) {
    if (stack == null || !WOOL.matches(stack.getType())) {
      return false;
    }

    boolean enemyOwned = false;
    GoalMatchModule gmm = team.getMatch().getModule(GoalMatchModule.class);

    if (gmm != null) {
      for (Goal<?> goal : gmm.getGoals()) {
        if (goal instanceof MonumentWool wool) {
          if (wool.hasShowOption(ShowOption.STATS)
              && !wool.isPlaced()
              && COLOR_UTILS.isColor(MaterialData.item(stack), wool.getDyeColor())) {
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
