package tc.oc.occ.matchshare.listeners;

import static tc.oc.occ.matchshare.utils.WoolUtils.WOOL;
import static tc.oc.pgm.util.material.ColorUtils.COLOR_UTILS;

import com.google.common.collect.HashMultimap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import tc.oc.occ.dispense.events.objectives.PGMWoolDestroyEvent;
import tc.oc.occ.matchshare.MatchShare;
import tc.oc.pgm.api.match.event.MatchFinishEvent;
import tc.oc.pgm.api.match.event.MatchLoadEvent;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.api.player.ParticipantState;
import tc.oc.pgm.api.player.event.MatchPlayerDeathEvent;
import tc.oc.pgm.api.tracker.info.FallInfo;
import tc.oc.pgm.goals.Goal;
import tc.oc.pgm.goals.GoalMatchModule;
import tc.oc.pgm.goals.ShowOption;
import tc.oc.pgm.util.event.entity.EntityDespawnInVoidEvent;
import tc.oc.pgm.util.material.MaterialData;
import tc.oc.pgm.wool.MonumentWool;

public class WoolDestructionListener extends ShareListener {

  private final Map<Item, ParticipantState> droppedWools;
  private final HashMultimap<DyeColor, UUID> destroyedWools;

  public WoolDestructionListener(MatchShare plugin) {
    super(plugin);
    this.droppedWools = new WeakHashMap<>();
    this.destroyedWools = HashMultimap.create();
  }

  public void acceptWoolDestroy(ParticipantState player, DyeColor color) {
    if (!this.destroyedWools.containsEntry(color, player.getId())) {
      this.destroyedWools.put(color, player.getId());
      Player bukkit = Bukkit.getPlayer(player.getId());
      if (bukkit != null) {
        callNewEvent(new PGMWoolDestroyEvent(bukkit, color));
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void handleCraft(final CraftItemEvent event) {
    if (!(event.getWhoClicked() instanceof Player)) return;

    ParticipantState player = getParticipantState(event.getWhoClicked().getUniqueId());
    if (player == null) return;

    creditWools(player, event.getInventory().getMatrix());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onVoidDeath(MatchPlayerDeathEvent event) {
    if (!(event.getDamageInfo() instanceof FallInfo fall) || fall.getTo() != FallInfo.To.VOID) {
      return;
    }

    ParticipantState player = event.getVictim().getParticipantState();
    if (player == null) return;

    creditWools(player, event.getVictim().getBukkit().getInventory().getContents());
  }

  private void creditWools(ParticipantState player, ItemStack[] stacks) {
    Set<DyeColor> colors = EnumSet.noneOf(DyeColor.class);
    for (ItemStack stack : stacks) {
      if (isDestroyableWool(stack, player.getParty())) {
        DyeColor color = getColor(stack);
        if (color != null) {
          colors.add(color);
        }
      }
    }
    colors.forEach(color -> acceptWoolDestroy(player, color));
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDrop(PlayerDropItemEvent event) {
    ParticipantState player = getParticipantState(event.getPlayer().getUniqueId());
    if (player == null) return;

    Item itemDrop = event.getItemDrop();
    if (isDestroyableWool(itemDrop.getItemStack(), player.getParty())) {
      this.droppedWools.put(itemDrop, player);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onVoidDespawn(EntityDespawnInVoidEvent event) {
    Entity entity = event.getEntity();
    if (entity instanceof Item item) {
      creditDestroyed(item);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onWoolDamage(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Item item)) return;
    if (!this.droppedWools.containsKey(item)) return;

    plugin.getServer().getScheduler().runTask(plugin, () -> {
      if (item.isDead()) {
        creditDestroyed(item);
      }
    });
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerPickup(PlayerPickupItemEvent event) {
    if (event.getRemaining() <= 0) {
      this.droppedWools.remove(event.getItem());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onInventoryPickup(InventoryPickupItemEvent event) {
    this.droppedWools.remove(event.getItem());
  }

  private void creditDestroyed(Item item) {
    ParticipantState player = this.droppedWools.remove(item);
    if (player == null) return;

    ItemStack stack = item.getItemStack();
    if (isDestroyableWool(stack, player.getParty())) {
      DyeColor color = getColor(stack);
      if (color != null) {
        acceptWoolDestroy(player, color);
      }
    }
  }

  @EventHandler
  public void onMatchEnd(MatchFinishEvent event) {
    reset();
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onMatchLoad(MatchLoadEvent event) {
    reset();
  }

  private void reset() {
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

  private @Nullable DyeColor getColor(ItemStack stack) {
    for (DyeColor color : DyeColor.values()) {
      if (COLOR_UTILS.isColor(MaterialData.item(stack), color)) {
        return color;
      }
    }
    return null;
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
